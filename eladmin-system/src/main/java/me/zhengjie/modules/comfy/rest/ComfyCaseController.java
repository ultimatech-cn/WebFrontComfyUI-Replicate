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
import me.zhengjie.modules.comfy.domain.ComfyCase;
import me.zhengjie.modules.comfy.service.ComfyCaseService;
import me.zhengjie.modules.comfy.service.dto.ComfyCaseQueryCriteria;
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
import me.zhengjie.modules.comfy.service.dto.ComfyCaseDto;

/**
* @website https://eladmin.vip
* @author mjy
* @date 2024-08-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "案例管理")
@RequestMapping("/api/comfyCase")
public class ComfyCaseController {

    private final ComfyCaseService comfyCaseService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('comfyCase:list')")
    public void exportComfyCase(HttpServletResponse response, ComfyCaseQueryCriteria criteria) throws IOException {
        comfyCaseService.download(comfyCaseService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询案例")
    @ApiOperation("查询案例")
    @PreAuthorize("@el.check('comfyCase:list')")
    public ResponseEntity<PageResult<ComfyCaseDto>> queryComfyCase(ComfyCaseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(comfyCaseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增案例")
    @ApiOperation("新增案例")
    @PreAuthorize("@el.check('comfyCase:add')")
    public ResponseEntity<Object> createComfyCase(@Validated @RequestBody ComfyCase resources){
        comfyCaseService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改案例")
    @ApiOperation("修改案例")
    @PreAuthorize("@el.check('comfyCase:edit')")
    public ResponseEntity<Object> updateComfyCase(@Validated @RequestBody ComfyCase resources){
        comfyCaseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除案例")
    @ApiOperation("删除案例")
    @PreAuthorize("@el.check('comfyCase:del')")
    public ResponseEntity<Object> deleteComfyCase(@RequestBody Long[] ids) {
        comfyCaseService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}