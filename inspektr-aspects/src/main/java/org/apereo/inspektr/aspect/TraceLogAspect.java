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
package org.apereo.inspektr.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author Scott Battaglia
 * @author Misagh Moayyed
 * @since 1.2
 */
@Aspect
public class TraceLogAspect {

    /**
     * Added TRACE-level log entries for the executing target.
     *
     * @param proceedingJoinPoint the proceeding join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("(execution (public * org.apereo..*.*(..))) && !(execution( * org.apereo..*.set*(..)))")
    public Object traceMethod(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        
        Logger logger = null;
        
        try {
            logger = this.getLog(proceedingJoinPoint);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
        
        if (logger == null) {
            System.err.println("Could not obtain logger object from the proceeding joinpoint");
            return proceedingJoinPoint.proceed();
        }
        
        Object returnVal = null;
        final String methodName = proceedingJoinPoint.getSignature().getName();
        try {
            if (logger.isTraceEnabled()) {
                final Object[] args = proceedingJoinPoint.getArgs();
                final String arguments;
                if (args == null || args.length == 0) {
                    arguments = "";
                } else {
                    arguments = Arrays.deepToString(args);
                }
                logger.trace("Entering method [{}] with arguments [{}]", methodName, arguments);
            }
            returnVal = proceedingJoinPoint.proceed();
            return returnVal;
        } finally {
            logger.trace("Leaving method [{}] with return value [{}].", methodName,
                    returnVal != null ? returnVal.toString() : "null");
        }
    }

    /**
     * Gets the logger object for the join point target.
     *
     * @param joinPoint the join point
     * @return the log
     */
    protected Logger getLog(final JoinPoint joinPoint) {
        final Object target = joinPoint.getTarget();

        if (target != null) {
            return LoggerFactory.getLogger(target.getClass());
        }

        return LoggerFactory.getLogger(getClass());
    }

}
