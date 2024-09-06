- docker composition with several sftp servers (password-based and key-based)
  [docker composition with sftp](https://github.com/atmoz/sftp?tab=readme-ov-file#using-docker-compose)
    [How to use atmoz/sftp? (in docker)](https://stackoverflow.com/questions/68459632/how-to-use-atmoz-sftp)


### external configuration 
 move to spring indepenent project, leave only sftp-related condigs

```bash
$ java -jar myproject.jar --spring.config.import=\
    classpath:datasource.properties,\
    classpath:mysql-properties.properties,\
    optional:file:./cloud-deployment.properties,\
    classpath:test-properties/
```

pass in docker composition!

### move all the configurations into spring config demo project, leave in this project only sftp-specific

### how to mount sftp to a local folder

### what is it SftpProgressMonitor

public void put(String src, String dst, SftpProgressMonitor monitor)

### spring provides its own sftp integration solution!

### sftp server logging

[Logging SFTP operations](https://github.com/atmoz/sftp/issues/86)

# Enable this for more logs
#LogLevel VERBOSE
https://github.com/atmoz/sftp/blob/master/files/sshd_config
