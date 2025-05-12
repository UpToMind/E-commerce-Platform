# E-commerce-Platform
마이크로서비스 아키텍처를 기반으로 한 전자상거래 플랫폼입니다.
사용자, 주문, 결제, 재고와 같은 핵심 서비스를 구현하며, 
헥사고널 아키텍처, 이벤트 기반 아키텍처(EDA), SAGA 패턴, Outbox 패턴 등을 적용하였습니다.

## 목차
- [이벤트 스토밍](#이벤트-스토밍-결과)
- [기본 시나리오](#기본-시나리오)
- [이벤트 추출](#이벤트-추출)
- [커맨드,액터,정책](#커맨드-액터-정책)
- [설치 방법](#설치-방법)

## 이벤트 스토밍 결과
<img width="1396" alt="image" src="https://github.com/user-attachments/assets/eb5cb9f3-3e63-48d3-9041-c5b4026c20ff" />

## 기본 시나리오
- 고객1이 회원서비스를 통해 회원 가입을 한다.
- 고객1이 상품서비스를 통해 상품을 등록한다.(상품 이름, 상품 가격, 재고 수량)
- 고객2가 회원서비스를 통해 회원 가입을 한다.
- 고객2가 상품서비스에서 상품을 조회하고, 주문 서비스를 통해 구매를 요청한다.
- 주문서비스가 제품서비스에 재고를 확인한다.
- 재고가 있다면 결제서비스가 결제를 처리한다.
- 결제가 성공하면 제품서비스가 재고량을 감소시킨다.
- 결제 성공상태에서 주문을 취소한다면 제품서비스가 재고량을 증가시킨다.
- 재고 부족 또는 결제 실패 시 사용자에게 에러 메시지를 반환한다.

## 이벤트 추출
### 이벤트 목록 및 흐름

| 이벤트 이름         | 발행 서비스      | 구독 서비스       | 설명                                                                 |
|---------------------|------------------|------------------|----------------------------------------------------------------------|
| `UserCreated`       | `UserService`    | `OrderService`   | 사용자가 등록될 때 발생. `OrderService`가 사용자 정보를 참조 가능.  |
| `OrderCreated`      | `OrderService`   | `PaymentService` | 주문이 생성되면 발생. `PaymentService`가 결제를 시작.               |
| `OrderCancelled`    | `OrderService`   | `ProductService` | 주문이 취소되면 발생. `ProductService`가 재고를 증가(`IncreaseStock`). |
| `OrderPaid`         | `OrderService`   | `ProductService` | 주문 결제가 완료되면 발생. `ProductService`가 재고를 감소(`DecreaseStock`). |
| `PaymentCompleted`  | `PaymentService` | `OrderService`   | 결제가 성공적으로 완료되면 발생. `OrderService`가 주문 상태를 업데이트(`ChangeOrderStatus`). |
| `PaymentCancelled`  | `PaymentService` | `OrderService`   | 결제가 취소되면 발생. `OrderService`가 주문 상태를 업데이트(`ChangeOrderStatus`). |
| `PaymentFailed`     | `PaymentService` | `OrderService`   | 결제가 실패하면 발생. `OrderService`가 주문 상태를 업데이트(`ChangeOrderStatus`). |

## 커맨드, 액터, 정책
### 커맨드 (Command)

| 커맨드 이름       | 서비스           | 설명                           |
|-------------------|------------------|-------------------------------|
| `RegisterUser`    | `UserService`    | 새로운 사용자를 등록.         |
| `CreateOrder`     | `OrderService`   | 새로운 주문을 생성.           |
| `CancelOrder`     | `OrderService`   | 기존 주문을 취소.             |
| `ChangeOrderStatus` | `OrderService` | 주문 상태를 변경(예: 결제 완료, 취소). |
| `ProcessPayment`  | `PaymentService` | 결제를 처리.                  |
| `CancelPayment`   | `PaymentService` | 결제를 취소.                  |
| `DecreaseStock`   | `ProductService` | 재고를 감소.                  |
| `IncreaseStock`   | `ProductService` | 재고를 증가.                  |

### 액터 (Actor)

| 액터 이름 | 서비스           | 설명                           |
|-----------|------------------|-------------------------------|
| `User`    | `UserService`    | 시스템에 사용자를 등록.       |
| `User`    | `OrderService`   | 주문을 생성하거나 취소.       |

### 정책 (Policy)

| 정책 이름         | 서비스           | 설명                           |
|-------------------|------------------|-------------------------------|
| `SaveUser`        | `OrderService`   | `UserCreated` 이벤트 발생 시 사용자 정보를 저장. |
| `DecreaseStock`   | `ProductService` | `OrderPaid` 이벤트 발생 시 재고 감소.      |
| `IncreaseStock`   | `ProductService` | `OrderCancelled` 이벤트 발생 시 재고 증가. |
| `ChangeOrderStatus` | `OrderService` | `PaymentCompleted`, `PaymentCancelled`, `PaymentFailed` 이벤트 발생 시 주문 상태 변경. |


## Aggregate
### 어그리게이트 (Aggregate)

| 어그리게이트 이름 | 서비스           | 속성                          | 설명                           |
|-------------------|------------------|-------------------------------|--------------------------------|
| `User`            | `UserService`    | `userId`, `username`          | 사용자 정보를 관리. 사용자 등록 시 생성. |
| `Order`           | `OrderService`   | `orderId`, `userId`, `orderPrice`, `orderStatus`, `failureMessages`, `items`, `trackingId` | 주문 정보를 관리. 주문 생성 및 상태 변경 시 사용. |
| `Payment`         | `PaymentService` | `paymentId`, `orderId`, `userId`, `price`, `paymentStatus`, `createdAt` | 결제 정보를 관리. 결제 처리 및 상태 관리 시 사용. |
| `Product`         | `ProductService` | `id`, `name`, `price`, `stock`, `createdAt`, `updatedAt` | 상품 정보를 관리. 재고 감소/증가 시 사용. |

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


