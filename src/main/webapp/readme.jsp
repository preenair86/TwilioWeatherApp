<html>
<title>Twilio Webservice App</title>
<body>
	<h1>Twilio Webservice App</h1>
	This project develops a twilio based web application, which asks the caller to enter a zip code. 
	Then a RESTful web service is used to say the weather in that area to the caller. 
	
	<h2>Operations supported by the developed RESTful web service.</h2>
	Make sure to change the hostname if different from the one used below.
		<ul>
			<li>http://localhost:8080/TwilioWeatherApp/weatherapp/help - displays the supported api.</li>
			<li>http://localhost:8080/TwilioWeatherApp/weatherapp/getZipCode - asks the caller for the zipcode.</li>
			<li>http://localhost:8080/TwilioWeatherApp/weatherapp/sayWeather - say the weather in the input zipcode.</li>
		</ul>

	<h3>Phase 1 - Simple TwiML Phone Buzz</h3>
	Phase 1 requires the development of a web application which requests a
	number from the user and computes the Fizz Buzz pattern for the number.
	The input number is obtained from the user and the output pattern is
	played back to the user using the
	<a href="https://www.twilio.com/">TwiML</a> api. The RESTful webservice
	developed here will create TwiML instructions to gather the number from
	the user, compute the Fizz Buzz pattern for it and then generate TwiML
	output to play the pattern to the user. The developed webservice is
	also hosted in AWS at
	<a
		href="http://phonebuzzapp-ktfetz3q44.elasticbeanstalk.com/phonebuzz/getNumber">this
		URI</a>. There are three options to view the output
	<ul>
		<li>Call the Twilio number (732) 333-6317 from your phone.</li>
		<li>Point your twilio number to the AWS URI <a
			href="http://phonebuzzapp-ktfetz3q44.elasticbeanstalk.com/phonebuzz/getNumber">
				"http://phonebuzzapp-ktfetz3q44.elasticbeanstalk.com/phonebuzz/getNumber"</a></li>
		<li>Build this project, host in your server and point your twilio
			number at the server URI.</li>
	</ul>
</body>
</html>
