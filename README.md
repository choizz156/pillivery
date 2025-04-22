 # Pillivery

Pillivery는 건강기능식품을 온라인으로 주문하고 정기적으로 결제, 배송받을 수 있는 전문 플랫폼을 목표로 하는 서비스.

> 실제 서비스가 아닌 팀 프로젝트의 일환으로 진행됐으며, 초기 팀 프로젝트 기간 종료 후 현재까지 개인적으로 개선을 진행 중.

## 1. 기간 및 역할

- 팀 프로젝트 기간 : 2022. 11 ~ 2022.12
- 참여 인원: 7명(FE: 4명, BE: 3명)
  - BE 3 명 중 서버 핵심 기능 로직 구현 (인증/인가, 결제 연동, 정기 결제).

- **개인 리팩토링 및 성능 개선(~ 현재)**
  - 실제 서비스 운영 환경을 고려하여 리팩토링을 진행.
  - 성능, 구조, 인프라 개선에 초점을 둠.

## 2. 기술 스택
### Back-end
| 카테고리             | 기술 스택                                                    |
| -------------------- | ------------------------------------------------------------ |
| **언어 & 빌드 도구** | Java 11, Gradle 7.5.1                                        |
| **프레임워크**       | Spring Boot 2.7.5, Spring Data JPA/JDBC, Spring Security, Spring Batch, Spring Rest Docs |
| **인증/인가**        | JWT, OAuth 2.0                                               |
| **데이터베이스**     | MySQL 8.0                                                    |
| **캐시**             | Redis (Redisson), Caffeine Cache                             |
| **인프라**           | NCP, Vultr, Docker, Jenkins, Nginx, Resilience4j             |
| **모니터링 & 로깅**  | Promtail, Loki, Prometheus, Grafana                          |
| **테스트**           | JUnit 5, Mockito, WireMock, RestAssured, FixtureMonkey       |
| **성능 테스트**      | Locust                                                       |
| **HTTP 클라이언트**  | WebClient, RestTemplate                                      |
| **기타**             | Quartz, EmbeddedRedis                                        |

---
## 3. 팀 프로젝트 기여(2022. 11 ~ 2022.12)



<details>
<summary><strong>1) 유저 도메인 Restful API 개발</strong></summary>

- User 회원가입, 정보 수정 등 API 개발  
- REST API 디자인 가이드:  
  - Resources 설계  
  - HTTP Methods 활용  
  - 적절한 Status Code 반환

</details>


<details>
<summary><strong>2) 인증/인가 시스템 구축 (Spring Security, JWT, OAuth2.0)</strong></summary>

### (1) 로그인 & 토큰 발급

- 로그인 요청 시 Access Token 발급  
- 인증 실패 시 예외 처리  

![Security Flow](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/secuirty%20diagram.jpeg)

### (2) OAuth 로그인

1. OAuth 로그인 시 추가 정보(주소, 전화번호) 입력 화면 이동  
2. 추가 정보 입력 완료 → Access Token 발급  
3. 리소스 서버 정보 애플리케이션 DB에 저장  
4. 저장 실패 시 예외 처리

![OAuth2 Flow](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/oauth2-sequence.jpg)

![추가정보 입력 흐름](https://github.com/choizz156/pillivery/blob/0fb84ed151e7ac9097764497d12ec676d4d81117/image/%E1%84%8E%E1%85%AE%E1%84%80%E1%85%A1%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%87%E1%85%A9%20diagram.jpg)

### (3) Refresh Token 관리

<!-- Refresh Token 시퀀스 다이어그램 추가 예정 -->

</details>



<details>
<summary><strong>3) 외부 결제 API 연동 (카카오페이)</strong></summary>

- **파사드 패턴**:  
  - 파사드 클래스에서 단건 결제 요청과 정기 결제 요청, 결제 승인을 서비스 계층에 위임.
  - 파사드 객체에서 단건 결제인지, 정기 결제인지를 구분하는 역할.
- **전략 패턴**:  
  - 결제 방식 변경 시 클라이언트 코드 최소 수정  

