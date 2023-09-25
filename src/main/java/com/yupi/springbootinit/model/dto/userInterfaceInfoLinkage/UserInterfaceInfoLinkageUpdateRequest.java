package com.yupi.springbootinit.model.dto.userInterfaceInfoLinkage;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 *
 */
@Data
public class UserInterfaceInfoLinkageUpdateRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

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