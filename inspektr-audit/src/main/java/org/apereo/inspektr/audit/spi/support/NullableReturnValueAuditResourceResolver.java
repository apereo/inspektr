package org.apereo.inspektr.audit.spi.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;
import org.springframework.webflow.execution.Event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link NullableReturnValueAuditResourceResolver}.
 *
 * @author Misagh Moayyed
 */
public class NullableReturnValueAuditResourceResolver implements AuditResourceResolver {
    private final AuditResourceResolver delegate;

    private AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    public NullableReturnValueAuditResourceResolver(final AuditResourceResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    @SuppressWarnings("JavaUtilDate")
    public String[] resolveFrom(final JoinPoint joinPoint, final Object o) {
        if (o == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if (o instanceof Event) {
            Event event = Event.class.cast(o);
            String sourceName = event.getSource().getClass().getSimpleName();
            Map values = new HashMap<String, String>();
            values.put("event", event.getId());
            values.put("timestamp", new Date(event.getTimestamp()));
            values.put("source", sourceName);
            if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
                return new String[]{AuditTrailManager.toJson(values)};
            }
            return new String[]{values.toString()};
        }
        return this.delegate.resolveFrom(joinPoint, o);
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Exception e) {
        return this.delegate.resolveFrom(joinPoint, e);
    }
}

