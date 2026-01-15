package com.commonLib.common.exception;

import com.commonLib.common.code.CommonErrorCode;
import com.commonLib.common.code.ErrorCode;
import com.commonLib.common.response.ApiResponse;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;
import com.commonLib.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 비즈니스 로직 실행 중 발생한 예외 처리
	 */
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(GlobalException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.error(errorResponse));
	}

	/**
	 * @Valid, @Validated 어노테이션을 이용한 유효성 검사 실패 시 처리
	 * CommonErrorCode.INVALID_PARAMETER 를 사용하며 상세 에러 목록을 포함함
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		List<ErrorResponse.ValidationError> validationErrors = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(ErrorResponse.ValidationError::of)
				.collect(Collectors.toList());

		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		ErrorResponse errorResponse = ErrorResponse.of(errorCode, validationErrors);

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.error(errorResponse));
	}

	/**
	 * 나머지 모든 예외 처리 (서버 내 오류)
	 * CommonErrorCode.INTERNAL_SERVER_ERROR
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception e) {
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.error(errorResponse));
	}

	/**
	 * AccessDeniedException(권한 오류) 처리 (클라이언트 입력 값 오류)
	 * HTTP Status 403 FORBIDDEN 반환
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDenied(AccessDeniedException e) {
		ErrorCode errorCode = CommonErrorCode.FORBIDDEN;
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.error(errorResponse));
	}

	/**
	 * IllegalArgumentException 처리 (클라이언트 입력 값 오류)
	 * HTTP Status 400 Bad Request 반환
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException e) {
		ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.error(errorResponse));
	}

	/**
	 * IllegalStateException 처리 (상태 충돌 또는 잘못된 상태 전이)
	 * HTTP Status 409 Conflict 반환
	 */
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalStateException(IllegalStateException e) {
		ErrorCode errorCode = CommonErrorCode.STATE_CONFLICT;
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ApiResponse.error(errorResponse));
	}
}