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

import me.zhengjie.modules.comfy.domain.ComfyMark;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.comfy.repository.ComfyMarkRepository;
import me.zhengjie.modules.comfy.service.ComfyMarkService;
import me.zhengjie.modules.comfy.service.dto.ComfyMarkDto;
import me.zhengjie.modules.comfy.service.dto.ComfyMarkQueryCriteria;
import me.zhengjie.modules.comfy.service.mapstruct.ComfyMarkMapper;
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
* @date 2024-09-02
**/
@Service
@RequiredArgsConstructor
public class ComfyMarkServiceImpl implements ComfyMarkService {

    private final ComfyMarkRepository comfyMarkRepository;
    private final ComfyMarkMapper comfyMarkMapper;

    @Override
    public PageResult<ComfyMarkDto> queryAll(ComfyMarkQueryCriteria criteria, Pageable pageable){
        Page<ComfyMark> page = comfyMarkRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(comfyMarkMapper::toDto));
    }

    @Override
    public List<ComfyMarkDto> queryAll(ComfyMarkQueryCriteria criteria){
        return comfyMarkMapper.toDto(comfyMarkRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ComfyMarkDto findById(Long caseId) {
        ComfyMark comfyMark = comfyMarkRepository.findById(caseId).orElseGet(ComfyMark::new);
        ValidationUtil.isNull(comfyMark.getCaseId(),"ComfyMark","caseId",caseId);
        return comfyMarkMapper.toDto(comfyMark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ComfyMark resources) {
        comfyMarkRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ComfyMark resources) {
        ComfyMark comfyMark = comfyMarkRepository.findById(resources.getCaseId()).orElseGet(ComfyMark::new);
        ValidationUtil.isNull( comfyMark.getCaseId(),"ComfyMark","id",resources.getCaseId());
        comfyMark.copy(resources);
        comfyMarkRepository.save(comfyMark);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long caseId : ids) {
            comfyMarkRepository.deleteById(caseId);
        }
    }

    @Override
    public void download(List<ComfyMarkDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ComfyMarkDto comfyMark : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("案例名称", comfyMark.getName());
            map.put("描述", comfyMark.getDescription());
            map.put("创建者", comfyMark.getCreateBy());
            map.put("更新者", comfyMark.getUpdateBy());
            map.put("创建日期", comfyMark.getCreateTime());
            map.put("更新时间", comfyMark.getUpdateTime());
            map.put("图片", comfyMark.getImgSrc());
            map.put("状态", comfyMark.getStatus());
            map.put("目标路径", comfyMark.getTargetSrc());
            map.put("prompt_id", comfyMark.getPromptId());
            map.put("倍数", comfyMark.getLargetNum());
            map.put(" imgWidth",  comfyMark.getImgWidth());
            map.put(" imgHeight",  comfyMark.getImgHeight());
            map.put(" text",  comfyMark.getText());
            map.put(" response",  comfyMark.getResponse());
            map.put("图片存储id", comfyMark.getFileId());
            map.put(" startDate",  comfyMark.getStartDate());
            map.put(" endDate",  comfyMark.getEndDate());
            map.put(" proCostTime",  comfyMark.getProCostTime());
            map.put(" targetPath",  comfyMark.getTargetPath());
            map.put("计算模型", comfyMark.getModel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}