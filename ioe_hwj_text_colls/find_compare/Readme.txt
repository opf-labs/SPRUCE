The three Perl scripts work with the metadata extraction tool. The purpose of the scripts is to enable
archivists to look inside collections of documents and get some idea about what they contain, look for
duplicates and so on.

INSTALLATION
On OSX or Linux the scripts will just work. On Windows install Strawberry Perl, accepting the
defaults. http://strawberryperl.com/.

RUNNING
Open a terminal or command window (e.g. on Windows type 'cmd' in the run box). Change to the folder
containing the scripts, and enter command line arguments as below. All of the scripts output text
reports. The scripts use file paths, and if the file paths include spaces put quotes around
them.

checksums.pl
This examines the 'textnormsha256.txt' metadata files to look for duplicate checksums. Duplicates
indicate that the content of the files is identical. Running the script outputs a text report
'copies.txt' in the same folder as the script. Files with no content such as images will produce
false matches so just ignore them.
Usage:
perl checksums.pl <path to files within metadata>

comparenames.pl
This searches for matching terms within filenames. This could indicate that the files have something
in common. The script works on the original content NOT the metadata. Terms can use wildcards at the
beginning, end, or both e.g. *minutes*, minutes*. If the terms contain spaces, e.g 'Fred Smith*' use
quotation marks around them. Bear in mind that looking for the term at the end of a filename may
involve using the extension as well. The script outputs a report of matching files, if any exist,
called 'namecomparisons.txt'.
Usage:
perl comparenames.pl <path to content> <searchterm>

findterm.pl
This looks inside the metadata content extracts for matching terms. This may be particularly useful
in gathering together content around a subject. Use quotes around multiple word terms. The report
produced is named after the search term.
Usage:
perl findterm.pl <metadata file path> <searchterm>