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
package me.zhengjie.modules.comfy.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://eladmin.vip
* @description /
* @author mjy
* @date 2024-08-09
**/
@Entity
@Data
@Table(name="comfy_larget")
public class ComfyLarget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`case_id`")
    @ApiModelProperty(value = "ID")
    private Long caseId;

    @Column(name = "`name`")
    @ApiModelProperty(value = "案例名称")
    private String name;

    @Column(name = "`description`")
    @ApiModelProperty(value = "描述")
    private String description;

    @Column(name = "`prompt_id`")
    @ApiModelProperty(value = "prompt_id")
    private String promptId;
    @Column(name = "`file_id`")
    @ApiModelProperty(value = "file_id")
    private Long fileId;

    @Column(name = "`create_by`")
    @ApiModelProperty(value = "创建者")
    private String createBy;

    @Column(name = "`update_by`")
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @Column(name = "`create_time`")
    @CreationTimestamp
    @ApiModelProperty(value = "创建日期")
    private Timestamp createTime;
    @Column(name = "`start_date`")
    @ApiModelProperty(value = "开始日期")
    private Timestamp startDate;
    @Column(name = "`end_date`")
    @ApiModelProperty(value = "结束日期")
    private Timestamp endDate;
    @Column(name = "`pro_cost_time`")
    @ApiModelProperty(value = "预计耗时")
    private Long proCostTime;
    @Column(name = "`target_path`")
    @ApiModelProperty(value = "结果路径")
    private String targetPath;
    @Column(name = "`model`")
    @ApiModelProperty(value = "模型")
    private String model;
    @Column(name = "`update_time`")
    @UpdateTimestamp
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;

    @Column(name = "`img_src`")
    @ApiModelProperty(value = "图片")
    private String imgSrc;

    @Column(name = "`status`")
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(name = "`target_src`")
    @ApiModelProperty(value = "目标路径")
    private String targetSrc;
    @Column(name = "`larget_num`")
    @ApiModelProperty(value = "放大倍数")
    private Long largetNum;
    @Column(name = "`try_time`")
    @ApiModelProperty(value = "重试次数")
    private Long tryTime;
    @Column(name = "`img_width`")
    @ApiModelProperty(value = "宽度")
    private Long imgWidth;
    @Column(name = "`img_height`")
    @ApiModelProperty(value = "高度")
    private Long imgHeight;
    @Column(name = "`text`")
    @ApiModelProperty(value = "提示词")
    private String text;
    @Column(name = "`response`")
    @ApiModelProperty(value = "返回数据")
    private String response;
    public void copy(ComfyLarget source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
