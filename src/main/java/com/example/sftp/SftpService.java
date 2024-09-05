package com.example.sftp;

import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.sftp.SftpConfiguration.KNOWN_HOSTS_PATH;

public class SftpService implements SftpApi {

  private final Session jschSession;
  private final ChannelSftp sftpChannel;

  private SftpService(Session jschSession, ChannelSftp sftpChannel) {
    this.jschSession = jschSession;
    this.sftpChannel = sftpChannel;
  }

  public static SftpApi instance(SftpConfiguration sftpConfiguration) {
    try {
      var jsch = new JSch();
      JSch.setLogger(new SftpDebugLogger());
      jsch.setKnownHosts(KNOWN_HOSTS_PATH);
      Session jschSession = jsch.getSession(
        sftpConfiguration.sftpUser(),
        sftpConfiguration.host(),
        sftpConfiguration.port());
      jschSession.setPassword(sftpConfiguration.sftpPassword());
      jschSession.connect();

      ChannelSftp sftpChannel = (ChannelSftp) jschSession.openChannel("sftp");
      sftpChannel.connect();

      return new SftpService(jschSession, sftpChannel);
    } catch (JSchException e) {
      throw new IllegalStateException(e);
    }
  }

  private static class SftpDebugLogger implements com.jcraft.jsch.Logger {

    private static final Logger logger = LogManager.getLogger(com.example.sftp.SftpService.class.getName());

    @Override
    public boolean isEnabled(int ignored) {
      return logger.isDebugEnabled();
    }

    @Override
    public void log(int ignored, String message) {
      logger.debug(() -> message);
    }
  }


  @Override
  public Stream<Map.Entry<String, Supplier<InputStream>>> filesStream(String sftpPath) {
    return allFolders(sftpPath).stream()
      .flatMap(folder -> uncheckCall(() -> sftpChannel.ls(folder)).stream()
        .filter(entry -> !entry.getAttrs().isDir())
        .map(ChannelSftp.LsEntry::getFilename)
        .map(fileName -> folder + "/" + fileName)
        .map(sftpFilePath -> new AbstractMap.SimpleEntry<>(
          sftpFilePath,
          () -> uncheckCall(() -> sftpChannel.get(sftpFilePath)))));
  }

  @Override
  public Stream<Map.Entry<String, List<String>>> dirsStream(String sftpPath) {
    return allFolders(sftpPath).stream()
      .map(folderPath -> new AbstractMap.SimpleEntry<>(
        folderPath,
        uncheckCall(() -> sftpChannel.ls(folderPath).stream()
          .filter(entry -> !entry.getAttrs().isDir())
          .map(ChannelSftp.LsEntry::getFilename)
          .toList())));
  }

  @Override
  public boolean fileExists(String sftpPath) {
    try {
      var attrs = sftpChannel.stat(sftpPath);
      return !attrs.isDir();
    } catch (SftpException e) {
      //todo it might ber permission issue, not only not existing
      return false;
    }
  }

