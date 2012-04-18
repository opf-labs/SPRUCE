#!/usr/bin/python2

from PIL import Image
from sys import argv
import os

validExtensions = ['.tif', '.tiff', '.jpg', '.jpeg', '.gif', '.png']

def process_file(f):
    """Takes the name of a file and opens it and gets a histogram of the colours.
    The percentage of pixels which are black is calculated.
    The name of the file, the width and height, number of black pixels and percentage are output in CSV format."""
    name, extension = os.path.splitext(f)
    extension = extension.lower()
    if extension not in validExtensions:
        return 
    
    # initialise
    pixels, blackPixels, bpp, w, h = 0, 0, 0.0, 0, 0
    
    try:
        # get size and histogram of image
        img = Image.open(f)
        (w, h) = img.size
        hist = img.histogram()
    except Exception:
        # skip the else block
        pass
    else:
        # number of pixels
        pixels = w * h * 1.0
        
        # black values from 3 streams in histogram
        blackPixels = (hist[0] + hist[256] + hist[512]) / 3
        
        # black pixel percentage
        bpp = (100.0 / pixels) * blackPixels
    
    print '%s,%d,%d,%d,%f' % (f, w, h, blackPixels, bpp)

# check for name of file
if 2 > len(argv):
    raise Exception('Need name of file or directory')

# CSV header
print 'filename,width,height,black pixel count,percentage of black pixels'

# loop over args, skipping first item
for f in argv[1:]:
    # have file, so process
    if os.path.isfile(f):
        process_file(f)
    # directory, so look for files below
    elif os.path.isdir(f):
        for root, dirs, files in os.walk(f):
            for ff in files:
                process_file(os.path.join(root, ff))
