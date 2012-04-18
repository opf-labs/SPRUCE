black_pixel_report.py

http://wiki.opf-labs.org/display/SPR/Malformed+TIFF+images+solution

The script is run as so: 

./black_pixel_report.py directory1 somefile.tif another.tiff
directory2

It will process any images below directory1, the files somefile.tif,
another.tiff and any images below directory2. The output is in CSV: 

./samples/NL-HaNA_2.10.26_746_0274.tif, 2565, 3634, 0, 0
./samples/NL-HaNA_2.10.26_746_0264.tif, 2545, 3644, 8881168, 95.764364
./samples/NL-HaNA_2.10.26_746_0265.tif, 0, 0, 0, 0

The first line shows an image which has no black pixels, so no
percentage of black pixels. The second line shows an image which has a
large number of black pixels, covering 95% of the image. The third
line shows an image which is full of zeros. It couldn't be opened by
PIL as an image, so the width and height couldn't be established.

black_area_identifier.py

I tried some edge detection, but the pages full of text and line
drawings had too many edges, so the edges of the corrupted areas were
no more visible than before. 
I converted the JPEGs to smaller 1 bit PNGs, so that processing them
would be quicker. 

for j in ls directory/*.jpg; do jpegtopnm ${j} | pamscale -xscale 0.3
-yscale 0.3 | pamditherbw -threshold | pnmtopng >
small_directory/basename ${j} .jpg.png; done

I wrote a Python2 script to find areas of black. The program would
first look for rows which had a higher than average number of black
pixels and were contiguous. Within these rows, it would then look for
columns which were largely black and contiguous. It reports files
which have such areas and also produces mask image files which show
where the black areas were found. 
It is run as so: 

./black_area_identifier.py
small_newspapers/DUCR-1896-10-31-000[0-9].png > results.txt

The mask files will be put into small_newspapers, and the names of any
images with black areas will be put into results.txt.
