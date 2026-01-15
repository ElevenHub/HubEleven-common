package com.commonLib.common.exception;

import com.commonLib.common.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GlobalException extends RuntimeException {

	private final ErrorCode errorCode;
}
