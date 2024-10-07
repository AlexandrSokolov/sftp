package com.example.sftp.config;

import java.util.Optional;

public record SftpConfiguration (
  String host,
  Integer port,

  String username,
  String password,
  String identityFile,

  String home,
  String fileNameEncoding
)  {
  public SftpConfiguration {
    home = Optional.ofNullable(home).orElse(DEFAULT_SFTP_HOME);
  }

  public static final String KNOWN_HOSTS_PATH = "~/.ssh/known_hosts";
  public static final String DEFAULT_SFTP_HOME = "/";
}
