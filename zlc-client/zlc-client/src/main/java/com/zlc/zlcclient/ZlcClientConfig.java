package com.zlc.zlcclient;


import com.zlc.zlcclient.client.ZlcClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("yuapi.client")
@Data
@ComponentScan
public class ZlcClientConfig {

    private String accesskey;

    private String secrekey;

    @Bean
    public  ZlcClient zlcClient(){
        return  new ZlcClient(accesskey,secrekey);

    }

}
