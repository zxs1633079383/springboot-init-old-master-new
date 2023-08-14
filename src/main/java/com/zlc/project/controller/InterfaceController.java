package com.zlc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.zlc.project.annotation.AuthCheck;

import com.zlc.project.common.*;
import com.zlc.project.constant.CommonConstant;
import com.zlc.project.exception.BusinessException;

import com.zlc.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.zlc.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.zlc.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.zlc.model.dto.interfaceinfo.InterfaceInvokeRequest;
import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.User;
import com.zlc.model.enums.InterfaceStatusEnum;
import com.zlc.project.service.InterfaceInfoService;
import com.zlc.project.service.UserService;
import com.zlc.zlcclient.client.ZlcClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ZlcClient zlcClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQueryRequest.getDescription();
//        String content = interfaceInfoQuery.getContent();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /***
     * 发布
     * @param request
     * @return
     * todo 方法是固定的client方法
     */

    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean>onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                    HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        InterfaceInfo oldInter = interfaceInfoService.getById(id);
        if(oldInter == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        com.zlc.zlcclient.model.User user = new com.zlc.zlcclient.model.User();
        user.setName("test");
        // 是否可用调用.
        String userNameByPost = zlcClient.getUserNameByPost(user);
        if(StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统调用失败");
        }
        // 仅本人和管理可以修改 上线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);


    }

    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> oflineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        InterfaceInfo oldInter = interfaceInfoService.getById(id);
        if(oldInter == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        com.zlc.zlcclient.model.User user = new com.zlc.zlcclient.model.User();
        user.setName("test");
        // 是否可用调用.
        String userNameByPost = zlcClient.getUserNameByPost(user);
        if(StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统调用失败");
        }
        // 仅本人和管理可以修改 上线
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceStatusEnum.OFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }


    /***
     *  测试调用
     * @param interfaceInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    // todo 给用户分配或者用户申请申请次数
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInvokeRequest interfaceInvokeRequest,
                                                     HttpServletRequest request) {
        if(interfaceInvokeRequest == null || interfaceInvokeRequest.getId() <=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInvokeRequest.getId();
        String userRequestparams = interfaceInvokeRequest.getUserRequestParams();

        //判断是否存在
        InterfaceInfo oldInter = interfaceInfoService.getById(id);

        if(oldInter == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        if(oldInter.getStatus() == InterfaceStatusEnum.OFLINE.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已关闭");
        }

        //调用 签名
        User loginUser = userService.getLoginUser(request);

        String accessKey = loginUser.getAccessKey();
        String secreKey = loginUser.getSecreKey();
        ZlcClient client = new ZlcClient(accessKey,secreKey);
        Gson gson = new Gson();
        com.zlc.zlcclient.model.User resultUser = gson.fromJson(userRequestparams, com.zlc.zlcclient.model.User.class);
        String result = client.getUserNameByPost(resultUser);
        log.info("结果:",result);


        return ResultUtils.success(result);
    }

}
