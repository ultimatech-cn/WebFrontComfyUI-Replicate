package me.zhengjie.modules.quartz.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.config.FileProperties;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.comfy.service.ComfyLargetService;
import me.zhengjie.modules.comfy.service.dto.ComfyLargetDto;
import me.zhengjie.modules.comfy.service.dto.ComfyLargetQueryCriteria;
import me.zhengjie.modules.comfy.service.mapstruct.ComfyLargetMapper;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.StringUtils;
import net.dreamlu.mica.core.result.R;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ComfyUITask {
    @Autowired
    ComfyLargetService comfyLargetService;
    @Autowired
    FileProperties properties;
    @Value("${comfyui.max}")
    private int max;
    @Value("${comfyui.try_time}")
    private int tryTime;
    @Value("${comfyui.apitoken}")
    private String apiToken;
    @Value("${urlBase}")
    private String urlBase;
    @Value("${comfyui.predictions_url}")
    private String predictionsUrl;
    @Value("${comfyui.getpredictions_url}")
    private String getPredictionsUrl;
    @Value("${comfyui.cancelpredictions_url}")
    private String cancelpredictionsUrl;


    public void run() throws IOException {
        log.info("comfyUITask 开始执行");
        //查询 数据库中待处理列表；
        //如果 处理中 >最大并发数量 ，则不提交新任务
        //小于最大并发数量，提交新任务，记录任务编号到表中

        //增加中止逻辑 取出等待中止 记录
        ComfyLargetQueryCriteria criteriaWatingCancle = new ComfyLargetQueryCriteria();
        criteriaWatingCancle.setStatus("6");
        List<ComfyLargetDto> allWatingCancleList =  comfyLargetService.queryAll(criteriaWatingCancle);
        log.info("comfyUITask：当前正在等待中止的任务数量："+allWatingCancleList.size());
        if(CollectionUtils.isNotEmpty(allWatingCancleList))
        {
            for(ComfyLargetDto cancle:allWatingCancleList)
            {
                String res = cancleTask(cancle.getPromptId());
                JSONObject jsonObject = JSONObject.parseObject(res);
                //执行成功
                //更新一下状态为执行成功
                cancle.setStatus("5");
                comfyLargetService.update(cancle);
            }

        }

        //增加重试逻辑 取出失败的记录&&小于重试次数
        ComfyLargetQueryCriteria criteriaError = new ComfyLargetQueryCriteria();
        criteriaError.setStatus("4");
        List<ComfyLargetDto> allErrorList =  comfyLargetService.queryAll(criteriaError);
        log.info("comfyUITask：当前失败的任务数量："+allErrorList.size());
        if(CollectionUtils.isNotEmpty(allErrorList))
        {
            for(ComfyLargetDto error:allErrorList)
            {
                if(error.getTryTime()>=tryTime)
                {
                    log.info("comfyUITask：超过最大重试次数："+error.getCaseId());
                }
                else
                //修改为已提交，重试次数+1；
                {
                    error.setStatus("1");
                    error.setTryTime(null==error.getTryTime()?1: error.getTryTime()+1);
                    comfyLargetService.update(error);
                }

            }

        }




        ComfyLargetQueryCriteria criteria = new ComfyLargetQueryCriteria();
        criteria.setStatus("2");
        List<ComfyLargetDto> allDoingList =  comfyLargetService.queryAll(criteria);
        log.info("comfyUITask：当前正在执行的任务数量："+allDoingList.size()+",系统最大支持数量："+max);
        //查询一次结果信息
        if(CollectionUtils.isNotEmpty(allDoingList))
        {
            for(ComfyLargetDto dto:allDoingList)
            {
                if(StringUtils.isNotEmpty(dto.getPromptId()))
                {
                    String res = queryTask(dto.getPromptId());
                    JSONObject jsonObject = JSONObject.parseObject(res);
                    if(jsonObject.get("status").equals("succeeded"))
                    {
                        //执行成功
                        //更新一下状态为执行成功
                        dto.setStatus("3");
                        dto.setEndDate(DateUtil.date().toTimestamp());
                        JSONArray jsonArray = jsonObject.getJSONArray("output");
                        if(null!=jsonArray&&jsonArray.size()>0)
                        {
                            dto.setTargetSrc(jsonArray.get(0).toString());
                        }
                        //下载保存图片
                        String suffix = FileUtil.getExtensionName(dto.getTargetSrc());
                        String type = FileUtil.getFileType(suffix);
                        String destinationPath = properties.getPath().getPath() + type +  File.separator;
                        try {
                            String fileName = dto.getPromptId();
                            String path = destinationPath + fileName+"."+suffix;
                            // getCanonicalFile 可解析正确各种路径
                            File dest = new File(path).getCanonicalFile();
                            // 检测是否存在目录
                            if (!dest.getParentFile().exists()) {
                                if (!dest.getParentFile().mkdirs()) {
                                    System.out.println("was not successful.");
                                }
                            }
                            saveImageFromUrl(dto.getTargetSrc(), path);
                            dto.setTargetPath(path);
                            dto.setTargetSrc(urlBase+"file/IMAGE/"+fileName+"."+suffix);
                            comfyLargetService.update(dto);
                            log.info("Image saved successfully.");
                        } catch (Exception e) {
                            log.info("Error saving image: " + e.getMessage());
                        }

                    }
                    if(jsonObject.get("status").equals("processing"))
                    {
                        //正在执行
                        dto.setStatus("2");
                        comfyLargetService.update(dto);
                    }
                    if(jsonObject.get("status").equals("failed"))
                    {
                        //失败
                        dto.setStatus("4");
                        dto.setEndDate(DateUtil.date().toTimestamp());
                        comfyLargetService.update(dto);
                    }
                    if(jsonObject.get("status").equals("canceled"))
                    {
                        //取消
                        dto.setStatus("5");
                        dto.setEndDate(DateUtil.date().toTimestamp());
                        comfyLargetService.update(dto);
                    }

                    log.info(jsonObject.toString());
                }

            }

        }

        ComfyLargetQueryCriteria criteria2 = new ComfyLargetQueryCriteria();
        criteria2.setStatus("2");
        List<ComfyLargetDto> allDoingList2 =  comfyLargetService.queryAll(criteria2);

        if(CollectionUtils.isNotEmpty(allDoingList2)&&allDoingList2.size()>=max)
        {
            log.info("comfyUITask：已超过最大支持数量，等待资源释放");
            return;
        }
        else
        {
            ComfyLargetQueryCriteria criteriaWating = new ComfyLargetQueryCriteria();
            criteriaWating.setStatus("1");
            List<ComfyLargetDto> allWatingList =  comfyLargetService.queryAll(criteriaWating);
            log.info("comfyUITask：当前正在等待执行的任务数量："+allWatingList.size());
            if(CollectionUtils.isNotEmpty(allWatingList))
            {
                ComfyLargetDto first = allWatingList.get(0);
                log.info("comfyUITask：开始执行任务");
                String res = doTask(first.getDescription(),first.getModel());
                JSONObject jsonObject = JSONObject.parseObject(res);
                //更新一下状态为执行中
                first.setStatus("2");
                first.setStartDate(DateUtil.date().toTimestamp());
                first.setPromptId(jsonObject.get("id").toString());
                comfyLargetService.update(first);
                return;

            }

        }

        log.info("run 执行成功");
    }

    public String  doTask(String jsonStr,String model)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSON json =JSONObject.parseObject(jsonStr);
        headers.set("Authorization","Bearer "+apiToken);
        HttpEntity<Object> entity = new HttpEntity<>(json, headers);
        String response = restTemplate.postForObject(predictionsUrl.replaceAll("model",model),entity,String.class);
        log.info(response);
        return response;

    }
    public String  queryTask(String id)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(getPredictionsUrl.replaceAll("id",id), HttpMethod.GET, entity, String.class);
        String body = response.getBody();
        log.info(body);
        return body;

    }
    public String  cancleTask(String id)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String response = restTemplate.postForObject(cancelpredictionsUrl.replaceAll("id",id),  entity, String.class);
        log.info(response);
        return response;

    }
    public void saveImageFromUrl(String imageUrl, String destinationPath) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+apiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Resource> response = restTemplate.exchange(imageUrl,HttpMethod.GET, entity,Resource.class);
        Resource resource = response.getBody();
        if (resource != null) {
            try (InputStream inputStream = resource.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            log.info("Image not found at " + imageUrl);
        }
    }
}
