package com.example.sftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("app")
public class AppExternalConfiguration {

  NotificationConfiguration notification;

  //system name -> auth mappings
  Map<String, AuthConfiguration> systemsAuth;

  private List<SftpConfiguration> sftpServers;

  public NotificationConfiguration getNotification() {
    return notification;
  }

  public void setNotification(NotificationConfiguration notification) {
    this.notification = notification;
  }

  public Map<String, AuthConfiguration> getSystemsAuth() {
    return systemsAuth;
  }

  public void setSystemsAuth(Map<String, AuthConfiguration> systemsAuth) {
    this.systemsAuth = systemsAuth;
  }

  public List<SftpConfiguration> getSftpServers() {
    return sftpServers;
  }

  public void setSftpServers(List<SftpConfiguration> sftpServers) {
    this.sftpServers = sftpServers;
  }
}
