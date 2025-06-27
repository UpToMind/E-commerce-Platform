package com.ecommerce.system.order.service;

import com.ecommerce.system.domain.valueobject.OrderStatus;
import com.ecommerce.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.ecommerce.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.ecommerce.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.ecommerce.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.ecommerce.system.outbox.OutboxStatus;
import com.ecommerce.system.saga.SagaStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.ecommerce.system.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(classes = OrderTestConfiguration.class)
@TestPropertySource(properties = {
    // PostgreSQL Testcontainer 설정 (실제 PostgreSQL 사용)
    "spring.datasource.url=jdbc:postgresql://localhost:5432/postgres?currentSchema=order&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified",
    "spring.datasource.username=postgres",
    "spring.datasource.password=1445",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.show-sql=true",
    "spring.sql.init.mode=never", // 스키마 자동 생성 비활성화 (이미 존재)
    "logging.level.com.ecommerce.system=DEBUG"
})
public class OptimisticLockConcurrencyTest {

    @MockBean
    private PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private PaymentOutboxHelper paymentOutboxHelper;

    @Autowired
    private ObjectMapper objectMapper;

    // 실제 데이터의 UUID 사용
    private final UUID USER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID SELLER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID SAGA_ID = UUID.randomUUID();
    private final UUID MESSAGE_ID = UUID.randomUUID();

    private OrderPaymentOutboxMessage testOutboxMessage;
    private final AtomicInteger versionCounter = new AtomicInteger(0);
    private final AtomicReference<OutboxStatus> currentStatus = new AtomicReference<>(OutboxStatus.STARTED);

    @BeforeEach
    public void setUp() {
        // 버전 카운터 초기화
        versionCounter.set(0);
        currentStatus.set(OutboxStatus.STARTED);

        // 실제 PaymentEventPayload 생성
        OrderPaymentEventPayload payload = OrderPaymentEventPayload.builder()
                .orderId(ORDER_ID.toString())
                .userId(USER_ID.toString())
                .price(new BigDecimal("25.00"))
                .createdAt(ZonedDateTime.now())
                .paymentOrderStatus("PENDING")
                .build();

        // 테스트용 Outbox 메시지 생성
        testOutboxMessage = OrderPaymentOutboxMessage.builder()
                .id(MESSAGE_ID)
                .sagaId(SAGA_ID)
                .createdAt(ZonedDateTime.now())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(payload))
                .orderStatus(OrderStatus.PENDING)
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .version(0)
                .build();

        // 모킹 설정
        setupMockBehavior();
        
