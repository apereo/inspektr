package org.apereo.inspektr.audit.support;

import org.apereo.inspektr.audit.AuditActionContext;
import org.junit.Test;

import java.util.Date;

/**
 * This is {@link ConsoleAuditTrailManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ConsoleAuditTrailManagerTests {

    @Test
    public void testJson() {
        ConsoleAuditTrailManager c = new ConsoleAuditTrailManager();
        c.setAuditFormat(AbstractStringAuditTrailManager.AuditFormats.JSON);
        c.setUseSingleLine(true);
        c.record(new AuditActionContext("user", "RESOURCE",
            "ACTION", "CODE", new Date(), "clientIp", "serverIp"));
    }
}
