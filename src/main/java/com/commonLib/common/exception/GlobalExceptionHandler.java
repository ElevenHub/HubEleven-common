package com.commonLib.common.exception;

import com.commonLib.common.code.CommonErrorCode;
import com.commonLib.common.response.ApiResponse;
import com.commonLib.common.response.ApiResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	ResponseEntity<?> globalExceptionHandler(GlobalException e) {
		return ApiResponseEntity.onFailure(e.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleException(final Exception e) {
		return ApiResponseEntity.onFailure(CommonErrorCode.SERVER_ERROR);
	}
}