  @Override
  public InputStream fileDownload(String sftpPath) {
    try {
      return sftpChannel.get(sftpPath);
    } catch (SftpException e) {
      sftpExceptionHandler(sftpPath, e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void fileUpload(String sftpPath, InputStream inputStream) {
    try {
      sftpChannel.put(inputStream, sftpPath);
    } catch (SftpException e) {
      sftpExceptionHandler(sftpPath, e);
    }
  }

  @Override
  public boolean dirExists(String sftpPath) {
    try {
      var attrs = sftpChannel.stat(sftpPath);
      return attrs.isDir();
    } catch (SftpException e) {
      //todo it might ber permission issue, not only not existing
      return false;
    }
  }

  @Override
  public void mkdir(String sftpPath) {
    try {
      var pathElements = Arrays.stream(sftpPath.split("/"))
        .filter(s -> !s.isEmpty())
        .toList();
      for (int index = 0; index < pathElements.size(); index++) {
        var subPath = String.join("/", pathElements.subList(0, index + 1));
        if (!dirExists(subPath)) {
          sftpChannel.mkdir(subPath);
        }
      }
      System.out.println();
    } catch (SftpException e) {
      sftpExceptionHandler(sftpPath, e);
    }
  }

  @Override
  public void remove(String sftpPath) {
    try {
      sftpChannel.rm(sftpPath);
    } catch (SftpException e) {
      sftpExceptionHandler(sftpPath, e);
    }
  }

  @Override
  public void removeDir(String sftpPath) {
    try {
      sftpChannel.rmdir(sftpPath);
    } catch (SftpException e) {
      sftpExceptionHandler(sftpPath, e);
    }
  }

  @Override
  public void mvFile(String sourcePath, String destinationPath) {
    try {
      if (!fileExists(sourcePath)) {
        throw new IllegalStateException("Source file does not exist: " + sourcePath);
      }
      var destinationPathAsList = Arrays.stream(destinationPath.split("/"))
        .filter(s -> !s.isEmpty())
        .toList();
      var destinationFolder = String.join("/",
        destinationPathAsList.subList(0, destinationPathAsList.size() - 1));
      if (!dirExists(destinationFolder)) {
        mkdir(destinationPath);
        //(octal) 0777 = 7*8^2 + 7*8 + 7 = 511 (decimal)
        sftpChannel.chmod(Integer.parseInt("777",8), destinationFolder);
      }
      var currentPermissions = Optional.ofNullable(sftpChannel.stat(destinationFolder))
        .map(SftpATTRS::getPermissionsString)
        .orElseThrow(() -> new IllegalStateException("Could not get stat for " + destinationFolder));
      if (!WRITABLE_PERMISSIONS_STR.equals(currentPermissions)) {
        throw new IllegalStateException(
          "The destination folder = `" + destinationFolder + "` has not enough permissions to move file into that folder."
            + "Current permissions: '" +  currentPermissions + "'."
            + "Expected permissions: " + WRITABLE_PERMISSIONS_STR);
      }
      sftpChannel.rename(sourcePath, destinationPath);
    } catch (SftpException e) {
      sftpExceptionHandler(destinationPath, e);
    }
  }

  @Override
  public void mvDir(String sourcePath, String destinationPath) {
    try {
      if (!dirExists(sourcePath)) {
        throw new IllegalStateException("Source dir does not exist: " + sourcePath);
      }
      if (dirExists(destinationPath)) {
        throw new IllegalStateException("Destination dir already exists: " + destinationPath + ". Delete it first.");
      }

      sftpChannel.rename(sourcePath, destinationPath);
    } catch (SftpException e) {
      sftpExceptionHandler(destinationPath, e);
    }
  }

  private void sftpExceptionHandler(String sftpPath, SftpException e) {
    if (PERMISSION_DENIED.equals(e.getMessage()))  {
      var pathElements = Arrays.stream(sftpPath.split("/")).toList();
      var path2permission = new HashMap<String, String>();
      for (int index = pathElements.size() - 1; index >= 0; index--) {
        var currentPath = String.join("/", pathElements.subList(0, index));
        try {
          var attr = sftpChannel.stat(currentPath);
          var permissions = attr.getPermissionsString();
          path2permission.put(currentPath, permissions);
        } catch (SftpException ignore) {
        }
      }
      if (path2permission.isEmpty()) {
        throw new IllegalStateException(
          "Users can't create files/dirs directly under their own home directory! Sftp path = " + sftpPath);
      }
      throw new IllegalStateException(
        String.format(
          "Permission denied. Sftp path: '%s' Permissions for existing paths: %s",
          sftpPath,
          toString(path2permission)));
    }
    throw new IllegalStateException("Sftp exception for sftp path: '" + sftpPath + "'. Cause: " + e.getMessage());
  }

  private List<String> allFolders(String sftpPath) {
    var allFolders = new ArrayList<String>();
    allFolders.add(sftpPath);
    accumulateFolders(sftpPath, allFolders);
    return allFolders;
  }

  private String toString(Map<?, ?> map) {
    return map.keySet().stream()
      .map(key -> key + "=" + map.get(key))
      .collect(Collectors.joining(", ", "{", "}"));
  }


  private void accumulateFolders(String path, List<String> list) {
    try {
      Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(path);

      for (ChannelSftp.LsEntry entry : files) {
        if (entry.getAttrs().isDir()
          && !".".equals(entry.getFilename())
          && !"..".equals(entry.getFilename())) {
          var subPath = path + "/" + entry.getFilename();
          list.add(subPath);
          accumulateFolders(subPath, list);
        }
      }
    } catch (SftpException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> T uncheckCall(Callable<T> callable) {
    try {
      return callable.call();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() {
    Optional.ofNullable(sftpChannel).ifPresent(ChannelSftp::disconnect);
    Optional.ofNullable(jschSession).ifPresent(Session::disconnect);
  }

  public ChannelSftp sftpChannel() {
    return sftpChannel;
  }
}
