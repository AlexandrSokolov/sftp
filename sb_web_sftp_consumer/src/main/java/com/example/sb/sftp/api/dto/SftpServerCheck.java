package com.example.sb.sftp.api.dto;

public record SftpServerCheck (
  SftpServer sftpServer,
  Boolean isConnectionValid
) {
}
