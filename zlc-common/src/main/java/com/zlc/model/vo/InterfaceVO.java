package com.zlc.model.vo;

import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息封装视图
 *
 * @author yupi
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceVO extends InterfaceInfo {

    /**
     * 是否已点赞
     */
    private int totalNum;

    private static final long serialVersionUID = 1L;
}