<?php
/*
EXIF to DC XML normaliser v1.0 FINAL
A SPRUCE MASHUP TOOL - SUPPORT YOUR DIGITAL PRESERVATION COMMUNITY
Author: Maurice de Rooij, Dutch National Archives, september 2012
License: APACHE2
Info: http://wiki.opf-labs.org/display/TR/EXIF+to+DC+XML+normaliser
*/

# configure location of exiftool (os independent)
$exiftool = "C:\\utils\\exiftool\\exiftool.exe";
# configure location of ini file of default metadata
$inifile = "./exif_to_dc.ini";
### BE CAREFUL NOW !!! ###
# configure location of XML HEADER snippet
$xml_header = file_get_contents("./xml_templates/header.xml");
# configure location of RECORD snippet
$record_snippet = file_get_contents("./xml_templates/record.xml");
# configure location of XML FOOTER snippet
$xml_footer = file_get_contents("./xml_templates/footer.xml");
# configure extensions as an array to scan while working folders (case insensitive)
$image_extensions = array("tif", "tiff", "jpg", "jpeg", "jp2");

/* # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # \
##########################################################################
#  DO NOT EDIT BELOW THIS LINE UNLESS YOU'RE CERTAIN WHAT YOU'RE DOING!  #
##########################################################################
\ # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # */

# alter the exiftool commandline options if necessary
$cmd = "{$exiftool} -php -q -IPTC:DateCreated -XMP-dc:Description -XMP-dc:Creator -XMP-dc:Subject -XMP-dc:Format -System:Filename ";

# fetch cli options
parse_str(implode('&', array_slice($argv, 1)), $opts);

# here is where the magic happens
if(count($opts) == 0) {
	echo "-= EXIF TO DC XML normaliser v1.0-FINAL =-";
	echo "\n";
	echo "Usage:";
	echo "\n";
	echo "       {$GLOBALS["_SERVER"]["PHP_SELF"]} file=FILENAME";
	echo "\n";
	echo "       {$GLOBALS["_SERVER"]["PHP_SELF"]} folder=FOLDERNAME ; (NOTE: recursive!)";
	echo "\n";
	echo "Optionally, specify an XML output file:";
	echo "\n";
	echo "       {$GLOBALS["_SERVER"]["PHP_SELF"]} file=FILENAME|folder=FOLDERNAME xmlout=FILE.xml";
	echo "\n";
	echo "otherwise it will print XML to STDOUT\n";
	exit;
	}

$folderwalk = FALSE;
$files = array();
if(!isset($opts["file"]) and !isset($opts["folder"])) {
	echo "Please specify name of file or folder\n";
	exit;
	}
if(isset($opts["file"]) and isset($opts["folder"])) {
	echo "Please specify file or folder only\n";
	exit;
	}
if(isset($opts["file"]) && !isset($opts["folder"])) {
	if(!is_file($opts["file"])) {
		echo "Error: '{$opts["file"]}' is not a valid file";
		exit;
		}
	$file = $opts["file"];
	}
if(isset($opts["folder"]) && !isset($opts["file"])) {
	if(!is_dir($opts["folder"])) {
		echo "Error: '{$opts["folder"]}' is not a valid folder";
		exit;
		}
	$folderwalk = TRUE;
	$files = dirListRecursive($opts["folder"]);
	if(count($files) == 0) {
		echo "Error: '{$opts["folder"]}' does not contain image files";
		exit;
		}
	}
if(isset($opts["xmlout"])) {
	$result = @file_put_contents($opts["xmlout"],"");
	if(!is_file($opts["xmlout"])) {
		echo "Error: '{$opts["xmlout"]}' is not writable or unknown error occured";
		exit;
		}
	$xmlout = $opts["xmlout"];
	}

$content = "";
$content .= $xml_header;

if($folderwalk && count($files) != 0) {
	foreach($files as $key => $file) {
		$content .= return_record($file);
		}
	}
if (!$folderwalk && isset($file)) {
	$content .= return_record($file);
	}

$content .= $xml_footer;

