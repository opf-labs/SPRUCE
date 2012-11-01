# Facebook Import extension for MediaWiki

A MediaWiki extension to facilitate the transfer of information and resources from Facebook into a wiki. It does this in two ways.
- When users log in using Facebook (via the [Facebook Open Graph Extension](http://www.mediawiki.org/wiki/Extension:Facebook), they are presented with a form populated with information from their Facebook profile, submission of which populates their User Page with that information.
- A Special Page is created to which can be submitted the URL of a photograph on Facebook. Upon submission, the photo is imported into the wiki.

## Dependencies
- Developed using MediaWiki 1.19, untested on other versions.
- Requires the [Facebook Open Graph Extension](http://www.mediawiki.org/wiki/Extension:Facebook).
	- If only the User Page population on initial login via Facebook is required, the standard version of the extension at [garbear/facebook-mediawiki](https://github.com/garbear/facebook-mediawiki) will do.
	- For the photo import, [my fork of that extension](https://github.com/pgmccann/facebook-mediawiki) which allows the extension to request additional Facebook permissions is required.
- The MediaWiki API needs to be enabled.
- Uploads and Copy Uploads need to be enabled in the wiki, and users need to have the upload and upload_by_url permissions, in order to import photos.

## Installation
Standard MediaWiki extension installation - place in extensions folder and include in your wiki's LocalSettings.php e.g. <tt>require_once("$IP/extensions/FacebookImport/FacebookImport.php");</tt>.

## Configuration
Firstly, make sure you've got the Facebook Open Graph Extension configured correctly to work with your Facebook application. If you're using my fork of that extension in order to enable photo imports, you'll need to add the user_photos and/or friends_photos permissions to the $wgFbPermissions array in your config.php, as well as enabling those permissions in your Facebook app.

Configuration options for this extension take the form of two arrays in FacebookImport.php, $wgFbImportOptions and $wgFbConnectionImportOptions. The elements of those arrays are themselves arrays, each with three elements - a boolean value to indicate whether that item should be included in the User Page import form, a string for the label for that input, and a string for the type of input (textfield or textarea).