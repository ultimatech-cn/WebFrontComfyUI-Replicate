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
import me.zhengjie.modules.comfy.domain.ComfyFal;
import me.zhengjie.modules.comfy.service.ComfyFalService;
import me.zhengjie.modules.comfy.service.dto.ComfyFalQueryCriteria;
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
import me.zhengjie.modules.comfy.service.dto.ComfyFalDto;

/**
* @website https://eladmin.vip
* @author mjy
* @date 2024-09-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "fal管理")
@RequestMapping("/api/comfyFal")
public class ComfyFalController {

    private final ComfyFalService comfyFalService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('comfyFal:list')")
    public void exportComfyFal(HttpServletResponse response, ComfyFalQueryCriteria criteria) throws IOException {
        comfyFalService.download(comfyFalService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询fal")
    @ApiOperation("查询fal")
    @PreAuthorize("@el.check('comfyFal:list')")
    public ResponseEntity<PageResult<ComfyFalDto>> queryComfyFal(ComfyFalQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(comfyFalService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增fal")
    @ApiOperation("新增fal")
    @PreAuthorize("@el.check('comfyFal:add')")
    public ResponseEntity<Object> createComfyFal(@Validated @RequestBody ComfyFal resources){
        comfyFalService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改fal")
    @ApiOperation("修改fal")
    @PreAuthorize("@el.check('comfyFal:edit')")
    public ResponseEntity<Object> updateComfyFal(@Validated @RequestBody ComfyFal resources){
        comfyFalService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除fal")
    @ApiOperation("删除fal")
    @PreAuthorize("@el.check('comfyFal:del')")
    public ResponseEntity<Object> deleteComfyFal(@RequestBody Long[] ids) {
        comfyFalService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}