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
package me.zhengjie.modules.comfy.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.comfy.domain.ComfyMark;
import me.zhengjie.modules.comfy.service.ComfyMarkService;
import me.zhengjie.modules.comfy.service.dto.ComfyMarkQueryCriteria;
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
import me.zhengjie.modules.comfy.service.dto.ComfyMarkDto;

/**
* @website https://eladmin.vip
* @author mjy
* @date 2024-09-02
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "图片涂鸦管理")
@RequestMapping("/api/comfyMark")
public class ComfyMarkController {

    private final ComfyMarkService comfyMarkService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('comfyMark:list')")
    public void exportComfyMark(HttpServletResponse response, ComfyMarkQueryCriteria criteria) throws IOException {
        comfyMarkService.download(comfyMarkService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询图片涂鸦")
    @ApiOperation("查询图片涂鸦")
    @PreAuthorize("@el.check('comfyMark:list')")
    public ResponseEntity<PageResult<ComfyMarkDto>> queryComfyMark(ComfyMarkQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(comfyMarkService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增图片涂鸦")
    @ApiOperation("新增图片涂鸦")
    @PreAuthorize("@el.check('comfyMark:add')")
    public ResponseEntity<Object> createComfyMark(@Validated @RequestBody ComfyMark resources){
        comfyMarkService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改图片涂鸦")
    @ApiOperation("修改图片涂鸦")
    @PreAuthorize("@el.check('comfyMark:edit')")
    public ResponseEntity<Object> updateComfyMark(@Validated @RequestBody ComfyMark resources){
        comfyMarkService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除图片涂鸦")
    @ApiOperation("删除图片涂鸦")
    @PreAuthorize("@el.check('comfyMark:del')")
    public ResponseEntity<Object> deleteComfyMark(@RequestBody Long[] ids) {
        comfyMarkService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}