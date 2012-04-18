'''
Created on 17 Apr 2012

@author: pmay
'''

import csv
import sys
from collections import defaultdict
from sets import Set

class Summariser(object):
    def __init__(self):
        self.file_type_count = defaultdict(int)

        self.start_date = None
        self.end_date = None
        self.dates_missing = False

        self.contribs = Set([])

    def summariseOutput(self, csvfile):
        reader = csv.DictReader(open(csvfile, 'rb'))
        
        for line in reader:
            # date ranges
            dt = line['Creation-Date'][0:10]
            if len(dt)==0:
                self.dates_missing=True
            else:
                if dt<self.start_date or self.start_date==None:
                    self.start_date = dt
                if dt>self.end_date or self.end_date==None:
                    self.end_date = dt
                
            # Contributors
            author = line['Author']
            if len(author)>0:
                self.contribs.add(author)
            
            # Count file types
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
            
        print ""
        print "Date Range:"
        print self.start_date,"-",self.end_date
        if self.dates_missing:
            print "Dates missing"
            
    def writeToCSV(self, filename):
        HEADINGS = ["Covering dates", "Genre", "Contributors", "Scope and content", "Rights Information", "Access Restrictions", "File Formats"]
        writer = csv.DictWriter(open(filename, 'wb'), HEADINGS, extrasaction='ignore')
        writer.writeheader()
        
        alist = ""
        for author in self.contribs:
            alist+=author+", "
        
        fcount = ""
        for ft in self.file_type_count.keys():
            fcount+=ft+": "+str(self.file_type_count[ft])+", "
        
        rowdict = {"Covering dates":self.start_date+"-"+self.end_date,
                   "Contributors":alist[:-1],
                   "File Formats":fcount[:-1]}
        writer.writerow(rowdict)

if __name__ == '__main__':
    if len(sys.argv)>=2:
        summer = Summariser()
        summer.summariseOutput(sys.argv[1])
    if len(sys.argv)==3:
        summer.writeToCSV(sys.argv[2])