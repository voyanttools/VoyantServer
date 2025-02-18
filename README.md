## Voyant Server ##

Voyant Server is a web application launcher for Voyant Tools – it makes it easy to run a stand-alone instance of Voyant Tools on your local machine, which has several potential advantages (performance, security, privacy, reliability, etc.) compared to the [hosted version](https://voyant-tools.org).

## Installation ##

1. Voyant Server requires a specific version of Java, 11, to be installed. If you have multiple versions of Java installed, you must ensure that 11 is enabled. Java 11 can be downloaded from the following locations:
   * [Adoptium](https://adoptium.net/temurin/releases/?version=11)
   * [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

2. Go to the [latest release](https://github.com/voyanttools/VoyantServer/releases/latest) and download the VoyantServer zip file. Unzip the file (be sure to actually extract the contents into a real directory) and double-click on the VoyantServer.jar file (on Macs you may need to Ctrl-click on the VoyantServer.jar, select open and confirm the opening – this is because of security precautions in OS X).

3. Running the VoyantServer.jar will open a controller window that automatically starts the server and opens the website in a new browser tab. The controller window has functionality to stop the server, re-open the website, view logs, and change the port and maximum memory used by the server.

For more detailed instructions, see the [tutorial in the wiki](https://github.com/voyanttools/VoyantServer/wiki/VoyantServer-Tutorial).

## Troubleshooting ##

### Java Version ###

Voyant Server requires Java 11 and may not work with other versions.

### Mac OS ###

If you receive an error message on Mac OS:
> The Java JAR file “VoyantServer.jar” could not be launched. Check the Console for possible error messages.

You may open VoyantServer via the terminal as a workaround using: `java -jar VoyantServer.jar`

NB: you must run the above command from the directory into which you extracted the JAR file.


## License ##
Voyant Server is released under the same license as JettyDesktop, the GNU General Public License v3.0 (see license-GPL3.txt in this directory).
