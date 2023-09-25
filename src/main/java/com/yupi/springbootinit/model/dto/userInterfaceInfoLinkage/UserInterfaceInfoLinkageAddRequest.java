package com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 */
@Data
public class UserInterfaceInfoLinkageAddRequest implements Serializable {

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除(0-未删,1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}