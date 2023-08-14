package com.zlc.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {

    private Long id;

    private Long userId;




    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 调用次数
     */
    private Integer total_num;

    /**
     * 剩余调用次数
     */
    private Integer left_num;

    private Integer status;





    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}