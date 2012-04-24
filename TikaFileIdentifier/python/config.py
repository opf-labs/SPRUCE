###################################################################
#
# Edit the following to reflect the setup on your system


###################################################################
# TikaRunner

# Path to Tika JAR file
TIKA        = "c:/Projects/SPRUCE/Tika/tika-app-1.2-SNAPSHOT.jar"

###################################################################
# CSVFormatter

# HEADINGS to ignore - columns/keywords in this set will be ignored from
# the JSON files output by TikaRunner.  The following tags typically
# contain a lot of numeric data which results in odd line splits in the
# final aggregated CSV.

# The following 3 headings are seen in TIF files, and contain lists of
# numbers 32 32 32 32 32 10 ..... etc
IGNORE = ['Inter Color Profile', 'Unknown tag (0x02bc)', 'Unknown tag (0x8649)']


###################################################################
# ISORunner

# Path to WinCDEmu batchmnt.exe
MOUNTER = "C:/Program Files (x86)/WinCDEmu/batchmnt.exe"

# Free directory to mount an ISO image to
# IMPORTANT: Ensure the mount point ends with a :
MOUNT_POINT = 'V:'
