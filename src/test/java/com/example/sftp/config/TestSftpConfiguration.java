package com.example.sftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@ConfigurationProperties("sftp-servers")
public record TestSftpConfiguration (List<SftpConfiguration> sftpServers) {

}
