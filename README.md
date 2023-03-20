# LWJGL2.9.3-Controller-Patch
Completely overhauls the LWJGL 2.9.3 Controllers API, including updated libraries, better compatibility, and better flexibility.


Required Libraries:

LWJGL 2.9.3 (Not confirmed for other LWJGL 2 versions) -> https://legacy.lwjgl.org/download.php.html

JInput 2.0.9 -> https://search.maven.org/search?q=a:jinput

XInput-Plugin-For-JInput 1.2.0 -> https://github.com/RalleYTN/XInput-Plugin-for-JInput

XInput-Wrapper 1.2.1 -> https://github.com/RalleYTN/XInput-Wrapper/tree/1.2.1

JNA 5.12.1 (Can be found under /libs/jars/jna/5.12.1 in the XInput-Wrapper repository)


Extra Source Files (Normal JInput source does not contain the sources of these plugins, but does contain the compiled binaries):

JInput OSX Plugin 2.0.9 -> https://search.maven.org/search?q=a:osx-plugin

JInput Linux Plugin 2.0.9 -> https://search.maven.org/search?q=a:linux-plugin

JInput Windows Plugin 2.0.9 -> https://search.maven.org/search?q=a:windows-plugin


Installation:
Replace the given classes into their respective packages within LWJGL. Import libraries nominally. If you would like to apply the "Extra Source Files", simply add the contents of the respective plugins' source jars to the JInput source jar. Editing the Javadoc is significantly more difficult, but doesn't seem terribly necessary.


Usage:
Read in-code documentation via applying the given source jar.
