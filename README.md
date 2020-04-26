# goa systems phonebook
## What is the phonebook
Imagine you start in a new company, know nobody and have to find a number, a email address or some ohter information of a coworker. This is where the phonebook tool comes in handy: You can search all employees, just a specific name or a specific company. The live search shows only contacts that meet the search criteria.
## Technology
This is a Java 8 compatible application that can run on all Java platform versions from 1.8 to the latest available. It uses the Spring Boot framework and React for data presentation. The endpoints can be connected to other programs as well and provide internal company information.
## Future
Authentication, feature levels and designs are planed but not yet implemented because this application is developed by me in my spare time.
## Development
The application can be connected to [Microsoft Active Directory](https://docs.microsoft.com/en-us/windows-server/identity/ad-ds/active-directory-domain-services) or a [Samba 4](https://www.samba4.org) domain. For a future release a local database is planned.
### Development environment
This application is developed using [Eclipse](https://www.eclipse.org) and the required plugins:
* [Buildship Gradle integration](https://marketplace.eclipse.org/content/buildship-gradle-integration)
* [Spring Tool suite](https://marketplace.eclipse.org/content/spring-tools-4-aka-spring-tool-suite-4)
* [EGit](https://marketplace.eclipse.org/content/egit-git-integration-eclipse)
### Configuration
#### Run configuration
A example launcher for the [Eclipse IDE](https://www.eclipse.org) is provided in the folder "launchers". The file is called "phonebook.launch.tpl". It can be copied to a new file and the new file should be renamed to something like "phonebook.launch". Important is, that the launcher ends with ".launch". It can then be configured as follows. Right click on the new file "phonebook.launch" and in the opening menu choose the following points.

![Dialogue selection](/doc/run_config1.png)

The parameters can then be set in the dialogue.

![Settings dialogue](/doc/run_config2.png)

Parameters that are required to run the application properly are:

```
ldap.url = ldaps://example.com:636
ldap.domain = dom.example.com
ldap.basedn = DC=dom,DC=example,DC=com
ldap.username = username
ldap.word = password
ldap.usergroup = CN=ExampleGroup,CN=Users,DC=dom,DC=example,DC=com
```

They can also be stored in the file "application.properties" in the directory "src/main/resources". This way they do not have to be provided via launcher or command line. This should only be done using internal applications or on the developer machine. Do not share server configuration and credentials in public repositories.

Username and password have to be provided because of the anonymous access to the LDAP directory. User authentication is not yet implemented and not required to access the phonebook.
#### Test configuration
Development is often done using tests. The configuration for LDAP access has to be available there too. It can be loaded again from the configuration file or from command line parameters. The next screenshot shows how to configure Spring Boot parameters for JUnit tests.

![Settings dialogue](/doc/run_config3.png)

To use this example the file "TestLdap.launch.tpl" in the directory "launchers" has to be copied to "TestLdap.launch". Then it can be configured over the run configurations dialogue shown in the screenshot above.

### React

The react libraries are stored locally and can be downloaded using the Gradle task "downloadReact". The used React major version is defined in the ext block in the "build.gradle" file.
```
ext {
	reactmajorversion = 16
}
```
