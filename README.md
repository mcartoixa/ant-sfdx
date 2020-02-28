# ant-sfdx
[![Build status](https://travis-ci.org/mcartoixa/ant-sfdx.svg?branch=master)](https://travis-ci.org/mcartoixa/ant-sfdx)
[![Build Status](https://dev.azure.com/mcartoixa/ant-sfdx/_apis/build/status/ant-sfdx-CI)](https://dev.azure.com/mcartoixa/ant-sfdx/_build/latest?definitionId=1)
[![BCH compliance](https://bettercodehub.com/edge/badge/mcartoixa/ant-sfdx?branch=master)](https://bettercodehub.com/)

Ant tasks that encapsulate the Salesforce DX CLI

## Usage

The current documentation (`master` branch) for these tasks can be found at https://mcartoixa.github.io/ant-sfdx/.

These tasks can be downloaded directly [from the releases section](https://github.com/mcartoixa/ant-sfdx/releases) or with
a dependency manager like [Apache Ivy](http://ant.apache.org/ivy/) (preferred). You will need the following settings in your
`ivysettings.xml` file:
```xml
<ivysettings>
  <!-- GET is required by github: HEAD returns 403 -->
  <settings defaultResolver="default" httpRequestMethod="GET" />

  <resolvers>
    <url name="github">
      <ivy pattern="https://github.com/[organisation]/[module]/releases/download/[revision]/ivy.xml" />
      <artifact pattern="https://github.com/[organisation]/[module]/releases/download/[revision]/[artifact].[ext]" />
    </url>
  </resolvers>
  <modules>
    <module organisation="mcartoixa" name="*" resolver="github" />
  </modules>
</ivysettings>
```

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

### Build
The build script at the root of the project can be used to perform different tasks:
* `./build.sh clean`: cleans the workspace.
* `./build.sh compile`: compiles the project.
* `./build.sh test`: executes unit tests.
* `./build.sh analysis`: source code analysis (with [PMD](https://pmd.github.io/)).
* `./build.sh package`: generates deployment packages.
* `./build.sh build`: `compile` + `test` + `analysis`.
* `./build.sh rebuild`: `clean` + `build`.
* `./build.sh release`: `rebuild` + `package`.

### Environment
* [NetBeans](https://netbeans.apache.org/download/index.html) 11.2:
  * [EditorConfig plugin](https://github.com/welovecoding/editorconfig-netbeans)
  * Execute the build script at least once before opening NetBeans.
