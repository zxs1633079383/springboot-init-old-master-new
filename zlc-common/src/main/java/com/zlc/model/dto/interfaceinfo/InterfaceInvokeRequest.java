package com.zlc.model.dto.interfaceinfo;


import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceInvokeRequest implements Serializable {


    private long id;


    /***
     * 请求参数
     */
    private String userRequestParams;






}