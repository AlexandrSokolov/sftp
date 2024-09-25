### todo:

how to fix this structure:
```yaml
sftp-servers:
# connect via ssh to each sftp hosts, to add host key to `known_hosts`
  sftp-servers:
    - host: localhost
      port: 22
      username: foo
      password: pass
      home: ./
```
to:
```yaml
sftp-servers:
# connect via ssh to each sftp hosts, to add host key to `known_hosts`
  - host: localhost
    port: 22
    username: foo
    password: pass
    home: ./
```

### how to mount sftp to a local folder

### what is it SftpProgressMonitor

public void put(String src, String dst, SftpProgressMonitor monitor)

### spring provides its own sftp integration solution!