if($xmlout == "") {
	echo $content;
	}
else {
	file_put_contents($xmlout, $content);
	}

exit;

function return_record($file) {
	global $cmd, $inifile, $record_snippet;
	if($file == "") {
		return "<record>NO FILENAME PASSED TO 'return_record()'</record>\n";
		}
	if(!file_exists($file) or is_dir($file)) {
		return "<record>ILLEGAL FILENAME ('{$file}') PASSED TO 'return_record()'</record>\n";
		}
	# evaluate the command and put result in $exif_array
	eval('$exif_array=' . `{$cmd} {$file}`);	
	############################
	# clean up and format EXIF #
	############################
	# get rid of parent [0] key (makes life easier)
	$exif_array = $exif_array[0];
	# mangle "DateCreated" to make it right (xxxx:xx:xx => xxxx-xx-xx)
	$exif_array["DateCreated"] = str_replace(":","-", $exif_array["DateCreated"]);
	# remove first line + linebreak of "Description" holding the photographers name
	$tmp = explode("\n", $exif_array["Description"]);
	$exif_array["Description"] = $tmp[1];
	# normalize creator name
	$exif_array["Creator"] = ucwords(strtolower($exif_array["Creator"]));
	
	# prepare "Subject"'s for XML
	$tmp = "";
	if($exif_array["Subject"]) {
		for ($i=0;$i<count($exif_array["Subject"]);$i++) {
			if($i != 0){
				$tmp .= "\n\t";
				}
			$tmp .= "<dc:subject>{$exif_array["Subject"][$i]}</dc:subject>";
			}
		}
	$exif_array["Subject"] = $tmp;
	
	# parse ini file for additional metadata
	$ini_array = parse_ini_file($inifile, true);
	$tmp = "";
	$i = 0;
	foreach($ini_array["subject_metadata"] as $key => $value) {
		if($ini_array["subject_metadata"]) {
			if ($i > 0) {
				$tmp .= "\n\t";
				}
			# test if is SUBJECT or SUBJECTTGM
			# dcterms:lcsh : SUBJECT
			# dcterms:tgm: SUBJECTTGM
			# we do a substring here to spot "SUBJECTTGM" without a postfix
			# kindaclumsy ... butitworks
			$test = substr($key, 0, 10);
			if ($test != "SUBJECTTGM") {
				$node = "dcterms:lcsh";
				}
			else {
				$node = "dcterms:tgm";
				}
			$tmp .= "<{$node}>{$value}</{$node}>";
			}
		$i++;
		}
	$ini_array["subject_metadata"] = $tmp;
	
	# mangle $ini_array[variables] to "real" variables with prefixes
	foreach($ini_array["dc_metadata"] as $key => $value) {
		$tmpval = "ini_{$key}";
		$$tmpval = $value;
		}
	
	# mangle $ini_array["subject_metadata"] to variable
	$ini_subject_metadata = $ini_array["subject_metadata"];
	
	# mangle $ini_array[variables] to "real" variables with prefixes
	foreach($exif_array as $key => $value) {
		$tmpval = "exif_{$key}";
		$$tmpval = $value;
		}

	$content = "";	
	# now we have got everything, glue it together in <record> node
	$content .= preg_replace("/\[(\w+)\]/e", '\\$$1', $record_snippet);
	
	return $content;
	}

function dirListRecursive($directory) {
	global $image_extensions;
	$results = array();
	if(!is_dir($directory)) {
		return $results;
		}
	$handler = opendir($directory);
	while ($file = readdir($handler)) {
		if ($file != '.' && $file != '..' && !is_dir($file)) {
			$info = pathinfo($file);
			if(in_array(strtolower($info["extension"]), $image_extensions)) {
				$results[] = realpath($directory.DIRECTORY_SEPARATOR.$file);
				}
			}
		if ($file != '.' && $file != '..' && is_dir($file)) {
			$values = dirListRecursive(realpath($file));
			foreach($values as $key => $value) {
				$results[] = $value;
				} 
			}
		}
	closedir($handler);
	return $results;
}
?>
