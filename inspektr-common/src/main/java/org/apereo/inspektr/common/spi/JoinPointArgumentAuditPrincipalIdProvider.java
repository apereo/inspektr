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
