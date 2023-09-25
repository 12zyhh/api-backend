package com.yupi.springbootinit.model.dto.interfaceInfo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    /**
     * id
     */
    private long id;

    /**
     * 请求参数
     */
    private String userRequestParams;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}