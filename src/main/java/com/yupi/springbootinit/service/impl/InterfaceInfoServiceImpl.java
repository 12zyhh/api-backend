package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.InterfaceInfoConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.InterfaceInfoMapper;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.entity.InterfaceInfo;
import com.yupi.springbootinit.model.vo.InterfaceInfoVO;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 赵
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-09-11 15:09:39
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    /**
     * 校验接口参数
     *
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {

        if (null == interfaceInfo) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 第一次添加,校验参数是否参数非空
        if (add) {
            Field[] declaredFields = interfaceInfo.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                String fieldName = field.getName();
                if (shouldIgnoreField(fieldName)) {
                    continue;
                }
                field.setAccessible(true); // 允许访问
                try {
                    Object value = field.get(interfaceInfo);
                    if (null == value || value instanceof String && StringUtils.isAnyBlank((String) value)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + "不能为空");
                    }
                } catch (IllegalAccessException e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "访问权限异常" + e.getMessage());
                }
            }
        } else {
            // 更新 参数校验
            if (StringUtils.isNotEmpty(interfaceInfo.getName()) && interfaceInfo.getName().length() > InterfaceInfoConstant.MAX_NAME_LENGTH) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称过长");
            }
            if (StringUtils.isNotEmpty(interfaceInfo.getUrl())) {
                Pattern pattern = Pattern.compile(InterfaceInfoConstant.URL_REGEX);
                if (!pattern.matcher(interfaceInfo.getUrl()).matches()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不正确");
                }
            }
            if (StringUtils.isNotEmpty(interfaceInfo.getMethod()) && !InterfaceInfoConstant.ALLOW_METHODS.contains(interfaceInfo.getMethod())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求类型不正确");
            }
        }

    }

    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (null == interfaceInfoQueryRequest) {
            return queryWrapper;
        }
        // 拼接查询条件
        Long id = interfaceInfoQueryRequest.getId();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        Long userId = interfaceInfoQueryRequest.getUserId();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.eq(StringUtils.isNotBlank(url), "url", url);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(StringUtils.isNotBlank(method), "method", method);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent()
                , interfaceInfoPage.getSize()
                , interfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(interfaceInfoList)) {
            return interfaceInfoVOPage;
        }
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream()
                .map(InterfaceInfoVO::objToVo).
                collect(Collectors.toList());
        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return interfaceInfoVOPage;
    }

    private boolean shouldIgnoreField(String fieldName) {
        List<String> fieldNameList = new ArrayList<String>() {{
            add("id");
            add("userId");
            add("isDelete");
            add("createTime");
            add("updateTime");
            add("status");
        }};
        return fieldNameList.contains(fieldName);
    }
}




