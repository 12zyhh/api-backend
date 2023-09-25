package com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoLinkageQueryRequest extends PageRequest implements Serializable {

    private Long id;

    /**
     * 调用用户Id
     */
    private Long userId;

    /**
     * 调用接口Id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    private String status;

    /**
     * 是否删除(0-未删,1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}