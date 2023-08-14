package com.zlc.zlcgateway;

import com.zlc.model.entity.InterfaceInfo;
import com.zlc.model.entity.User;
import com.zlc.service.InnerInterfaceInfoService;
import com.zlc.service.InnerUserInterfaceInfoService;
import com.zlc.service.InnerUserService;
import com.zlc.zlcclient.util.SignUntils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHILE_LIST = Arrays.asList("127.0.0.1");

    private static final String INNER_URL_PREFIX = "http://localhost:8123";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 2. 请求日志
        //todo URL真实动态改变
        // 获取controller 包下所有 controller类.  利用反射读取注解的值. 还有请求方法,参数的值.

        ServerHttpRequest request = exchange.getRequest();
        String path =  INNER_URL_PREFIX +request.getPath().value();
        String method = request.getMethod().toString();
                log.info("请求唯一标识: " + request.getId());
        log.info("请求唯一路径: " + request.getPath().value());
        log.info("请求唯一方法: " + request.getMethod());
        log.info("请求参数: " + request.getQueryParams());
        log.info("请求对应地址: " + request.getLocalAddress().getHostString());
        log.info("请求来源地址: " + request.getRemoteAddress());
        String resouceAddress = request.getLocalAddress().getHostString();
        //获取响应
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHILE_LIST.contains(resouceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // 4. 用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String body = headers.getFirst("body");
        String sign = headers.getFirst("sign");
        // todo 去数据口查询是否分配给用户.
        User invokeUser = null;

        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);

        }catch (Exception e){
            log.error("invokeUser null Error");
        }
        if(invokeUser == null){
            return  handleNoAuth(response);
        }



//        if (!"yupi".equals(accessKey)) {
//            return handleNoAuth(response);
//        }
        if (Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(response);
        }
        long currentTime = System.currentTimeMillis() / 1000;
        Long FIVE_MINTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINTES) {
            return handleNoAuth(response);
        }

        // 实际是数据口查出
        String secreKey = invokeUser.getSecreKey();
        String serverSign = SignUntils.getSign(body, secreKey);

        if (sign == null || !serverSign.equals(sign)) {
//            throw new RuntimeException("无权限");
            return  handleNoAuth(response);
        }

        // 5. 请求的模拟接口是否存在.
        // todo 从数据口查询数据和接口是否存在. 以及请求方法是否匹配(还可以校验参数)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        }catch (Exception e){
            log.error("getInterface Error",e);
        }
        if(interfaceInfo ==null){
            return handleNoAuth(response);
        }


        // 6. 请求转发,调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
//        log.info("相应: " + response.getStatusCode());
        Long interfaceId = interfaceInfo.getId();
        Long userId = invokeUser.getId();

        //todo 判断是否还有调用次数

        return testResponseLog(exchange, chain,interfaceId,userId);
        // 7. 相应日志
        // 8. todo 调用成功, 调用次数+1. 现在接口id和用户id都是死的
        // 9. 调用失败,反回一个规范的错误码
//        if(response.getStatusCode() == HttpStatus.OK){
//
//        }else{
//            return handleInvokeError(response);
//        }
//
////        log.info("custom global filter");
//        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    //增强response功能, 添加响应日志
    public Mono<Void> testResponseLog(ServerWebExchange exchange, GatewayFilterChain chain,Long interfaceId,Long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    // 方法调用完才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        //todo 调用成功 次数+1
                                        // 调用成功/失败
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceId,userId);
                                        }catch (Exception e){
                                            log.error("invokeCOutn error",e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        sb2.append("<--- {} {} \n");
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        //rspArgs.add(requestUrl);
                                        String data = new String(content, StandardCharsets.UTF_8);//data
                                        sb2.append(data);
                                        //打印日志
                                        log.info("响应结果: " + data);
                                        log.info(sb2.toString(), rspArgs.toArray());//log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }

    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
