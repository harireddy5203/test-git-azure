/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.data.repository;

import com.git.azure.commons.data.jpa.repository.ExtendedJpaRepository;
import com.git.azure.features.platform.data.model.persistence.DeptEntity;
import org.springframework.stereotype.Repository;

/**
 * Repository interface to handle the operations pertaining to domain models of type "DeptEntity".
 *
 * @author Mahalingam Iyer
 */
@Repository
public interface DeptRepository extends ExtendedJpaRepository<DeptEntity, Integer> {
    // Any custom methods can be added here.
}
