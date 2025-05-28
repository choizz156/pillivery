# Pillivery (지속적 개발 및 개선: 2022 - 현재)

> 실제 서비스가 아닌 팀 프로젝트의 일환으로 진행.(현재 배포 상태 x)

Pillivery는 건강기능식품 온라인 주문 및 정기 결제/배송 플랫폼으로, 초기 팀 프로젝트 이후 개인적으로 실제 서비스 수준의 아키텍처와 성능을 목표로 지속 개선 중인 프로젝트.

## 1. 개발 과정

### 초기 개발(2022. 11 ~ 2022.12  )

- 팀 프로젝트 참여 인원: 7명(FE: 4명, BE: 3명)
- **주요 기여:** BE 3명 중 핵심 서버 기능 로직 구현 담당
    - 인증/인가 시스템 설계 및 구현 (JWT, OAuth2.0)
    - 외부 결제 API 연동 (카카오페이)
    - 정기 결제 스케줄링 시스템 구축 (Quartz)

### 👨‍💻 개인적인 개선 및 확장 (2023.01 - 현재)

- 실제 서비스 운영 환경을 고려하여 리팩토링을 진행.
- 성능, 구조, 인프라 개선에 초점을 둠.

## 2. 기술 스택

### Back-end

| 카테고리           | 기술 스택                                                                                    |
|----------------|------------------------------------------------------------------------------------------|
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

![](https://github.com/choizz156/pillivery/blob/bc5b6506863ed51915aac34ade83ac3b5c113597/image/refresh%20token%20diagram.png)

</details>  

<details>  
<summary><strong>3) 외부 결제 API 연동 (카카오페이)</strong></summary>  

- **파사드 패턴**:
    - 파사드 클래스에서 단건 결제 요청과 정기 결제 요청, 결제 승인을 서비스 계층에 위임.
    - 파사드 객체에서 단건 결제인지, 정기 결제인지를 구분하는 역할.
- **전략 패턴**:
    - 결제 방식 변경 시 클라이언트 코드 최소 수정

