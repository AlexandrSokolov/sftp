package com.example.sftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record AppExternalConfiguration (List<SftpConfiguration> sftpServers) {
}
