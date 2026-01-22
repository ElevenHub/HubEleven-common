# HubEleven Common Library

MSA(Microservices Architecture) 환경에서 사용되는 공통 라이브러리입니다.

## 개요

- **Group ID**: `com.github.ElevenHub`
- **Artifact ID**: `HubEleven-common`
- **Version**: `v0.0.1`
- **Java Version**: 17
- **Spring Boot Version**: 3.5.8

---

## 설치 방법 (Installation)

### Gradle

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ElevenHub:HubEleven-common:v0.0.1'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.ElevenHub</groupId>
    <artifactId>HubEleven-common</artifactId>
    <version>v0.0.1</version>
</dependency>
```

---

## 필수 설정 (Configuration)

이 라이브러리의 Bean과 Entity를 인식하기 위해 Main Application Class에 아래 설정이 필요합니다.

```java
package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.service",      // 현재 서비스 패키지
    "com.commonLib.common"       // 공통 모듈 패키지
})
@EntityScan(basePackages = {
    "com.example.service",      // 현재 서비스 엔티티
    "com.commonLib.common"       // 공통 모듈 엔티티 (BaseEntity)
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## 주요 기능

### 1. 공통 응답 형식 (API Response)

통일된 API 응답 형식을 제공합니다.

#### ApiResponse 응답 형식

```json
{
  "code": "SUCCESS",
  "message": "성공했습니다.",
  "result": { ... }
}
```

#### ApiResponseEntity 사용 예시

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 성공 응답 (200 OK)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(user));
    }

    // 생성 응답 (201 Created)
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(user));
    }
}
```

---

### 2. 페이징 처리

표준화된 페이징 요청/응답을 제공합니다.

#### CommonPageRequest 사용

컨트롤러에서 `CommonPageRequest`를 파라미터로 받으면 자동으로 쿼리 파라미터가 바인딩되고, 검증 및 기본값 설정이 처리됩니다.

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseEntity<ApiResponse<CommonPageResponse<UserResponse>>> getUsers(
            CommonPageRequest pageRequest  // 자동으로 쿼리 파라미터 바인딩
    ) {
        // pageRequest.toPageable()로 Spring Data Pageable 생성
        Page<User> userPage = userService.getUsers(pageRequest.toPageable(), pageRequest.keyword());

        // Entity -> DTO 변환과 함께 페이징 응답 생성
        CommonPageResponse<UserResponse> response = PagingUtils.convert(userPage, UserResponse::from);

        return ApiResponseEntity.success(response);
    }
}
```

#### API 요청 예시

```
GET /api/users?page=0&size=10&sortType=CREATED_AT&direction=DESC&keyword=홍길동
```

#### 페이징 응답 형식

```json
{
  "code": "SUCCESS",
  "message": "성공했습니다.",
  "result": {
    "content": [
      { "id": 1, "name": "User1" },
      { "id": 2, "name": "User2" }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false
  }
}
```

#### 페이징 파라미터 및 자동 검증

CommonPageRequest의 compact constructor에서 자동으로 검증 및 기본값 설정이 처리됩니다:

- `page`: 페이지 번호
  - 음수인 경우 → 자동으로 `0`으로 변환
- `size`: 페이지 크기
  - `10`, `30`, `50`만 허용
  - 다른 값인 경우 → 자동으로 `10`으로 변환
- `sortType`: 정렬 기준
  - 가능한 값: `CREATED_AT`, `UPDATED_AT`
  - null인 경우 → 자동으로 `CREATED_AT`으로 설정
- `direction`: 정렬 방향
  - 가능한 값: `ASC`, `DESC`
  - null인 경우 → 자동으로 `DESC`로 설정
- `keyword`: 검색 키워드 (선택사항)

---

### 3. 예외 처리

전역 예외 처리를 위한 구조를 제공합니다.

#### Step 1: ErrorCode 정의

각 서비스에서 `ErrorCode` 인터페이스를 구현하여 도메인별 에러 코드를 정의합니다.

```java
package com.example.service.code;

import com.commonLib.common.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
```

#### Step 2: GlobalException 사용

```java
@Service
public class UserService {

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        // 사용자 생성 로직...
    }
}
```

#### 에러 응답 형식

```json
{
  "code": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다.",
  "result": null
}
```

#### GlobalExceptionHandler

모든 예외를 자동으로 처리합니다:
- `GlobalException`: ErrorCode를 사용한 응답
- 기타 예외: 500 서버 에러 응답 (CommonErrorCode.SERVER_ERROR)

---

### 4. JPA Auditing

엔티티의 생성/수정 정보를 자동으로 관리합니다.

#### BaseEntity 상속

```java
@Entity
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // createdAt, createdBy, updatedAt, updatedBy, deletedAt, deletedBy는
    // BaseEntity에서 자동으로 관리됩니다.
}
```

#### 자동 관리되는 필드

| 필드 | 타입 | 설명 |
|------|------|------|
| `createdAt` | LocalDateTime | 생성 일시 (자동 설정, 수정 불가) |
| `createdBy` | Long | 생성자 ID (X-User-Id 헤더에서 추출) |
| `updatedAt` | LocalDateTime | 수정 일시 (자동 갱신) |
| `updatedBy` | Long | 수정자 ID (X-User-Id 헤더에서 추출) |
| `deletedAt` | LocalDateTime | 삭제 일시 (Soft Delete) |
| `deletedBy` | Long | 삭제자 ID |

#### Soft Delete 사용

```java
@Service
public class UserService {

    @Transactional
    public void deleteUser(Long id, Long userId) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));

        // Soft Delete 수행
        user.delete(userId);
        // DB에서 실제로 삭제되지 않고 deletedAt, deletedBy만 설정됨
    }

    public List<User> getActiveUsers() {
        List<User> users = userRepository.findAll();
        // 삭제된 사용자 필터링
        return users.stream()
            .filter(user -> !user.isDeleted())
            .collect(Collectors.toList());
    }
}
```

---

### 5. 공통 코드

#### CommonErrorCode

기본 제공되는 에러 코드입니다.

```java
    CommonErrorCode.STATE_CONFLICT(HttpStatus.CONFLICT, "해당 데이터가 이미 존재합니다."),
    CommonErrorCode.FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    CommonErrorCode.INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터가 올바르지 않습니다."),
    CommonErrorCode.RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    CommonErrorCode.INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다.")
```

---

## 유틸리티

### PagingUtils

Page 객체를 CommonPageResponse로 변환할 때 사용합니다.

```java
// Entity -> DTO 변환과 함께 페이징 응답 생성
CommonPageResponse<UserDTO> response = PagingUtils.convert(page, UserDTO::from);

// 또는 람다식 사용
CommonPageResponse<UserDTO> response = PagingUtils.convert(page, user -> new UserDTO(user));
```

---

## 버전 관리 (Versioning)

- **v0.0.1**: 초기 버전
  - BaseEntity (JPA Auditing)
  - ApiResponse, ApiResponseEntity
  - GlobalException, GlobalExceptionHandler
  - CommonPageRequest, CommonPageResponse
  - PagingUtils
  - SuccessCode, CommonErrorCode