package com.example.sb.sftp.api.dto;

import java.util.List;

public record ConnectionsCheck(
  List<SftpServerCheck> sftpServers
) {
}