![결제 클래스 다이어그램](https://github.com/choizz156/pillivery/blob/6becdab1dc8817e7e4425f42be778e85b6c1a92e/image/%EA%B2%B0%EC%A0%9C%ED%81%B4%EB%9E%98%EC%8A%A4%20%EB%8B%A4%EC%96%B4%EA%B7%B8%EB%9E%A8.jpg)

- RestTemplate 동기 호출
    - Connection Pool, 타임아웃 설정
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

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%80%E1%85%B5%E1%84%89%E1%85%AE%E1%86%AF%E1%84%89%E1%85%B3%E1%84%90%E1%85%A2%E1%86%A8%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%89%E1%85%A5%E1%86%BC%E1%84%82%E1%85%B3%E1%86%BC%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%89%E1%85%A5%E1%86%AF%E1%84%80%E1%85%A8%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%8B%E1%85%A1%E1%86%AB%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%89%E1%85%A5%E1%86%BC%20%26%20%E1%84%91%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%91%E1%85%B3%E1%84%85%E1%85%A1%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

---

### 🗄️ 모듈 구조(싱글 -> 멀티)

- 관심사 분리를 통한 코드의 유지, 보수 향상 및 확장성 고려.
- 모듈 간 결합도 최소화 및 단방향 의존성.

#### 모듈 종류

```  
├── module-api : 사용자 API 로직   
├── module-batch : 정기 결제 batch 로직  
├── module-core : 도메인 및 비지니스 로직  
├── module-event : 이벤트 저장 및 발행 로직  
├── module-external-api :외부 API 통신 로직  
├── module-logging : 로깅 관련 공통 모듈  
├── module-redis : 분산 락, refresh token 로직  
```  

#### 모듈 의존성(단방향)

  <img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%86%E1%85%A9%E1%84%83%E1%85%B2%E1%86%AF%E1%84%8B%E1%85%B4%E1%84%8C%E1%85%A9%E1%86%AB%E1%84%83%E1%85%A9%20333.png?raw=true" width="70%">

---

### 💽 ERD

#### 주요 엔티티

- **users**: 사용자 계정 정보(계정, 개인정보, 연락처)와 장바구니 연결 관리.
- **item**: 상품 정보, 가격, 이미지, 카테고리 분류 및 상품 상세 정보.
- **item_category**: 아이템이 가진 카테고리 종류.
- **cart/cart_item**: 사용자 장바구니 및 담긴 상품 관리, 가격 계산.
- **orders/order_item**: 주문 정보, 배송 정보, 주문 상품 목록 관리.
- **subscription_order**: 구독 주문 관리.
- **review**: 상품에 대한 사용자 평가, 별점, 리뷰 내용.
- **category**: 아이템 성분 분류.
- **api_event/fail_event** : 이벤트 등록 및 실패 이벤트 관리.
- 그 외 Batch, Quartz 관련 스키마.

> 도메인 특성 상 카테고리 변경 가능성이 매우 적다는 판단 하에 AttributeConverter를 사용하여 한 컬럼에 다중 카테고리 속성 저장.
>- category 테이블과 연관관계 제거 -> category 테이블과 join 하지 않음.
>- @ElementCollection을 이용한 item_category 생성.
   >
   >   <img src="https://github.com/choizz156/pillivery/blob/2b1b02b0a65209c081186284c4d7a4c59d979679/image/%E1%84%8F%E1%85%A1%E1%84%90%E1%85%A6%E1%84%80%E1%85%A9%E1%84%85%E1%85%B5%20%E1%84%8B%E1%85%A7%E1%84%85%E1%85%A5%E1%84%80%E1%85%A2%20%E1%84%90%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%87%E1%85%B3%E1%86%AF.png?raw=true" width="20%">

#### 논리적 ERD

![논리적 erd](https://github.com/choizz156/pillivery/blob/fda4797842035845bf5d4dbc4aa32b9b5e7ae9e6/image/%E1%84%82%E1%85%A9%E1%86%AB%E1%84%85%E1%85%B5%E1%84%8C%E1%85%A5%E1%86%A8%20erd.png)
  
---  

## ⚙️ 인프라 아키텍처 개선

> Client → EC2 → RDS의 단순 3-tier → 확장성과 운영 효율성을 고려한 아키텍처로 개선.

#### ⚠️ 단일 장애 지점을 고려하여, Cloud 서비스와 Grafana를 이용한 Application Load Balancer, MySQL 모니터링.

![아케택쳐](https://github.com/choizz156/pillivery/blob/5d60e935f2e10eccda9f9f00ec5c590df81b1f1d/image/%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A7%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%8D%E1%85%A1%20%E1%84%8E%E1%85%AC%E1%84%8C%E1%85%A9%E1%86%BC.png)

### 1 아키텍처 개선

#### 1.1 Bastion Host 사용

- 서비스 정상 트래픽과 관리자용 트래픽을 분리하여 보안성 강화.
- 터미널 접근을 위한 키 관리, 작업 감사로그 수집 및 보안 구성.
- 악성 루트킷·랜섬웨어 감염 시에도 Bastion만 재구성하면 되므로, 서비스 영향 최소화.

#### 1.2 로드밸런서(ALB) 적용

- 로드밸런스 서브넷만 포트 개방(443/80) → Nginx/WAS는 Private Subnet에 격리.
- 현재 가장 적은 수의 연결(요청)을 처리 중인 서버에 트래픽을 전달.
- SSL/TLS Offloding으로 암호화 오버헤드 제거.
- 헬스 체크로 Nginx 장애 시 트래픽 전달 중단하여 장애 전파 방지.

#### 1.3 Nginx 적용

- 장바구니 경로에 Sticky Session 적용.
  <details>  
  <summary>carts 경로 sticky session 설정</summary>  

      ```bash
      # ... 생략

       upstream app_sticky {
        	server <app 서버 1 ip>:8080;
        	server <app 서버 2 ip>:8080;
        	sticky name=srv_id expires=1h domain=pillivery path=/api/carts;
        	keepalive 10;
    	}
      
      server {
        listen 80;
        
        location ^~ /api/carts {
            proxy_pass http://app_sticky;
            proxy_http_version 1.1;

            proxy_set_header X-RequestID          $request_id;
            proxy_set_header Host                 $host;
            proxy_set_header X-Real-IP            $remote_addr;
            proxy_set_header X-Forwarded-For      $proxy_add_x_forwarded_for;
        }
      }
      
      #... 생략
      ```
  </details>

- 정기 결제 승인 경로 ip 제한
  <details>  
  <summary>정기 결제 승인 경로 ip 제한 설정</summary>  

      ```bash
      # ... 생략

      location ^~ /api/payments/apporve/subscription/ {
        allow <batch 서버 ip>;
       
        deny all;

        proxy_pass         http://app;           # upstream 또는 백엔드 주소
        proxy_http_version 1.1;

        proxy_set_header   X-RequestID        $request_id;
        proxy_set_header   Host               $host;
        proxy_set_header   X-Real-IP          $remote_addr;
        proxy_set_header   X-Forwarded-For    $proxy_add_x_forwarded_for;
      }

      #... 생략   
      
      
      ```
  </details>

#### 1.4 Lamda, NCP API를 통한 Batch Server 실행

- 애플리케이션 서버와 배치 서버를 격리하여 서로 장애 발생 시 전파되지 않도록 함.
- Lamda, EventBridge를 활용한 cron 스케줄링(매일 새벽 2시)
   <details>  
    <summary>Lamda ncp vm on script</summary>  

  ```javascript

        const NCP_ACCESS_KEY = "<access key>";
        const NCP_SECRET_KEY = "<secrety key>";
        const SERVER_INSTANCE_NO = "104679588";
        const NCP_API_ENDPOINT = "https://ncloud.apigw.ntruss.com";
        
        import crypto from 'crypto';
        import https from 'https';
        import { URL } from 'url';
        
        
        export const handler = async (event) => {
        
            const API_PATH = "/vserver/v2/startServerInstances";
            const METHOD = 'GET'; 
        
            const QUERY_PARAMS = `?serverInstanceNoList.1=${SERVER_INSTANCE_NO}`;
        
         
            const REQUEST_URI = `${API_PATH}${QUERY_PARAMS}`;
        
            const FULL_API_URL = `${NCP_API_ENDPOINT}${REQUEST_URI}`;
        
            console.log(`Request URI for signing: ${REQUEST_URI}`);
            console.log(`Full API URL for request: ${FULL_API_URL}`);
            console.log(`HTTP Method: ${METHOD}`);
        
        
            console.log(`Generated Timestamp: ${TIMESTAMP}`);
        
            const stringToSign = `${METHOD} ${REQUEST_URI}\n${TIMESTAMP}\n${NCP_ACCESS_KEY}`;
        
            console.log(`String to sign:\n${stringToSign}`);
        
        
            const hmac = crypto.createHmac('sha256', NCP_SECRET_KEY);
            hmac.update(stringToSign);
            const SIGNATURE = hmac.digest('base64');
        
            console.log(`Generated Signature: ${SIGNATURE}`);
        
            const apiUrlParsed = new URL(FULL_API_URL);
        
            const options = {
                hostname: apiUrlParsed.hostname,
                path: apiUrlParsed.pathname + apiUrlParsed.search, 
                headers: {
                    'Accept': 'application/json', /
                    'x-ncp-apigw-timestamp': TIMESTAMP,
                    'x-ncp-iam-access-key': NCP_ACCESS_KEY,
                    'x-ncp-apigw-signature-v2': SIGNATURE,
                },
                timeout: 30000 /
            };
        
            console.log("HTTPS Request Options:", options);
        
            return new Promise((resolve, reject) => {
                const req = https.request(options, (res) => {
                    let responseBody = '';
                    console.log(`Status Code: ${res.statusCode}`);
                    console.log(`Headers: ${JSON.stringify(res.headers)}`);
        
                    res.on('data', (chunk) => {
                        responseBody += chunk;
                    });
        
                    res.on('end', () => {
                        console.log(`Raw Response Body:\n${responseBody}`);
        
                       
                        if (res.statusCode < 200 || res.statusCode >= 300) {
                             console.error(`API returned non-2xx status code: ${res.statusCode}`);
                             return reject({ 
                                 statusCode: res.statusCode,
                                 body: JSON.stringify({
                                     message: `API returned unexpected HTTP status code ${res.statusCode}`,
                                     rawResponse: responseBody 
                                 })
                             });
                        }
        
                        
                        let parsedResponse = null;
                        let isApiSuccess = false; 
                        let formatIssue = false; 
        
                        try {
                            parsedResponse = JSON.parse(responseBody);
                            console.log("Parsed response as JSON:", parsedResponse);
        
                            if (parsedResponse && parsedResponse.returnCode === '0') {
                                isApiSuccess = true;
                            } else {
                                 console.warn("API Call failed based on JSON returnCode:", parsedResponse?.returnCode);
                            }
        
                        } catch (e) {
                            console.warn("Failed to parse response as JSON. Checking for XML success indicator.");
                            formatIssue = true; 
                            parsedResponse = responseBody; 
        
                            if (responseBody.includes('<returnCode>0</returnCode>')) {
                                 console.log("Found XML success indicator (<returnCode>0</returnCode>).");
                                 isApiSuccess = true; 
                            } else {
                                 console.warn("XML success indicator (<returnCode>0</returnCode>) not found in raw response.");
                            }
                        }
        
                        if (isApiSuccess) {
                            console.log("API Call considered successful (returnCode: 0 found).");
                            resolve({ 
                                statusCode: 200,
                                body: JSON.stringify({
                                    message: formatIssue ? "API call successful but response format was not JSON (likely XML)." : "API call successful (JSON).",
                                    response: parsedResponse 
                                })
                            });
                        } else {
                            console.error("API Call failed based on returnCode or format issue.");
                             resolve({ 
                                 statusCode: 200, 
                                 body: JSON.stringify({
                                     message: formatIssue ? "API response format was not JSON and API returnCode was not 0 (or not found)." : "API call failed (JSON returnCode was not 0).",
                                     response: parsedResponse 
                                 })
                             });
                        }
                    });
                });
        
                req.on('error', (e) => {
                    console.error(`Request Error: ${e.message}`);
                    reject({ 
                        statusCode: 500,
                        body: JSON.stringify(`Request Error: ${e.message}`)
                    });
                });
        
                req.on('timeout', () => {
                     console.error("Request timed out.");
                 req.destroy(); 
                 reject({ 
                     statusCode: 500,
                     body: JSON.stringify("Request timed out.")
                 });
            });
    
           
            req.end();
        });
        };
  ```
      </details>  

- VM이 켜지지면 바로 Batch app 실행 후 정상 수행 시 종료.
  - system daemon에 등록
  ```
  [Unit]
  Description=Run batch job on boot
  After=network.target

  [Service]
  Type=oneshot
  ExecStart=/usr/local/bin/run_batch.sh
  RemainAfterExit=yes

  [Install]
  WantedBy=multi-user.target

  ```
  <details>
  <summary>vm on 스크립트</summary>

  ```bash
  #!/bin/bash


  LOG_FILE="/var/log/app_execution.log"

  
  log_message() {
  local timestamp=$(date "+%Y-%m-%d %H:%M:%S")
  echo "[$timestamp] $1" >> $LOG_FILE
  echo "[$timestamp] $1"
  }


  JAR_FILE="/root/module-batch-boot.jar"
  PROFILE="batch"

 
  log_message "애플리케이션 실행 시작"
  java -jar -Dspring.profiles.active=$PROFILE $JAR_FILE

 
  EXIT_CODE=$?
  log_message "애플리케이션 종료 코드: $EXIT_CODE"


  if [ $EXIT_CODE -eq 0 ]; then
  log_message "애플리케이션이 정상적으로 종료되었습니다. VM 종료를 진행합니다."
  sudo shutdown -h now
  else
  log_message "애플리케이션이 오류 코드 $EXIT_CODE 로 종료되었습니다. VM 종료를 진행하지 않습니다."
  fi

  log_message "스크립트 실행 완료"
  exit 0 
  ```
  </details>

### 2. 배포 개선 및 CI/CD 적용
#### 2.1 Docker를 통한 배포

- 인프라 환경의 일관성 확보.
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



#### 2.2 Jenkins, Docker, Container Registry → 무중단 CI/CD 구성(Rolling).

- 배포 시간 단축.
- Jenkins에 business, batch 두 개의 파이프라인 설정.
- Bastion 호스트를 통한 프라이빗 서버 배포.
- 빌드 시 테스트(CI), 배포 후 헬스 체크(CD).
- Slack을 통한 배포 알람 설정.

##### 📌 Jenkins PipeLine Stage 종류

<details>
  <summary>Check out</summary>

  ```groovy
  stage('Checkout') {
    steps {
        checkout([
                $class           : 'GitSCM',
                branches         : [[name: 'main']],
                extensions       : [[
                                            $class             : 'SubmoduleOption',
                                            disableSubmodules  : false,
                                            parentCredentials  : true,
                                            recursiveSubmodules: true
                                    ]],
                userRemoteConfigs: [[
                                            url          : 'https://github.com/choizz156/pillivery.git',
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

>
> <details>
>   <summary><strong>docker_deploy.sh - 무중단 배포 스크립트</strong></summary>
>
>   ```bash
  >   #!/bin/bash
  >   if [ "$#" -ne 4 ]; then
  >       echo "⚠️파라미터가 부족합니다.⚠️"
  >       echo "=> $0 serverIp containerName registryUrl imageTag"
  >       exit 1
  >   fi
  >
  >   serverIp=$1
  >   containerName=$2
  >   registryUrl=$3
  >   imageTag=$4
  >
  >   echo "배포 과정 시작: $serverIp..."
  >
  >   ssh -o StrictHostKeyChecking=no root@$serverIp "
  >       if docker ps -q --filter name=$containerName; then
  >           docker rm -f ${containerName}-backup || true
  >               echo '기존 백업 컨테이너 삭제'
  >           docker rename $containerName ${containerName}-backup
  >               echo '백업 컨테이너 설정'
  >           docker stop ${containerName}-backup || true
  >               echo  '기존 컨테이너 종료'
  >       fi
  >   "
  >
  >   echo "새로운 컨테이너 배포"
  >   ssh -o StrictHostKeyChecking=no root@$serverIp "
  >       docker pull ${registryUrl}:${imageTag}
  >       echo '컨테이너 배포 시작'
  >       docker run -d \
  >         --name $containerName \
  >         --restart unless-stopped \
  >         --network server \
  >         -p 8080:8080 \
  >         -v app-logs:/root/logs \
  >         ${registryUrl}:${imageTag}
  >   "
  >
  >   echo "✅ 배포 완료: $serverIp"
  >   ```
> </details>
>
> <details>
>   <summary><strong>health_check.sh - 헬스 체크 및 롤백 트리거</strong></summary>
>
>   ```bash
  >   #!/bin/bash
  >
  >   if [ "$#" -ne 5 ]; then
  >       echo "⚠️ 파라미터가 부족합니다. ⚠️"
  >       echo "=> $0 serverIp containerName healthCheckUrl maxAttempts sleepInterval"
  >       exit 1
  >   fi
  >
  >   serverIp=$1
  >   containerName=$2
  >   healthCheckUrl=$3
  >   maxAttempts=$4
  >   sleepInterval=$5
  >
  >   attempts=0
  >   healthCheckSuccess=false
  >
  >   while [ $attempts -lt $maxAttempts ]; do
  >       httpCode=$(ssh -o StrictHostKeyChecking=no root@$serverIp "curl -s -o /dev/null -w \"%{http_code}\" $healthCheckUrl")
  >       echo "Health check 횟수 $((attempts + 1)). HTTP Status: $httpCode"
  >
  >       if [ "$httpCode" == "200" ]; then
  >           echo "✅ CD 완료 $serverIp."
  >           healthCheckSuccess=true
  >           break
  >       fi
  >
  >       attempts=$((attempts + 1))
  >       sleep $sleepInterval
  >   done
  >
  >   if [ "$healthCheckSuccess" == "false" ]; then
  >       echo "❌ Health check 실패 : $serverIp."
  >       exit 1
  >   else
  >       ssh -o StrictHostKeyChecking=no root@$serverIp "
  >           docker rm -f ${containerName}-backup || true
  >       "
  >       echo "✅ 배포 성공 : $serverIp"
  >   fi
  >   ```
> </details>
>
> <details>
>   <summary><strong>rollback.sh - 롤백 스크립트</strong></summary>
>
>   ```bash
  >   #!/bin/bash
  >
  >   if [ $# -ne 2 ]; then
  >       echo "⚠️파라미터가 부족합니다.⚠️"
  >       echo "=> $0 serverIp containerName"
  >       exit 1
  >   fi
  >
  >   serverIp=$1
  >   containerName=$2
  >
  >   echo "새롭게 배포하려던 컨테이너 정지 및 삭제 on $serverIp..."
  >   ssh -o StrictHostKeyChecking=no root@$serverIp "
  >       docker stop $containerName || true
  >       docker rm -f $containerName || true
  >   "
  >
  >   backupExists=$(ssh -o StrictHostKeyChecking=no root@$serverIp "docker ps -a --filter name=${containerName}-backup -q")
  >
  >   if [ -n "$backupExists" ]; then
  >       echo "백업 컨테이너 재시작 on $serverIp..."
  >       ssh -o StrictHostKeyChecking=no root@$serverIp "
  >           docker rename ${containerName}-backup $containerName
  >           docker start $containerName
  >       "
  >       echo "✅ 롤백 성공 on $serverIp"
  >   else
  >       echo "❌ 롤백 가능한 컨테이너 존재하지 않음. on $serverIp."
  >       exit 1
  >   fi
  >   ```
> </details>

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
                    ssh -o StrictHostKeyChecking=no root@${serverIp} "chmod +x /tmp/docker_deploy.sh && /tmp/docker_deploy.sh ${serverIp} ${containerName} ${env.VULTR_REGISTRY_URL} ${env.IMAGE_TAG}"
                    
                    # 헬스 체크 스크립트 복사 및 실행
                    scp -o StrictHostKeyChecking=no /tmp/health_check.sh root@${serverIp}:/tmp/
                    ssh -o StrictHostKeyChecking=no root@${serverIp} "chmod +x /tmp/health_check.sh && /tmp/health_check.sh ${serverIp} ${containerName} ${healthCheckUrl} 40 5"
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
            } catch (e) {
            }

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


<div style="display: flex; gap: 10px;">
  <img src="https://github.com/choizz156/pillivery/blob/d81d6e21d28f3c49f8ced4785cd3af652440d87e/image/%E1%84%87%E1%85%A2%E1%84%91%E1%85%A9%20%E1%84%89%E1%85%B5%E1%86%AF%E1%84%91%E1%85%A6%20slack%20message.png?raw=true" width="35%">
  <img src="https://github.com/choizz156/pillivery/blob/d81d6e21d28f3c49f8ced4785cd3af652440d87e/image/%E1%84%87%E1%85%A2%E1%84%91%E1%85%A9%20%E1%84%89%E1%85%A5%E1%86%BC%E1%84%80%E1%85%A9%E1%86%BC%20slack%20%E1%84%86%E1%85%A6%E1%84%89%E1%85%B5%E1%84%8C%E1%85%B5.png?raw=true" width="45%">
</div>

### 3. 모니터링 시스템

#### 3.1 Promtail, Loki, Promethues, Grafana → 로깅 및 관제 시스템 도입.

- grafana

  <img src="https://github.com/choizz156/pillivery/blob/e6ec666b987f73bbc08630745c34cd89602bd77d/image/grafana.png?raw=true" width="70%">

- loki

  <img src="https://github.com/choizz156/pillivery/blob/e6ec666b987f73bbc08630745c34cd89602bd77d/image/loki.png?raw=true" width="70%">

#### 3.2 Slack 연동 알림 시스템으로 장애 감지(Log, CPU).

- Error log 1분 간 10개, Warn log 5분간 20개, CPU 50% 이상 시 알람 설정.

<img src="https://github.com/choizz156/pillivery/blob/e6ec666b987f73bbc08630745c34cd89602bd77d/image/slack%20error%20%E1%84%8B%E1%85%A1%E1%86%AF%E1%84%85%E1%85%A1%E1%86%B7.png?raw=true" width="30%">


---

## ⚙️ 도메인 개선 및 리팩토링

### 1. 도메인 모델 개선

#### 1.1 연관관계 리팩토링

생명 주기가 다른 도메인 간에 불필요한 JPA 양방향 관계 제거하여 도메인간 결합도를 낮추고 변경 유연성 확보.

- 개선 사항
  - order ↔ user, review ↔ item, user 관계를 단방향으로 전환.
  - 생명주기가 다르다고 판단되는 객체 간 연관관계 최소화.
  - 필요한 경우 외래키 ID만 참조하는 방식으로 변경.
- 효과
  - 테스트 용이성과 유지보수 향상.
  - cascade 범위 최소화로 예측 가능한 객체 생명주기 관리.

#### 1.2 도메인 책임 분리

비지니스 특성에 맞춰 도메인 객체를 명확히 분리

- 개선 사항
  - 단건 결제(Order)와 정기 결제(SubscriptionOrder)로 분리.
  - 각각 도메인의 고유 책임과 비지니스 규칙 명확화.
- 효과
  - 단건 결제와 정기 결제의 독립적인 기능 확장 가능.
  - 정기 결제 기능 확장시 기능 로직 영향 최소화.
  - 도메인 모델의 명확성 향상.

### 2. 기술 스택 변경

#### 2.1 QueryDsl 도입

Native Query에서 QueryDSL로 전환하여 타입 안전성과 유지보수성을 강화.

- 효과
  - 컴파일 타임에 오류를 확인할 수 있어 안정성 향상.
  - 복잡한 검색 조건을 유연하게 처리 가능하게 함.
  - 쿼리 재사용성과 가독성 향상.

#### 2.2 Spring Rest Docs 도입

API 문서를 작성

- 효과
  - 테스트 기반 문서화로 신뢰성 확보.
  - 프로덕션 코드에 문서 작성을 위한 코드 침투 방지.
  - 변경 사항 발생 시 테스트 실패를 통한 문서 업데이트 필요성 즉시 감지.

<img src="https://github.com/choizz156/pillivery/blob/ba02fc54340612667146ec1141134da6c70ff2ea/image/api%20%E1%84%86%E1%85%AE%E1%86%AB%E1%84%89%E1%85%A5.png?raw=true" width="70%">

### 3. 이벤트 기반 아키택처 도입

#### 3.1 이벤트 패턴 적용.

핵심 비지니스 로직과 부가 기능을 분리하여 시스템 응답성과 유연성 향상.

- 개선 내용
  - 장바구니 삭제, 아이템 판매량 증가 등 핵심 트랜잭션에 포함되지 않아도 되는 로직을 이벤트 기반으로 분리하여 비동기 처리.
  - Spring의 ApplicationEventPublisher 활용한 이벤트 발행/구독 구조 구현.
  - @Async 어노테이션을 활용한 비동기 이벤트 처리.
- 효과
  - 주요 트랜잭션 처리 시간 및 범위 단축.
  - 도메인 간 결합도 감소.
  - 기능 확장 시 유연성 확보.

#### 3.2 비동기 이벤트 저장소를 통한 서버와 외부 API 통신 트랜잭션 분리

- 개선 사항
  - 주문 취소 처리, 외부 서비스(SID 발급 등) 호출과 같은 외부 API 연동 로직을 비동기 이벤트로 저장하여 트랜잭션과 분리.
  - 스케쥴링 기반의 후속 처리 방식 적용.
- 효과
  - 핵심 트랜잭션과 부가 트랜잭션을 분리하여 서버 로직과의 결합도를 낮추고 트랜잭션을 최소화.
  - 서버 응답 속도 개선 및 외부 API 장애로부터 핵심 비즈니스 로직 보호.

<img src="https://github.com/choizz156/pillivery/blob/17b2c6646322f4e4d3648c5ccdebfce76acd3c04/image/sid%20flow.png?raw=true" width="70%">

#### 3.3 분산 환경을 고려한 분산 락(Distributed Lock) 적용

다중 서버 환경의 이벤트 처리 신뢰성 확보를 위한 Redis 분산 락 적용.

- 기술 내용
  - Redisson을 활용한 분산 락 적용.
  - 다중 서버에서 이벤트 중복 처리 방지.
- 효과
  - 데이터 중복 등록 및 경쟁 조건 해결.
  - 비동기 이벤트 저장 로직의 신뢰성과 일관성 확보.

<img src="https://github.com/choizz156/pillivery/blob/9b009d062d9fc1c487c7536c48a64d81cb06b415/image/distributedLock.png?raw=true" width="70%">

### 4. 성능 개선

#### 4.1 Redis를 활용한 데이터 액세스 최적화

캐싱 전략을 통해 db 부하를 감소 시키고 응답 속도 개선.

- Redis(Redisson)의 HyperLogLog를 활용하여 조회 수 증가 캐싱

  - 아이템 조회 시, IP 중복 조회를 효율적으로 방지하기 위해 Redis의 HyperLogLog 자료구조 적용.
  - 메모리 사용량(12KB)을 최소화하면서도 오차(0.82)가 적은 고유 방문자 수 집계 가능.
  - 약간의 오차 허용 범위 내에서 정확성보다 처리 성능을 고려.

- 장바구니 추가/삭제 로직을 DB 통신에서 로컬 캐시 기반으로 변경

  - 일정 시간 후 DB에 저장하는 이벤트 발행.
  - 장바구니 관련 통신일 경우, Sticky Session을 적용.
  - 높은 VUser 상황(1000 이상)에서도 응답 시간 1초 이내 응답 → DB 부하 감소.

#### 4.2 확장 가능한 결제 시스템

다양한 결제 시스템 통합을 고려한 유연한 아키텍쳐 구현

- PG사 확장을 고려한 인터페이스 설계.
- 멱등성이 있는 로직(결제 조회)에 외부 API 비동기 통신 도입.

<img src="https://github.com/choizz156/pillivery/blob/9157523f361269e27b1002ad1b88b1298370dbc4/image/%E1%84%80%E1%85%A7%E1%86%AF%E1%84%8C%E1%85%A6.drawio.png?raw=true" width="70%">

### 5. 테스트 및 품질 관리

#### 5.1 테스트 코드 작성(Test Coverage 85%)

- FixtureMonkey 라이브러리를 사용하여 모의 객체 생성.
- 도메인 테스트, 통합 테스트에 Junit5, Mockito, Fake 객체 사용.
- 가독성을 고려하여, E2E 테스트에 RestAssured 사용.

![]("https://github.com/choizz156/pillivery/blob/e5a7a7d3acf5b2134403d729b8371083aebb6a5e/image/test_code.png)

#### 5.2 Logback과 MDC를 사용한 로깅
Logback과 MDC를 통해 멀티 스레드 환경에서 로깅 추적을 용이하게함.
- AOP를 사용하여 로깅.
- Nginx 로깅과 동기화.
- 로그 내용 파일 저장 및 Promtail을 통한 Grafana Loki에 전송.


---

## 🗄️ 기술적 도전

### 1️⃣ Circuit Breaker 패턴을 활용한 시스템 안정성 확보

#### 배경

- 외부 API 통신과의 의존성으로 인한 장애 전파 방지.
- 시스템 안정성과 복원력 향상 목적.

#### 아키택처

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │  Circuit Breaker│     │                 │
│  Client Service │────▶│   CLOSE/OPEN/   │────▶│ External Service│
│                 │     │    HALF-OPEN    │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

#### 핵심 설계

1. 장애 감지 및 차단 매커니즘
  - 실패율 임계치: 75% 이상 시 Circuit Open 전환
  - Retry 로직과 연계: 최대 3회 재시도 후 최종 실패로 판단
  - Recovery 전략: Open 상태에서 10초 후 Half-Open으로 자동 전환
  - 상태 복구 검증: Half-Open 상태에서 10회 호출 샘플링하여 임계치 이하면 Close 상태로 전환
2. 커넥션 풀 격리
  - 독립된 커넥션 풀 구성으로 서비스 간 영향도 최소화
3. Fast-Fail 응답 처리
  - Circuit Open 상태에서 즉시 실패 응답으로 시스템 부하 감소

#### 📌 Circuit Breaker 안정성 테스트

> - Locust 사용.
> - VUser 100으로 설정.
> - 자체 Mock Server를 사용하여 통신 실패 확률 20%로 설정.

- 총 재시도(3 번) 갯수: 4,179개, 최종 실패 총 174개

$$
재시도  실패율:\frac{174}{4179} \times 100 ≈ 4.16\%
$$

- 총 25,777개의 시도하여 실패 개수 174개(재시도 횟수 포함)

$$
최종 실패율 :\frac{174}{25777} \times 100 ≈ 0.6\%
$$

#### 📌 Circuit Breaker 기능 테스트

> - 기능 테스트를 위해 mock server 실패율은 일정 시간 후 75%까지 오르도록 설정, 지연 시간 5초.
> - open 임계값은 50%로 설정.

- 초기(closed) 정상 응답 처리.
- 임계값을 넘어 장애 발생 시(open), fast-fail로 빠른 응답시간(약 0ms).
- 복구 단계(half-open)에서 제한적 요청 허용.

<img src="https://github.com/choizz156/pillivery/blob/2cbde14fba519a83cc57bda3dfa1dd64763a57a4/image/circuitbraekertest.png?raw=true">

### 2️⃣ 정기 결제 로직 Spring Scheduled → Quartz → Spring Batch로 고도화

> - 정기 결제 주기마다 결제가 되어야함.
> - 유저는 배송 주기를 변경할 수 있고, 정기 결제 취소를 할 수 있음.
> - 잔액 부족 등의 문제 발생 시 재시도 로직이 포함되어야 함(1일,3일,5일).

#### `Phase 1`: `@Scheduled` 사용

##### 구현 내용

- 정해진 주기마다 정기 결제 요청.

##### 한계점

- 동적 스케쥴링 주기 변경이 불가.
- 스케줄 영속적 저장 불가.
- 재시도 로직 구현의 복잡성.

#### `Phase 2`: Quartz 도입

##### 개선 사항

- 동적 스케쥴링 변경 가능.
- 스케쥴러를 db에 영속적 저장/관리 가능.

##### 한계점

- 결제 재시도 로직 복잡성 증가.
- 많은 수의 스케쥴 관리 시 성능 문제 가능성 있음.

#### `Phase 3` : Spring Batch 기반 재설계

##### 개선 사항

- 결제 처리와 비지니스 로직 분리.
- 일일 배치 작업으로 해당 일자의 결제 대상 일괄 처리.
- 결제 실패 시 재시도 전략 효율적 구현.

1. 서버 오류로 인한 실패:
  	- 1시간 간격으로 최대 3회 자동 재시도.
2. 사용자 측 이슈(잔액 부족 등)으로 인한 실패
  	- 1일, 3일, 5일 간격의 단계적 재시도 로직.
  	- 5일차 재시도 실패 시 구독 해지 상태 변경.

##### 배치 job

1. step 기반 작업 분리

```
┌─────────────────────────────────┐
│ 정기결제 배치 Job               │
└─────────────────────────────────┘
    │
    ├─▶ Step 1: 당일 정기결제 처리
    │
    ├─▶ Step 2: 1일차 재시도 대상 처리
    │
    ├─▶ Step 3: 3일차 재시도 대상 처리
    │
    └─▶ Step 4: 5일차 재시도 대상 처리(최종)
```

2. 배치 처리

	- chunk 기반 처리로 메모리 사용량 최적화.
	- ItemReader, ItemProcessor, ItemWriter 분리로 단계별 처리 로직 명확화.

3. API 통신 멱등성 보장

	- 결제 요청 마다 고유 멱등키 생성하여 중복 결제 방지.





---
## ‼️Load/Stress 테스트

- Load 테스트를 통한 error 해결 및 성능 개선.
- Stress 테스트를 통한 서버 한계점 파악.
- 빈번한 API 요청 및 외부 API 관련 로직 테스트.

### ♻️ 테스트 환경

- Locust 사용
- vcpu 2, memory 2G
- Load 테스트 약 10분 이상, Stress 테스트 20분 이상.
- Mock Item 4만개, Mock Order 10만개.

### 👥 VUser 추정

- 타 사이트(필라**) MAU(500,000)를 참고하여 DAU 추정
    - DAU/MAU = 0.3이라고 가정 => DAU ≈ 150,000.
- 1인당 API 요청 수 : 5개
- 총 요청 수 / 1일 : 150,000 x 5 = 750,000
- 초당 평균 요청 수(RPS) : 750,000 / 86,400(s) ≈ 8.68
- 최대 집중률 : 10배라고 가정
- 최대 RPS : 8.68 x 10 ≈ 86.8
- 응답 시간 목표: 약 0.2초

> - T = (시나리오 상 요청 수 * 목표 응답 시간) + ⍺(예상 지연 시간) → (1 * 0.2) + 0 = **0.2**<br/>
> - 목표 최대 RPS = (VUser * 요청 수) / 목표 응답 시간(T)<br/>
> - VUser = (최대 RPS x 목표 응답 시간 ) / api 요청 수
    >   →  (86.8 × 0.2) / 1 = 17.36 ≈ **18**<br/>
    => VUser 값을 18로 두고 테스트하여 요청 시간이 0.2초를 유지한다면 대상 시스템은 86.8의 처리량을 보장한다고 가정할 수 있음.

> ⚠️ 학습 목적 상 추정된 VUser 18은 너무 적다고 판단하여 그 이상의 수로 테스트를 수행함.

### 📌 카테고리 별 아이템 테스트

> - 1 ~ 10 페이지 조회(1 ~ 5 페이지는 캐싱).
> - VUser 30으로 설정

#### 📈 Load Test

- 캐싱 평균 응답 시간: 10-20ms
- 캐싱되지 않은 요청 평균 응답 시간: 200-800ms

  <img src="https://github.com/choizz156/pillivery/blob/522a581e3c9bce295c6229aac2444068a0795fce/image/itemcatetgoryloadtest.png?raw=true" width="70%">

#### 📈 Stress Test

- 캐싱된 데이터들 제외하고 VUser 300부터 대기 중인 커넥션 풀이 증가하며 응답 시간 급격하게 증가.

  <img src="https://github.com/choizz156/pillivery/blob/522a581e3c9bce295c6229aac2444068a0795fce/image/itemcategorystresstest.png?raw=true" width="70%">

### 📌 결제 승인 테스트

> - 자체로 만든 Mock Server 사용.
> - 지연 시간 약 2초 적용.
> - Circuit Breaker, Retry 적용
> - VUser 100으로 설정.

#### 📈 Load Test

- 평균 응답 시간: 약 2초.

  <img src="https://github.com/choizz156/pillivery/blob/522a581e3c9bce295c6229aac2444068a0795fce/image/paymentApproveLoadTest.png?raw=true" width="70%">

#### 📈 Stress Test

- VUser 300부터 Circuit Breaker 발동 확인.

  <img src="https://github.com/choizz156/pillivery/blob/522a581e3c9bce295c6229aac2444068a0795fce/image/paymentApproveStressTest.png?raw=true" width="70%">

---

## ⚡️성능 개선 및 문제 해결

> ⚠️ Load Test 상황에서 문제 발생.
<details>
<summary> 1. 아이템 조회 API 낮은 응답 속도 인덱싱을 통해 해결 </summary>

#### (1) **문제 상황**

- **Load Test(VUser = 100)** 수행 시 아이템 조회 API 응답 시간이 목표치(200ms) 대비 현저히 느림.
- 실제 서비스 상황을 가정한 **4만 개의 Mock Item 환경**에서 성능 저하 발생.

#### (2) **문제의 원인**

- **MySQL 실행계획(EXPLAIN)** 분석 방법을 학습하여 **Full Table Scan 문제**.

```
...
 -> Filter: (i.real_price between 1000 and 50000)  (cost=0.277 rows=0.5) (actual time=0.00543..0.00546 rows=0.444 loops=9771)
...
```

#### (3) **해결 방법**

- 조건절에 사용되는 컬럼에 대한 **인덱스 설계 및 적용**.

```
...
-> Index range scan on i using idx_item_real_price over (1000 <= real_price <= 50000), with index condition: (i.real_price between 1000 and 50000)  (cost=9226 rows=19334) (actual time=0.0367..48.9 rows=17593 loops=1)
...
```

#### (4) **결과**

- 아이템 조회 API 응답 시간 **약 95% 개선** (평균 응답시간 750ms → 28.5ms).


</details>

<details>
<summary> 2. 아이템 조회 시 DB 부하 및 응답 속도 개선에 로컬 캐싱 도입 </summary>

#### (1) **문제 상황**

- 아이템 조회 시 DB 부하 문제.

#### (2) **문제의 원인**

- 모든 아이템을 조회할 때마다 DB와 통신.

#### (3) **해결 방법**

- 빈번히 조회되는 아이템(카테고리, 세일)에 **로컬 캐싱 도입(Caffeine Cache)**(1 ~ 5페이지까지).

#### (4) **결과**

- 응답속도 개선 : 1,117ms -> **39.8ms**(약 95% 개선)

</details>

<details>
<summary> 3. 장바구니 아이템 추가 시 동시성 문제(ConcurrentModificationException)를 방어적 복사로 해결 </summary>

#### (1) **문제 상황**

- 장바구니 아이템 추가 시, 아이템 중복 확인 로직에서(ConcurrentModificationException) 예외 발생.

#### (2) **문제의 원인**

- List에서 item 조회 stream 처리 중, 다른 스레드가 List를 수정한 것으로 추정.

```java
List<CartItemVO> cartItems = List.copyOf(cartVO.getCartItems());
```

#### (3) **해결 방법**

- List를 **방어적 복사** 후 stream 처리.

#### (4) **결과**

- 데이터 정합성 확보.

</details>

<details>
<summary> 4. 결제 승인 요청 시 DB 커넥션 풀 request time out error 해결  </summary>

#### (1) **문제 상황**

- 결제 승인 요청 시, DB 커넥션 풀 request time out error 발생.

#### (2) **문제의 원인**

- 외부 API 통신 로직과 서버 트랜잭션이 같은 로직에 포함돼 있어, 서버의 커넥션들이 외부 API 대기 시간에 영향을 받음(외부 API 처리 시간 2초로 가정).
- 판매량 증가 로직을 비동기 수행 시, 새로운 트랜잭션이 필요하게 됨.

#### (3) **해결 방법**

- 외부 API 통신 로직과 **트랜잭션 로직 분리**.
- 아이템 판매량 증가 쿼리 배치 처리.

</details>

---

## 📖 추후 개선 예정 사항

- DB master-slave 설정을 통해 DB 부하 감소 및 성능 개선.
- CQRS 패턴 적용하여 조회 최적화 적용.
- 비동기 처리를 위한 메시지 브로커(Kafka, RabbitMQ ..) 적용을 통한 유연성과 확장성 확보. 
  


