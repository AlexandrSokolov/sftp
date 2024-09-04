package com.example.sftp;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
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

  private List<String> allFolders(String sftpPath) {
    var allFolders = new ArrayList<String>();
    allFolders.add(sftpPath);
    accumulateFolders(sftpPath, allFolders);
    return allFolders;
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
}
