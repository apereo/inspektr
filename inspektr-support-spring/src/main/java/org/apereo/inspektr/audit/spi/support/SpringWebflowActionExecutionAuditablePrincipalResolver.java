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
