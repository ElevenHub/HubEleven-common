package com.commonLib.common.global;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuditorAwareImpl implements AuditorAware<Long> {
    // Gateway에서 넣어주는 헤더 이름
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Optional<Long> getCurrentAuditor() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)) {
            return Optional.empty();
        }

        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String userIdStr = servletRequestAttributes.getRequest().getHeader(USER_ID_HEADER);

        if (!StringUtils.hasText(userIdStr)) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(userIdStr));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
