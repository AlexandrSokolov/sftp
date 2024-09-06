Estimation effort: 3PMD

Includes:
- sftp communication
- sftp configurations for spring project
- integration tests with sftp docker containers

1. [Copy maven dependencies](pom.xml)
2. [Copy classes from `config` package](src/main/java/com/example/sftp/config)
3. Enable `AppExternalConfiguration` via [`@EnableConfigurationProperties` in your project DI configuration](src/main/java/com/example/sftp/config/SftpDiConfiguration.java)

    Suppose it is `MyProjectDiConfiguration`:
    ```java
    @Configuration
    @ComponentScan("your.project.root.package")
    @EnableConfigurationProperties(AppExternalConfiguration.class)
    public class MyProjectDiConfiguration {
    }
    ```
4. Add `sftp` package in your project, add [`SftpApi`](src/main/java/com/example/sftp/SftpApi.java) and [`SftpService`](src/main/java/com/example/sftp/SftpService.java) into this package
5. Go through methods in `SftpApi`, remove not needed ones
6. Update packages in `SftpService`, remove not needed code
7. Add sftp integration tests classes, update their packages:
    - [SftpDockerConstants](src/test/java/com/example/sftp/docker/SftpDockerConstants.java)
    - [TestSftpContainer](src/test/java/com/example/sftp/docker/TestSftpContainer.java)
    - [SftpApiTest](src/test/java/com/example/sftp/SftpApiTest.java)
8. It might happen that you do not use the `MyProjectDiConfiguration` in your tests to avoid, for instance db-beans loading.

    Create your di configuration class, list the packages, required for test cases, 
    and enable `AppExternalConfiguration` via `@EnableConfigurationProperties`:
    ```java
    /**
     * This DI configuration allows to test services, that do not depend on datasource,
     *  without db-specific components loading
     */
    @Configuration
    @ComponentScan({
      "com.my.project.client",
      "com.my.project.config",
      "com.my.project.sftp" })
    @EnableConfigurationProperties(AppExternalConfiguration.class)
    public class TestWithoutDbDiConfiguration {
    }
    ```
   Set `TestWithoutDbDiConfiguration` instead of `SftpDiConfiguration` in `SftpApiTest`
9. Add `external.config.yaml` in the test resources, or merge it with existing one
10. Remove the configuration of extra sftp servers, that is not needed. 
11. Import the configuration file in [`application.yaml` in test resources](src/test/resources/application.yaml):
    ```yaml
    spring:
      config:
        import: classpath:external.config.yaml
    ```
12. Add ssh key pair only for a single sftp
13. Add [`testcontainers/file.txt`](src/test/resources/testcontainers/file.txt) exactly on the same path  
14. Run the following sftp integration test:
    ```java
    @SpringBootTest
    @ContextConfiguration(classes = {
      TestWithoutDbDiConfiguration.class,
      TestSftpContainer.class})
    @Testcontainers
    public class SftpApiTest {
    
      private static final Logger logger = LogManager.getLogger(SftpApiTest.class.getName());
    
    
      @Autowired
      private AppExternalConfiguration appExternalConfiguration;
    
      private SftpConfiguration sftpConfiguration;
    
      @BeforeEach
      public void init() {
        Assertions.assertNotNull(appExternalConfiguration);
        Assertions.assertNotNull(appExternalConfiguration.getSftpServers());
        sftpConfiguration = appExternalConfiguration
          .getSftpServers()
          .getFirst();
      }
    
      @Test
      public void test() {
        try (SftpApi sftpApi = SftpService.instance(sftpConfiguration)) {
         Assertions.assertTrue(sftpApi.fileExists("/upload/testcontainers/file.txt"));
        }
      }
    }
    ```
15. Rename `SftpApiTest` into `SftpApiIT` to avoid its execution each time when you build the project.