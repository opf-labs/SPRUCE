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
 
//Only run for Facebook users and when the write API is enabled
if ($.inArray("fb-user", wgUserGroups) != '-1' && wgEnableWriteAPI) {
	//Only want to create pages, so need to query the user page to check it exists
	$.ajax({
		type: "GET",
		url: wgServer + wgScriptPath + "/api.php?format=json&action=query&prop=info&titles=User:"+wgUserName,
		dataType: 'json',
		success: function(data) {
			var obj = data.query.pages;
			for (var prop in obj) {
				if (obj.hasOwnProperty(prop)) {
					//only proceed if the page doesn't exist
					if (obj[prop].missing != undefined && obj[prop].touched == undefined) {
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
								//make request for user profile information
								makeFacebookRequest("", accessToken);
								//what connections do we want to request?
								var fbOptions = fbImportOptions.split(",");
								for (var i = 0; i < fbOptions.length; i++) {
									//make request for connection
									makeFacebookRequest(fbOptions[i], accessToken);
								}
								$( "#fb-import-form" ).dialog( "open" );
							}
						});
					}
				}
			}
		}
	});
}

//handles jQuery UI form
$(function() {
	var buttonsOpts = {}
	var placeholder = {};
	placeholder["User Page"] = "This is a User Page. If it's yours, please consider editing it and adding some information. If not, you'll find a link to yours at the top of the page.";
	var submitted = false;
	buttonsOpts["Save to your "+wgSiteName+" User Page"] = function() {
		var output = {};
		var count = 0;
		$("#fb-import-form input, textarea").each(function() {
			var val = $(this).val();
			if (val.length > 0) { //don't want to be submitting empty fields
				var label = $("label[for="+$(this).attr('id')+"]");
				output[label.text()] = val;
				count++; //counting the number of non-empty fields. If all empty, submission == cancellation
			}
		});
		if (count > 0) {
			submitted = true;
			updateUserPage(output); //update the user page
		}
		$( this ).dialog( "close" );
	}
	buttonsOpts["Cancel"] = function() {
		$( this ).dialog( "close" );
	}
  buttons : buttonsOpts;
	$( "#fb-import-form" ).dialog({
		autoOpen: false,
		height: $(window).height(), //expecting long forms
		width: 600,
		modal: true,
		buttons : buttonsOpts,
		close: function(submitted) {
			if(!submitted) {
				updateUserPage(placeholder); //update User Page with placeholder text to avoid annoying users with repeated forms
				displayFeedback("cancel");
			} else {
				displayFeedback("success");
			}
		}
	});
});


//handles dialogs following form submission
function displayFeedback(div) {
	$( "#fb-import-"+div ).dialog({
		modal: true,
		buttons: {
			"Go to User Page": function() {
				$( this ).dialog( "close" );
				window.location.href = wgServer + wgScriptPath + "/index.php/User:" + wgUserName;
			},
			"Stay on this page": function() {
				$( this ).dialog( "close" );
			},
		}
	});
}

function makeFacebookRequest(field, accessToken) {
	FB.api('/me/'+field+'?access_token='+accessToken, function(response) {
		$.each(response, function(key, val){
			var selector = "#fb-import-form #";
			//if empty field supplied, want to update field corresponding to each key, else update supplied field
			//i.e. multiple fields making up basic request
			if (field == "") {
				selector += key;
			}
			else {//request for specified connection
				selector += field;
			}
			if ($(selector).length > 0) {
				var curr = $(selector).val();
				$(selector).val(curr+parse(key, val)); //Concatenate with existing field contents. Needs to be smarter.
			}
		});
	});
}

//traverses JSON, constructing strings to populate form fields
//returns string containing one or more lines of form "Name/other info [(http://www.facebook.com/RESOURCE_ID)]"
//URL only included where id is available
function parse(key, val) {
	//Ignoring these fields for now. Should possibly be making use of category - but only for certain kinds of content
	if (key == "category" || key == "created_time" || key == "next" ) {
		return "";
	}
	var fb_string_root = "(http://www.facebook.com/";
	if (key == "id") {
		return fb_string_root+val+")";
	}
	if(typeof val == "object") {
		var output = "";
		$.each(val, function(k,v){
			var add = parse(k,v);
			if (add.length > 0) {
				if (add.indexOf(fb_string_root) != 0) {
					if (output.indexOf(fb_string_root) == 0) {
						output = add + " " + output; //If ID encountered before other info (name), place other info before FB URL.
					}
					else {
						if (output != "") {
							output += "\n"; //If appending to text containing FB url, add new line first.
						}
						output += add;
					}
				}
				else {
					if (output != "") {
						output += " ";
					}
					output += add;
				}
			}
		});
		return output;
	}
	else {
		return val;
	}
}

function updateUserPage(input) {
	//Get edit token for page
	$.ajax({
		type: "GET",
		url: wgServer + wgScriptPath + "/api.php",
		data: "format=json&action=query&prop=info&intoken=edit&titles=User:"+wgUserName,
		dataType: 'json',
		success: function(data) {
			var obj = data.query.pages;
			for (var prop in obj) {
				if (obj.hasOwnProperty(prop)) {
					wtoken = encodeURIComponent(obj[prop].edittoken);
					//Got the token! Can now update the page - after checking again that it doesn't exist.
					if (obj[prop].missing != undefined && obj[prop].touched == undefined) {
						//Content of each form field is added as a section headed by label
						$.each(input, function(id, val) {
							if(val != "") {
								$.ajax({
									type: "POST",
									url: wgServer + wgScriptPath + "/api.php",
									data: "action=edit&title=User:"+wgUserName+"&section=new&summary="+encodeURIComponent(id)+"&text="+encodeURIComponent(val.replace(/\n\r?/g,"<br />"))+"&token="+wtoken,
								});
							}
						});
					}
				}
			}
		}
	});
}