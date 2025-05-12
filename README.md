# Pillivery (지속적 개발 및 개선: 2022 - 현재) 
  
Pillivery는 건강기능식품을 온라인으로 주문하고 정기적으로 결제, 배송받을 수 있는 전문 플랫폼을 목표로 하는 서비스를 목표 함.  
  
> 실제 서비스가 아닌 팀 프로젝트의 일환으로 진행됐으며, 초기 팀 프로젝트 기간 종료 후 현재까지 개인적으로 개선을 진행 중.  
  
## 1. 기간 및 역할  
  
- 팀 프로젝트 기간 : 2022. 11 ~ 2022.12  
- 참여 인원: 7명(FE: 4명, BE: 3명)  
  - BE 3 명 중 서버 핵심 기능 로직 구현 (인증/인가, 결제 연동, 정기 결제).  

**개인 리팩토링 및 성능 개선(~ 현재)**  
- 실제 서비스 운영 환경을 고려하여 리팩토링을 진행.  
- 성능, 구조, 인프라 개선에 초점을 둠.  

  
## 2. 기술 스택  

### Back-end  

| 카테고리           | 기술 스택                                                                                    |
| -------------- | ---------------------------------------------------------------------------------------- |
| **언어 & 빌드 도구** | Java 11, Gradle 7.5.1                                                                    |
| **프레임워크**      | Spring Boot 2.7.5, Spring Data JPA/JDBC, Spring Security, Spring Batch, Spring Rest Docs |
| **인증/인가**      | JWT, OAuth 2.0                                                                           |
| **데이터베이스**     | MySQL 8.0                                                                                |
| **캐시**         | Redis (Redisson), Caffeine Cache                                                         |
| **인프라**        | NCP, Vultr, Docker, Jenkins, Nginx, Resilience4j                                         |
| **모니터링 & 로깅**  | Promtail, Loki, Prometheus, Grafana, cAdvisor                                            |
| **테스트**        | JUnit 5, Mockito, WireMock, RestAssured, FixtureMonkey                                   |
| **성능 테스트**     | Locust                                                                                   |
| **HTTP 클라이언트** | WebClient, RestTemplate                                                                  |
| **기타**         | Quartz, EmbeddedRedis                                                                    |
  
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
  
- JobKey/TriggerKey API로 조회·취소·변경 기능 구현  
- 중복 실행 방지 로직 포함  
  
⛔ 예외 발생 시 재시도 정책  
  
1. 1회차 에러: 즉시 재시도  
2. 2회차 에러: 3일간 24시간 간격 재시도  
3. 이후 에러: Job 취소 및 로그 기록  
  
