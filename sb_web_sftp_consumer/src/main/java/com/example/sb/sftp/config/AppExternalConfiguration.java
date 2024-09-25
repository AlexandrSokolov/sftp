package com.example.sb.sftp.config;

import com.example.sftp.config.SftpConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record AppExternalConfiguration (List<SftpConfiguration> sftpServers) {
}
