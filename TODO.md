- iterate within root and its subfolders (with maxDepth) (display even empty folders)
- multiple sftp servers configuration
- [file moving bug](https://stackoverflow.com/questions/78951835/sftp-file-moving-not-working-but-file-renaming-works)
- docker composition with several sftp servers (password-based and key-based)
  [docker composition with sftp](https://github.com/atmoz/sftp?tab=readme-ov-file#using-docker-compose)
    [How to use atmoz/sftp? (in docker)](https://stackoverflow.com/questions/68459632/how-to-use-atmoz-sftp)
- add for all iterations maxdepth, see example for files 
```java
try (Stream<Path> paths = Files.find("/some/folder/path", /*maxDepth*/ 1) {
  paths.map(Path::toAbsolutePath)
    .map(Path::toString)
    .max(String.CASE_INSENSITIVE_ORDER) //take the last file
    .ifPresent(filePath -> handleFile(filePath));
}

```

### how to mount sftp to a local folder

### what is it SftpProgressMonitor

public void put(String src, String dst, SftpProgressMonitor monitor)

### spring provides its own sftp integration solution!

### sftp server logging

[Logging SFTP operations](https://github.com/atmoz/sftp/issues/86)

# Enable this for more logs
#LogLevel VERBOSE
https://github.com/atmoz/sftp/blob/master/files/sshd_config
