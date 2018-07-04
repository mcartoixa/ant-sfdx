# ant-sfdx
Ant tasks that encapsulate the Salesforce DX CLI

## Development

### Prerequisites
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  * Ubuntu:
    * `sudo add-apt-repository ppa:webupd8team/java`
    * `sudo apt update`
    * `sudo apt install oracle-java8-installer`
    * Set the `JAVA_HOME` environment variable in your `~/.profile` file: `export JAVA_HOME="/usr/lib/jvm/java-8-oracle"`
  * Windows:
    * Execute the installer.
    * Set the `JAVA_HOME` environment variable (e.g. `C:\Program Files\Java\jdk1.8.0_65`).
* [Ant 1.9.x](https://ant.apache.org/manual/install.html)
  * Ubuntu:
    * `wget http://wwwftp.ciril.fr/pub/apache//ant/binaries/apache-ant-1.9.12-bin.tar.gz`
    * `sudo tar -xf apache-ant-1.9.12-bin.tar.gz -C /usr/local/share`
    * Add the `ANT_HOME` environment variable in your personal environment file`.env` at the root of the project:
```
ANT_HOME=/usr/local/share/apache-ant-1.9.12
```
  * Windows:
    * Unzip the archive in the folder of your choice.
    * Add the `ANT_HOME` environment variable in your personal environment file`.env` at the root of the project:
```
ANT_HOME=C:\Program Files\apache-ant-1.9.12
```
* [PMD 6.x](https://pmd.github.io/pmd-6.4.0/pmd_userdocs_getting_started.html)
  * Ubuntu:
    * Unzip the archive in the folder of your choice.
    * Set the `PMD_HOME` environment variable in your `~/.profile` file: `export PMD_HOME="$HOME/pmd-bin-6.4.0"`
  * Windows:
    * Unzip the archive in the folder of your choice.
    * Set the `PMD_HOME` environment variable to this folder (e.g. `C:\Program Files\pmd`).
* Perl (unused on Windows) :
  * Ubuntu:
    * `sudo apt install perl`

### Environment
* [NetBeans](https://netbeans.org/downloads/) 8.2:
  * Initialize the *ANT_HOME* ant variable (cf. prerequisites).
  * [EditorConfig plugin](https://github.com/welovecoding/editorconfig-netbeans)
