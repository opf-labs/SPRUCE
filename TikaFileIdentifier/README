Windows Installation
--------------------

1) Ensure you have Java JDK 6 installed (not tried with Java 7).
   Download from http://www.oracle.com/technetwork/java/javase/downloads/index.html
   Follow default installation
   
   Check it works (from Command Line), type:
   java -version
   
2) Ensure you have Python 2.7.3 installed
   http://www.python.org/ftp/python/2.7.3/python-2.7.3.msi
   Follow default installation
   
3) Copy scripts and Java files to an appropriate installation directory <INSTALL_DIR>
   (e.g. C:/SPRUCE/TikaFileIdentifier)
   
4) If you don't have it, download the Tika JAR file to <INSTALL_DIR>/tika
   Tika available from: http://tika.apache.org/download.html

5) Compile the Java Tika wrapper.  From command line, type:
   cd <INSTALL_DIR>/java
   javac -cp ../tika/tika-app-1.1.jar TikaWrapper.java
   (Tika jar may be named differently)
   
6) Update the following variables to suit your installation:
   TikaRunner.py:
     TIKA		= "<path to your Tika JAR file>"
     JAVA_FILES = "<path to the java directory in your INSTALL_DIR>"
     
Running
-------

The 3 scripts are run one at a time, in order.

TikaRunner.py:
From the command line, type:
  cd <INSTALL_DIR>/python
  python TikaRunner.py <input file directory> <temp output directory>
  
  e.g.
  python TikaRunner.py C:/SPRUCE/Data/Seven_Stories/ C:/SPRUCE/Output/Data/Seven_Stories
  
  This script will run Tika over all files in the input file directory, providing information about expected time left

CSVFormatter.py:
From the command line, type:
  python CSVFormatter.py <input file directory> <temp output directory> <output csv file>
  
  e.g.
  python CSVFormatter.py C:/SPRUCE/Data/Seven_Stories/ C:/SPRUCE/Output/Data/Seven_Stories C:/SPRUCE/Output/results.csv

Note: DO NOT save the output csv file in the same directory as the temp output directory, otherwise if you repeatedly run the script it will attempt to aggregate this CSV into itself.
Note 2: The input file directory enables the filenames listed in the CSV to reflect the actual original file, rather than the temp output file

Summariser.py:
From the command line, type:
  python Summariser.py <output csv file>
  or
  python Summariser.py <output csv file> <summarised csv file>
  
  e.g.
  python Summariser.py C:/SPRUCE/Output/results.csv
  or
  python Summariser.py C:/SPRUCE/Output/results.csv C:/SPRUCE/Output/summary.csv
  
The first approach just outputs summary statistics to the command line.
The second approach also outputs the summary into a single row in a CSV file


   