![결제 클래스 다이어그램](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EA%B2%B0%EC%A0%9C%ED%81%AC%EB%A6%AC%EB%8D%94%EA%B8%B0%EB%8A%A5.jpg)

- RestTemplate 동기 호출  
  - Connection Pool, 타임아웃 설정 권장  
- 결제 실패 시 카카오페이 → 지정 URL로 리다이렉트  
- 리다이렉트 후 에러 정보 클라이언트 전달

</details>



<details>
<summary><strong>4) 정기 결제 시스템 구축 (Quartz)</strong></summary>

- Quartz로 스케줄링 모듈 분리 (멀티 모듈)  
- JobKey/TriggerKey API로 조회·취소·변경 기능 구현  
- 중복 실행 방지 로직 포함  

⛔ 예외 발생 시 재시도 정책  
1. 1회차 에러: 즉시 재시도  
2. 2회차 에러: 3일간 24시간 간격 재시도  
3. 이후 에러: Job 취소 및 로그 기록  

![Quartz 시퀀스](https://github.com/choizz156/pillivery/blob/6db8979f27cc751349ffd8bf51600cb30a1c9398/image/%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%80%E1%85%B5%E1%84%80%E1%85%A7%E1%86%AF%E1%84%8C%E1%85%A6%20%E1%84%89%E1%85%B5%E1%84%8F%E1%85%AF%E1%86%AB%E1%84%89%E1%85%B3%202.jpg)

</details>


## 3. Git Flow

- 서로가 맡은 기능을 완료한 뒤, dev 브랜치에 merge를 하고 최종적으로 main 브랜치에 merge하는 전략을 사용.
- 잦은 시행착오를 겪으며 git에 대해 학습.   


## 4. ERD

![erd 수정](https://user-images.githubusercontent.com/106965005/228384360-5a59318c-74c4-4449-9717-f097a6903ee3.png)

---
## 5.프로젝트 모듈 구조
  - 
  - 단일 프로젝트 안에서 api, core, quartz의 모듈 분리.
    - 중복될 수 있는 코드를 방지

############ 이미지 삽입

---

## 6. 내가 만든 기능

  - 


---
#### 3) 외부 결제 API 연동(카카오 페이) 📌[core 모듈](https://github.com/choizz156/pilivery/tree/main/server/module-core/src/main/java/com/team33/modulecore/domain/payment) 📌[api 모듈](https://github.com/choizz156/pillivery/blob/main/server/module-api/src/main/java/com/team33/moduleapi/controller/payment/PayController.java)
  - `파사드 패턴`을 활용하여 파사드 클래스에서 단건 결제 요청과 정기 결제 요청, 결제 승인을 서비스 계층에 위임.
     - 파사드 객체에서 단건 결제인지, 정기 결제인지를 구분하는 역할.   
  - 결제 요청과 결제 승인에 `전략 패턴`을 활용하여 변경이 생겼을 경우 클라이언트 코드의 변경을 최소화.

  ![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EA%B2%B0%EC%A0%9C%ED%81%B4%EB%9E%98%EC%8A%A4%20%EB%8B%A4%EC%96%B4%EA%B7%B8%EB%9E%A8.jpg)

  - RestTemplate을 이용해 외부 API와 통신했습니다.
    - 동기 방식을 사용하므로 요청이 많아질 시 응답 지연을 고려.
    - Connection Pool을 설정하고, 연결 시간 타임아웃과 응답 시간 타임아웃 설정해 유저들에게 결과를 빠르게 피드백하도록 구현.

 #결제 요청 및 결제 승인 시퀀스 다이어그램

![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EA%B2%B0%EC%A0%9C%20%EC%8B%9C%ED%80%80%EC%8A%A4.jpg)
  ⛔️ 결제 요청 및 승인이 실패할 경우, 카카오페이 서버에서 제가 결제 요청 입력한 지정한 URL로 리다이렉트.</br>
  ⛔️ 리다이렉트 후 에러 정보를 클라이언트에게 보냄.


---

#### 4) 정기 구독(결제) 기능 구현 📌[quartz 모듈](https://github.com/choizz156/pilivery/tree/main/server/module-quartz/src/main/java/com/team33/modulequartz/subscription)📌[api 모듈](https://github.com/choizz156/pillivery/blob/main/server/module-api/src/main/java/com/team33/moduleapi/controller/Scheduler/ScheduleController.java)
- 정기 구독 시 **Quartz** 라이브러리를 이용하여 특정 날짜에 결제가 이루어지도록 결제 API와 연동.
    - **멀티 모듈**을 활용하여 스케쥴링 시스템을 독립적인 모듈로 구성.
    - Jobkey API와 TriggerKey API를 활용하여 특정 job과 trigger를 조회, 취소, 변경기능 구현.
    - 스케쥴러에서 설정한 스케쥴에 실행되지 않을 시 중복 실행을 방지.
      

⛔️ 만약, job 수행 시 예외가 발생할 경우,
  - 첫 번째 에러 : 경우 바로 job 재시도.
  - 두 번째 에러 : 3일 동안 24시간 간격으로 job을 재시도.
  - 그 후에도 예외가 발생한다면 job을 취소하고 로그로 기록.
    

![](https://github.com/choizz156/pillivery/blob/6db8979f27cc751349ffd8bf51600cb30a1c9398/image/%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%80%E1%85%B5%E1%84%80%E1%85%A7%E1%86%AF%E1%84%8C%E1%85%A6%20%E1%84%89%E1%85%B5%E1%84%8F%E1%85%AF%E1%86%AB%E1%84%89%E1%85%B3%202.jpg)

---
#### 5) Exception 핸들링과 공통 Exception Response 구현 📌[core 모듈](https://github.com/choizz156/pilivery/tree/main/server/module-core/src/main/java/com/team33/modulecore/global/exception)
- **정적 팩토리 메서드**를 통해 에러 응답 객체 생성 후 예외를 처리.
  - 각 예외마다 객체 생성에 필요한 파라미터가 다르기 때문에, 정적 팩토리 메서드를 통한 공통 에러 응답 객체 기반으로 예외 처리. 
```java
{
  "status": 400,
  "customFieldErrors": [
                          {
                              "field": "email",
                              "rejectedValue": "1234567!",
                              "reason": "비밀번호는 숫자+영문자+특수문자 조합으로 8자리 이상이어야 합니다."
                          }
                      ]
}
```
---
#### 6) 단위 테스트(RestAssured) 및 통합 테스트 작성(Junit5) 📌[api 모듈](https://github.com/choizz156/pillivery/tree/main/server/module-api/src/test/java/com/team33/moduleapi/controller)

- 프로젝트 개발 후 테스트 코드의 필요성을 인지하여 통합 테스트를 약 70개(Rest Docs를 위한 테스트 포함) 작성(일부 단위테스트 포함).
    - Junit5를 사용해 단위 테스트를 진행.
    - mokito를 사용해 외부 API에 독립적인 테스트 환경을 마련.
    - @SpringBootTest와 사용하기에 RestAssured가 가독성이 좋다고 생각해 인수 테스트에 RestAssured를 사용.


![image](https://github.com/choizz156/pillivery/assets/106965005/a6e6c7c6-ee01-4e39-af68-9b894f6e39cd)


---

#### 7) Spring Rest Docs를 활용한 API 문서 작성 📌[api 모듈](https://github.com/choizz156/pillivery/tree/main/server/module-api/src/test/java/com/team33/moduleapi/docs)
- 테스트 코드 작성 후 Spring Rest Docs를 이용한 API 문서를 작성.
    - 테스트 코드를 작성해야 문서가 작성되기 때문에 코드의 신뢰성을 보장.
    - swagger와 다르게 프로덕션 코드에 문서 작성을 위한 코드가 침투하지 않음.
  ![image](https://github.com/choizz156/pillivery/assets/106965005/1d8cf440-66db-4577-a79a-edd49b52d09f)

---
### 📌 트러블 슈팅 및 개선

<details>
<summary>1. @Schduled를 문제를 해결한 Quartz</summary>
<div markdown="1">

#### (1) **트러블 및 트러블의 원인**

- Spring의 @Scheduled을 이용하여 스케쥴링을 시도했지만, 몇 가지 문제가 있었습니다.

#### a. 구독 주기 변경 문제

- 유저가 구독 주기 변경 시, 첫 정기 결제일을 기준으로 주기를 변경해야 했습니다.
- @Scheduled를 사용하여 런타임 환경에서 구독 주기를 변경하려면, 기존 스케쥴을 null로 변경 후 변경 시점을 기준으로 새로운 스케쥴을 다시 할당해야 했습니다.
- 이렇게 되면, 첫 정기 결제일을 기준으로 구독 주기 변경이 불가능했습니다.

#### b. 특정 스케쥴러 조회 문제

- 만약 유저가 본인의 정기 구독 주기를 변경하거나 구독을 취소한다면, 애플리케이션에서 그 유저에 할당된 스케쥴러를 조회 후 처리해야합니다.
- @Scheduled 사용 시 특정 스케쥴러를 조회하는 방법이 없었습니다.

#### (2) **해결 방법**
- Spring Batch를 학습하기엔 주어진 시간에 비해 학습 비용이 크다고 생각하여 Quartz를 선택했습니다.
- `Quartz`의 Trigger API 사용함으로써 런타임 환경에서 첫 정기 구독일을 기준으로 구독 주기를 변경시킬 수 있었습니다.
- `Quartz` JobKey API를 사용함으로써 특정 스케쥴러 조회가 가능했습니다.
>  [정기 배송 구현에 scheduler 사용](https://velog.io/@choizz/%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EC%A0%95%EA%B8%B0-%EB%B0%B0%EC%86%A1-%EA%B5%AC%ED%98%84%EC%97%90-Scheduler-%EC%82%AC%EC%9A%A9)</br> [정기 배송 구현에 quartz 사용](https://velog.io/@choizz/%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EC%A0%95%EA%B8%B0-%EA%B2%B0%EC%A0%9C-%EA%B5%AC%ED%98%84-Quartz.-v2)</br>

</div>
</details>

<details>
<summary>2. Jpa에서 동일한 엔티티 참조 에러</summary>
<div markdown="1">

#### (1) **문제 상황**

- Quartz를 사용하여 정기 결제 Job을 구현할 때, 첫 번째 정기 결제 때 사용된 order 객체의 정보들을 그대로 복사해서 다음 정기 결제 때 사용해야 했습니다.
- 처음에 첫 결제 때 사용한 order 엔티티를 가지고 와서 그대로 사용하려 했지만 에러가 발생했습니다.
    - `(org.hibernate.HibernateException: Found shared references to a collection)`

#### (2) **문제의 원인**

- `swallow copy`를 함으로써 원본 엔티티와 복사한 엔티티가 **Heap에서 동일한 주솟값**을 참조했습니다.
- 하지만, 하이버네이트에서 이미 영속화된 엔티티와 동일한 주솟값을 가지는 엔티티를 또 다시 영속화할 수 없었습니다.

#### (3) **해결 방법**

- order 엔티티에 deep copy를 위한 생성자를 추가하여 `deep copy` 했습니다.

#### (4) **알게된 점**

- Java에서 copy에 관한 개념에 대해 학습했습니다.
- JPA에서 동일한 엔티티는 영속화 할 수 없다는 것을 알게 됐습니다.

> [deep copy와 swallow copy](https://velog.io/@choizz/Java에서-deep-copy와-swallow-copy#swallow-copy얕은-복사)</br>

</div>
</details>

---

## 7. 회고
### 👉 기술 회고
[꼭 JWT를 써야 했을까?](https://velog.io/@choizz/%ED%9A%8C%EA%B3%A0-JWT%EB%A5%BC-%EA%BC%AD-%EC%8D%A8%EC%95%BC%EB%90%90%EC%9D%84%EA%B9%8C)</br>
[무엇인가 잘못된 유저 객체 가지고 오기](https://velog.io/@choizz/%ED%9A%8C%EA%B3%A0-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80-%EC%9E%98%EB%AA%BB%EB%90%9C-%EA%B2%83-%EA%B0%99%EC%9D%80-User-%EA%B0%9D%EC%B2%B4-%EA%B0%80%EC%A0%B8%EC%98%A4%EA%B8%B0)</br>