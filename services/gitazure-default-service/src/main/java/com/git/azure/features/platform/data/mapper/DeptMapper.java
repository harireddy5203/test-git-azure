/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.data.mapper;

import com.git.azure.features.platform.data.model.experience.dept.CreateDeptRequest;
import com.git.azure.features.platform.data.model.experience.dept.Dept;
import com.git.azure.features.platform.data.model.experience.dept.UpdateDeptRequest;
import com.git.azure.features.platform.data.model.persistence.DeptEntity;
import java.util.Collection;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper contract that maps / transforms data from an instance of type {@link DeptEntity to {@link Dept and vice-versa.
 *
 * @author Mahalingam Iyer
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface DeptMapper {

    /**
     * This method transforms an instance of type {@link CreateDeptRequest} to an instance of type
     * {@link DeptEntity}.
     *
     * @param source Instance of type {@link CreateDeptRequest} that needs to be transformed to
     *     {@link DeptEntity}.
     * @return Instance of type {@link DeptEntity}.
     */
    DeptEntity transform(CreateDeptRequest source);

    /**
     * This method transforms an instance of type {@link DeptEntity} to an instance of type {@link
     * Dept}.
     *
     * @param source Instance of type {@link DeptEntity} that needs to be transformed to {@link
     *     Dept}.
     * @return Instance of type {@link Dept}.
     */
    Dept transform(DeptEntity source);

    /**
     * This method converts / transforms the provided collection of {@link DeptEntity} instances to
     * a collection of instances of type {@link Dept}.
     *
     * @param source Instance of type {@link DeptEntity} that needs to be transformed to {@link
     *     Dept}.
     * @return Collection of instance of type {@link Dept}.
     */
    default Collection<Dept> transformListTo(Collection<DeptEntity> source) {
        return source.stream().map(this::transform).collect(Collectors.toSet());
    }
    /**
     * This method transforms an instance of type {@link UpdateDeptRequest} to an instance of type
     * {@link DeptEntity}.
     *
     * <p>The provided instance ({@code target}) will be updated instead of creating a new instance.
     *
     * @param source Instance of type {@link UpdateDeptRequest} that needs to be transformed to
     *     {@link DeptEntity}.
     * @param target Instance of type {@link DeptEntity} that will be updated instead of creating
     *     and returning a new instance.
     */
    void transform(UpdateDeptRequest source, @MappingTarget DeptEntity target);

    /**
     * This method transforms an instance of type {@link UpdateDeptRequest} to an instance of type
     * {@link DeptEntity}.
     *
     * @param source Instance of type {@link UpdateDeptRequest} that needs to be transformed to
     *     {@link DeptEntity}.
     * @return Instance of type {@link DeptEntity}.
     */
    DeptEntity transform(UpdateDeptRequest source);

    /**
     * This method converts / transforms the provided collection of {@link UpdateDeptRequest}
     * instances to a collection of instances of type {@link DeptEntity}.
     *
     * @param source Instance of type {@link UpdateDeptRequest} that needs to be transformed to
     *     {@link DeptEntity}.
     * @return Instance of type {@link DeptEntity}.
     */
    default Collection<DeptEntity> transformList(Collection<UpdateDeptRequest> source) {
        return source.stream().map(this::transform).collect(Collectors.toSet());
    }
}
