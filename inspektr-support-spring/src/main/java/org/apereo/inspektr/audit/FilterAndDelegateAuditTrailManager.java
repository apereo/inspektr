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
package org.apereo.inspektr.audit;

import org.apereo.inspektr.audit.annotation.Audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is {@link FilterAndDelegateAuditTrailManager}.
 *
 * @author Misagh Moayyed
 */
public class FilterAndDelegateAuditTrailManager implements AuditTrailManager, ApplicationEventPublisherAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterAndDelegateAuditTrailManager.class);

    private final Collection<AuditTrailManager> auditTrailManagers;

    private final List<String> supportedActionsPerformed;

    private final List<String> excludedActionsPerformed;

    private ApplicationEventPublisher applicationEventPublisher;

    public FilterAndDelegateAuditTrailManager(final Collection<AuditTrailManager> auditTrailManagers,
                                              final List<String> supportedActionsPerformed,
                                              final List<String> excludedActionsPerformed) {
        this.auditTrailManagers = auditTrailManagers;
        this.supportedActionsPerformed = supportedActionsPerformed;
        this.excludedActionsPerformed = excludedActionsPerformed;
    }

    @Override
    public void setAuditFormat(final AuditFormats auditFormat) {
        auditTrailManagers.forEach(mgr -> mgr.setAuditFormat(auditFormat));
    }

    @Override
    public void record(final AuditActionContext auditActionContext) {
        boolean matched = supportedActionsPerformed
            .stream()
            .anyMatch(action -> {
                String actionPerformed = auditActionContext.getActionPerformed();
                return "*".equals(action) || Pattern.compile(action).matcher(actionPerformed).find();
            });

        if (matched) {
            matched = excludedActionsPerformed
                .stream()
                .noneMatch(action -> {
                    String actionPerformed = auditActionContext.getActionPerformed();
                    return "*".equals(action) || Pattern.compile(action).matcher(actionPerformed).find();
                });
        }
        if (matched) {
            LOGGER.trace("Recording audit action context [{}]", auditActionContext);
            auditTrailManagers.forEach(mgr -> mgr.record(auditActionContext));

            if (applicationEventPublisher != null) {
                AuditApplicationEvent auditEvent = new AuditApplicationEvent(auditActionContext.getPrincipal(),
                    auditActionContext.getActionPerformed(), auditActionContext.getApplicationCode(),
                    auditActionContext.getClientIpAddress(), auditActionContext.getServerIpAddress(),
                    auditActionContext.getResourceOperatedUpon(), auditActionContext.getWhenActionWasPerformed().toString());
                applicationEventPublisher.publishEvent(auditEvent);
            }
        } else {
            LOGGER.trace("Skipping to record audit action context [{}] as it's not authorized as an audit action among [{}]",
                auditActionContext, supportedActionsPerformed);
        }
    }

    @Override
    public Set<? extends AuditActionContext> getAuditRecordsSince(final LocalDate localDate) {
        return auditTrailManagers
            .stream()
            .map(mgr -> mgr.getAuditRecordsSince(localDate))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAll() {
        auditTrailManagers.forEach(AuditTrailManager::removeAll);
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}

