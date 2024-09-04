package com.example.sftp;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface SftpApi extends AutoCloseable {

  Stream<Map.Entry<String, Supplier<InputStream>>> filesStream(String sftpPath);

  void close();
}
