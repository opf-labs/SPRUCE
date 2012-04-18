#!/usr/bin/python2

from PIL import Image, ImageDraw
import sys
import os

# minimum height of black area as proportion of original image
# the higher it is, the bigger the areas have to be to be recognised
min_height = 0.01

# minimum width of black area as proportion of original image
# the higher it is, the bigger the areas have to be to be recognised
min_width = 0.02

# amount to weight average by
# the higher it is, the more black pixels need to be in each row
weight = 1.8

# amount of image border to ignore
# the higher it is, the more of the sides are ignored
border = 0.1

# the proportion of pixels in a column which need to be black
vertical_density = 0.6

def find_row_counts(width, height, data):
    """Loop over data row by row and count the number of black pixels in each row.
    The counts are put in a list and this is returned"""
    
    counts = []
    yy = width * (int(height * border))
    
    # get counts of black pixels in each row
    for y in range(int(height * border), int(height * (1 - border))):
        c = 0
        for x in range(int(width * border), int(width * (1 - border))):
            if data[yy + x] == 0:
                c += 1
        counts.append(c)
        yy += width
        
    return counts

def find_rows(height, counts):
    """Using the row counts, find the first and last rows of contiguous rows which have a large number of black pixels.
    Put the start and end into a list an return this."""
    
    # get weighted average count for data
    avg = (float(sum(counts)) / len(counts)) * weight
    
    # append 0 to counts so that else block gets executed
    # when last count will match if
    
    # find the starting and ending of contiguous rows
    # with higher than average counts of black pixels
    cluster, start, end = 0, -1, 0
    found = []
    for y, c in enumerate(counts):
        # row has more than average number of black pixels
        if c > avg:
            cluster += (c - avg)
            if start == -1:
                start = y
        else:
            # end of cluster, was it big enough
            if cluster > avg:
                end = y - 1
                # are there enough contiguus rows
                if end - start > (height * min_height):
                    start = int(start + (height * border))
                    end = int(end + (height * border))
                    found.append((start, end))
            cluster, start, end = 0, -1 ,0
            
    return found

def find_col_counts(width, data, start, end):
    """Knowing which rows to look for (the rows between start and end), get the black pixel counts for the (short) columns in these rows.
    Put these counts into a list and return it."""
    
    col_counts = []
    for x in range(int(width * border), int(width * (1 - border))):
        c = 0
        yy = start * width
        for y in range(start, end + 1):
            if data[yy + x] == 0:
                c += 1
            yy += width
        col_counts.append(c)
    return col_counts

def find_cols(width, height, rows, counts):
    """Find columns which have a large number of black pixels and which are contiguous.
    Put the first and last columns into a list and return it."""
    
    found = []
    
    # add 0 to counts, so that else block gets executed
    # when last count matches if
    counts.append(0)
    
    start, end = -1, 0
    for x, c in enumerate(counts):
        # are there enough black pixels in column
        if c > (rows * vertical_density):
            if start == -1:
                start = x
        else:
            end = x
            # have enough contiguous columns
            if start > -1 and end - start > (width * min_width):
                start = int(start + (width * border))
                end = int(end + (width * border))
                found.append((start, end))
            start, end = -1, 0
    
    return found

def process_data(width, height, data):
    """Take data and image size and find rectangles of largely black pixels.
    Returns a list of bounding coordinates."""
    found = []
    
    # get counts of black pixels per row
    row_counts = find_row_counts(width, height, data)
    # get start and of contiguous rows with high counts
    found_rows = find_rows(height, row_counts)
    #print found_rows
    
    for start_r, end_r in found_rows:
        col_counts = find_col_counts(width, data, start_r, end_r)
        found_cols = find_cols(width, height, (end_r - start_r), col_counts)
        #print col_counts
        
        for start_c, end_c in found_cols:
            found.append((start_c, start_r, end_c, end_r))
    return found

def create_mask_image(filename, width, height, found):
    """Create an image the same size as the original, 
    but with a white background and black rectangles in the places where these were found in the original."""
    # create filename for mask based on input filename
    name, extension = os.path.splitext(filename)
    filename = name + '.mask' + extension
    
    img = Image.new('1', (width, height), 1)
    draw = ImageDraw.Draw(img)
    for coords in found:
        draw.rectangle(coords, fill=0)
    img.save(filename, 'PNG')

if len(sys.argv) < 2:
    raise Exception('Need name of file/s')

for f in sys.argv[1:]:
    img = Image.open(f)
    width, height = img.size
    data = list(img.getdata())
    
    found = process_data(width, height, data)
    if len(found):
        print f
        create_mask_image(f, width, height, found)
