package com.commonLib.common.utils;

import com.commonLib.common.response.CommonPageResponse;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public class PagingUtils {

	public static <E, D> CommonPageResponse<D> convert(Page<E> page, Function<E, D> mapper) {
		return CommonPageResponse.of(page.map(mapper));
	}
}
