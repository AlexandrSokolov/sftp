package com.example.sb.sftp.rest.service;

import com.example.sb.sftp.api.ConnectionCheckRestApi;
import com.example.sb.sftp.api.dto.ConnectionsCheck;
import com.example.sb.sftp.config.AppExternalConfiguration;
import com.example.sb.sftp.service.ConnectionCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionCheckRestService implements ConnectionCheckRestApi {

  @Autowired
  private ConnectionCheckService connectionCheckService;

  @Autowired
  private AppExternalConfiguration appExternalConfiguration;

  @Override
  public ConnectionsCheck connectionsCheck() {
    return new ConnectionsCheck(
      appExternalConfiguration.sftpServers().stream()
        .map(connectionCheckService::checkConnection)
        .toList()
      );
  }
}
