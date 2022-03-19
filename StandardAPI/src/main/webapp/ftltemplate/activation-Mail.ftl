[#ftl]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Welcome to FirstRain!</title>
</head>

<body>
<table width="100%" cellspacing="0" cellpadding="0" border="0" style="font-family: Arial,Helvetica,sans-serif;">
	<tbody>
		<tr valign="top">
		  <td valign="top">
			<table width="100%" cellspacing="0" cellpadding="0" border="0" style="border:1px solid #999999;">
          		<tbody>
            		<tr valign="top">
		              <td valign="top">
							<div style="background-color: #17a6e5;">
		                  		<table width="100%" cellspacing="0" cellpadding="0" border="0" style="background-color: #17a6e5; padding: 0px; margin: 0px; width: 100%; height: 60px; background: -moz-linear-gradient(top,  #17a6e5 0%, #2884c7 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#17a6e5), color-stop(100%,#2884c7));background: -webkit-linear-gradient(top,  #17a6e5 0%,#2884c7 100%);background: -o-linear-gradient(top,  #17a6e5 0%,#2884c7 100%);background: -ms-linear-gradient(top,  #17a6e5 0%,#2884c7 100%);background: linear-gradient(top,  #17a6e5 0%,#2884c7 100%);filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#17a6e5', endColorstr='#2884c7',GradientType=0 );">
		                    		<tbody>
		                      			<tr valign="top">
		                      				[#assign imgUrl][#if imgBaseURL?starts_with("http") || imgBaseURL?starts_with("https")]${imgBaseURL}[#else]https:${imgBaseURL}[/#if][/#assign]
					                        <td style="padding-top: 15px; padding-bottom: 15px; padding-left:15px;"><a href="http://www.firstrain.com/" target="_blank" style="text-decoration: none;"> <img width="134" height="25" border="0" alt="FirstRain" src="${imgUrl}/images/email-logo-and-tagline1.png"/> </a></td>
											<td align="right" style="padding-top: 10px;padding-right:10px;">
												<table cellspacing="0" cellpadding="0" border="0" style="font-family: Arial,Helvetica,sans-serif;">
													<tbody></tbody>
												</table>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
					<tr valign="top">
		              <td valign="top"><table width="100%" cellspacing="0" cellpadding="0" border="0" style="font-family: Arial,Helvetica,sans-serif;">
		                  <tbody>
		                    <tr valign="top">
		                      <td style="padding: 35px 20px 35px 20px; font-family: Arial,Helvetica,sans-serif;">											
		                      	[#if password = "*****"]
									<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #3b4348; text-align: left; display:block; margin:0; padding:0;">Hi ${admin.firstName},  </p>
									[#if multiEmails ??]
										[#if multiEmails?size &gt; 1]
											<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">Please note that FirstRain accounts for the following users have been created successfully. An email with log-in credentials has been sent to each of the new users below:</p>
										[#else]
											<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">Please note that FirstRain account for the following user has been created successfully. An email with the log-in credentials has been sent to the new user.</p>
										[/#if]
										<ul style="padding-top: 25px;">
											[#list multiEmails?sort as email]
												<li style="list-style-type: disc; margin-bottom: 10px; margin-left: 20px;">
													<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #3b4348; text-align: left; display:block; margin:0; padding:0;">${email}</p>
												</li>
											[/#list]
										</ul>
									[#else]
										<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">Please note that FirstRain account for the following user has been created successfully. An email with the log-in credentials has been sent to the new user.</p>								
										<ul style="padding-top: 25px;">
											<li style="list-style-type: disc; margin-bottom: 10px; margin-left: 20px;">
												<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #3b4348; text-align: left; display:block; margin:0; padding:0;">${user.userName}</p>
											</li>
										</ul>
									[/#if]
									<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">For any further information or assistance, please feel free to contact FirstRain support team at <a href="mailto:support@firstrain.com">support@firstrain.com</a> or +1 (650) 356-9010.</p>
								[#else]
									<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #3b4348; text-align: left; display:block; margin:0; padding:0;">Hi ${user.firstName},  </p>
									<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">Your organization has created a FirstRain account for you! You will start receiving relevant, real-time intelligence to your account immediately. To use the FirstRain <a href="http://firstra.in/ipadApp">iPad</a>, <a href="http://firstra.in/j6cOjJ">iPhone</a>, or <a href="https://play.google.com/store/apps/details?id=com.firstrain.frandroid">Android</a> apps, you can sign in with:</p>
									<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #3b4348; text-align: left; display:block; margin:35px 0 0 0; padding:0;"><strong>Username:</strong> ${user.userName}</p>
									<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #3b4348; text-align: left; display:block; margin:0; padding:0;"><strong>Password:</strong> ${password}</p>
	                       			<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">
										We recommend saving this email, in case you need to update your settings in the future. 
	                       			</p>
	                       			<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">
										For more information about FirstRain's features and how it can help you become an expert on your top customers and markets, visit the FirstRain Learning Center. 
	                       			</p>
									[#if  admin ?? && admin.email = "support@firstrain.com"]
										<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">Don't hesitate to contact me for help or you can contact support at <a href="mailto:support@firstrain.com">support@firstrain.com</a> or +1 (650) 356-9010 or Contact your FirstRain administrator.</p>
									[#else]
									 	<p style="font-family: Arial,Helvetica,sans-serif; font-size: 18px; color: #333333; text-align: left; display:block; margin:35px 0 0 0; padding:0;">Have questions or need help? Just send an email to <a href="mailto:support@firstrain.com">support@firstrain.com</a> or call us at +1 650 356 9010.</p>
									[/#if]
								[/#if]
								<p style="color:#333333; font-size:18px; line-height:25px; font-family: Arial,Helvetica,sans-serif; margin:35px 0 0 0; padding:0;"> 
                       				Best,<br/>
                       				The FirstRain Team<br/>
									<a href="http://www.firstrain.com/" target="_blank">www.firstrain.com</a>
                      			</p>	
		                     </td>
		                     </tr> 	
						</tbody>
					</table>
				  </td>
				</tr>
			</tbody>
		</table>
		</td>
		</tr>
		</tbody>
		</table>
	</body>
</html>