Windows Setup
=============

1) Ensure you have Java JDK 6 installed (not tried with Java 7).
   
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

Out of the box, the code will not build until you add the jar file dependencies in the libs/ folder:

1) Select the "metadata-analyser" project, then click Project->Properties

2) Select "Java Build Path", then the "Libraries" tab, and add click "Add JARS..."

3) Select the two JARS in the libs/ folder, and OK out of the pop-up windows.

The project should now build.

Running
=======
1) Click the downarrow next to the green run symbol

2) Select "Run As"->Java Application

3) Eclipse will search for the initial main application, you'll need to point it towards "MetadataAnalyser".

4) The first run will probably fail as the program is expecting two directories: an input folder of files, and a HTML file to write the results too.

5) Click the downarrow next to the green run symbol and select "Run COnfigurations..."

6) Find the relevant "Java Application" configuration, then select the "Arguments" tab.

7) In the Program Arguments box, enter two paths: the input folder and an output HTML file, for example

    "C:\Projects\SPRUCE\London_Sept2012\Data\CombinedData" "C:\Projects\SPRUCE\London_Sept2012\Output\VB.html"

These only need to be separated by a space. Enclose them in ""s.

8) Click Apply and Run and you should get the specified VB.html output file which you can open in a web browser.

Supporting JAR files
====================
This code makes use of the following two JARS:

1) Gagawa HTML Generator library (http://code.google.com/p/gagawa/)

2) BEAM simple word cloud generator (https://github.com/pxuxp/Cloud)