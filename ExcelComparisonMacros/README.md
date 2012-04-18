# ExcelComparisonMacros

## Overview
These are three Excel spreadsheets/VBA macros for similar preservation issues regarding comparisons of collections of files. Originally developed for the NLS's use-case of comparing a spreadsheet of original files to a folder of dissemination copies; which was then copied/modified for the ADS's and PRONI's similar issues.

For reference: 

http://wiki.opf-labs.org/display/SPR/File+management+and+matching+of+tif%2C+htm+and+pdf+files
http://wiki.opf-labs.org/display/SPR/File+management+and+matching+of+tif%2C+htm+and+pdf+files+solution

The three included here are:

* `Checking tool for OCR.xls` - as described above, for the NLS: compare a list of files to a folder on disk
* `FolderComparator.xls` - for the ADS: comparing two folders (recursively, but ignoring hierarchy), and showing differences
* `ListComparator.xls` - for PRONI: generic list comparison tool, for 2 or more lists, showing matches and discrepancies

(These files are probably only immediately useful to the people they were developed with; but could provide a starting point, or tips and techniques, for anyone interested in doing similar work.)

## Future development:
There's clearly scope here to abstract/generalise out, at least one large step. 

All three tasks are broadly similar: comparing two-or-more lists of files; the differences are:

* Where those lists are (e.g., a spreadsheet, or a collection of files on disk)
* How you traverse the disk (recursively? ignoring structure?)
* How you compare files (e.g., a .DOC on one list, might need to be matched to a .PDF file in another list; or other dissemination/migration issues)

Ideally, I would like to be able to extend this out into one spreadsheet/set-of-macros that could perform all three of these types of tasks (with sufficiently simple user customisation and interaction.)

If there are any questions/problems/etc., please get in touch.
