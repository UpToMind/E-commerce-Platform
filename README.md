# E-commerce System (전자상거래 시스템)

마이크로서비스 아키텍처를 기반으로 한 전자상거래 플랫폼입니다.
DDD(Domain-Driven Design), 이벤트 기반 아키텍처(EDA), SAGA 패턴, **Outbox 패턴** 등 현대적인 분산 시스템 패턴을 적용하여 구현되었습니다.

## 📋 목차
- [시스템 아키텍처](#시스템-아키텍처)
- [핵심 기능](#핵심-기능)
- [마이크로서비스 구성](#마이크로서비스-구성)
- [기술적 특징](#기술적-특징)
- [이벤트 스토밍 결과](#이벤트-스토밍-결과)
- [기본 시나리오](#기본-시나리오)
- [이벤트 플로우](#이벤트-플로우)
- [도메인 모델](#도메인-모델)
- [기술 스택](#기술-스택)
- [설치 및 실행](#설치-및-실행)

## 🏗️ 시스템 아키텍처

본 시스템은 **헥사고날 아키텍처(Hexagonal Architecture)**와 **DDD(Domain-Driven Design)**를 기반으로 설계되었으며, 각 서비스는 다음과 같은 계층으로 구성됩니다:

```
📦 Service Architecture
├── 🎯 Domain Layer (Core Business Logic)
│   ├── Domain Core (Entities, Value Objects, Domain Services)
│   └── Application Service (Use Cases, Domain Events)
├── 🔌 Infrastructure Layer
│   ├── Data Access (JPA Repositories, Database)
│   ├── Messaging (Kafka Producers/Consumers)
│   └── Container (Spring Boot Configuration)
└── 🌐 Application Layer (REST Controllers, DTOs)
```

## 🚀 핵심 기능

### ✨ 분산 시스템 패턴
- **🔄 Outbox Pattern**: 트랜잭션 일관성과 메시지 전달 보장
- **📋 SAGA Pattern**: 분산 트랜잭션 관리 및 보상 처리
- **🎯 Event-Driven Architecture**: 비동기 이벤트 기반 통신
- **🏛️ Domain-Driven Design**: 도메인 중심 설계

### 🛡️ 신뢰성 및 일관성
- **원자성 보장**: 데이터베이스 트랜잭션과 메시지 발행의 원자성
- **장애 복구**: 자동 재시도 및 보상 트랜잭션
- **동시성 제어**: 낙관적 락킹을 통한 동시성 관리
- **이벤트 순서 보장**: 메시지 순서 처리 및 중복 방지

## 🔧 마이크로서비스 구성

### 📊 서비스 개요
| 서비스 | 역할 | 주요 기능 | 포트 |
|--------|------|-----------|------|
| **Order Service** | 주문 관리 | 주문 생성/취소, SAGA 오케스트레이션 | 8181 |
| **Payment Service** | 결제 처리 | 결제 승인/취소, 결제 상태 관리 | 8182 |
| **Seller Service** | 판매자 관리 | 상품 승인, 재고 관리 | 8183 |
| **User Service** | 사용자 관리 | 회원 가입/관리, 인증 | 8184 |

### 🏗️ 공통 인프라스트럭처
```
📦 Infrastructure
├── 📤 Outbox (메시지 발행 보장)
├── 🔄 SAGA (분산 트랜잭션 관리)  
├── 📨 Kafka (메시지 브로커)
├── 🗄️ Common Domain (공통 도메인 객체)
└── 🐳 Docker Compose (컨테이너 오케스트레이션)
```

## 🎯 기술적 특징

### 🔄 Outbox Pattern 구현
- **트랜잭션 보장**: 데이터베이스 변경과 메시지 발행의 원자성
- **자동 재시도**: OutboxScheduler를 통한 실패 메시지 재처리
- **상태 관리**: STARTED → COMPLETED → FAILED 상태 추적
- **배치 처리**: 성능 최적화를 위한 배치 메시지 처리

### 📋 SAGA Pattern 구현
- **오케스트레이션**: Order Service가 전체 플로우 관리
- **보상 트랜잭션**: 실패 시 자동 롤백 처리
- **상태 추적**: 각 단계별 상태 관리 및 모니터링

### 🏛️ DDD 적용
- **Aggregate**: 일관성 경계 정의
- **Domain Events**: 도메인 변경사항 전파
- **Value Objects**: 불변 객체를 통한 데이터 무결성
- **Repository Pattern**: 데이터 접근 추상화

## 이벤트 스토밍 결과
<img width="1396" alt="image" src="https://github.com/user-attachments/assets/eb5cb9f3-3e63-48d3-9041-c5b4026c20ff" />

## 📝 기본 시나리오

### 🛒 주문 처리 플로우
1. **회원 가입**: 고객이 User Service를 통해 회원 가입
2. **상품 등록**: 판매자가 Seller Service를 통해 상품 등록
3. **주문 생성**: 고객이 Order Service를 통해 주문 생성
4. **재고 확인**: Seller Service에서 재고 가용성 확인
5. **결제 처리**: Payment Service에서 결제 승인 처리
6. **재고 차감**: 결제 성공 시 Seller Service에서 재고 감소
7. **주문 완료**: 모든 단계 성공 시 주문 상태를 완료로 변경

### 🔄 보상 트랜잭션 (실패 시)
- **결제 실패**: 주문 취소 및 사용자 알림
- **재고 부족**: 결제 취소 및 주문 취소
- **주문 취소**: 결제 취소 및 재고 복원

## 🔄 이벤트 플로우

### 📨 이벤트 목록 및 흐름

| 이벤트 이름 | 발행 서비스 | 구독 서비스 | 설명 |
|-------------|-------------|-------------|------|
| `UserCreated` | User Service | Order Service | 사용자 등록 완료 |
| `OrderCreated` | Order Service | Payment Service, Seller Service | 주문 생성 완료 |
| `OrderCancelled` | Order Service | Payment Service, Seller Service | 주문 취소 |
| `PaymentCompleted` | Payment Service | Order Service | 결제 완료 |
| `PaymentCancelled` | Payment Service | Order Service, Seller Service | 결제 취소 |
| `PaymentFailed` | Payment Service | Order Service | 결제 실패 |
| `OrderApproved` | Seller Service | Order Service | 주문 승인 완료 |
| `OrderRejected` | Seller Service | Order Service, Payment Service | 주문 거부 |

### 🎯 커맨드 및 정책

#### 커맨드 (Command)
| 커맨드 | 서비스 | 설명 |
|--------|--------|------|
| `CreateOrder` | Order Service | 새로운 주문 생성 |
| `ProcessPayment` | Payment Service | 결제 처리 |
| `ApproveOrder` | Seller Service | 주문 승인 및 재고 차감 |
| `CancelOrder` | Order Service | 주문 취소 |

#### 정책 (Policy)
| 정책 | 서비스 | 트리거 이벤트 | 설명 |
|------|--------|---------------|------|
| `ProcessPaymentPolicy` | Payment Service | `OrderCreated` | 주문 생성 시 결제 처리 |
| `ApproveOrderPolicy` | Seller Service | `PaymentCompleted` | 결제 완료 시 주문 승인 |
| `CompleteOrderPolicy` | Order Service | `OrderApproved` | 승인 완료 시 주문 완료 |
| `CancelPaymentPolicy` | Payment Service | `OrderCancelled` | 주문 취소 시 결제 취소 |

## 🗄️ 도메인 모델

### Aggregate 구성

#### 📦 Order Aggregate
```java
Order {
    - orderId: OrderId
    - userId: UserId
    - sellerId: SellerId
    - orderStatus: OrderStatus
    - price: Money
    - items: List<OrderItem>
    - trackingId: TrackingId
    - failureMessages: List<String>
}
```

#### 💳 Payment Aggregate
```java
Payment {
    - paymentId: PaymentId
    - orderId: OrderId
    - userId: UserId
    - price: Money
    - paymentStatus: PaymentStatus
    - createdAt: ZonedDateTime
}
```

#### 🏪 Seller Aggregate
```java
Seller {
    - sellerId: SellerId
    - products: List<Product>
    - orders: List<SellerOrder>
    - sellerApproval: SellerApproval
}
```

#### 👤 User Aggregate
```java
User {
    - userId: UserId
    - username: String
    - email: String
    - createdAt: ZonedDateTime
}
```

## 🛠️ 기술 스택

### 🔧 Backend
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.4.5
- **빌드 도구**: Maven
- **아키텍처**: Hexagonal Architecture + DDD

### 🗄️ Database & Messaging
- **데이터베이스**: PostgreSQL
- **메시지 브로커**: Apache Kafka
- **스키마 레지스트리**: Confluent Schema Registry
- **직렬화**: Apache Avro

### 🏗️ Infrastructure
- **컨테이너화**: Docker & Docker Compose
- **모니터링**: Spring Boot Actuator
- **테스트**: JUnit 5, Mockito
- **문서화**: OpenAPI/Swagger

### 📦 주요 의존성
```xml
<!-- Core Framework -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Messaging -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

## 🚀 설치 및 실행

### 📋 사전 요구사항
- **Java 17+**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git**

### 🔧 로컬 개발 환경 설정

#### 1️⃣ 프로젝트 클론
```bash
git clone https://github.com/your-username/e-commerce-system.git
cd e-commerce-system
```

#### 2️⃣ 인프라스트럭처 실행
```bash
# Kafka, Zookeeper, PostgreSQL 실행
cd infrastructure/docker-compose
docker-compose -f common.yml -f zookeeper.yml -f kafka_cluster.yml up -d

# Kafka 토픽 초기화
docker-compose -f init_kafka.yml up
```

#### 3️⃣ 애플리케이션 빌드
```bash
# 루트 디렉토리에서 전체 빌드
mvn clean compile
```

#### 4️⃣ 서비스 실행
```bash
# 각 서비스를 별도 터미널에서 실행
# Order Service
cd order-service/order-container
mvn spring-boot:run

# Payment Service  
cd payment-service/payment-container
mvn spring-boot:run

# Seller Service
cd seller-service/seller-container
mvn spring-boot:run

# User Service
cd user-service/user-container
mvn spring-boot:run
```

### 🐳 Docker로 전체 실행
```bash
# 전체 시스템을 Docker Compose로 실행
docker-compose up -d
```

### 🧪 테스트 실행
```bash
# 전체 테스트 실행
mvn test

# 특정 서비스 테스트
cd order-service
mvn test
```

### 🔍 모니터링
- **Kafka UI**: http://localhost:9021 (Confluent Control Center)
- **PostgreSQL**: localhost:5432 (사용자: postgres, 비밀번호: admin)
- **Application Health**: http://localhost:818X/actuator/health

## 📈 향후 개발 계획
- [ ] **API Gateway** 통합
- [ ] **Service Mesh** (Istio) 적용
- [ ] **분산 추적** (Jaeger) 구현
- [ ] **메트릭 수집** (Prometheus/Grafana)
- [ ] **Event Sourcing** 구현

## 📄 라이선스
이 프로젝트는 MIT 라이선스 하에 있습니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조해주세요.

---



