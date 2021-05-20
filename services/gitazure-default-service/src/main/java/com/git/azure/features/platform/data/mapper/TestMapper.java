/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.data.mapper;

import com.git.azure.features.platform.data.model.experience.test.CreateTestRequest;
import com.git.azure.features.platform.data.model.experience.test.Test;
import com.git.azure.features.platform.data.model.experience.test.UpdateTestRequest;
import com.git.azure.features.platform.data.model.persistence.TestEntity;
import java.util.Collection;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper contract that maps / transforms data from an instance of type {@link TestEntity to {@link Test and vice-versa.
 *
 * @author Mahalingam Iyer
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TestMapper {

    /**
     * This method transforms an instance of type {@link CreateTestRequest} to an instance of type
     * {@link TestEntity}.
     *
     * @param source Instance of type {@link CreateTestRequest} that needs to be transformed to
     *     {@link TestEntity}.
     * @return Instance of type {@link TestEntity}.
     */
    TestEntity transform(CreateTestRequest source);

    /**
     * This method transforms an instance of type {@link TestEntity} to an instance of type {@link
     * Test}.
     *
     * @param source Instance of type {@link TestEntity} that needs to be transformed to {@link
     *     Test}.
     * @return Instance of type {@link Test}.
     */
    Test transform(TestEntity source);

    /**
     * This method converts / transforms the provided collection of {@link TestEntity} instances to
     * a collection of instances of type {@link Test}.
     *
     * @param source Instance of type {@link TestEntity} that needs to be transformed to {@link
     *     Test}.
     * @return Collection of instance of type {@link Test}.
     */
    default Collection<Test> transformListTo(Collection<TestEntity> source) {
        return source.stream().map(this::transform).collect(Collectors.toSet());
    }
    /**
     * This method transforms an instance of type {@link UpdateTestRequest} to an instance of type
     * {@link TestEntity}.
     *
     * <p>The provided instance ({@code target}) will be updated instead of creating a new instance.
     *
     * @param source Instance of type {@link UpdateTestRequest} that needs to be transformed to
     *     {@link TestEntity}.
     * @param target Instance of type {@link TestEntity} that will be updated instead of creating
     *     and returning a new instance.
     */
    void transform(UpdateTestRequest source, @MappingTarget TestEntity target);

    /**
     * This method transforms an instance of type {@link UpdateTestRequest} to an instance of type
     * {@link TestEntity}.
     *
     * @param source Instance of type {@link UpdateTestRequest} that needs to be transformed to
     *     {@link TestEntity}.
     * @return Instance of type {@link TestEntity}.
     */
    TestEntity transform(UpdateTestRequest source);

    /**
     * This method converts / transforms the provided collection of {@link UpdateTestRequest}
     * instances to a collection of instances of type {@link TestEntity}.
     *
     * @param source Instance of type {@link UpdateTestRequest} that needs to be transformed to
     *     {@link TestEntity}.
     * @return Instance of type {@link TestEntity}.
     */
    default Collection<TestEntity> transformList(Collection<UpdateTestRequest> source) {
        return source.stream().map(this::transform).collect(Collectors.toSet());
    }
}
