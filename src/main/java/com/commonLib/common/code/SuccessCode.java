package com.commonLib.common.code;

import static org.springframework.http.HttpStatus.OK;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements StatusCode {
	SUCCESS(OK, "성공했습니다."),
	CREATED(HttpStatus.CREATED, "생성되었습니다."),
	UPDATED(OK, "수정되었습니다."),
	DELETED(OK, "삭제되었습니다.");

	private final HttpStatus status;
	private final String message;

	@Override
	public HttpStatus getHttpStatus() {
		return status;
	}

	@Override
	public String getName() {
		return message;
	}
}
