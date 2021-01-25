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

