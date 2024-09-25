package com.example.sb.sftp.api.dto;


import com.example.sftp.config.SftpConfiguration;

public record SftpServer(
  String host,
  Integer port,
  String user
) {
  public SftpServer(SftpConfiguration sftpConfiguration) {
    this(sftpConfiguration.host(), sftpConfiguration.port(), sftpConfiguration.username());
  }
}
