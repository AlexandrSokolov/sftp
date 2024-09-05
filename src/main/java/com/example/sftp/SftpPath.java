package com.example.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.InputStream;
import java.nio.file.Paths;

public record SftpPath (
  String sftpPath,
  ChannelSftp sftpChannel
) {


  public String parentPath() {
    return Paths.get(sftpPath).getParent().toString();
  }

  public InputStream fileInputStream() {
    try {
      return sftpChannel.get(sftpPath);
    } catch (SftpException e) {
      throw new IllegalStateException(e);
    }
  }

  public boolean isFile() {
    return false;
  }

  public boolean isDirectory() {
    return false;
  }

}
