package com.example.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.nio.file.Paths;

public record SftpPath (
  String sftpPath,
  ChannelSftp sftpChannel
) {

  public String fileName() {
    if (sftpPath.contains("\\")) {
      throw new IllegalStateException("Cannot extract file name when it contains '\\'. Current path: " + sftpPath);
    }
    return FilenameUtils.getName(sftpPath);
  }

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
