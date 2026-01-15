package com.commonLib.common.code;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
	STATE_CONFLICT(HttpStatus.CONFLICT, "해당 데이터가 이미 존재합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터가 올바르지 않습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	CommonErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
