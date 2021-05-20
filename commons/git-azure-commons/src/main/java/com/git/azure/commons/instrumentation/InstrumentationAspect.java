/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.commons.instrumentation;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Aspect that deals with the "Logging / Instrumentation" cross-cutting concern.
 * <p>
 * This aspect provides the functionality of logging the time spent in the execution of methods that are annotated with
 * {@link Instrument} annotation.
 *
 * @author Mahalingam Iyer
 */
@Slf4j
@Aspect
@Component
public class InstrumentationAspect {
    /** Logging message format for method entry. */
    private static final String METHOD_ENTRY_LOG_MSG = "Entering method [{}], class [{}], at [{}]";

    /** Logging message format for method exit. */
    private static final String METHOD_EXIT_LOG_MSG = "Exiting method [{}], class [{}], at [{}], time-spent [{} milliseconds]";

    /**
     * Around advice which gets triggered whenever the control enters a method that is annotated with the annotation
     * {@link Instrument}.
     *
     * @param proceedingJoinPoint
     *         Proceeding join point.
     *
     * @return Object which is the return data from the respective join point.
     *
     * @throws Throwable
     *         Exception that might be thrown from the actual join point
     */
    @Around("@annotation(com.git.azure.commons.instrumentation.Instrument)")
    public Object instrumentMethodInvocation(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String className = Objects.nonNull(proceedingJoinPoint.getTarget())
                ? proceedingJoinPoint.getTarget().getClass().getSimpleName()
                : "";
        final String methodName = proceedingJoinPoint.getSignature().getName();

        final long startTime = System.currentTimeMillis();
        InstrumentationAspect.LOGGER.debug(InstrumentationAspect.METHOD_ENTRY_LOG_MSG, methodName, className, startTime);

        // Proceed to the actual join point
        final Object returnData = proceedingJoinPoint.proceed();

        final long endTime = System.currentTimeMillis();
        InstrumentationAspect.LOGGER.debug(InstrumentationAspect.METHOD_EXIT_LOG_MSG, methodName, className, endTime, (endTime - startTime));

        return returnData;
    }
}
