'''
Created on 17 Apr 2012

@author: Peter May
@organization: The British Library
@contact: Peter.May@bl.uk
'''

import csv
import json
import os
import sys
from sets import Set

# Useful properties
PROPS = ['Content-Type', 'Application-Name', 'creator', 'producer', 'Author', 'Content-Length', 'Page-Count', 'Revision-Number', 'Creation-Date', 'Last-Modified', 'Last-Save-Date', 'Last-Printed']

# CSV Headings
HEADINGS = ['Filename', 'isValid']
#HEADINGS.extend(PROPS)      # add in the useful properties headings

def __listFilesInDir(directory):
    """Lists all files (recursively) in the specified directory"""
    fileList = []
    #print directory
    for root, subFolders, files in os.walk(directory):
        #print root, subFolders, files
        for file in files:
            fileList.append(os.path.join(root, file))
    return fileList

def processdir(origdir, directory, outputfile):
    fileList = __listFilesInDir(directory)
    
    # Work out all headings
    prop_headings = Set([])
    for file in fileList:
        fname   = os.path.basename(file)
        fileObj = open(file, 'rb')
        
        metadata = {}
        try:
            # load json
            metadata = json.load(fileObj, encoding="latin-1")
        except:
            pass
        #print metadata.keys()
        prop_headings|=Set(metadata.keys())
    # append all property headings to the HEADINGS list
    #print prop_headings
    
    HEADINGS.extend(list(prop_headings))
    
    # now create an output file
    writer = csv.DictWriter(open(outputfile, 'wb'), HEADINGS, extrasaction='ignore')
    writer.writeheader()
    
    print len(fileList)
    
    # now re-run through all the files again and fill out each row 
    for file in fileList:
        fname   = os.path.basename(file)
        fileObj = open(file, 'rb')
        
        metadata = {}
        
        try:
            # load json
            metadata = json.load(fileObj)
        except:
            try:
                metadata = json.load(fileObj, encoding="cp1252")
            except Exception, e:
                print file, e 
                metadata['isValid'] = False
        
        # add in the filename
        relpath     = os.path.dirname(os.path.relpath(file, directory))
        absOutPath  = os.path.join(origdir, relpath, fname)
        metadata['Filename']=absOutPath
        
        # write output to file
        writer.writerow(metadata)
        
        fileObj.close()


if __name__ == '__main__':
    if len(sys.argv)==4:
        processdir(sys.argv[1], sys.argv[2], sys.argv[3])