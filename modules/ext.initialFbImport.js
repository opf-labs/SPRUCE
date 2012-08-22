alert("!");
if ($.inArray("fb-user", wgUserGroups) != '-1' && wgEnableWriteAPI) {
	$.ajax({
		type: "GET",
		url: wgServer + wgScriptPath + "/api.php?format=json&action=query&prop=info&intoken=edit&titles=User:"+wgUserName,
		dataType: 'json',
		success: function(data) {
			var obj = data.query.pages;
			for (var prop in obj) {
				if (obj.hasOwnProperty(prop)) {
					wtoken = encodeURIComponent(obj[prop].edittoken);
					//if (obj[prop].missing != undefined && obj[prop].touched == undefined) {
						FB.init({
							appId  : fbAppId,
							status : true, // check login status
							cookie : true, // enable cookies to allow the server to access the session
							xfbml  : true  // parse XFBML
						});
						FB.getLoginStatus(function(response) {
							if (response.status === 'connected') {
								// the user is logged in and has authenticated your
								// app, and response.authResponse supplies
								// the user's ID, a valid access token, a signed
								// request, and the time the access token
								// and signed request each expire
								var uid = response.authResponse.userID;
								var accessToken = response.authResponse.accessToken;
								FB.api('/me?access_token='+accessToken, function(response) {
									alert(JSON.stringify(response));
									//updateUserPage(wtoken, JSON.stringify(response));
								});
							}
						});
					//}
				}
			}
		}
	});
}

function updateUserPage(token, user_data) {
	$.ajax({
		type: "POST",
		url: "http://localhost/wiki/api.php",
		data: "action=edit&title=User:"+wgUserName+"&createonly=true&section=new&summary=testing&text="+encodeURIComponent(user_data)+"&token="+token,
		success: function(data) {}
	});
}