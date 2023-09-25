package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage.UserInterfaceInfoLinkageAddRequest;
import com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage.UserInterfaceInfoLinkageQueryRequest;
import com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage.UserInterfaceInfoLinkageUpdateRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.UserInterfaceInfoLinkage;
import com.yupi.springbootinit.service.UserInterfaceInfoLinkageService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/userInterfaceInfoLinkage")
@Slf4j
public class UserInterfaceInfoLinkageController {

    @Resource
    private UserInterfaceInfoLinkageService userInterfaceInfoLinkageService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param userInterfaceInfoLinkageAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addInterfaceInfo(@RequestBody UserInterfaceInfoLinkageAddRequest userInterfaceInfoLinkageAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoLinkageAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfoLinkage userInterfaceInfoLinkage = new UserInterfaceInfoLinkage();
        BeanUtils.copyProperties(userInterfaceInfoLinkageAddRequest, userInterfaceInfoLinkage);
        userInterfaceInfoLinkageService.validInterfaceInfo(userInterfaceInfoLinkage, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfoLinkage.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoLinkageService.save(userInterfaceInfoLinkage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = userInterfaceInfoLinkage.getId();
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
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfoLinkage oldUserInterfaceInfoLinkage = userInterfaceInfoLinkageService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfoLinkage == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfoLinkage.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoLinkageService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userInterfaceInfoLinkageUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody UserInterfaceInfoLinkageUpdateRequest userInterfaceInfoLinkageUpdateRequest) {
        if (userInterfaceInfoLinkageUpdateRequest == null || userInterfaceInfoLinkageUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfoLinkage userInterfaceInfoLinkage = new UserInterfaceInfoLinkage();
        BeanUtils.copyProperties(userInterfaceInfoLinkageUpdateRequest, userInterfaceInfoLinkage);
        // 参数校验
        userInterfaceInfoLinkageService.validInterfaceInfo(userInterfaceInfoLinkage, false);
        long id = userInterfaceInfoLinkageUpdateRequest.getId();
        userInterfaceInfoLinkage.setId(id);
        // 判断是否存在
        UserInterfaceInfoLinkage oldInterfaceInfo = userInterfaceInfoLinkageService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userInterfaceInfoLinkageService.updateById(userInterfaceInfoLinkage);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfoLinkage> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfoLinkage userInterfaceInfoLinkage = userInterfaceInfoLinkageService.getById(id);
        if (userInterfaceInfoLinkage == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userInterfaceInfoLinkage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userInterfaceInfoLinkageQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfoLinkage>> listInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoLinkageQueryRequest userInterfaceInfoLinkageQueryRequest,
                                                                         HttpServletRequest request) {
        long current = userInterfaceInfoLinkageQueryRequest.getCurrent();
        long size = userInterfaceInfoLinkageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfoLinkage> userInterfaceInfoLinkagePage = userInterfaceInfoLinkageService.page(new Page<>(current, size),
                userInterfaceInfoLinkageService.getQueryWrapper(userInterfaceInfoLinkageQueryRequest));
        return ResultUtils.success(userInterfaceInfoLinkageService.getUserInterfaceInfoLinkagePage(userInterfaceInfoLinkagePage, request));
    }

}
