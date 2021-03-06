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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apereo.inspektr.common.Cleanable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Set;

/**
 * An interface used to make an audit trail record.
 *
 * @author Dmitriy Kopylenko
 * @since 1.0
 */
public interface AuditTrailManager extends Cleanable {
    Logger LOG = LoggerFactory.getLogger(AuditTrailManager.class);

    ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    static String toJson(final Object arg) {
        try {
            return MAPPER.writeValueAsString(arg);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Make an audit trail record. Implementations could use any type of back end medium to serialize audit trail
     * data i.e. RDBMS, log file, IO stream, SMTP, JMS queue or what ever else imaginable.
     * <p>
     * This concept is somewhat similar to log4j Appender.
     *
     * @param auditActionContext the audit action context
     */
    void record(AuditActionContext auditActionContext);

    /**
     * Gets audit records since.
     *
     * @param sinceDate the since date
     * @return the audit records since
     */
    Set<? extends AuditActionContext> getAuditRecordsSince(LocalDate sinceDate);

    /**
     * Remove all.
     */
    void removeAll();

    @Override
    default void clean() {
    }

    default void setAuditFormat(AuditTrailManager.AuditFormats auditFormat) {
    }

    enum AuditFormats {
        DEFAULT {
            @Override
            public String serialize(final Object object) {
                return object.toString();
            }
        },
        JSON {
            @Override
            public String serialize(final Object object) {
                return AuditTrailManager.toJson(object);
            }
        };
        public abstract String serialize(Object object);
    }
}
