## 1. 제작 기간 및 참여 인원
- 제작 기간: 2022.11.09 ~ 2022.12.05
- 참여 인원: 7명(FE: 4명, BE: 3명)
#### 🖥 Front-end
|도현수 (팀장)|방기현 (서기)|김세연|안지환|
|:-:|:-:|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/105625895?v=4" width=150>|<img src="https://avatars.githubusercontent.com/u/102677317?s=400&u=d1fc15bf19c4d0fb775e7b0f58ce83bd91fbe72c&v=4" width=150>|<img src="https://avatars.githubusercontent.com/u/107875909?v=4" width=150>|<img src="https://cdn.discordapp.com/attachments/1035955628742553732/1049618694122262538/1.jpeg" width=150>|
|[@dohyeons](https://github.com/dohyeons)|[@kihyeoon](https://github.com/kihyeoon)|[@uxolrv](https://github.com/uxolrv)|[@jihwanAn](https://github.com/jihwanAn)|


#### ⚙️ Back-end
|김지효 (BE 팀장)|김지수|최민석|
|:-:|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/107678471?v=4" width=150>|<img src="https://avatars.githubusercontent.com/u/94853413?v=4" width=150>|<img src="https://avatars.githubusercontent.com/u/106965005?v=4" width=150>|
|[@zirryo](https://github.com/zirryo)|[@jisoo27](https://github.com/jisoo27)|[@choizz156](https://github.com/choizz156)|

---

## 2. 기술 스택
### Front-end
  - JavaScript, React Create React App, Styled components, React query, axios, Node.js, npm, Redux toolkit

### Back-end

|Back-end|
|:------:|
|`Java 11`|
|`Spring Sercurity`|
|`Spring boot 2.7.5`|
|`Spring JPA`|
|`Gradle`|
|`MySQL 8`|
|`JWT 0.11.5`|
|`OAuth 2.0`|
|`Quartz 2.3.2`|

---

## 3. ERD

![erd 수정](https://user-images.githubusercontent.com/106965005/228384360-5a59318c-74c4-4449-9717-f097a6903ee3.png)

---
## 4.프로젝트 모듈 구조
- 멀티 모듈
  - api, core, quartz의 모듈로 나누어 중복 코드를 방지하고 코드의 관리를 용이하게 했습니다. 

![](https://github.com/choizz156/pillivery/blob/904ee15bce3430ef9ef0a4fab8e65b448748f9e2/image/%E1%84%86%E1%85%A9%E1%84%83%E1%85%B2%E1%86%AF%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%8B%E1%85%A5%E1%84%80%E1%85%B3%E1%84%85%E1%85%A2%E1%86%B7.jpg)

---

## 5. 내가 만든 기능
#### 1) User 도메인 CRUD 📌[디렉토리 이동](https://github.com/choizz156/pilivery/tree/main/server/module-core/src/main/java/com/team33/modulecore/domain/user)
- 회원가입, 정보 수정, 회원 탈퇴, 회원 정보 조회와 같은 User 도메인 api를 개발했습니다.
- Rest ApI 디자인 가이드 중  Resources, Http Methods, Status Code를 지키며 개발했습니다.
  - 회원 정보 => `GET`
  - 회원 가입 => `POST`
  - 회원 정보 수정 => `PATCH`
  - 회원 탈퇴 => `DELETE`
---
#### 2) Sping Security를 활용한 인증/인가 구현(JWT, OAuth 2.0) 📌[디렉토리 이동](https://github.com/choizz156/pilivery/tree/main/server/module-core/src/main/java/com/team33/modulecore/global/security)
- 회원가입 후 로그인하면 바로 토큰을 발급합니다.
![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/secuirty%20diagram.jpeg)
- OAuth 로그인 시 추가 정보(주소, 전화 번호) 기입 창으로 이동하고, 추가 정보 기입이 완료되면 토큰이 발급됩니다. 

  - 리소스 서버에서 받은 리소스와 따로 추가한 정보는 애플리케이션 서버의 데이터베이스에서 저장합니다.
![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/oauth2-sequence.jpg)
  - 추가 정보 기입을 완료한 후에는 OAuth 로그인 시 바로 토큰이 발급됩니다.
  - 추가 정보 기입 후 OAuth 로그인은 바로 토큰이 발급됩니다.
![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EC%B6%94%EA%B0%80%EC%A0%95%EB%B3%B4%20diagram.jpg)
---  
#### 3) 외부 결제 API 연동(카카오 페이) 📌[디렉토리 이동](https://github.com/choizz156/pilivery/tree/main/server/module-core/src/main/java/com/team33/modulecore/domain/payment)
  - 결제 요청이 들어올 때, `파사드 패턴`을 활용하여 파사드 클래스에서 단건 결제 요청과 정기 결제 요청을 구분하여 결제 요청 서비스에 위임합니다.
  - 결제 요청과 결제 승인에 `전략 패턴`을 활용했습니다.

![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EA%B2%B0%EC%A0%9C%ED%81%B4%EB%9E%98%EC%8A%A4%20%EB%8B%A4%EC%96%B4%EA%B7%B8%EB%9E%A8.jpg)

- 결제 요청 및 결제 승인 시퀀스 다이어그램

![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EA%B2%B0%EC%A0%9C%20%EC%8B%9C%ED%80%80%EC%8A%A4.jpg)

---
  
#### 4) 정기 구독(결제) 기능 구현 📌[디렉토리 이동](https://github.com/choizz156/pilivery/tree/main/server/module-quartz/src/main/java/com/team33/modulequartz/subscription)
- 정기 구독 시 Quartz 라이브러리를 이용하여 특정 날짜에 결제가 이루어지도록 결제 API와 연동합니다..
    - **멀티 모듈**을 구성하여 API 모듈과 스케쥴링 모듈을 분리했습니다.
    - 정기 구독 구현 시 **Quartz 라이브러리**를 사용하여 특정 스케쥴러 조회를 가능하게 했습니다.
    - 특정 날짜에 결제가 일어나도록 Trigger와 Job을 설정했습니다.
    - 스케쥴러에서 설정한 스케쥴에 실행되지 않을 시 중복 실행을 방지했습니다.
      
![](https://github.com/choizz156/pillivery/blob/5484b755fba956a825bdcba2867269f198e035d2/image/%EC%A0%95%EA%B8%B0%EA%B2%B0%EC%A0%9C%20%EC%8B%9C%ED%80%80%EC%8A%A4.jpg)

---
#### 5) Exception 핸들링과 공통 Exception Response 구현 📌[디렉토리 이동](https://github.com/choizz156/pilivery/tree/main/server/module-core/src/main/java/com/team33/modulecore/global/exception)
- `@RestControllerAdivce`를 이용하여 Exception을 핸들링하고, 공통적인 예외 Response 객체를 만들어 응답을 보냈습니다.


## 6. 회고
### 👉 기술 회고
[꼭 JWT를 써야 했을까?](https://velog.io/@choizz/%ED%9A%8C%EA%B3%A0-JWT%EB%A5%BC-%EA%BC%AD-%EC%8D%A8%EC%95%BC%EB%90%90%EC%9D%84%EA%B9%8C)</br>
[무엇인가 잘못된 유저 객체 가지고 오기](https://velog.io/@choizz/%ED%9A%8C%EA%B3%A0-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80-%EC%9E%98%EB%AA%BB%EB%90%9C-%EA%B2%83-%EA%B0%99%EC%9D%80-User-%EA%B0%9D%EC%B2%B4-%EA%B0%80%EC%A0%B8%EC%98%A4%EA%B8%B0)</br>
[정기 배송 구현에 scheduler 사용](https://velog.io/@choizz/%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EC%A0%95%EA%B8%B0-%EB%B0%B0%EC%86%A1-%EA%B5%AC%ED%98%84%EC%97%90-Scheduler-%EC%82%AC%EC%9A%A9)</br>
[정기 배송 구현에 quartz 사용](https://velog.io/@choizz/%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EC%A0%95%EA%B8%B0-%EA%B2%B0%EC%A0%9C-%EA%B5%AC%ED%98%84-Quartz.-v2)</br>
[deep copy와 swallow copy](https://velog.io/@choizz/Java에서-deep-copy와-swallow-copy#swallow-copy얕은-복사)</br>

---

### 커뮤니케이션
- 저는 클라이언트와 서버 둘 다와 관련 있는 JWT나 OAuth 같은 용어들을 당연히 프론트엔드 팀원들도 알고 있을 거로 생각하고 사용했습니다. 그런데, 프론트엔드 팀원들은 이런 용어들을 알지 못했습니다.
- 제가 상대방도 당연히 알 것으로 생각했던 것들을 상대방을 모를 수도 있다는 것을 깨달았습니다. 그래서 상대방과 커뮤니케이션할 때는 최대한 상대방이 이해하기 쉬운 용어와 표현을 사용하는 것이 의사소통에 중요하다는 것을 알게 됐습니다.
---
### 의사 결정
- 프로젝트 회의 당시, 앞단에서 유저와 상호작용하는 프론트엔드 팀원들이 기능에 대한 아이디어를 많이 냈습니다.
- 개발 중, 제가 속한 백엔드 팀원들은 초기에 개발하기로 정했던 기능들을 주어진 기간 내에 완성하지 못할 것이라는 판단을 했습니다.
- 그래서 팀원 전체가 다 같이 모여 의견 교환을 통해, 기능에 대한 중요도를 정하고, 가장 중요도가 높다고 생각하는 기능(조회, 정렬, 정기 구독, 결제 등) 위주로 개발을 하기로 했습니다.
- 이러한 의사 결정 과정을 거치면서, 현재 우리 팀의 리소스를 판단하여, 중요도가 높은 것들 위주로 기능을 개발한 것이 기간 내에 프로젝트를 완성하는 데 큰 역할을 한 것 같습니다.
- 또한, 팀원들과의 커뮤니케이션의 중요성을 느낄 수 있었습니다. 
---
### 아쉬웠던 점
- 프로젝트 중 테스트 코드를 작성하지 못 했던 점이 아쉽습니다. 테스트 코드가 없이 프로덕션 코드를 짜다보니 어떤 수정 사항이 있을 때마다 postman을 가지고 일일이 요청을 보내고 응답을 확인해야 하는 과정이 매우 번거로웠습니다. 그래서 현재까지 테스트 코드를 공부하며 테스트 코드를 작성하고 리팩토링을 하고 있습니다.</br>
- JPA와 데이터베이스에 대한 학습이 부족하다는 것을 느꼈습니다. 그래서 sql, RDS, JPA를 학습하고 있습니다.

