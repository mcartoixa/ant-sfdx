# ant-sfdx
[![Build status](https://travis-ci.org/mcartoixa/ant-sfdx.svg?branch=master)](https://travis-ci.org/mcartoixa/ant-sfdx)
[![Build Status](https://dev.azure.com/mcartoixa/ant-sfdx/_apis/build/status/ant-sfdx-CI)](https://dev.azure.com/mcartoixa/ant-sfdx/_build/latest?definitionId=1)
[![Code coverage](https://codecov.io/gh/mcartoixa/ant-sfdx/branch/master/graph/badge.svg)](https://codecov.io/gh/mcartoixa/ant-sfdx)
[![BCH compliance](https://bettercodehub.com/edge/badge/mcartoixa/ant-sfdx?branch=master)](https://bettercodehub.com/)

Ant tasks that encapsulate the Salesforce DX CLI

## Rationale

One of the promises of the Salesforce DX CLI is to allow easy Continuous Integration on Salesforce projects. There are many samples
of how to achieve that on platforms like [CircleCI](https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_ci_circle.htm),
[Jenkins](https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_ci_jenkins.htm)
or [Travis CI](https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_ci_travis.htm).

The direct use of the CLI for CI integration seems to be alright for simple scenarii but there are several limitations to it:
* the build is difficult to reproduce locally.
* the build is tied to the shell technology provided by the platform, be it *bash*, *PowerShell* or (gasp!) *Windows Command*.
* the CLI commands outputs can be very verbose making the build log difficult to interpret when there is a problem.
* the real fun begins when you have an advanced scenario where the execution of a command depends on the result of another command...
  * the CLI commands have a JSON output option to allow interpretation, but then you lose the regular output.
  * there is no easy way to interpret JSON in, say, *bash* (try [jq](https://stedolan.github.io/jq/) if you want to do just that).
  * the JSON to be interpreted can be (and is often) inconsistent: the *result* property can be an object or an array for the same command depending on the status...

[Apache Ant](http://ant.apache.org/) is not for everyone. You have to understand it in order to respect it (like any technology) and you must not be allergic to XML.
But if you can handle it, this project allows you to:
* repeat your build locally, cross-platform.
* significantly clean the output of your build.
* handle complex scenarii where the execution of a command depends on the result of another command:
  * with regular Ant [properties](http://ant.apache.org/manual/properties.html) and [conditions](http://ant.apache.org/manual/Tasks/condition.html).
  * with native JSON support in Ant [scripts](http://ant.apache.org/manual/Tasks/script.html) if necessary.
* easily integrate third-party Java based tools like [PMD](https://pmd.github.io/) or [ApexDoc](https://github.com/SalesforceFoundation/ApexDoc).
* read your build more easily (if you can pass past [the angle bracket tax](https://blog.codinghorror.com/xml-the-angle-bracket-tax/)). What's more readable:
  * `sfdx force:org:create -v HubOrg -s -f config/project-scratch-def.json -a ciorg --wait 2` ?
  * or `<sfdx:force-org-create targetdevhubusername="HubOrg" defaultusername="true" definitionfile="config/project-scratch-def.json" alias="ciorg" wait="2" />` ?

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
      <ivy pattern="https://github.com/[organisation]/[module]/releases/download/v[revision]/ivy.xml" />
      <artifact pattern="https://github.com/[organisation]/[module]/releases/download/v[revision]/[artifact].[ext]" />
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
