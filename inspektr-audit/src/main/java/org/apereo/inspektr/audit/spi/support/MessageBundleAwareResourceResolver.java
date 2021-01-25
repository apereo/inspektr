package org.apereo.inspektr.audit.spi.support;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is {@link MessageBundleAwareResourceResolver}.
 *
 * @author Misagh Moayyed
 */
public class MessageBundleAwareResourceResolver extends ReturnValueAsStringResourceResolver {

    private final ApplicationContext context;
    
    public MessageBundleAwareResourceResolver(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Exception e) {
        String[] resolved = super.resolveFrom(joinPoint, e);
        return resolveMessagesFromBundleOrDefault(resolved, e);
    }

    private String[] resolveMessagesFromBundleOrDefault(final String[] resolved, final Exception e) {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultKey = String.join("_",
            StringUtils.splitByCharacterTypeCamelCase(e.getClass().getSimpleName())).toUpperCase();
        return Stream.of(resolved)
            .map(key -> toResourceString(context.getMessage(key, null, defaultKey, locale)))
            .filter(Objects::nonNull)
            .toArray(String[]::new);
    }
}
