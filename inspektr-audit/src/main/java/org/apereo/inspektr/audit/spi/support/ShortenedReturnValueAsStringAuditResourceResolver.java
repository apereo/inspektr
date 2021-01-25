package org.apereo.inspektr.audit.spi.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This is {@link ShortenedReturnValueAsStringAuditResourceResolver}.
 *
 * @author Misagh Moayyed
 */
public class ShortenedReturnValueAsStringAuditResourceResolver extends ReturnValueAsStringResourceResolver {
    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval) {
        String[] resources = super.resolveFrom(auditableTarget, retval);
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return resources;
        }
        if (resources != null) {
            return Arrays.stream(resources)
                .map(r -> StringUtils.abbreviate(r, 125))
                .collect(Collectors.toList())
                .toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        }
        return null;
    }
}

