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

import org.apereo.inspektr.audit.AuditTrailManager;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Inspektr ResourceResolver that resolves resource as a target object's toString method call
 *
 * @author Dmitriy Kopylenko
 */
public class ObjectToStringResourceResolver implements AuditResourceResolver {

    private AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    public String[] resolveFrom(JoinPoint target, Object returnValue) {
        return new String[]{toResourceString(target.getTarget())};
    }

    @Override
    public String[] resolveFrom(JoinPoint target, Exception exception) {
        Map<String, String> values = new HashMap<>();
        values.put("target", toResourceString(target.getTarget()));
        values.put("exception", toResourceString(exception.getMessage()));
        return new String[]{toResourceString(values)};
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return AuditTrailManager.toJson(arg);
        }
        return arg.toString();
    }
}
