/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.modules.comfy.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://eladmin.vip
* @description /
* @author mjy
* @date 2024-08-09
**/
@Data
public class ComfyLargetDto implements Serializable {

    /** ID */
    private Long caseId;
    private Long fileId;
    private Long largetNum;
    private Long tryTime;
    private Long imgWidth;
    private Long imgHeight;
    private String targetPath;
    private String model;
    /** 案例名称 */
    private String name;

    /** 描述 */
    private String description;
    private String text;
    private String response;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 创建日期 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 图片 */
    private String imgSrc;

    /** 状态 */
    private String status;

    /** 目标路径 */
    private String targetSrc;
    /** prompt_id */
    private String promptId;

    private Timestamp startDate;
    private Timestamp endDate;
    private Long proCostTime;
}
