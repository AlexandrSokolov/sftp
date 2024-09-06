package com.example.sftp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example.sftp")
@EnableConfigurationProperties(AppExternalConfiguration.class)
public class SftpDiConfiguration {

}
