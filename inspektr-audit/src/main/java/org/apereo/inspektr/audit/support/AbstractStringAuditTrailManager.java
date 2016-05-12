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
package org.apereo.inspektr.audit.support;

import org.apereo.inspektr.audit.AuditActionContext;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.hjson.JsonObject;
import org.hjson.Stringify;

import java.io.StringWriter;

/**
 * Abstract AuditTrailManager that turns the AuditActionContext into a printable String.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0.1
 */
public abstract class AbstractStringAuditTrailManager implements AuditTrailManager {

    public enum AuditFormats {
        DEFAULT, JSON
    }

    /** what format should the audit log entry use? */
    private AuditFormats auditFormat = AuditFormats.DEFAULT;
    
    /** Use multi-line output by default **/
    private boolean useSingleLine = false;

    /** Separator for single line log entries */
    private String entrySeparator = ",";

    protected String getEntrySeparator() {
            return this.entrySeparator;
    }

    public void setEntrySeparator(final String separator) {
        this.entrySeparator = separator;
    }

    public void setUseSingleLine(final boolean useSingleLine) {
        this.useSingleLine = useSingleLine;
    }

    public void setAuditFormat(final AuditFormats auditFormat) {
        this.auditFormat = auditFormat;
    }

    protected String toString(final AuditActionContext auditActionContext) {
        if (auditFormat == AuditFormats.JSON) {
            final StringBuilder builder = new StringBuilder();
            builder.append("Audit trail record BEGIN\n");
            builder.append("=============================================================");
            if (this.useSingleLine) {
                builder.append(getJsonObjectForAudit(auditActionContext).toString(Stringify.PLAIN));
            }
            builder.append(getJsonObjectForAudit(auditActionContext).toString(Stringify.FORMATTED));
            builder.append("\n");
            builder.append("=============================================================");
            builder.append("\n\n");
            return builder.toString();
        }
        
        if (this.useSingleLine) {
            return getSingleLineAuditString(auditActionContext);
        }
        return getMultiLineAuditString(auditActionContext);
    }
        
    protected String getMultiLineAuditString(final AuditActionContext auditActionContext) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Audit trail record BEGIN\n");
        builder.append("=============================================================\n");
        builder.append("WHO: ");
        builder.append(auditActionContext.getPrincipal());
        builder.append("\n");
        builder.append("WHAT: ");
        builder.append(auditActionContext.getResourceOperatedUpon());
        builder.append("\n");
        builder.append("ACTION: ");
        builder.append(auditActionContext.getActionPerformed());
        builder.append("\n");
        builder.append("APPLICATION: ");
        builder.append(auditActionContext.getApplicationCode());
        builder.append("\n");
        builder.append("WHEN: ");
        builder.append(auditActionContext.getWhenActionWasPerformed());
        builder.append("\n");
        builder.append("CLIENT IP ADDRESS: ");
        builder.append(auditActionContext.getClientIpAddress());
        builder.append("\n");
        builder.append("SERVER IP ADDRESS: ");
        builder.append(auditActionContext.getServerIpAddress());
        builder.append("\n");
        builder.append("=============================================================");
        builder.append("\n\n");

        return builder.toString();
    }

    protected String getSingleLineAuditString(final AuditActionContext auditActionContext) {
        final StringBuilder builder = new StringBuilder();
        builder.append(auditActionContext.getWhenActionWasPerformed());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getApplicationCode());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getResourceOperatedUpon());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getActionPerformed());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getPrincipal());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getClientIpAddress());
        builder.append(getEntrySeparator());
        builder.append(auditActionContext.getServerIpAddress());

        return builder.toString();
    }

    protected JsonObject getJsonObjectForAudit(final AuditActionContext auditActionContext) {
        final JsonObject jsonObject = new JsonObject()
                        .add("who", auditActionContext.getPrincipal())
                        .add("what", auditActionContext.getResourceOperatedUpon())
                        .add("action", auditActionContext.getActionPerformed())
                        .add("application", auditActionContext.getApplicationCode())
                        .add("when", auditActionContext.getWhenActionWasPerformed().toString())
                        .add("clientIpAddress", auditActionContext.getClientIpAddress())
                        .add("serverIpAddress", auditActionContext.getServerIpAddress());
        return jsonObject;
    }
}
