Appraisomatic
peter.cliff@bl.uk

A GUI that wraps some scripts identifying and describing an otherwise unknown collection
of predominantly textual documents using Apache Tika to extract metadata and full-text,
create some word clouds for the documents and the collection as a whole and finally 
collate some of that metadata into a HTML summary report.

The initial work for this was at the SPRUCE London Workshop (see: http://bit.ly/spruce-london) 
and I've pottered with it since - mostly because I wanted to try Griffon/Groovy.

For more information on the problem/solution see here:

http://wiki.opf-labs.org/display/SPR/Extracting+and+aggregating+metadata+with+Apache+Tika

The code is an ugly mix of Groovy and Java (in the same methods in some cases!) cut and paste
from the SPRUCE work with some nasty hacking along the way so plenty of room for improvement
to put it politely!

** USAGE **

To build the project you need Griffon and Groovy. It should then
be possible to check out and run with: 

> griffon run-app

Alternatively, I've put the executable jar here:

http://dl.dropbox.com/u/1806257/appraisomatic.jar

where the only requirement is Java and run as usual with:

> java -jar appraisomatic.jar

To keep the GitHub files small, I've removed the library files too. To build this you'll 
need the three library files that are bundled here:

http://dl.dropbox.com/u/1806257/appraisomatic_lib.zip.jar

These are argo, beam-cloud and the directory characteriser jar built from the code developed
at the workshop. The dc jar includes Tika.

Told you it was ugly! :-)

Once running you'll get a nice simple window and you basically click each button from the top 
down:

1) Set Collection Path - this is the path to the source collection files. While appraisomatic 
does NOT write to these files you're advised to try it on a subset of your files, a copy or 
using a read-only disk partition.

2) Set Output Path - this is the path to the output directory. This should be something like: 
C:\Scratch. Appraisomatic writes all the output files to this directory - these are:

C:\Scratch\COLNAME (where COLNAME is the last directory in the path given in 1).
  - collection_description.html => the summary report.
  - collated.txt => the collated output of all the other n-gram files.
  - unicolcloud.html => 30 most popular words found in collated.txt
  - bicolcloud.html => 30 most popular pairings of words found in collated.txt

C:\Scratch\COLNAME\FILE_meta
  - tika_metadata.json => Apache Tika generated metadata in JSON format.
  - tika_metadata.xml => Apache Tika generated metadata in XML format.
  - sha256.txt => The SHA256 of the original file.
  - fulltext.txt => The fulltext of the file extracted by Tika.
  - unicloud.html => 30 most popular words found in fulltext.txt as HTML.
  - bicloud.html => 30 most popular words found in fulltext.txt as HTML.
  
3) Process - starts execution. To stop it hit Quit. (Existing output files are not
deleted if you do). Or wait to the end and then navigate to the Output Path and 
enjoy! :-)

The files created in the Output Path are then suitable for further processing via
the scripts created as part of the SPRUCE work - eg. duplicate detection script.

See:

https://github.com/openplanets/SPRUCE/tree/master/ioe_hwj_text_colls/find_compare

** GIANTS **

This work includes a TikaWrapper that configures Tika and provides the nifty 
output routines for JSON and XML, etc. created by Carl Wilson and Peter May:

https://github.com/openplanets/SPRUCE/tree/master/TikaFileIdentifier

It also includes Apache Tika in binary distribution and you are referred to its
NOTICE and DEPENDENCIES files for information on licensing of Apache Tika and its
parts:

http://tika.apache.org/

It also includes a JSON parser called Argo (Apache 2.0):

http://argo.sourceforge.net/index.html

Aside from the restrictions in the dependencies, any of my code is yours to take.
Good luck sorting out the mess! :-)
