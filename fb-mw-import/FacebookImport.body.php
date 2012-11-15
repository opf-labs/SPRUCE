<?php
/*
 * Copyright 2012 Open Planets Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
if ( !defined( 'MEDIAWIKI' ) ) {
	die( 'This file is a MediaWiki extension, it is not a valid entry point' );
}

# This extension is a type of SpecialPage.
class FacebookImport extends SpecialPage {
	function __construct() {
		parent::__construct( 'FacebookImport', '', false );
		wfLoadExtensionMessages( 'FacebookImport' );
	}

	# This is where the special page's output is created.
	function execute( $par ) {
		global $wgOut;
		$wgOut->addModules( 'ext.FacebookImport' );
		# Intialize the output page.
		$this->setHeaders();
		$wgOut->addHTML( "<div id='fb-import'></div>" );
		$wgOut->addHTML( "<form id='import-form'><div><label for='fbid'>Facebook Photo URL</label><input type='text' id='fburl' name='fburl' /><input id='fbid' name='fbid' type='hidden' /><input id='submitted' name='submitted' type='hidden' value='1' /><input type='submit' /></div></form>" );
}

?>