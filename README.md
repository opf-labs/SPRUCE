# Facebook Import extension for MediaWiki

A MediaWiki extension to facilitate the transfer of information and resources from Facebook into a wiki. It does this in two ways.
- When users log in using Facebook (via the [Facebook Open Graph Extension](http://www.mediawiki.org/wiki/Extension:Facebook)) they are presented with a form populated with information from their Facebook profile, submission of which populates their User Page with that information.
- A Special Page is created to which can be submitted the URL of a photograph on Facebook. Upon submission, the photo is imported into the wiki.

## Dependencies
- Developed using MediaWiki 1.19, untested on other versions.
- Requires the [Facebook Open Graph Extension](http://www.mediawiki.org/wiki/Extension:Facebook).
- The MediaWiki API needs to be enabled.
- Uploads and Copy Uploads need to be enabled in the wiki, and users need to have the upload and upload_by_url permissions, in order to import photos.

## Installation
Standard MediaWiki extension installation - place in extensions folder and include in your wiki's LocalSettings.php e.g. <tt>require_once("$IP/extensions/FacebookImport/FacebookImport.php");</tt>.

## Configuration
Firstly, make sure you've got the Facebook Open Graph Extension configured correctly to work with your Facebook application. In order to enable photo imports, you'll need to add  a hook to your LocalSettings.php file as shown on the [Facebook Open Graph Extension wiki page](http://www.mediawiki.org/wiki/Extension:Facebook#Facebook_permissions) to allow users to import their own photos (or photos they're tagged in) you'll need to add the 'user_photos' permission. The 'friends_photos' permission is need to allow users to import their friends' photos. These permissions also need to be enabled in your Facebook Application.

Configuration options for this extension take the form of two arrays in FacebookImport.php, $wgFbImportOptions and $wgFbConnectionImportOptions. The elements of those arrays are themselves arrays, each with three elements - a boolean value to indicate whether that item should be included in the User Page import form, a string for the label for that input, and a string for the type of input (textfield or textarea).