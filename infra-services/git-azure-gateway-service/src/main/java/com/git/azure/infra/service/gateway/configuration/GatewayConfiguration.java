/*
 * Copyright (c) 2021 REPLACE_CUSTOMER_NAME. All rights reserved.
 *
 * This file is part of test-git-azure.
 *
 * test-git-azure project and associated code cannot be copied
 * and/or distributed without a written permission of REPLACE_CUSTOMER_NAME,
 * and/or its subsidiaries.
 */
package com.git.azure.infra.service.gateway.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
/**
 * Implementation for configuring Web Security for the application..
 *
 * @author Mahalingam Iyer
 */

@Slf4j
@Configuration
public class GatewayConfiguration {
    /** Pattern to match a microservice name. */
    private static final String PATTERN_MATCH_MICROSERVICE = ".*-service";

    /** Service Suffix. */
    private static final String SUFFIX_SERVICE = "-service";

    /** Route locator instance of type {@link RouteDefinitionLocator}. */
    private final RouteDefinitionLocator locator;

    /**
     * Constructor.
     *
     * @param locator
     *         Route locator instance of type {@link RouteDefinitionLocator}.
     */
    public GatewayConfiguration(final RouteDefinitionLocator locator) {
        this.locator = locator;
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        // Get the route definitions.
        final List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        if (Objects.isNull(definitions) || definitions.isEmpty()) {
            return Collections.emptyList();
        }

        return definitions.stream()
                .filter(routeDefinition -> {
                    final String routeId = routeDefinition.getId();
                    return routeId.matches(GatewayConfiguration.PATTERN_MATCH_MICROSERVICE) && !routeId.equals("authentication-service");
                })
                .map(routeDefinition -> {
                    // Route id.
                    final String routeId = routeDefinition.getId();
                    // What are the paths to match?
                    final List<String> pathsToMatch = new ArrayList<>();

                    // Get the predicates of this route definition.
                    final List<PredicateDefinition> predicates = routeDefinition.getPredicates();
                    // Are there any predicates?
                    if (Objects.isNull(predicates) || predicates.isEmpty()) {
                        pathsToMatch.add(generateDefaultPathName(routeId));
                    } else {
                        pathsToMatch.addAll(predicates.stream()
                                                    .filter(predicateDefinition -> predicateDefinition.getName().equals("Path"))
                                                    .map(predicateDefinition -> predicateDefinition.getArgs().values())
                                                    .flatMap(Collection::stream)
                                                    .collect(Collectors.toList()));
                    }

                    return GroupedOpenApi.builder()
                            .pathsToMatch(pathsToMatch.toArray(new String[0]))
                            .setGroup(pathsToMatch.get(0).replace("*", "").replace("/", ""))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Based on the provided service identifier, this method attempts to generate a default path name by stripping out
     * "-service" from the provided service identifier.
     *
     * @param serviceId
     *         Service identifier (e.g. administration-service, authentication-service, etc.).
     *
     * @return Path name to match.
     */
    private String generateDefaultPathName(final String serviceId) {
        return serviceId.replace(GatewayConfiguration.SUFFIX_SERVICE, StringUtils.EMPTY);
    }
}
