package com.example.sftp.config;

public class SftpConfiguration {

  public static final String KNOWN_HOSTS_PATH = "~/.ssh/known_hosts";

  private String host;
  private Integer port;

  private String username;
  private String password;
  private String identityFile;

  private String home;
  private String fileNameEncoding;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getIdentityFile() {
    return identityFile;
  }

  public void setIdentityFile(String identityFile) {
    this.identityFile = identityFile;
  }

  public String getHome() {
    return home;
  }

  public void setHome(String home) {
    this.home = home;
  }

  public String getFileNameEncoding() {
    return fileNameEncoding;
  }

  public void setFileNameEncoding(String fileNameEncoding) {
    this.fileNameEncoding = fileNameEncoding;
  }
}
