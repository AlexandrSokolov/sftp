package com.example.sftp;

public interface SftpConfiguration {

  String KNOWN_HOSTS_PATH = "~/.ssh/known_hosts";

  String host();
  Integer port();

  String sftpUser();
  String sftpPassword();
}
