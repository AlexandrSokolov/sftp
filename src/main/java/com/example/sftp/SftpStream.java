package com.example.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import java.util.Optional;
import java.util.stream.Stream;

public class SftpStream<T> extends AbstractSftpStream<T> {

  private final Stream<T> originalStream;

  private final Session jschSession;

  private final ChannelSftp sftpChannel;

  public SftpStream(
    final Stream<T> originalStream,
    final Session jschSession,
    final ChannelSftp sftpChannel) {
    super(originalStream);
    this.originalStream = originalStream;
    this.jschSession = jschSession;
    this.sftpChannel = sftpChannel;
  }

  @Override
  public Stream<T> onClose(Runnable runnable) {
    try {
      return originalStream.onClose(runnable);
    } finally {
      closeConnections();
    }
  }

  @Override
  public void close() {
    closeConnections();
  }

  private void closeConnections() {
    Optional.ofNullable(sftpChannel).ifPresent(ChannelSftp::disconnect);
    Optional.ofNullable(jschSession).ifPresent(Session::disconnect);
  }
}
