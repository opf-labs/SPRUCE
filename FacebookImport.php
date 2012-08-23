<?php

$wgFbImportOptions = array(
	"name" => array(true, "Name", "textfield"),
	"first_name" => array(false, "First Name", "textfield"),
	"middle_name" => array(false, "Middle Name", "textfield"),
	"last_name" => array(false, "Last Name", "textfield"),
	"gender" => array(true, "Gender", "textfield"),
	"locale" => array(false, "Locale", "textfield"),
	"languages" => array(true, "Languages", "textarea"),
	"link" => array(true, "Facebook Profile", "textfield"),
	"username" => array(false, "Username", "textfield"),
	"timezone" => array(false, "Timezone", "textfield"),
	"updated_time" => array(false, "Updated Time", "textfield"),
	"verified" => array(false, "Verified", "textfield"),
	"bio" => array(true, "Bio", "textarea"),
	"birthday" => array(false, "Birthday", "textfield"),
	"education" => array(true, "Education", "textarea"),
	"email" => array(false, "Email", "textfield"),
	"hometown" => array(true, "Hometown", "textfield"),
	"interested_in" => array(false, "Interested In", "textfield"),
	"location" => array(true, "Location", "textfield"),
	"political" => array(false, "Political", "textarea"),
	"quotes" => array(false, "Quotes", "textarea"),
	"relationship_status" => array(false, "Relationship Status", "textfield"),
	"religion" => array(false, "Religion", "textfield"),
	"significant_other" => array(false, "Significant Other", "textfield"),
	"website" => array(true, "Website", "textfield"),
	"work" => array(true, "Work", "textarea")
);

$wgFbConnectionImportOptions = array(
	"accounts" => array(true, "Accounts", "textarea"),
	"activities" => array(false, "Activities", "textarea"),
	"albums" => array(false, "Photo Albums", "textarea"),
	"books" => array(true, "Books", "textarea"),
	"checkins" => array(false, "Check-ins", "textarea"),
	"events" => array(false, "Events", "textarea"),
	"feed" => array(false, "Feed", "textarea"),
	"games" => array(false, "Games", "textarea"),
	"groups" => array(true, "Groups", "textarea"),
	"home" => array(false, "Home", "textarea"),
	"interests" => array(true, "Interests", "textarea"),
	"likes" => array(false, "Likes", "textarea"),
	"links" => array(true, "Links", "textarea"),
	"locations" => array(false, "Locations", "textarea"),
	"movies" => array(true, "Movies", "textarea"),
	"music" => array(true, "Music", "textarea"),
	"permissions" => array(false, "Permissions", "textarea"),
	"photos" => array(false, "Photos", "textarea"),
	"picture" => array(false, "Profile Picture", "image"),
	"posts" => array(false, "Posts", "textarea"),
	"scores" => array(false, "Scores", "textarea"),
	"statuses" => array(false, "Statuses", "textarea"),
	"tagged" => array(false, "Tagged", "textarea"),
	"television" => array(true, "Television", "textarea"),
	"videos" => array(false, "Videos", "textarea")
);

$dir = dirname( __FILE__ ) . '/';

$wgExtensionFunctions[] = 'init';
$wgHooks['ResourceLoaderGetConfigVars'][] = 'ConfigFbOptionsForResourceLoader';

$wgAutoloadClasses['FacebookImport'] = $dir.'FacebookImport.body.php';
$wgExtensionMessagesFiles['FacebookImport'] = $dir.'FacebookImport.i18n.php';
$wgExtensionAliasesFiles['FacebookImport'] = $dir.'FacebookImport.aliases.php';
$wgSpecialPages['FacebookImport'] = 'FacebookImport';

$wgResourceModules['ext.facebook.sdk'] = array(
	'scripts' => 'ext.facebook.sdk.js',
	'messages' => array(),
	'dependencies' => array(),
	'position' => 'bottom',
	'localBasePath' => $dir.'/modules',
	'remoteExtPath' => 'Facebook/modules'
);
$wgResourceModules['ext.FacebookImport'] = array(
	'scripts' => 'ext.initialFbImport.js',
	'styles' => 'ext.jQueryUI.css',
	'messages' => array(),
	'dependencies' => array( 'ext.facebook.sdk', 'jquery.ui.dialog' ),
	'position' => 'bottom',
	'localBasePath' => $dir.'/modules',
	'remoteExtPath' => 'FacebookImport/modules'
);

function init() {
	global $wgOut;
	$wgOut->addModules( 'ext.FacebookImport' );
	$wgOut->addHTML( createForm() );
}

function ConfigFbOptionsForResourceLoader( &$vars ) {
		global $wgFbConnectionImportOptions;
		$var = "";
		foreach ($wgFbConnectionImportOptions as $option=>$details) {
			if ($details[0]) {
				$var .= ",".$option;
			}
		}
		$vars['fbImportOptions'] = substr($var, 1);
		return true;
	}

function createForm() {
	global $wgFbImportOptions, $wgFbConnectionImportOptions, $wgSitename;
	$form = "<div id='fb-import-form' title='Fill in your ".$wgSitename." User Page' style='display:none'>";
	$form .= "<p>Every ".$wgSitename." user has their own public-facing User Page. Below is some information from your Facebook profile - please consider using it to update your User Page by submitting this form.</p>";
	$form .= "<form><fieldset>";
	$form .= createFormRows($wgFbImportOptions);
	$form .= createFormRows($wgFbConnectionImportOptions);
	$user_page_msg = "<p>You can edit your User Page whenever you like, just like any other page. After logging in, just click on your Username at the top of any page to go to it.</p>";
	$form .= "</fieldset></form></div>";
	$form .= "<div id='fb-import-success' title='Success!' style='display:none'><p>Your ".$wgSitename." User Page has been updated with the information you submitted.</p>".$user_page_msg."</div>";
	$form .= "<div id='fb-import-cancel' title='Cancelled' style='display:none'><p>You've declined to update your ".$wgSitename." User Page at for the moment.</p>".$user_page_msg."</div>";
	return $form;
}

function createFormRows($field_list) {
	$rows = "";
	foreach ($field_list as $label=>$field) {
		if ($field[0]) {
			if ($field[2] == "textfield") {
				$rows .= "<label for='".$label."'>".$field[1]."</label><input type='text' name='".$label."' id='".$label."' class='text ui-widget-content ui-corner-all' />";
			}
			elseif ($field[2] == "textarea") {
				$rows .= "<label for='".$label."'>".$field[1]."</label><textarea name='".$label."' id='".$label."' class='text ui-widget-content ui-corner-all'></textarea>";
			}
		}
	}
	return $rows;
}