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
package me/zhengjie/modules/comfy.rest;

import me.zhengjie.annotation.Log;
import me/zhengjie/modules/comfy.domain.ComfyLarget;
import me/zhengjie/modules/comfy.service.ComfyLargetService;
import me/zhengjie/modules/comfy.service.dto.ComfyLargetQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import me.zhengjie.utils.PageResult;
import me/zhengjie/modules/comfy.service.dto.ComfyLargetDto;

/**
* @website https://eladmin.vip
* @author mjy
* @date 2024-08-09
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "图片放大管理")
@RequestMapping("/api/comfyLarget")
public class ComfyLargetController {

    private final ComfyLargetService comfyLargetService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('comfyLarget:list')")
    public void exportComfyLarget(HttpServletResponse response, ComfyLargetQueryCriteria criteria) throws IOException {
        comfyLargetService.download(comfyLargetService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询图片放大")
    @ApiOperation("查询图片放大")
    @PreAuthorize("@el.check('comfyLarget:list')")
    public ResponseEntity<PageResult<ComfyLargetDto>> queryComfyLarget(ComfyLargetQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(comfyLargetService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增图片放大")
    @ApiOperation("新增图片放大")
    @PreAuthorize("@el.check('comfyLarget:add')")
    public ResponseEntity<Object> createComfyLarget(@Validated @RequestBody ComfyLarget resources){
        comfyLargetService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改图片放大")
    @ApiOperation("修改图片放大")
    @PreAuthorize("@el.check('comfyLarget:edit')")
    public ResponseEntity<Object> updateComfyLarget(@Validated @RequestBody ComfyLarget resources){
        comfyLargetService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除图片放大")
    @ApiOperation("删除图片放大")
    @PreAuthorize("@el.check('comfyLarget:del')")
    public ResponseEntity<Object> deleteComfyLarget(@RequestBody Long[] ids) {
        comfyLargetService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}