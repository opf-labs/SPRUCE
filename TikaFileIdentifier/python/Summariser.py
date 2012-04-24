'''
Created on 17 Apr 2012

@author: pmay
'''

import csv
import os
import sys
from collections import defaultdict
from sets import Set

class Summariser(object):
    def __init__(self, origFile=None):
        self.filename = ""
        if origFile is not None:
            self.filename = origFile
        self.file_type_count = defaultdict(int)

        self.start_date = None
        self.end_date = None
        self.dates_missing = False

        self.contribs = Set([])

    def summariseOutput(self, csvfile):
        if len(self.filename)==0:
            self.filename = csvfile[:-4]
        
        reader = csv.DictReader(open(csvfile, 'rb'))
        
        for line in reader:
            # date ranges
            if 'Creation-Date' in line:
                dt = line['Creation-Date'][0:10]
                if len(dt)==0:
                    self.dates_missing=True
                else:
                    if dt<self.start_date or self.start_date==None:
                        self.start_date = dt
                    if dt>self.end_date or self.end_date==None:
                        self.end_date = dt
                
            # Contributors
            if 'Author' in line:
                author = line['Author']
                if len(author)>0:
                    self.contribs.add(author)
            
            # Count file types
            if 'Content-Type' in line:
                ft = line['Content-Type']
                if len(ft)==0:
                    ft = 'Unknown'
                self.file_type_count[ft]+=1
        
        print "File Counts:"
        for ft in self.file_type_count.keys():
            print ft,self.file_type_count[ft]
        
        print "" 
        print "Author List:"
        for author in self.contribs:
            print author

        dr = ""
        if self.start_date is not None:
            dr = self.start_date
        dr+="-"
        if self.end_date is not None:
            dr += self.end_date         
        print ""
        print "Date Range:"
        print dr
        if self.dates_missing:
            print "Dates missing"
            
def writeToCSV(summarisers, filename):
    """Writes the specified summarised data to the specified file"""
    HEADINGS = ["File", "Covering dates", "Genre", "Contributors", "Scope and content", "Rights Information", "Access Restrictions", "File Formats"]
    writer = csv.DictWriter(open(filename, 'wb'), HEADINGS, extrasaction='ignore')
    writer.writeheader()
    
    for summariser in summarisers:
        alist = ""
        for author in summariser.contribs:
            alist+=author+", "
        
        fcount = ""
        for ft in summariser.file_type_count.keys():
            fcount+=ft+": "+str(summariser.file_type_count[ft])+", "
            
        dr = ""
        if summariser.start_date is not None:
            dr = summariser.start_date
        dr+="-"
        if summariser.end_date is not None:
            dr += summariser.end_date 
        
        rowdict = {"File":summariser.filename,
                   "Covering dates":dr,
                   "Contributors":alist[:-1],
                   "File Formats":fcount[:-1]}
        writer.writerow(rowdict)


def summariseFiles(origfiles, aggfiles, outputfile):
    """Summarise all the specified aggregate files into the output CSV file.
       origfiles is a mapping of original filenames to the aggfiles so that
       the correct filename is written into the CSV file"""
    
    summaries = []
    for (of, af) in zip(origfiles, aggfiles):       
        summer = Summariser(of)
        summer.summariseOutput(af)
        summaries.append(summer)
    
    writeToCSV(summaries, outputfile)

if __name__ == '__main__':
    if len(sys.argv)>=2:
        summer = Summariser()
        summer.summariseOutput(sys.argv[1])
        
        # write to file?
        if len(sys.argv)==3:
            writeToCSV([summer], sys.argv[2])