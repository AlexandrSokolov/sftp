package com.example.sb.sftp.service;


import com.example.sb.sftp.api.dto.SftpServer;
import com.example.sb.sftp.api.dto.SftpServerCheck;
import com.example.sftp.SftpService;
import com.example.sftp.config.SftpConfiguration;
import org.springframework.stereotype.Service;

@Service
public class ConnectionCheckService {

  public SftpServerCheck checkConnection(final SftpConfiguration sftpConfiguration) {
    try (SftpService sftpService = (SftpService) SftpService.instance(sftpConfiguration)) {
      if (sftpService.sftpChannel().isConnected()) {
        return new SftpServerCheck(new SftpServer(sftpConfiguration), true);
      }
      return new SftpServerCheck(new SftpServer(sftpConfiguration), false);
    } catch (Exception e) {
      return new SftpServerCheck(
        new SftpServer(
          sftpConfiguration.host(),
          sftpConfiguration.port(),
          sftpConfiguration.username()),
        false
      );
    }
  }
}