        log.info("테스트 Outbox 메시지 생성 완료 - ID: {}, Version: {}", 
                testOutboxMessage.getId(), testOutboxMessage.getVersion());
    }

    @Test
    public void testOptimisticLockingWithConcurrentSchedulers() throws InterruptedException {
        log.info("=== 낙관적 락 동시성 테스트 시작 ===");
        
        final int SCHEDULER_COUNT = 5; // 5개의 스케줄러 시뮬레이션
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(SCHEDULER_COUNT);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger lockFailureCount = new AtomicInteger(0);
        final List<String> results = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(SCHEDULER_COUNT);

        // 여러 스케줄러가 동시에 같은 메시지 처리 시뮬레이션
        for (int i = 0; i < SCHEDULER_COUNT; i++) {
            final int schedulerId = i + 1;
            executor.submit(() -> {
                try {
                    // 모든 스케줄러가 동시에 시작하도록 대기
                    startLatch.await();
                    
                    log.info("스케줄러 {} 시작 - 메시지 처리 시도", schedulerId);
                    
                    // 실제 스케줄러처럼 메시지를 조회하고 상태 업데이트
                    simulateSchedulerProcessing(schedulerId);
                    
                    successCount.incrementAndGet();
                    synchronized (results) {
                        results.add("스케줄러 " + schedulerId + ": 성공");
                    }
                    log.info("✅ 스케줄러 {} 성공적으로 메시지 처리 완료", schedulerId);
                    
                } catch (OptimisticLockingFailureException e) {
                    lockFailureCount.incrementAndGet();
                    synchronized (results) {
                        results.add("스케줄러 " + schedulerId + ": 낙관적 락 실패");
                    }
                    log.info("🔒 스케줄러 {} 낙관적 락으로 인한 실패: {}", schedulerId, e.getMessage());
                    
                } catch (Exception e) {
                    synchronized (results) {
                        results.add("스케줄러 " + schedulerId + ": 예상치 못한 오류 - " + e.getMessage());
                    }
                    log.error("❌ 스케줄러 {} 예상치 못한 오류: {}", schedulerId, e.getMessage(), e);
                    
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스케줄러 동시 시작
        log.info("모든 스케줄러 동시 시작!");
        startLatch.countDown();
        
        // 모든 스케줄러 완료 대기 (최대 10초)
        boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "모든 스케줄러가 시간 내에 완료되지 않았습니다");

        executor.shutdown();

        // 결과 출력 및 검증
        log.info("=== 테스트 결과 ===");
        log.info("성공한 스케줄러: {}", successCount.get());
        log.info("낙관적 락 실패: {}", lockFailureCount.get());
        
        results.forEach(result -> log.info("- {}", result));
        
        // 핵심 검증: 정확히 1개의 스케줄러만 성공해야 함
        assertEquals(1, successCount.get(), 
                "낙관적 락으로 인해 정확히 1개의 스케줄러만 성공해야 합니다");
        assertEquals(SCHEDULER_COUNT - 1, lockFailureCount.get(), 
                "나머지 스케줄러들은 낙관적 락 예외로 실패해야 합니다");

        // 최종 상태 검증
        assertEquals(OutboxStatus.COMPLETED, currentStatus.get(), 
                "최종 상태는 COMPLETED여야 합니다");
        assertEquals(1, versionCounter.get(), 
                "버전은 1번만 증가해야 합니다");
    }

    @Test 
    public void testSequentialProcessingWorks() {
        log.info("=== 순차 처리 테스트 시작 ===");
        
        // 순차 처리용 모킹 재설정
        setupSequentialMockBehavior();
        
        // 순차적으로 처리하면 모두 성공해야 함
        for (int i = 1; i <= 3; i++) {
            log.info("순차 처리 {} 시작", i);
            simulateSchedulerProcessing(i);
            log.info("✅ 순차 처리 {} 완료", i);
        }
        
        log.info("순차 처리 테스트 완료 - 최종 버전: {}, 상태: {}", 
                versionCounter.get(), currentStatus.get());
        
        // 순차 처리에서는 3번 모두 성공해야 함
        assertEquals(3, versionCounter.get(), "순차 처리로 버전이 3번 증가해야 합니다");
        assertEquals(OutboxStatus.COMPLETED, currentStatus.get(), 
                "최종 상태는 COMPLETED여야 합니다");
    }

    private void simulateSchedulerProcessing(int schedulerId) {
        // 1. 메시지 조회 (실제 스케줄러처럼)
        Optional<OrderPaymentOutboxMessage> messageOpt = paymentOutboxRepository
                .findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, SAGA_ID, SagaStatus.STARTED);
        
        assertTrue(messageOpt.isPresent(), "처리할 메시지를 찾을 수 없습니다");
        OrderPaymentOutboxMessage message = messageOpt.get();

        log.info("스케줄러 {} - 메시지 조회 완료 (현재 버전: {})", schedulerId, message.getVersion());

        // 2. 메시지 발행 시뮬레이션 (실제로는 Kafka 발행)
        try {
            Thread.sleep(10); // 네트워크 지연 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 3. 상태를 COMPLETED로 업데이트
        message.setOutboxStatus(OutboxStatus.COMPLETED);
        message.setProcessedAt(ZonedDateTime.now());

        log.info("스케줄러 {} - 메시지 상태 업데이트 시도 (버전: {})", schedulerId, message.getVersion());

        // 4. 저장 (여기서 낙관적 락 체크 발생)
        paymentOutboxHelper.save(message);
        
        log.info("스케줄러 {} - 메시지 저장 완료", schedulerId);
    }

    private void setupMockBehavior() {
        // 초기화
        versionCounter.set(0);
        currentStatus.set(OutboxStatus.STARTED);

        // 조회 시 항상 같은 메시지 반환 (동시성 테스트용)
        when(paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
                anyString(), eq(OutboxStatus.STARTED), any(SagaStatus[].class)))
                .thenReturn(Optional.of(List.of(testOutboxMessage)));
        
        // SagaId로 조회하는 메서드도 모킹 - 가변 인수를 올바르게 처리
        when(paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
                eq(ORDER_SAGA_NAME), eq(SAGA_ID), eq(SagaStatus.STARTED)))
                .thenReturn(Optional.of(testOutboxMessage));

        // 동시성 테스트용 모킹: 첫 번째만 성공, 나머지는 OptimisticLockingFailureException
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class)))
                .thenAnswer(invocation -> {
                    OrderPaymentOutboxMessage message = invocation.getArgument(0);
                    
                    // 첫 번째 호출만 성공, 나머지는 OptimisticLockingFailureException
                    if (versionCounter.compareAndSet(0, 1)) {
                        // 첫 번째 성공 - 새로운 메시지 생성
                        OrderPaymentOutboxMessage updatedMessage = OrderPaymentOutboxMessage.builder()
                                .id(message.getId())
                                .sagaId(message.getSagaId())
                                .createdAt(message.getCreatedAt())
                                .processedAt(message.getProcessedAt())
                                .type(message.getType())
                                .payload(message.getPayload())
                                .sagaStatus(message.getSagaStatus())
                                .orderStatus(message.getOrderStatus())
                                .outboxStatus(message.getOutboxStatus())
                                .version(1)
                                .build();
                        currentStatus.set(updatedMessage.getOutboxStatus());
                        log.info("🟢 첫 번째 저장 성공 - 버전: {}", updatedMessage.getVersion());
                        return updatedMessage;
                    } else {
                        // 나머지는 낙관적 락 실패
                        log.info("🔴 낙관적 락 실패 - 이미 다른 스레드가 업데이트함");
                        throw new OptimisticLockingFailureException("Row was updated or deleted by another transaction");
                    }
                });
    }

    private void setupSequentialMockBehavior() {
        // 순차 처리용 모킹 재설정
        versionCounter.set(0);
        currentStatus.set(OutboxStatus.STARTED);

        // 조회 시 항상 같은 메시지 반환 (순차 처리용)
        when(paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
                anyString(), eq(OutboxStatus.STARTED), any(SagaStatus[].class)))
                .thenReturn(Optional.of(List.of(testOutboxMessage)));
        
        // SagaId로 조회하는 메서드도 모킹 - 가변 인수를 올바르게 처리
        when(paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
                eq(ORDER_SAGA_NAME), eq(SAGA_ID), eq(SagaStatus.STARTED)))
                .thenReturn(Optional.of(testOutboxMessage));

        // 순차 처리에서는 모든 저장이 성공해야 함
        when(paymentOutboxRepository.save(any(OrderPaymentOutboxMessage.class)))
                .thenAnswer(invocation -> {
                    OrderPaymentOutboxMessage message = invocation.getArgument(0);
                    int newVersion = versionCounter.incrementAndGet();
                    // 새로운 메시지 생성
                    OrderPaymentOutboxMessage updatedMessage = OrderPaymentOutboxMessage.builder()
                            .id(message.getId())
                            .sagaId(message.getSagaId())
                            .createdAt(message.getCreatedAt())
                            .processedAt(message.getProcessedAt())
                            .type(message.getType())
                            .payload(message.getPayload())
                            .sagaStatus(message.getSagaStatus())
                            .orderStatus(message.getOrderStatus())
                            .outboxStatus(message.getOutboxStatus())
                            .version(newVersion)
                            .build();
                    currentStatus.set(updatedMessage.getOutboxStatus());
                    log.info("🟢 순차 저장 성공 - 버전: {}", newVersion);
                    return updatedMessage;
                });
    }

    private String createPayload(OrderPaymentEventPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Payload 생성 실패", e);
        }
    }
} 