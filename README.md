# ant-sfdx
Ant tasks that encapsulate the Salesforce DX CLI

## Development

### Prerequisites
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  * MacOS:
    * Execute the installer.
    * Set the `JAVA_HOME` environment variable in your `~/.zprofile` file: `export JAVA_HOME="``/usr/libexec/java_home -v 1.8``"`
  * Ubuntu:
    * `sudo add-apt-repository ppa:webupd8team/java`
    * `sudo apt update`
    * `sudo apt install oracle-java8-installer`
    * Set the `JAVA_HOME` environment variable in your `~/.profile` file: `export JAVA_HOME="/usr/lib/jvm/java-8-oracle"`
  * Windows:
    * Execute the installer.
    * Set the `JAVA_HOME` environment variable (e.g. `C:\Program Files\Java\jdk1.8.0_65`).
* [Ant 1.10.x](https://ant.apache.org/manual/install.html)
  * Ubuntu:
    * `sudo apt install ant`
    * Set the `ANT_HOME` environment variable in your `~/.profile` file: `export JAVA_HOME="/usr/share/ant"`
  * Windows:
    * Unzip the archive in the folder of your choice.
    * Set the `ANT_HOME` environment variable to this folder (e.g. `C:\Program Files\apache-ant-1.10.3`).
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
* [Visual Studio Code](https://code.visualstudio.com/) :
  * Extension [EditorConfig](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)
