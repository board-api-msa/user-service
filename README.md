# **User Service**

User Service는 사용자 등록, 인증, 사용자 정보 조회를 처리합니다.   
Gateway Server와 공유하는 비밀키로 JWT 토큰을 발행하여 사용자를 인증합니다.   
스프링 시큐리티를 사용하여 사용자 인증 프로세스를 구현하였습니다.

## **기술스택**

- **Java 21**
- **Spring boot**
- **Spring Security**
- **Spring Cloud Netflix Eureka Client**
- **Spring Cloud Config Client**
- **Spring Cloud OpenFeign**
- **JWT (JSON Web Tokens)**
- **Apache Kafka**
- **Resilience4j CircuitBreaker**


## **API 엔드포인트**

### **사용자 등록**

- **URI**: `POST /api/users`
- **요청 본문**:

  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "name": "홍길동"
  }
 

### **사용자 인증**

- **URI**: `POST /api/users/login`
- **요청 본문**:

   ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }

### **사용자 정보조회**

- **URI**: `POST /api/users/me`
- **요청 헤더**: 

    ```http
    Authorization: Bearer <JWT 토큰>

### **사용자 삭제**

- **URI**: `DELETE /api/users`
- **요청 헤더**: 

    ```http
    Authorization: Bearer <JWT 토큰>
- 사용자 삭제 요청시, 카프카서버로 메시지를 보냅니다. 해당 메시지를 Post Service가 구독하여 처리합니다.