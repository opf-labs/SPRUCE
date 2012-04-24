Windows Installation
====================

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
   
6) Update the variables in config.py to suit your installation:
     TIKA		 = "<path to your Tika JAR file>"
     IGNORE		 = List of Metadata headings to ignore. Usually cause problems with formatting CSV
     MOUNTER	 = <Path to WinCDEmu batchmnt.exe>
     MOUNT_POINT = Free directory to mount an ISO image to, e.g. V:
     
Running
=======

There are 5 scripts: 1 configuration script, 3 scripts that do the bulk of the work, and 1 ISO file workflow script.

config.py
---------
Contains user settings.  Edit these values to ensure they reflect your system setup

ISORunner.py
------------
From the command line, type:
  cd <INSTALL_DIR>/python
  python ISORunner.py <input ISO file directory> <temp output directory> <summary CSV to create>
  
  e.g.
  python TikaRunner.py C:/SPRUCE/Data/Seven_Stories/ C:/SPRUCE/Output/Seven_Stories C:/SPRUCE/Output/SS_Summary.csv
  
This script will identify all .ISO files within the specified directory, and mount each file to make it's content available.
It will then run TikaRunner.py over the files contained in the ISO file, outputting the results to the temporary output folder. 
Each ISO's results are aggregated using CSVFormatter.py into a single CSV file.  
Once all ISOs have been processed, Summariser.py is used to summarise the data into one summary CSV file.

Note: ISORunner creates two sub directories under the <temp output directory>: /TikaRunner and /Aggregated. TikaRunner.py results files are placed in the first, the aggregated CSVs are placed in the second. Therefore the summary CSV can be placed in the same <temp output directory> without the problem mentioned in the CSVFormatter.py notes below.

TikaRunner.py
-------------
From the command line, type:
  python TikaRunner.py <input file directory> <temp output directory>
  
  e.g.
  python TikaRunner.py C:/SPRUCE/Data/Seven_Stories/ C:/SPRUCE/Output/Data/Seven_Stories
  
This script will run Tika over all files in the input file directory, providing information about expected time left

CSVFormatter.py
---------------
From the command line, type:
  python CSVFormatter.py <input file directory> <temp output directory> <output csv file>
  
  e.g.
  python CSVFormatter.py C:/SPRUCE/Data/Seven_Stories/ C:/SPRUCE/Output/Data/Seven_Stories C:/SPRUCE/Output/results.csv

Note: DO NOT save the output CSV file in the same directory as the temp output directory, otherwise if you repeatedly run the script it will attempt to aggregate this CSV into itself.
Note 2: The input file directory enables the filenames listed in the CSV to reflect the actual original file, rather than the temp output file

Summariser.py
-------------
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


   