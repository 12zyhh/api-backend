package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage.UserInterfaceInfoLinkageQueryRequest;
import com.yupi.springbootinit.model.entity.InterfaceInfo;
import com.yupi.springbootinit.model.entity.UserInterfaceInfoLinkage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 赵
* @description 针对表【user_interface_info_linkage(用户接口关系表)】的数据库操作Service
* @createDate 2023-09-25 15:07:59
*/
public interface UserInterfaceInfoLinkageService extends IService<UserInterfaceInfoLinkage> {
    void validInterfaceInfo(UserInterfaceInfoLinkage userInterfaceInfoLinkage, boolean add);

    QueryWrapper<UserInterfaceInfoLinkage> getQueryWrapper(UserInterfaceInfoLinkageQueryRequest userInterfaceInfoLinkageQueryRequest);

    Page<UserInterfaceInfoLinkage> getUserInterfaceInfoLinkagePage(Page<UserInterfaceInfoLinkage> userInterfaceInfoLinkagePage, HttpServletRequest request);

    boolean trackInterfaceUsageStats(long interfaceInfoId, long userId);
}
