# E-commerce-Platform
마이크로서비스 아키텍처를 기반으로 한 전자상거래 플랫폼입니다.
사용자, 주문, 결제, 재고와 같은 핵심 서비스를 구현하며, 
헥사고널 아키텍처, 이벤트 기반 아키텍처(EDA), SAGA 패턴, Outbox 패턴 등을 적용하였습니다.

## 목차
- [기본 시나리오](#기본-시나리오)
- [이벤트 추출](#이벤트-추출)
- [커맨드,액터,정책](#커맨드-액터-정책)
- [설치 방법](#설치-방법)
- [프로젝트 실행](#프로젝트-실행)

## 기본 시나리오
- 고객1이 회원서비스를 통해 회원 가입을 한다.
- 고객1이 상품서비스를 통해 상품을 등록한다.(상품 이름, 상품 가격, 재고 수량)
- 고객2가 회원서비스를 통해 회원 가입을 한다.
- 고객2가 상품서비스에서 상품을 조회하고, 주문 서비스를 통해 구매를 요청한다.
- 주문서비스가 재고서비스에 재고를 확인한다.
- 재고가 있다면 결제서비스가 결제를 처리한다.
- 결제가 성공하면 재고서비스가 재고량을 감소시킨다.
- 배송서비스가 주문 완료 이벤트를 받아 배송을 시작한다.
- 재고 부족 또는 결제 실패 시 사용자에게 에러 메시지를 반환한다.

## 이벤트 추출
- 회원 가입: UserRegistered → 다른 서비스에서 사용자 정보 동기화.
- 상품 등록: ProductRegistered → 재고 서비스에서 재고 초기화.
- 구매 요청: OrderRequested → 재고 서비스로 재고 확인 요청.
- 재고 확인: StockChecked → 주문 서비스에서 결제 진행 여부 결정.
- 결제 완료: PaymentCompleted → 재고 서비스로 재고 감소 요청.
- 재고 감소: StockDecreased → 주문 서비스로 주문 확정 요청.
- 주문 확정: OrderConfirmed → 배송 서비스로 배송 시작 요청.
- 배송 시작: DeliveryStarted → 사용자에게 알림.
- 실패 처리: StockDecreaseFailed, PaymentFailed, OrderFailed → 롤백 및 사용자 알림.

## 커맨드, 액터, 정책
|도메인 이벤트|커맨드|액터|설명|정책|
|---|---|---|---|---|
|UserRegistered|RegisterUser|고객1, 고객2|고객1 또는 고객2가 회원 가입을 요청하여 회원 서비스가 사용자 정보를 등록.|1. 주문 서비스: 사용자 정보를 캐싱하여 주문 생성 시 인증 확인.<br>2. 상품 서비스: 판매자 역할 확인 후 상품 등록 권한 부여.|
|ProductRegistered|RegisterProduct|고객1 (판매자)|고객1이 상품 서비스에 상품 정보(이름, 가격, 재고)를 등록.|1. 재고 서비스: `InitializeStock(productId, initialStock)` 커맨드 실행하여 재고 초기화.<br>2. 주문 서비스: 상품 정보를 캐싱하여 조회 성능 최적화.|
|OrderRequested|CreateOrder|고객2 (구매자)|고객2가 주문 서비스에 상품 구매 요청을 생성.|1. 주문 서비스: `CheckStock(productId, quantity)` 커맨드 실행하여 재고 서비스에 재고 확인 요청.|
|StockChecked|CheckStock|주문 서비스|주문 서비스가 재고 서비스에 재고 확인을 요청.|1. 주문 서비스: 재고가 충분하면 `ProcessPayment(orderId, amount)` 커맨드 실행.<br>2. 주문 서비스: 재고 부족 시 `OrderFailed` 이벤트 발행 및 주문 취소.|
|PaymentCompleted|ProcessPayment|고객2 (구매자)|고객2가 결제 서비스에 결제를 요청하고 결제가 성공적으로 완료.|1. 재고 서비스: `DecreaseStock(productId, quantity)` 커맨드 실행하여 재고 감소.|
|PaymentFailed|ProcessPayment|고객2 (구매자)|고객2의 결제 요청이 실패(예: 카드 한도 초과).|1. 주문 서비스: `OrderFailed` 이벤트 발행 및 주문 취소.|
|StockDecreased|DecreaseStock|결제 서비스|결제 성공 후 결제 서비스가 재고 서비스에 재고 감소를 요청.|1. 주문 서비스: `ConfirmOrder(orderId, productId, quantity)` 커맨드 실행하여 주문 확정.|
|StockDecreaseFailed|DecreaseStock|결제 서비스|재고 감소 요청이 실패(예: 재고 부족).|1. 결제 서비스: `RefundPayment(paymentId)` 커맨드 실행하여 결제 환불.<br>2. 주문 서비스: `OrderFailed` 이벤트 발행 및 주문 취소.|
|OrderConfirmed|ConfirmOrder|재고 서비스|재고 감소 성공 후 주문 서비스가 주문을 확정.|1. 배송 서비스: `StartDelivery(orderId, productId, userId)` 커맨드 실행하여 배송 시작.|
|OrderFailed|ConfirmOrder|재고 서비스 / 결제 서비스|재고 부족 또는 결제 실패로 주문 확정이 실패.|1. 주문 서비스: 주문 상태를 "취소"로 업데이트.|
|DeliveryStarted|StartDelivery|주문 서비스|주문 확정 후 배송 서비스가 배송 프로세스를 시작.|1. 배송 서비스: 외부 물류 시스템에 배송 요청 전송.|

## Aggregate
|어그리게이트|속성|처리 로직|
|---|---|---|
|User|- `userId`: 사용자 고유 ID<br>- `email`: 이메일<br>- `role`: 역할 ("seller", "buyer")<br>- `createdAt`: 가입 시각|1. **RegisterUser**: 이메일 고유성 검증 후 사용자 등록, `UserRegistered` 이벤트 발행.|
|Product|- `productId`: 상품 고유 ID<br>- `name`: 상품 이름<br>- `price`: 상품 가격<br>- `sellerId`: 판매자 ID<br>- `createdAt`: 등록 시각|1. **RegisterProduct**: 가격이 0 이상이고 판매자가 유효한지 검증 후 상품 등록, `ProductRegistered` 이벤트 발행.|
|Inventory|- `productId`: 상품 고유 ID<br>- `availableQuantity`: 사용 가능한 재고 수량<br>- `lastUpdatedAt`: 마지막 업데이트 시각|1. **InitializeStock**: 초기 재고 설정, 상태 업데이트.<br>2. **CheckStock**: 요청 수량과 재고 비교, `StockChecked` 이벤트 발행.<br>3. **DecreaseStock**: 재고 충분 여부 확인 후 감소, `StockDecreased` 또는 `StockDecreaseFailed` 이벤트 발행.|
|Order|- `orderId`: 주문 고유 ID<br>- `userId`: 구매자 ID<br>- `productId`: 상품 ID<br>- `quantity`: 주문 수량<br>- `totalAmount`: 총액<br>- `status`: 주문 상태 ("Requested", "Confirmed", "Failed")<br>- `createdAt`: 주문 생성 시각|1. **CreateOrder**: 구매자와 상품 유효성 검증 후 주문 생성, `OrderRequested` 이벤트 발행.<br>2. **ConfirmOrder**: 주문 상태가 "Requested"인지 확인 후 확정, `OrderConfirmed` 이벤트 발행.<br>3. **FailOrder**: 주문 실패 사유 기록 후 상태를 "Failed"로 변경, `OrderFailed` 이벤트 발행.|
|Payment|- `paymentId`: 결제 고유 ID<br>- `orderId`: 주문 ID<br>- `userId`: 구매자 ID<br>- `amount`: 결제 금액<br>- `status`: 결제 상태 ("Completed", "Failed")<br>- `completedAt`: 결제 완료 시각|1. **ProcessPayment**: 주문 금액과 결제 금액 일치 여부 검증 후 결제 처리, `PaymentCompleted` 또는 `PaymentFailed` 이벤트 발행.<br>2. **RefundPayment**: 결제 상태가 "Completed"인지 확인 후 환불 처리, `PaymentRefunded` 이벤트 발행(필요 시).|
|Delivery|- `deliveryId`: 배송 고유 ID<br>- `orderId`: 주문 ID<br>- `productId`: 상품 ID<br>- `userId`: 구매자 ID<br>- `status`: 배송 상태 ("InPreparation", "Shipped")<br>- `startedAt`: 배송 시작 시각|1. **StartDelivery**: 주문과 상품 유효성 검증 후 배송 시작, `DeliveryStarted` 이벤트 발행.|

## 기술 스택
- 언어/프레임워크: Java17+/Spring Boot
- 빌드도구 : Maven
- 데이터베이스: PostgreSQL
- 메시지 브로커: Apache Kafka
- 컨테이너화: Docker 및 Docker Compose
- API 문서화: OpenAPI/Swagger
- 테스트: JUnit4+

## 설치 방법

## 실행 방법


