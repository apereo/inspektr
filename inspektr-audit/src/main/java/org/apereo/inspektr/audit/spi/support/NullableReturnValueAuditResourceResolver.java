/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apereo.inspektr.audit.spi.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;
import org.springframework.webflow.execution.Event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
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
                return resourcePostProcessor.apply(new String[]{AuditTrailManager.toJson(values)});
            }
            return resourcePostProcessor.apply(new String[]{values.toString()});
        }
        return resourcePostProcessor.apply(this.delegate.resolveFrom(joinPoint, o));
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Exception e) {
        return resourcePostProcessor.apply(this.delegate.resolveFrom(joinPoint, e));
    }
}

