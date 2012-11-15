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

$("#import-form").submit(function(){
	var url = $("#fburl").val();
	$("#fbid").val(getURLParameter(url,"fbid"));
});

//What are we looking at?
FB.init({
	appId  : fbAppId,
	status : true, // check login status
	cookie : true, // enable cookies to allow the server to access the session
	xfbml  : true  // parse XFBML
});

FB.getLoginStatus(function(response) {
	if (response.status === 'connected') { //this is the only status we're interested in
		// the user is logged in and has authenticated your
		// app, and response.authResponse supplies
		// the user's ID, a valid access token, a signed
		// request, and the time the access token
		// and signed request each expire
		var uid = response.authResponse.userID;
		var accessToken = response.authResponse.accessToken;
		//make request for resource
		var fbid = getURLParameter(location.search, "fbid")
		if (fbid) {
			makeFacebookRequest(fbid, accessToken);
		}
		else if (getURLParameter(location.search, "submitted")) {
			alert("Something went wrong. Was that a valid Facebook ID or URL?");
		}
	}
});

function getURLParameter(url, name){return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(url)||[,""])[1].replace(/\+/g,'%20'))||null;}

//Struggling to get Facebook permissions to propagate - this needs further work
function makeFacebookRequest(id, accessToken) {
	FB.api(id+'?access_token='+accessToken, function(response) {;
		if (response) {
			if (response.error) {
				alert("Facebook returned an error message. Are you sure that was a photo, and that you have permission to import it?");
			}
			if (response.images[0].source) {
				uploadFileFromURL(id, response.from.name, response.images[0].source);
			}
		}
		else {
			alert("Something went wrong. Was that a valid Facebook ID or URL?");
		}
	});
}

function uploadFileFromURL(id, name, url) {
	filename = encodeURIComponent(url.substring(url.lastIndexOf('/')+1));
	enc_url = encodeURIComponent(url);
 	$.ajax({
 		type: "GET",
 		url: wgServer + wgScriptPath + "/api.php",
 		data: "format=json&action=query&prop=info&intoken=edit&titles="+filename,
 		dataType: 'json',
 		success: function(data) {
 			var obj = data.query.pages;
 			for (var prop in obj) {
 				if (obj.hasOwnProperty(prop)) {
 					var wtoken = encodeURIComponent(obj[prop].edittoken);
 					if (obj[prop].missing != undefined && obj[prop].touched == undefined) {
 						$.ajax({
 							type: "POST",
 							url: wgServer + wgScriptPath + "/api.php",
 							data: "action=upload&url="+url+"&filename="+filename+"&token="+wtoken+"&comment="+encodeURIComponent("Imported from Facebook. View on Facebook at http://facebook.com/"+id+". Uploaded to Facebook by "+name),
 							success: function(data){
 								window.location.replace(wgServer + wgScriptPath + "/index.php/File:" + filename);
 							},
 							error: function (xhr, ajaxOptions, thrownError) {
 								alert("Something went wrong. Was that a valid Facebook ID or URL?");
 							}
 						});
 					}
 				}
 			}
 		}
 	});
}