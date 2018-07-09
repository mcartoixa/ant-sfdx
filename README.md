[![Build status](https://travis-ci.org/mcartoixa/ant-sfdx.svg?branch=master)](https://travis-ci.org/mcartoixa/ant-sfdx)
# ant-sfdx
Ant tasks that encapsulate the Salesforce DX CLI

## Development

### Prerequisites
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  * Ubuntu:
    * `sudo add-apt-repository ppa:webupd8team/java`
    * `sudo apt update`
    * `sudo apt install oracle-java8-installer`
    * Set the `JAVA_HOME` environment variable in your `~/.profile` file: `export JAVA_HOME="/usr/lib/jvm/java-8-oracle"`.
  * Windows:
    * Execute the installer.
* Perl (unused on Windows) :
  * Ubuntu:
    * `sudo apt install perl`

Alternatively you can set your environment variables in a local `.env` file, e.g.:
```
JAVA_HOME=/usr/lib/jvm/java-8-oracle
```

### Environment
* [NetBeans](https://netbeans.org/downloads/) 8.2:
  * [EditorConfig plugin](https://github.com/welovecoding/editorconfig-netbeans)
  * Execute the build script at least once before opening NetBeans.
