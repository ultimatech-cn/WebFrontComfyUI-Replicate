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

import me.zhengjie.modules.comfy.domain.ComfyFal;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.comfy.repository.ComfyFalRepository;
import me.zhengjie.modules.comfy.service.ComfyFalService;
import me.zhengjie.modules.comfy.service.dto.ComfyFalDto;
import me.zhengjie.modules.comfy.service.dto.ComfyFalQueryCriteria;
import me.zhengjie.modules.comfy.service.mapstruct.ComfyFalMapper;
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
* @date 2024-09-04
**/
@Service
@RequiredArgsConstructor
public class ComfyFalServiceImpl implements ComfyFalService {

    private final ComfyFalRepository comfyFalRepository;
    private final ComfyFalMapper comfyFalMapper;

    @Override
    public PageResult<ComfyFalDto> queryAll(ComfyFalQueryCriteria criteria, Pageable pageable){
        Page<ComfyFal> page = comfyFalRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(comfyFalMapper::toDto));
    }

    @Override
    public List<ComfyFalDto> queryAll(ComfyFalQueryCriteria criteria){
        return comfyFalMapper.toDto(comfyFalRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ComfyFalDto findById(Long caseId) {
        ComfyFal comfyFal = comfyFalRepository.findById(caseId).orElseGet(ComfyFal::new);
        ValidationUtil.isNull(comfyFal.getCaseId(),"ComfyFal","caseId",caseId);
        return comfyFalMapper.toDto(comfyFal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ComfyFal resources) {
        comfyFalRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ComfyFal resources) {
        ComfyFal comfyFal = comfyFalRepository.findById(resources.getCaseId()).orElseGet(ComfyFal::new);
        ValidationUtil.isNull( comfyFal.getCaseId(),"ComfyFal","id",resources.getCaseId());
        comfyFal.copy(resources);
        comfyFalRepository.save(comfyFal);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long caseId : ids) {
            comfyFalRepository.deleteById(caseId);
        }
    }

    @Override
    public void download(List<ComfyFalDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ComfyFalDto comfyFal : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("案例名称", comfyFal.getName());
            map.put("描述", comfyFal.getDescription());
            map.put("创建者", comfyFal.getCreateBy());
            map.put("更新者", comfyFal.getUpdateBy());
            map.put("创建日期", comfyFal.getCreateTime());
            map.put("更新时间", comfyFal.getUpdateTime());
            map.put("图片", comfyFal.getImgSrc());
            map.put("状态", comfyFal.getStatus());
            map.put("目标路径", comfyFal.getTargetSrc());
            map.put("prompt_id", comfyFal.getPromptId());
            map.put("倍数", comfyFal.getLargetNum());
            map.put(" imgWidth",  comfyFal.getImgWidth());
            map.put(" imgHeight",  comfyFal.getImgHeight());
            map.put(" text",  comfyFal.getText());
            map.put(" response",  comfyFal.getResponse());
            map.put("图片存储id", comfyFal.getFileId());
            map.put(" startDate",  comfyFal.getStartDate());
            map.put(" endDate",  comfyFal.getEndDate());
            map.put(" proCostTime",  comfyFal.getProCostTime());
            map.put(" targetPath",  comfyFal.getTargetPath());
            map.put("计算模型", comfyFal.getModel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}