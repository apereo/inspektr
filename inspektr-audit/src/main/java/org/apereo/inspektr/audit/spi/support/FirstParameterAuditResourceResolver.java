/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.apereo.inspektr.audit.util.AopUtils;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Converts the first argument object into a String resource identifier.
 * If the resource string is set, it will return the argument values into a list,
 * prefixed by the string. otherwise simply returns the argument value as a string.
 *
 * @author Scott Battaglia
 * @author Misagh Moayyed
 */
public class FirstParameterAuditResourceResolver implements AuditResourceResolver {


    protected Function<String[], String[]> resourcePostProcessor = inputs -> inputs;

    private String resourceString;

    private AuditTrailManager.AuditFormats auditFormat = AuditTrailManager.AuditFormats.DEFAULT;

    public void setResourceString(final String resourceString) {
        this.resourceString = resourceString;
    }

    public void setResourcePostProcessor(final Function<String[], String[]> resourcePostProcessor) {
        this.resourcePostProcessor = resourcePostProcessor;
    }


    @Override
    public void setAuditFormat(final AuditTrailManager.AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Object retval) {
        return toResources(getArguments(joinPoint));
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Exception exception) {
        return toResources(getArguments(joinPoint));
    }

    public String toResourceString(final Object arg) {
        if (auditFormat == AuditTrailManager.AuditFormats.JSON) {
            return AuditTrailManager.toJson(arg);
        }
        return resourceString != null
            ? this.resourceString + Arrays.asList(arg)
            : arg.toString();
    }

    private Object[] getArguments(final JoinPoint joinPoint) {
        return AopUtils.unWrapJoinPoint(joinPoint).getArgs();
    }

    /**
     * Turn the arguments into a list.
     *
     * @param args the args
     * @return the string[]
     */
    private String[] toResources(final Object[] args) {
        return this.resourcePostProcessor.apply(new String[]{toResourceString(args[0])});
    }
}
