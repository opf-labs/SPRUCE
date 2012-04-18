'''
Created on 17 Apr 2012

@author: Peter May
@organization: The British Library
@contact: Peter.May@bl.uk
'''

import os
import json
import subprocess
import sys
import time

TIKA_1_0 = "f:/SCAPE/Repos/tika/tika-app/target/tika-app-1.0-SNAPSHOT.jar"
TIKA_1_1 = "c:/SPRUCE/Workspace/Identifier/Identifier/tika/tika-app-1.2-SNAPSHOT.jar"

TIKA_WRAPPER = "TikaWrapper"

tikaPath = TIKA_1_1

def __listFilesInDir(directory):
    """Lists all files (recursively) in the specified directory"""
    fileList = []
    #print directory
    for root, subFolders, files in os.walk(directory):
        #print root, subFolders, files
        for file in files:
            fileList.append(os.path.join(root, file))
    return fileList

def __runTika(file, outfile):
    #subprocess.call(["java", "-jar", tikaPath, "-j", file],cwd=os.path.dirname(tikaPath), stdout=outfile)
    subprocess_flags = 0
    process = subprocess.Popen(["java", "-jar", tikaPath, "-j", file], 
                               stdout=outfile, stderr=subprocess.PIPE, 
                               creationflags=subprocess_flags)

    exitcode = process.wait()
    output = process.communicate()

#    if exitcode==0:
#        try:
#            json.dump(output[0], outfile)
#        except:
#            json.dump(output[0].decode("cp1252"), outfile)
    
    if exitcode==1:
        # Most likely Tika found a problem parsing the file, so just try to at least identify it
        __runTikaIdentOnly(file, outfile)
    
    
def __runTikaIdentOnly(file, outfile):
    cp = TIKA_1_1+";c:/SPRUCE/Workspace/Identifier/Identifier/java/"
    #subprocess.call("java -classpath \""+cp+"\" "+TIKA_WRAPPER+" \""+file+"\"", stdout=outfile, shell=True)
    subprocess_flags = 0
    process = subprocess.Popen("java -classpath \""+cp+"\" "+TIKA_WRAPPER+" \""+file+"\"", 
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE, 
                               creationflags=subprocess_flags, shell=True)

    exitcode = process.wait()
    output = process.communicate()
    obj = {"Content-Type":output[0].strip()}
    json.dump(obj, outfile)


def processdir(directory, outputdir):
    """Runs Tika over all files listed in the specified directory, outputting results
       to the specified output directory"""   
    fileList = __listFilesInDir(directory)
    
    print "Processing directory", directory
    fileCount = len(fileList)
    fileProc  = 0
    start = time.time()
    
    for file in fileList:
        fname       = os.path.basename(file)
        relpath     = os.path.dirname(os.path.relpath(file, directory))
        absOutPath  = os.path.join(outputdir, relpath)
        # create the output folder structure if it doesn't exist
        if not os.path.exists(absOutPath):
            os.makedirs(absOutPath)
            
        print "Processing", file
        sys.stdout.flush()
        
        outfile = open(os.path.join(absOutPath, fname+".txt"), 'wb')
        #subprocess.call(["java", "-jar", tikaPath, "-j", file],cwd=os.path.dirname(tikaPath), stdout=outfile,stderr=outfile)
        __runTika(file, outfile)
        outfile.close()
        stop = time.time()
        fileProc+=1
        avgTime = float(stop-start)/fileProc
        compTime = (avgTime*(fileCount-fileProc))
        print "Avg Time/File:",avgTime,"s"
        if compTime>60:
            print "Expected Completion in:",(compTime/60),"minutes"
        else:
            print "Expected Completion in:",compTime,"seconds"
        print ""

if __name__ == '__main__':
    if len(sys.argv)==3:
        processdir(sys.argv[1], sys.argv[2])