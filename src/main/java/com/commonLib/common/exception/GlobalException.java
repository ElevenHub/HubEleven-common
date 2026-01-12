package com.commonLib.common.exception;

import com.commonLib.common.code.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GlobalException extends RuntimeException {

	private final StatusCode errorCode;
}
