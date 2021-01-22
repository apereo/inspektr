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
package org.apereo.inspektr.common.spi;

import org.aspectj.lang.JoinPoint;

/**
 * This is {@link JoinPointArgumentAuditPrincipalIdProvider}.
 *
 * @author Misagh Moayyed
 */
public abstract class JoinPointArgumentAuditPrincipalIdProvider<T> implements PrincipalResolver {
    private int argumentPosition;

    private Class<T> argumentType;

    public JoinPointArgumentAuditPrincipalIdProvider(final int argumentPosition, final Class<T> argumentType) {
        this.argumentPosition = argumentPosition;
        this.argumentType = argumentType;
    }

    @Override
    public String resolveFrom(final JoinPoint auditTarget, final Object returnValue) {
        if (argumentPosition >= 0
            && argumentPosition <= auditTarget.getArgs().length - 1
            && argumentType.isAssignableFrom(auditTarget.getArgs()[argumentPosition].getClass())) {
            return resolveFrom((T) auditTarget.getArgs()[argumentPosition], auditTarget, returnValue);
        }
        return null;
    }

    @Override
    public String resolveFrom(final JoinPoint auditTarget, final Exception exception) {
        if (argumentPosition >= 0
            && argumentPosition > auditTarget.getArgs().length - 1
            && auditTarget.getArgs()[argumentPosition].getClass().equals(argumentType)) {
            return resolveFrom((T) auditTarget.getArgs()[argumentPosition], auditTarget, exception);
        }
        return null;
    }

    protected abstract String resolveFrom(T argument, JoinPoint auditTarget, Object returnValue);

}
