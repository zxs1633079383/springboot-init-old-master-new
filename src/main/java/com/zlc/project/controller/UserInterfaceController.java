package com.zlc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zlc.project.annotation.AuthCheck;
import com.zlc.project.common.*;
import com.zlc.project.constant.UserConstant;
import com.zlc.project.exception.BusinessException;

import com.zlc.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.zlc.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.zlc.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.zlc.model.entity.UserInterfaceInfo;
import com.zlc.model.enums.InterfaceStatusEnum;
import com.zlc.model.entity.User;
import com.zlc.project.service.UserService;
import com.zlc.service.UserInterfaceInfoService;
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
@RequestMapping("/userUserInterfaceInfo")
@Slf4j
public class UserInterfaceController {

    @Resource
    private UserInterfaceInfoService userUserInterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ZlcClient zlcClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param userUserInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userUserInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userUserInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoAddRequest, userUserInterfaceInfo);
        // 校验
        userUserInterfaceInfoService.validUserInterfaceInfo(userUserInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userUserInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userUserInterfaceInfoService.save(userUserInterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newUserInterfaceInfoId = userUserInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userUserInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param userUserInterfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userUserInterfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (userUserInterfaceInfoUpdateRequest == null || userUserInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoUpdateRequest, userUserInterfaceInfo);
        // 参数校验
        userUserInterfaceInfoService.validUserInterfaceInfo(userUserInterfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = userUserInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userUserInterfaceInfoService.updateById(userUserInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        return ResultUtils.success(userUserInterfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userUserInterfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userUserInterfaceInfoQueryRequest) {
        UserInterfaceInfo userUserInterfaceInfoQuery = new UserInterfaceInfo();
        if (userUserInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userUserInterfaceInfoQueryRequest, userUserInterfaceInfoQuery);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userUserInterfaceInfoQuery);
        List<UserInterfaceInfo> userUserInterfaceInfoList = userUserInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(userUserInterfaceInfoList);
    }


    // endregion

    /***
     * 发布
     * @param request
     * @return
     * todo 方法是固定的client方法
     */

    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean>onlineUserInterfaceInfo(@RequestBody IdRequest idRequest,
                                                    HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        UserInterfaceInfo oldInter = userUserInterfaceInfoService.getById(id);
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
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        userUserInterfaceInfo.setId(id);
        userUserInterfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        boolean result = userUserInterfaceInfoService.updateById(userUserInterfaceInfo);
        return ResultUtils.success(result);


    }

    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> oflineUserInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        UserInterfaceInfo oldInter = userUserInterfaceInfoService.getById(id);
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
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        userUserInterfaceInfo.setId(id);
        userUserInterfaceInfo.setStatus(InterfaceStatusEnum.OFLINE.getValue());
        boolean result = userUserInterfaceInfoService.updateById(userUserInterfaceInfo);
        return ResultUtils.success(result);
    }




}
