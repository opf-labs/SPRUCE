<?php
/*
 * Copyright © 2012 Patrick McCann
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
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
	}
}

?>