# Pillivery (지속적 개발 및 개선: 2022 - 현재)
Pillivery는 건강기능식품 온라인 주문 및 정기 결제/배송 플랫폼으로, 초기 팀 프로젝트 이후 개인적으로 실제 서비스 수준의 아키텍쳐와 성능을 목표로 지속 개선 중
> 실제 서비스 수준의 건강기능식품 온라인 플랫폼 구현 </br>

> [Notion 포트폴리오](https://choizz156.notion.site/2547a3559c6b8007b69ed207c46aec99)

## 1. 개발 과정

### 초기 개발(2022. 11 ~ 2022.12 )

- 팀 프로젝트 참여 인원: 7명(FE: 4명, BE: 3명)
- 3인 백엔드 팀에서 핵심 기능 구현 담당
    - 유저 도메인 Restful API 개발
    - 외부 API 결제 연동: 카카오 페이 외부 API 연동을 통한 결제 기능 개발
    - 인증/인가 시스템 구축: Spring Security와 JWT, OAuth 2.0를 활용
    - 정기 결제 시스템 구축: Quartz를 사용한 정기 결제 시스템 개발

### 👨‍💻 **개인적인 개선 및 확장 (2023.01 - 현재)**

- 실제 서비스 운영 환경을 고려하여 개선 및 리팩토링을 진행
- 성능, 구조, 인프라 개선에 초점을 둠

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


## 3. 개인 리팩토링 작업(~ 현재)

### 📊 프로젝트 개선 사항

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%80%E1%85%B5%E1%84%89%E1%85%AE%E1%86%AF%E1%84%89%E1%85%B3%E1%84%90%E1%85%A2%E1%86%A8%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%89%E1%85%A5%E1%86%BC%E1%84%82%E1%85%B3%E1%86%BC%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%89%E1%85%A5%E1%86%AF%E1%84%80%E1%85%A8%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%8B%E1%85%A1%E1%86%AB%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%89%E1%85%A5%E1%86%BC%20%26%20%E1%84%91%E1%85%AE%E1%86%B7%E1%84%8C%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

<img src="https://github.com/choizz156/pillivery/blob/5b45c347151655a3ec30ca560c40ee508806e0a7/image/%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%91%E1%85%B3%E1%84%85%E1%85%A1%E1%84%80%E1%85%A2%E1%84%89%E1%85%A5%E1%86%AB.png?raw=true" width="70%">

## ⚙️ 인프라 아키텍처 개선

> Client → EC2 → RDS의 단순 3-tier → 확장성과 운영 효율성을 고려한 아키텍처로 개선

![아케택쳐](https://github.com/choizz156/pillivery/blob/5d60e935f2e10eccda9f9f00ec5c590df81b1f1d/image/%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A7%20%E1%84%8C%E1%85%B5%E1%86%AB%E1%84%8D%E1%85%A1%20%E1%84%8E%E1%85%AC%E1%84%8C%E1%85%A9%E1%86%BC.png)








    



