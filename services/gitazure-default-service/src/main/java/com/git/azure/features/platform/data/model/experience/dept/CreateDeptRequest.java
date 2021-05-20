/*
 * Copyright (c) 2020-2021 Innominds inc. All Rights Reserved. This software is
 * confidential and proprietary information of Innominds inc. You shall not disclose
 * Confidential Information and shall use it only in accordance with the terms
 *
 */
package com.git.azure.features.platform.data.model.experience.dept;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Implementation of an experience model that is meant to be used by the API Layer for communication
 * either with the front-end or to the service-layer.
 *
 * @author Mahalingam Iyer
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class CreateDeptRequest {
    /** Reference to the name. */
    @NotBlank(message = "dept.name.not.blank.message")
    @Size(max = 20, message = "dept.name.size.message")
    private String name;
}
