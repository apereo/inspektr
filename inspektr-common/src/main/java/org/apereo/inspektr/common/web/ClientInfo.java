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
package org.apereo.inspektr.common.web;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;

/**
 * Captures information from the HttpServletRequest to log later.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0
 *
 */
public class ClientInfo {

    public static ClientInfo EMPTY_CLIENT_INFO = new ClientInfo();

    /** IP Address of the server (local). */
    private final String serverIpAddress;

    /** IP Address of the client (Remote) */
    private final String clientIpAddress;
    
    private ClientInfo() {
        this(null);
    }

    public ClientInfo(final HttpServletRequest request) {
        this(request, null, null, false);
    }
    
    public ClientInfo(final HttpServletRequest request,
                      final String alternateServerAddrHeaderName,
                      final String alternateLocalAddrHeaderName,
                      final boolean useServerHostAddress) {

        try {
            String serverIpAddress = request != null ? request.getLocalAddr() : null;
            String clientIpAddress = request != null ? request.getRemoteAddr() : null;

            if (request != null) {
                if (useServerHostAddress) {
                    serverIpAddress = Inet4Address.getLocalHost().getHostAddress();
                } else if (alternateServerAddrHeaderName != null && !alternateServerAddrHeaderName.isEmpty()) {
                    serverIpAddress = request.getHeader(alternateServerAddrHeaderName) != null
                            ? request.getHeader(alternateServerAddrHeaderName) : request.getLocalAddr();
                }

                if (alternateLocalAddrHeaderName != null && !alternateLocalAddrHeaderName.isEmpty()) {
                    clientIpAddress = request.getHeader(alternateLocalAddrHeaderName) != null ? request.getHeader
                            (alternateLocalAddrHeaderName) : request.getRemoteAddr();
                }
            }

            this.serverIpAddress = serverIpAddress == null ? "unknown" : serverIpAddress;
            this.clientIpAddress = clientIpAddress == null ? "unknown" : clientIpAddress;

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerIpAddress() {
        return this.serverIpAddress;
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }
}
