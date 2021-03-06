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

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Implementation of {@link AuditResourceResolver} that uses the toString version of the return value
 * as the resource.
 *
 * @author Scott Battaglia

 * @since 1.0
 *
 */
public class ReturnValueAsStringResourceResolver implements AuditResourceResolver {

    protected AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
    }


    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval) {
        if (retval instanceof Collection) {
            final Collection c = (Collection) retval;
            final String[] retvals = new String[c.size()];

            int i = 0;
            for (final Iterator iter = c.iterator(); iter.hasNext() && i < c.size(); i++) {
                final Object o = iter.next();

                if (o != null) {
                    retvals[i] = toResourceString(o);
                }
            }

            return retvals;
        }

        if (retval instanceof Object[]) {
            final Object[] vals = (Object[]) retval;
            final String[] retvals = new String[vals.length];
            for (int i = 0; i < vals.length; i++) {
                retvals[i] = toResourceString(vals[i]);
            }

            return retvals;
        }

        return new String[]{toResourceString(retval)};
    }

    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Exception exception) {
        final String message = exception.getMessage();
        if (message != null) {
            return new String[]{toResourceString(message)};
        }
        return new String[]{toResourceString(exception)};
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return postProcess(AuditTrailManager.toJson(arg));
        }
        return postProcess(arg.toString());
    }

    protected String postProcess(final String value) {
        return resourcePostProcessor.apply(new String[]{value})[0];
    }
}
