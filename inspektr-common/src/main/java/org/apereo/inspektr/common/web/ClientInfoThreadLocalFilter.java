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
package org.apereo.inspektr.common.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Creates a ClientInfo object and passes it to the {@link ClientInfoHolder}
 * <p>
 * If one provides an alternative IP Address Header (i.e. init-param "alternativeIpAddressHeader"), the client
 * IP address will be read from that instead.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0
 *
 */
public class ClientInfoThreadLocalFilter implements Filter {

    public static final String CONST_IP_ADDRESS_HEADER = "alternativeIpAddressHeader";
    public static final String CONST_SERVER_IP_ADDRESS_HEADER = "alternateServerAddrHeaderName";
    public static final String CONST_USE_SERVER_HOST_ADDRESS = "useServerHostAddress";

    private String alternateLocalAddrHeaderName;
    private boolean useServerHostAddress;
    private String alternateServerAddrHeaderName;
    
    public void destroy() {
        // nothing to do here
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        try {
            final ClientInfo clientInfo =
                    new ClientInfo((HttpServletRequest) request,
                            this.alternateServerAddrHeaderName,
                            this.alternateLocalAddrHeaderName,
                            this.useServerHostAddress);
            ClientInfoHolder.setClientInfo(clientInfo);
            filterChain.doFilter(request, response);
        } finally {
            ClientInfoHolder.clear();
        }
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        this.alternateLocalAddrHeaderName = filterConfig.getInitParameter(CONST_IP_ADDRESS_HEADER);
        this.alternateServerAddrHeaderName = filterConfig.getInitParameter(CONST_SERVER_IP_ADDRESS_HEADER);
        String useServerHostAddr = filterConfig.getInitParameter(CONST_USE_SERVER_HOST_ADDRESS);
        if (useServerHostAddr != null && !useServerHostAddr.isEmpty()) {
            this.useServerHostAddress = Boolean.valueOf(useServerHostAddr);
        }
    }
}