![Quartz 시퀀스](https://github.com/choizz156/pillivery/blob/6db8979f27cc751349ffd8bf51600cb30a1c9398/image/%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%80%E1%85%B5%E1%84%80%E1%85%A7%E1%86%AF%E1%84%8C%E1%85%A6%20%E1%84%89%E1%85%B5%E1%84%8F%E1%85%AF%E1%86%AB%E1%84%89%E1%85%B3%202.jpg)  
  
</details>  
  
## 4. 개인 리팩토링 작업(~ 현재)  
  
### 📊 프로젝트 개선 사항  
//이미지 추가
  
<!-- 개선요약 이미지1 -->  
  
<!-- 개선요약 이미지2 -->  
  
<!-- 개선요약 이미지3 -->  
  
<!-- 개선요약 이미지4 -->  
  
### 🗄️ 모듈 구조  
  
- 모듈 종류  
  
```  
├── module-api : 사용자 API 로직   
├── module-batch : 정기 결제 batch 로직  
├── module-core : 도메인 및 비지니스 로직  
├── module-event : 이벤트 저장 및 발행 로직  
├── module-external-api :외부 API 통신 로직  
├── module-logging : 로깅 관련 공통 모듈  
├── module-redis : 분산 락, refresh token 로직  
```  
  
- 모듈 의존성  
  //이미지 추가
<!-- 의존성 이미지 -->  
  
### 💽 ERD  
  
#### 주요 엔티티  
  
- **users**: 사용자 계정 정보(계정, 개인정보, 연락처)와 장바구니 연결 관리  
- **item**: 상품 정보, 가격, 이미지, 카테고리 분류 및 상품 상세 정보  
- **cart/cart_item**: 사용자 장바구니 및 담긴 상품 관리, 가격 계산  
- **orders/order_item**: 주문 정보, 배송 정보, 주문 상품 목록 관리  
- **subscription_order**: 구독 주문 관리  
- **review**: 상품에 대한 사용자 평가, 별점, 리뷰 내용  
- **Category**: 상품 분류 체계 관리.  
- **api_event/fail_event** : 이벤트 등록 및 실패 이벤트 관리.  

#### 논리적 ERD
  
![논리적 erd](https://github.com/choizz156/pillivery/blob/fda4797842035845bf5d4dbc4aa32b9b5e7ae9e6/image/%E1%84%82%E1%85%A9%E1%86%AB%E1%84%85%E1%85%B5%E1%84%8C%E1%85%A5%E1%86%A8%20erd.png)
  
---  
  
### ⚙️ 인프라 아키텍처 개선  
  
> Client → EC2 → RDS의 단순 3-tier → 확장성과 운영 효율성을 고려한 아키텍처로 개선.  

//이미지 추가
  
#### (1)  Docker를 통한 배포로, 인프라 환경 일관성 확보.  
  
- **멀티 스테이지 빌드** : Gradle 빌드 환경에서 애플리케이션을 빌드한 후 경량화된 JRE 환경에서만 실행하여 컨테이너 이미지 크기 최적화.  
  <details>  
  <summary><strong>api.dockerfile</strong></summary>  
  
  ```  
    FROM gradle:jdk11 AS build  
  
    WORKDIR /app  
  
    COPY --chown=gradle:gradle build.gradle settings.gradle gradlew ./  
    COPY --chown=gradle:gradle gradle/ ./gradle/  
    COPY --chown=gradle:gradle deploy_script/ ./deploy_script/  
    COPY --chown=gradle:gradle . .  
  
    RUN ./gradlew clean :module-api:build  
  
  
    FROM openjdk:11.0.16-jre-slim-buster  
  
    WORKDIR /app  
  
    COPY --from=build /app/module-api/build/libs/module-api-boot.jar app.jar  
  
    ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]  
  
  ```  
  </details>  
  <details>  
  <summary><strong>batch.dockerfile</strong></summary>  
  
  ```  
    FROM gradle:jdk11 AS build  
  
    WORKDIR /app  
  
    COPY --chown=gradle:gradle build.gradle settings.gradle gradlew ./  
    COPY --chown=gradle:gradle gradle/ ./gradle/  
    COPY --chown=gradle:gradle deploy_script/ ./deploy_script/  
    COPY --chown=gradle:gradle . .  
  
    RUN ./gradlew clean :module-batch:build  
  
  
    FROM openjdk:11.0.16-jre-slim-buster  
  
    WORKDIR /app  
  
    COPY --from=build /app/module-api/build/libs/module-batch-boot.jar app.jar  
  
    ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=batch", "app.jar"]  
  
  ```  
  </details>  
  
#### (2) Promtail, Loki, Promethues, Grafana → 로깅 및 관제 시스템 도입.  
  
// 이미지 추가
  
#### (3) Jenkins, Docker, Container Registry → 무중단 CI/CD 구성(Rolling).  

- Jenkins에 business, batch 두 개의 파이프라인 설정.  
- Bastion 호스트를 통한 프라이빗 서버 배포.
- 빌드 시 테스트(CI), 배포 후 헬스 체크(CD).  
- Slack을 통한 배포 알람 설정.
- Jenkins PipeLine Stage 종류
  <details>
  <summary>Check out</summary>
  
  ```groovy
  stage('Checkout') {
      steps {
          checkout([
              $class: 'GitSCM',
              branches: [[name: 'main']],
              extensions: [[
                  $class: 'SubmoduleOption',
                  disableSubmodules: false,
                  parentCredentials: true,
                  recursiveSubmodules: true
              ]],
              userRemoteConfigs: [[
                  url: 'https://github.com/choizz156/pillivery.git',
                  credentialsId: 'github_token'
              ]]
          ])
      }
  }
  ```
  </details>
  
  <details>
  <summary>Git 정보 및 환경 설정</summary>
  
  ```groovy
  stage('Set Git Info & Environment') {
      steps {
          script {
              env.GIT_HASH = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
              echo "${env.GIT_HASH}"
              env.GIT_AUTHOR = sh(returnStdout: true, script: 'git log -1 --pretty=format:%an').trim()
              echo "${env.GIT_AUTHOR}"
              env.GIT_COMMIT_MSG = sh(returnStdout: true, script: 'git log -1 --pretty=format:%s').trim()
              echo "${env.GIT_COMMIT_MSG}"
              env.GIT_BRANCH = 'main'
              env.IMAGE_TAG = "${env.GIT_HASH}-${BUILD_NUMBER}"
              echo "${env.IMAGE_TAG}"
              env.DEPLOY_ENVIRONMENT = env.GIT_BRANCH == 'main' ? '프로덕션' : (env.GIT_BRANCH == 'develop' ? '개발' : "스테이징 (${env.GIT_BRANCH})")
              echo "${env.DEPLOY_ENVIRONMENT}"
          }
      }
  }
  ```
  </details>
  
  <details>
  <summary>Docker 이미지 빌드(CI)</summary>
  
  ```groovy
  stage('Build Docker Image') {
      steps {
          script {
              sh "docker build -t ${VULTR_REGISTRY_URL}:${env.IMAGE_TAG} -f server/api.dockerfile server/"
          }
      }
  }
  ```
  </details>
  
  <details>
  <summary>Docker 이미지 Container Registry에 푸시</summary>
  
  ```groovy
  stage('Push Docker Image') {
      steps {
          script {
              withCredentials([usernamePassword(credentialsId: "${VULTR_CREDENTIALS_ID}", passwordVariable: 'VULTR_PASSWORD', usernameVariable: 'VULTR_USERNAME')]) {
                  sh "docker login ${env.VULTR_REGISTRY} -u ${VULTR_USERNAME} -p \"${VULTR_PASSWORD}\""
                  sh "docker push ${env.VULTR_REGISTRY_URL}:${env.IMAGE_TAG}"
              }
          }
      }
  }
  ```
  </details>
  
  <details>
  <summary>배포 -> 서버 내에서 스크립트 사용, 헬스 체크(CD)</summary>
  
  ```groovy
  def deployViaBastion(serverIp, containerName, healthCheckUrl) {
      withCredentials([usernamePassword(credentialsId: "${VULTR_CREDENTIALS_ID}", passwordVariable: 'VULTR_PASSWORD', usernameVariable: 'VULTR_USERNAME')]) {
          sshagent(['deploy_ssh_key']) {
              // bastion 호스트에 먼저 접속
              sh """
                  # bastion 호스트에 배포 스크립트 복사
                  scp -o StrictHostKeyChecking=no ./server/deploy_script/docker_deploy.sh ./server/deploy_script/health_check.sh root@${params.BASTION_HOST}:/tmp/
                  
                  # bastion 호스트에서 프라이빗 서버로 접속하여 배포 진행
                  ssh -o StrictHostKeyChecking=no root@${params.BASTION_HOST} << EOF
                      # 원격 서버 Docker 로그인
                      ssh -o StrictHostKeyChecking=no root@${serverIp} "docker login ${env.VULTR_REGISTRY} -u ${VULTR_USERNAME} -p \\"${VULTR_PASSWORD}\\""
                      
                      # 배포 스크립트 복사 및 실행
                      scp -o StrictHostKeyChecking=no /tmp/docker_deploy.sh root@${serverIp}:/tmp/
                      ssh -o StrictHostKeyChecking=no root@${serverIp} "chmod +x /tmp/docker_deploy.sh && /tmp/docker_deploy.sh localhost ${containerName} ${env.VULTR_REGISTRY_URL} ${env.IMAGE_TAG}"
                      
                      # 헬스 체크 스크립트 복사 및 실행
                      scp -o StrictHostKeyChecking=no /tmp/health_check.sh root@${serverIp}:/tmp/
                      ssh -o StrictHostKeyChecking=no root@${serverIp} "chmod +x /tmp/health_check.sh && /tmp/health_check.sh localhost ${containerName} ${healthCheckUrl} 40 5"
  EOF
              """
          }
      }
  }
  ```
  </details>
  
  <details>
  <summary>Slack 알람 (빌드 성공 or 실패)</summary>
  
  ```groovy
  post {
      success {
          script {
              def durationMillis = System.currentTimeMillis() - env.START_TIME.toLong()
              def durationMinutes = durationMillis / 60000.0
              def formattedDuration = String.format("%.1f", durationMinutes)
              
              slackSend(
                  channel: "${params.SLACK_CHANNEL}",
                  tokenCredentialId: "${SLACK_CREDENTIALS_ID}",
                  color: "good",
                  message: """
  *🚀 배포 성공: ${env.JOB_NAME} [#${env.BUILD_NUMBER}]*
                  
  *환경:* ${env.DEPLOY_ENVIRONMENT}
  *소요 시간:* ${formattedDuration}분
  *브랜치:* ${env.GIT_BRANCH}
  *커밋:* `${env.GIT_HASH}`
  *작성자:* ${env.GIT_AUTHOR}
  *이미지:* `${VULTR_REGISTRY_URL}:${env.IMAGE_TAG}`
  *커밋 메시지:* ${env.GIT_COMMIT_MSG}
  
  <${env.BUILD_URL}|빌드 상세 보기>
  
  배포 완료: ${new Date().format('yyyy-MM-dd HH:mm:ss', TimeZone.getTimeZone('Asia/Seoul'))}
                  """
              )
          }
      }
      
      failure {
          script {
              def failedStage = env.STAGE_NAME ?: "알 수 없음"
              def logExcerpt = "로그 가져오기 실패"
              try {
                  logExcerpt = sh(script: "curl -s '${env.BUILD_URL}consoleText' | tail -n 10 || echo '로그 가져오기 실패'", returnStdout: true).trim()
              } catch (e) {}
              
              slackSend(
                  channel: "${params.SLACK_CHANNEL}",
                  tokenCredentialId: "${SLACK_CREDENTIALS_ID}",
                  color: "danger",
                  message: """
  *❌ 배포 실패: ${env.JOB_NAME} [#${env.BUILD_NUMBER}]*
                  
  *실패 단계:* ${failedStage}
  *브랜치:* ${env.GIT_BRANCH}
  
  <${env.BUILD_URL}console|빌드 로그 보기>
  
  실패 시간: ${new Date().format('yyyy-MM-dd HH:mm:ss', TimeZone.getTimeZone('Asia/Seoul'))}
                  """
              )
          }
      }
  }
  ```
  </details>
	
  

  
#### (4) Slack 연동 알림 시스템으로 장애 조기 감지(Error Log, CPU 대상).  
  
- Error log 1분 간 10개, Warn log 5분간 20개, CPU 50% 이상 시 알람 설정.  
  
//이미지 추가
  
#### ⚠️ 단일 장애 지점을 고려하여, Cloud와 Grafana를 이용한 Application Load Balancer, Redis, MySQL 모니터링.

  
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
  
> [정기 배송 구현에 scheduler 사용](https://velog.io/@choizz/%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EC%A0%95%EA%B8%B0-%EB%B0%B0%EC%86%A1-%EA%B5%AC%ED%98%84%EC%97%90-Scheduler-%EC%82%AC%EC%9A%A9)</br> [정기 배송 구현에 quartz 사용](https://velog.io/@choizz/%ED%8C%80-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EC%A0%95%EA%B8%B0-%EA%B2%B0%EC%A0%9C-%EA%B5%AC%ED%98%84-Quartz.-v2)</br>  
  
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



