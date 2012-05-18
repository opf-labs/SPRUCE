'''
Created on 18 Apr 2012

@author: Peter May
@organization: The British Library
@contact: Peter.May@bl.uk
'''

import config
import CSVFormatter
import Summariser
import TikaRunner

import os
import subprocess
import sys
import time

def raw_string(s):
    if isinstance(s, str):
        s = s.encode('string-escape')
    elif isinstance(s, unicode):
        s = s.encode('unicode-escape')
    return s

def __listISOsInDir(directory):
    """Lists all ISO files (recursively) in the specified directory"""
    fileList = []
    for root, subFolders, files in os.walk(directory):
        for file in files:
            if file.endswith(".iso"):
                fileList.append(os.path.join(root, file))
    return fileList

def __mountIsoFile(isofile, mountpoint):
    """Mounts the specified ISO file to the specified mount point"""
    
    # ensure mountpoint has a ":" on the end to avoid bluescreens
    if mountpoint[-1]!=':':
        mountpoint+=":"
        
    print "Mounting",isofile,"to",mountpoint
    subprocess_flags = 0
    process = subprocess.Popen([raw_string(config.MOUNTER), isofile, mountpoint, "/wait"], 
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                               creationflags=subprocess_flags)

    exitcode = process.wait()
    output = process.communicate()
    
    print exitcode
    print output
    return exitcode

def __unmountIsoFile(mountpoint):
    """Unmounts the ISO file available on the specified mount point"""
    # ensure mountpoint has a ":" on the end to avoid bluescreens
    if mountpoint[-1]!=':':
        mountpoint+=":"
        
    print "Un-mounting",mountpoint
    
    subprocess_flags = 0
    process = subprocess.Popen([config.MOUNTER, "/unmount", mountpoint], 
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE, 
                               creationflags=subprocess_flags)

    exitcode = process.wait()
    output = process.communicate()
    return exitcode

def processDirectory(directory, outputdir, outputfile):
    """Mounts each ISO file in the specified directory, running TikaRunner over all the files
       and storing the JSON results in the outputdir.  These results are aggregated into a
       single CSV file in the outputdir, and finally the results are summarised in outputfile CSV"""
    print "[ISORunner] Processing directory", directory
    
    # List ISO files in directory
    isoList = __listISOsInDir(directory)

    # sort out mount point
    mp = config.MOUNT_POINT
    if mp[-1]!=':':
        mp+=":"
    
    fileCount = len(isoList)
    fileProc  = 0
    start = time.time()
    
    aggFileList = []
    
    print "[ISORunner] Processing",fileCount,"ISO files in directory", directory
    # now process each ISO file    
    for file in isoList:
        file = raw_string(file)
        fname       = os.path.basename(file)
        relpath     = os.path.dirname(os.path.relpath(file, directory))
        absOutPath  = os.path.join(outputdir, "TikaRunner", relpath, fname[:-4])
        aggFile     = os.path.join(outputdir, "Aggregated", relpath, fname+".csv")
        aggFileList.append(aggFile)
        # create the output folder structure if it doesn't exist
        # uses the filename to create an additional folder to store the parsed contents of the ISO file
        if not os.path.exists(absOutPath):
            os.makedirs(absOutPath)
            
        print "[ISORunner] Processing ISO file", file
        sys.stdout.flush()
        
        # mount the ISO file
        status = __mountIsoFile(file, mp)
        
        if status==0:
            # use TikaRunner to process the entire ISO file contents
            TikaRunner.processdir(mp+"/", absOutPath)
            
            # unmount the drive
            __unmountIsoFile(mp)
            
            # Aggregate the stats
            CSVFormatter.processdir(mp+"/", absOutPath, aggFile)
        
        # Timing Stats
        stop = time.time()
        fileProc+=1
        avgTime = float(stop-start)/fileProc
        compTime = (avgTime*(fileCount-fileProc))
        print "-"*50
        print "[ISORunner] Avg Time/ISO File:",avgTime,"s"
        if compTime>60:
            print "[ISORunner] Expected Completion in:",(compTime/60),"minutes"
        else:
            print "[ISORunner] Expected Completion in:",compTime,"seconds"
        print ""
    
    # Finally summarise aggregates
    Summariser.summariseFiles(isoList, aggFileList, outputfile)

if __name__ == '__main__':
    if len(sys.argv)==4:
        processDirectory(sys.argv[1], sys.argv[2], sys.argv[3])