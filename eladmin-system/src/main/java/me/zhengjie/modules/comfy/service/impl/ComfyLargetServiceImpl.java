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

import me.zhengjie.domain.LocalStorage;
import me.zhengjie.modules.comfy.domain.ComfyLarget;
import me.zhengjie.repository.LocalStorageRepository;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.comfy.repository.ComfyLargetRepository;
import me.zhengjie.modules.comfy.service.ComfyLargetService;
import me.zhengjie.modules.comfy.service.dto.ComfyLargetDto;
import me.zhengjie.modules.comfy.service.dto.ComfyLargetQueryCriteria;
import me.zhengjie.modules.comfy.service.mapstruct.ComfyLargetMapper;
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
* @date 2024-08-09
**/
@Service
@RequiredArgsConstructor
public class ComfyLargetServiceImpl implements ComfyLargetService {

    private final ComfyLargetRepository comfyLargetRepository;
    private final ComfyLargetMapper comfyLargetMapper;
    private final LocalStorageRepository localStorageRepository;

    @Override
    public PageResult<ComfyLargetDto> queryAll(ComfyLargetQueryCriteria criteria, Pageable pageable){
        Page<ComfyLarget> page = comfyLargetRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(comfyLargetMapper::toDto));
    }

    @Override
    public List<ComfyLargetDto> queryAll(ComfyLargetQueryCriteria criteria){
        return comfyLargetMapper.toDto(comfyLargetRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ComfyLargetDto findById(Long caseId) {
        ComfyLarget comfyLarget = comfyLargetRepository.findById(caseId).orElseGet(ComfyLarget::new);
        ValidationUtil.isNull(comfyLarget.getCaseId(),"ComfyLarget","caseId",caseId);
        return comfyLargetMapper.toDto(comfyLarget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ComfyLarget resources) {
        comfyLargetRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ComfyLarget resources) {
        ComfyLarget comfyLarget = comfyLargetRepository.findById(resources.getCaseId()).orElseGet(ComfyLarget::new);
        ValidationUtil.isNull( comfyLarget.getCaseId(),"ComfyLarget","id",resources.getCaseId());
        comfyLarget.copy(resources);
        comfyLargetRepository.save(comfyLarget);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ComfyLargetDto resources) {
        ComfyLarget comfyLarget = comfyLargetRepository.findById(resources.getCaseId()).orElseGet(ComfyLarget::new);
        ValidationUtil.isNull( comfyLarget.getCaseId(),"ComfyLarget","id",resources.getCaseId());
        comfyLarget.copy(comfyLargetMapper.toEntity(resources));
        comfyLargetRepository.save(comfyLarget);
    }
    @Override
    public void deleteAll(Long[] ids) {
        for (Long caseId : ids) {
            ComfyLarget comfyLarget = comfyLargetRepository.findById(caseId).orElseGet(ComfyLarget::new);
            //删除文件
            if(null!=comfyLarget.getFileId())
            {
                LocalStorage storage = localStorageRepository.findById(comfyLarget.getFileId()).orElseGet(LocalStorage::new);
                FileUtil.del(storage.getPath());
                localStorageRepository.delete(storage);
            }
            //删除结果
            if(comfyLarget.getStatus().equals("3"))
            {
                if(null!=comfyLarget.getTargetSrc())
                {
                    FileUtil.del(comfyLarget.getTargetPath());
                }
            }

            comfyLargetRepository.deleteById(caseId);
        }
    }

    @Override
    public void download(List<ComfyLargetDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ComfyLargetDto comfyLarget : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("案例名称", comfyLarget.getName());
            map.put("创建者", comfyLarget.getCreateBy());
            map.put("更新者", comfyLarget.getUpdateBy());
            map.put("创建日期", comfyLarget.getCreateTime());
            map.put("更新时间", comfyLarget.getUpdateTime());
            map.put("图片", comfyLarget.getImgSrc());
            map.put("状态", comfyLarget.getStatus());
            map.put("目标路径", comfyLarget.getTargetSrc());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
