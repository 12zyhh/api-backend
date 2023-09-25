package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.*;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoInvokeRequest;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.yupi.springbootinit.model.entity.InterfaceInfo;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.enums.InterfaceInfoStatusEnum;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.service.UserService;
import com.zymouse.apiclientsdk.client.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ApiClient apiClient;

    private final static Gson GSON = new Gson();

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
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
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
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(InterfaceInfoVO.objToVo(interfaceInfo));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
    }

    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> interfacePublish(@RequestBody IdRequest idRequest) {

        // 判断参数是否为空
        if (null == idRequest || 0 >= idRequest.getId()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();

        // 判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断接口是否可以调用
        com.zymouse.apiclientsdk.model.User user = new com.zymouse.apiclientsdk.model.User();
        user.setUsername("陈宗正");
        String username = apiClient.getUsernameByPost(user);// todo 由固定方法名改名实例地址调用
        ThrowUtils.throwIf(username == null, ErrorCode.SYSTEM_ERROR);

        // 修改接口状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(Integer.valueOf(InterfaceInfoStatusEnum.ONLINE.getValue()));
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }


    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> interfaceOffline(@RequestBody IdRequest idRequest) {

        // 判断参数是否为空
        if (null == idRequest || 0 >= idRequest.getId()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();

        // 判断接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 判断接口是否可以调用
        com.zymouse.apiclientsdk.model.User user = new com.zymouse.apiclientsdk.model.User();
        user.setUsername("陈宗正");
        String username = apiClient.getUsernameByPost(user);// todo 由固定方法名改名实例地址调用
        ThrowUtils.throwIf(username == null, ErrorCode.SYSTEM_ERROR);

        // 修改接口状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(Integer.valueOf(InterfaceInfoStatusEnum.OFFLINE.getValue()));
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }


    /**
     * 接口测试调用
     *
     * @param interfaceInfoInvokeRequest
     * @return
     */
    @PostMapping("/invoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Object> interfaceInvoke(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest
            , HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(null == interfaceInfoInvokeRequest || interfaceInfoInvokeRequest.getId() <= 0
                , ErrorCode.PARAMS_ERROR);
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(String.valueOf(InterfaceInfoStatusEnum.OFFLINE.getValue()).equals(interfaceInfo.getStatus())
                , ErrorCode.SYSTEM_ERROR);
        // 调用
        User loginUser = userService.getLoginUser(httpServletRequest);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        ApiClient tmpApiClient = new ApiClient(accessKey, secretKey);
        Gson gson = new Gson();
        com.zymouse.apiclientsdk.model.User apiClientUser = gson.fromJson(userRequestParams
                , com.zymouse.apiclientsdk.model.User.class);
        String response = tmpApiClient.getUsernameByPost(apiClientUser);// fixme 优化，这里目前写死 根据不同地址调用不同接口

        String typePattern = "type=([^,]+)";
        String statusPattern = "status=(\\d+)";
        String returnMessagePattern = "<div>(.*?)</div>";
        Pattern pattern = Pattern.compile(typePattern + ".*?" + statusPattern + ".*?" + returnMessagePattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String type = matcher.group(1);
            String status = matcher.group(3);
            String returnMessage = matcher.group(2);
            log.info("type: {} returnMessage: {} status: {}", type, status, returnMessage);
            return ResultUtils.success(gson.toJson(new ReturnInfo(type, status, returnMessage)));
        }
        return ResultUtils.success(new ReturnInfo(null, null, null));
    }

    private static class ReturnInfo {
        /**
         * 错误类型
         */
        private String type;

        /**
         * 返回报文
         */
        private String returnMessage;

        /**
         * 返回状态
         */
        private String status;

        public ReturnInfo(String type, String returnMessage, String status) {
            this.type = type;
            this.returnMessage = returnMessage;
            this.status = status;
        }
    }

}
