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

import org.apereo.inspektr.common.spi.JoinPointArgumentAuditPrincipalIdProvider;
import org.aspectj.lang.JoinPoint;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link SpringWebflowActionExecutionAuditablePrincipalResolver}.
 *
 * @author Misagh Moayyed
 */
public class SpringWebflowActionExecutionAuditablePrincipalResolver
    extends JoinPointArgumentAuditPrincipalIdProvider<RequestContext> {
    private final String attributeName;

    public SpringWebflowActionExecutionAuditablePrincipalResolver(final String attributeName) {
        super(0, RequestContext.class);
        this.attributeName = attributeName;
    }

    @Override
    protected String resolveFrom(final RequestContext requestContext, final JoinPoint auditTarget, final Object returnValue) {
        if (requestContext.getFlowScope().contains(attributeName)) {
            return requestContext.getFlowScope().get(attributeName).toString();
        }
        if (requestContext.getFlowScope().contains(attributeName)) {
            return requestContext.getFlowScope().get(attributeName).toString();
        }
        if (requestContext.getConversationScope().contains(attributeName)) {
            return requestContext.getConversationScope().get(attributeName).toString();
        }
        if (requestContext.getRequestParameters().contains(attributeName)) {
            return requestContext.getRequestParameters().getRequired(attributeName);
        }
        return UNKNOWN_USER;
    }
}
