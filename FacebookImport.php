<?
$wgExtensionFunctions[] = 'init';

function init() {
	global $wgOut;
	$wgOut->addModules( 'ext.FacebookImport' );
}

$wgResourceModules['ext.facebook.sdk'] = array(
	'scripts' => 'ext.facebook.sdk.js',
	'messages' => array(),
	'dependencies' => array(),
	'position' => 'bottom',
	'localBasePath' => dirname( __FILE__ ) . '/modules',
	'remoteExtPath' => 'Facebook/modules'
);
$wgResourceModules['ext.FacebookImport'] = array(
	'scripts' => 'ext.initialFbImport.js',
	'messages' => array(),
	'dependencies' => array( 'ext.facebook.sdk' ),
	'position' => 'bottom',
	'localBasePath' => dirname( __FILE__ ) . '/modules',
	'remoteExtPath' => 'FacebookImport/modules'
);

