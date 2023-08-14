package com.zlc.zlcinterface.aop;


import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InvokeCountAop {


    // 定义切面 什么时候执行方法. 执行下面方法

    public void doInvokeCount(){
        //调用方法
        //次数+1
    }

}
