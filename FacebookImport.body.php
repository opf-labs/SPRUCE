<?php

# This extension is a type of SpecialPage.
class FacebookImport extends SpecialPage {
	function __construct() {
		parent::__construct( 'FacebookImport', '', false );
		wfLoadExtensionMessages( 'FacebookImport' );
	}

	# This is where the special page's output is created.
	function execute( $par ) {
		global $wgOut;
		# Intialize the output page.
		$this->setHeaders();
		$wgOut->addHTML( "<div id='fb-import'></div>" );
	}
}

?>