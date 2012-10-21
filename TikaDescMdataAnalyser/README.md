Windows Setup
=============

1) Install the latest Java JDK 7 (at least update 6).
   
   Download from http://www.oracle.com/technetwork/java/javase/downloads/index.html
   
   Follow default installation
   
   Check it works (from Command Line), type:
   
    java -version
    
2) Install Java Eclipse

   Download from http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junor
   (Get the appropriate version for your system)

Maven should be setup be default in Eclipse.

Eclipse Setup
=============

Having installed Java and Eclipse, you'll need to import the code as a Maven project.

1) File->Import->Maven->Existing Maven Projects. Next.

2) Select the root project directory, then ensure that the project pom is selected. Finish

Also ensure that you are using Java JDK 7 JRE:

1) Window -> Preferense -> Java -> Installed JREs.

2) Add JDK 1.7 if it's not listed and make sure it is selected.

Finally, for the Maven build you need to add the following <profile> to your Maven settings.xml file:

    <profile>
		<id>javafx</id>
		<activation>
		    <activeByDefault>true</activeByDefault>
		</activation>
		<properties>
		    <fx.home>C:/Program Files/Java/jdk1.7.0_07/</fx.home>
		</properties>
    </profile>
  
(ensure <fx.home> points to the location of your Java JDK 7 install)    

Running
=======
1) Click the downarrow next to the green run symbol

2) Select "Run As"->Java Application

3) Eclipse will search for the initial main application, you'll need to point it towards "FileAnalyserUI".

4) Click OK, and it will launch the GUI.

The GUI
=======

The GUI asks for two things:

1) a Directory of files to analyser.  The button to the right can be use to help navigate to a directory.

2) a HTML file to save the results to.  The button to the right can be used to help find/create a file to save to in an appropriate directory.

Once these two things are filled in, click "Analyse" and let Tika do it's magic. Output will be in the specified HTML file, which can be opened in your web browser.

Note: the code has not been robustly tested. Odd things may happen, e.g. if you do not specify a directory for example. It's all part of the fun.

Descriptive Metadata Configuration Files
----------------------------------------

The program makes use of a configuration files which list the Descriptive Metadata fields relevant for each mime-type. These configuration files are listed in a properties file.

The program has a default set of configuration files for image/*, text/*, and application/* (along with a default properties file referencing these) in the "resources" directory.

However, the user is able to override these configurations with their own configuration by providing new files and selecting the appropriate .properties file through the GUI option.

A .properties file (text file with a name ending in ".properties") with each line having the format:

    <top_level mime-type>=<directory of configuration file>

For example:

    image=c:/configuration/desc_mdata_image.txt
    text=c:/configuration/desc_mdata_text.txt
    application=c:/configuration/desc_mdata_text.txt
    
(all file paths should use forward slashes ("/"))
    
Configuration files should simple list (one per line), the key descriptive metadata fields required, for example:

    Artist
    Author
    Copyright
    creator
    dc:creator
    
These keys are the ones fields that Tika will identify.  To find out what fields there are, try running Tika on a file of interest.

To Build the EXE Installer
==========================

The Maven build file is also set up to build an EXE installer.  This is a self-contained application which includes all the dependencies (i.e. Tika) and even a JRE. It shouldn't require admin rights to run the installer.

To build this, run as Maven build with goals: clean compile package install

You will need to make sure you have Inno Setup 5 or later (http://www.jrsoftware.org/isdl.php), and this must be set in your PATH environment variable.

The resulting distributable executable sits in <project>/target/deploy/bundles/

Be warned, it is about 50MB (as it includes all of Tika's Dependencies and a JRE).

Supporting JAR files
====================
This code makes use of the following two JARS:

1) Gagawa HTML Generator library (http://code.google.com/p/gagawa/)

2) BEAM simple word cloud generator (https://github.com/pxuxp/Cloud)