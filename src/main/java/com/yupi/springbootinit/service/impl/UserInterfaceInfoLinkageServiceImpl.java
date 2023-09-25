package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.UserInterfaceInfoLinkageMapper;
import com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage.UserInterfaceInfoLinkageQueryRequest;
import com.yupi.springbootinit.model.entity.InterfaceInfo;
import com.yupi.springbootinit.model.entity.UserInterfaceInfoLinkage;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;
import com.yupi.springbootinit.service.UserInterfaceInfoLinkageService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 赵
 * @description 针对表【user_interface_info_linkage(用户接口关系表)】的数据库操作Service实现
 * @createDate 2023-09-25 15:07:59
 */
@Service
public class UserInterfaceInfoLinkageServiceImpl extends ServiceImpl<UserInterfaceInfoLinkageMapper, UserInterfaceInfoLinkage>
        implements UserInterfaceInfoLinkageService {

    @Override
    public void validInterfaceInfo(UserInterfaceInfoLinkage userInterfaceInfoLinkage, boolean add) {
        if (userInterfaceInfoLinkage == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (add) {
            if (userInterfaceInfoLinkage.getInterfaceInfoId() <= 0 || userInterfaceInfoLinkage.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }

        if (userInterfaceInfoLinkage.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "剩余调用次数不足");
        }
    }

    @Override
    public QueryWrapper<UserInterfaceInfoLinkage> getQueryWrapper(UserInterfaceInfoLinkageQueryRequest userInterfaceInfoLinkageQueryRequest) {
        QueryWrapper<UserInterfaceInfoLinkage> queryWrapper = new QueryWrapper<>();
        if (null == userInterfaceInfoLinkageQueryRequest) {
            return queryWrapper;
        }

        Long id = userInterfaceInfoLinkageQueryRequest.getId();
        Long userId = userInterfaceInfoLinkageQueryRequest.getUserId();
        Long interfaceInfoId = userInterfaceInfoLinkageQueryRequest.getInterfaceInfoId();
        String status = userInterfaceInfoLinkageQueryRequest.getStatus();
        String sortField = userInterfaceInfoLinkageQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoLinkageQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq(interfaceInfoId != null, "interfaceInfoId", interfaceInfoId);
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    @Override
    public Page<UserInterfaceInfoLinkage> getUserInterfaceInfoLinkagePage(Page<UserInterfaceInfoLinkage> userInterfaceInfoLinkagePage, HttpServletRequest request) {
        List<UserInterfaceInfoLinkage> interfaceInfoList = userInterfaceInfoLinkagePage.getRecords();
        Page<UserInterfaceInfoLinkage> userInterfaceInfoLinkagePage1 = new Page<>(userInterfaceInfoLinkagePage.getCurrent()
                , userInterfaceInfoLinkagePage.getSize()
                , userInterfaceInfoLinkagePage.getTotal());
        userInterfaceInfoLinkagePage1.setRecords(CollectionUtils.isEmpty(interfaceInfoList) ? interfaceInfoList : new ArrayList<>());
        return userInterfaceInfoLinkagePage1;
    }

    @Override
    public boolean trackInterfaceUsageStats(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
        }
        UpdateWrapper<UserInterfaceInfoLinkage> userInterfaceInfoLinkageUpdateWrapper = new UpdateWrapper<>();
        userInterfaceInfoLinkageUpdateWrapper.eq("interfaceInfoId",interfaceInfoId);
        userInterfaceInfoLinkageUpdateWrapper.eq("userId",userId);
        userInterfaceInfoLinkageUpdateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(userInterfaceInfoLinkageUpdateWrapper);
    }
}




