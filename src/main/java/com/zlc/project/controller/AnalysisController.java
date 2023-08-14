package com.zlc.project.controller;

import cn.hutool.core.stream.CollectorUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.UserInterfaceInfo;
import com.zlc.model.vo.InterfaceVO;
import com.zlc.project.annotation.AuthCheck;
import com.zlc.project.common.BaseResponse;
import com.zlc.project.common.ErrorCode;
import com.zlc.project.common.ResultUtils;
import com.zlc.project.exception.BusinessException;
import com.zlc.project.mapper.UserInterfaceInfoMapper;
import com.zlc.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;


    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceVO>> listTopInterfaceInfo(){
        List<UserInterfaceInfo> userInterfaceInfos = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> interfaceInfoObjMap = userInterfaceInfos.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        //指定条件
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",interfaceInfoObjMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceVO> collect = list.stream().map(interfaceInfo -> {
            InterfaceVO interfaceVO = new InterfaceVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceVO);
            int totalNum = interfaceInfoObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceVO.setTotalNum(totalNum);
            return interfaceVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(collect);


    }

}

