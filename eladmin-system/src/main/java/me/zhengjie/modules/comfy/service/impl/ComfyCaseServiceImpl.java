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
package me.zhengjie.modules.comfy.service.impl;

import me.zhengjie.modules.comfy.domain.ComfyCase;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.comfy.repository.ComfyCaseRepository;
import me.zhengjie.modules.comfy.service.ComfyCaseService;
import me.zhengjie.modules.comfy.service.dto.ComfyCaseDto;
import me.zhengjie.modules.comfy.service.dto.ComfyCaseQueryCriteria;
import me.zhengjie.modules.comfy.service.mapstruct.ComfyCaseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import me.zhengjie.utils.PageResult;

/**
* @website https://eladmin.vip
* @description 服务实现
* @author mjy
* @date 2024-08-08
**/
@Service
@RequiredArgsConstructor
public class ComfyCaseServiceImpl implements ComfyCaseService {

    private final ComfyCaseRepository comfyCaseRepository;
    private final ComfyCaseMapper comfyCaseMapper;

    @Override
    public PageResult<ComfyCaseDto> queryAll(ComfyCaseQueryCriteria criteria, Pageable pageable){
        Page<ComfyCase> page = comfyCaseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(comfyCaseMapper::toDto));
    }

    @Override
    public List<ComfyCaseDto> queryAll(ComfyCaseQueryCriteria criteria){
        return comfyCaseMapper.toDto(comfyCaseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ComfyCaseDto findById(Long caseId) {
        ComfyCase comfyCase = comfyCaseRepository.findById(caseId).orElseGet(ComfyCase::new);
        ValidationUtil.isNull(comfyCase.getCaseId(),"ComfyCase","caseId",caseId);
        return comfyCaseMapper.toDto(comfyCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ComfyCase resources) {
        comfyCaseRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ComfyCase resources) {
        ComfyCase comfyCase = comfyCaseRepository.findById(resources.getCaseId()).orElseGet(ComfyCase::new);
        ValidationUtil.isNull( comfyCase.getCaseId(),"ComfyCase","id",resources.getCaseId());
        comfyCase.copy(resources);
        comfyCaseRepository.save(comfyCase);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long caseId : ids) {
            comfyCaseRepository.deleteById(caseId);
        }
    }

    @Override
    public void download(List<ComfyCaseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ComfyCaseDto comfyCase : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("字典名称", comfyCase.getName());
            map.put("描述", comfyCase.getDescription());
            map.put("创建者", comfyCase.getCreateBy());
            map.put("更新者", comfyCase.getUpdateBy());
            map.put("创建日期", comfyCase.getCreateTime());
            map.put("更新时间", comfyCase.getUpdateTime());
            map.put("图片", comfyCase.getImgSrc());
            map.put("状态", comfyCase.getStatus());
            map.put("目标路径", comfyCase.getTargetSrc());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}