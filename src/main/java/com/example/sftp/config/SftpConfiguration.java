package com.example.sftp.config;

public record SftpConfiguration (
  String host,
  Integer port,

  String username,
  String password,
  String identityFile,

  String home,
  String fileNameEncoding
)  {

  public static final String KNOWN_HOSTS_PATH = "~/.ssh/known_hosts";
}
