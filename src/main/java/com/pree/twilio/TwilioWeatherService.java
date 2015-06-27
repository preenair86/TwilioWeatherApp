// RESTful web service for Twilio Weather App.
// 
// The web service obtains a zipcode from callers and reads out the weather information in the
// input city.
// 
// See readme.jsp for more documentation.
//
// Author: Pree Nair (pree.nair86@gmail.com)

package com.pree.twilio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.javatuples.Pair;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.core.HttpContext;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.verbs.Gather;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

@Path("")
public class TwilioWeatherService {
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
	private static Logger log = Logger.getLogger(TwilioWeatherService.class);

	// Remove $currentSuffix from $currentAbsolutePath and append $newSuffix
	// to get the new absolute path. This function is useful to get the 
	// absolute path of web service apis based on the current absolute path.
	private String getNewAbsolutePath(String currentAbsolutePath,
			String currentSuffix, String newSuffix) {
		int currentSuffixIndex = currentAbsolutePath.indexOf(currentSuffix);
		return currentAbsolutePath.substring(0, currentSuffixIndex) + newSuffix;
	}

	// Displays the different operations supported by the web service.
	@GET
	@Path("help")
	public String readMe() {
		log.debug("In readme.");
		return "<h1>Twilio Weather App</h1>"
				+ "<p>Hello, welcome to twilio weather web service.</p>"
				+ "Supported operations are "
				+ "<ul><li>/twilio/help - displays the supported api.</li>"
				+ "<li>/twilio/getZipcode - asks the caller for the zipcode.</li>"
				+ "<li>/twilio/sayWeather - read the weather in the input zipcode.</li></ul>";
	}

	// Prompts the user to enter the zipcode on the key pad.
	@Path("getZipcode/")
	@GET
	@Produces("application/xml")
	public Response getZipcode(@Context HttpContext context) {
		log.debug("In getZipcode");
		// Generate the TwiML message.
		TwiMLResponse twiml = new TwiMLResponse();
		Gather gather = new Gather();
		gather.setMethod("GET");
		gather.setFinishOnKey("#");
		String outputPath = getNewAbsolutePath(context.getRequest()
				.getAbsolutePath().toString(), "getZipcode",
				"sayWeather");
		log.info("Output path is " + outputPath);
		gather.setAction(outputPath);
		Say askZipcode = new Say("Please enter the zipcode followed by #.");
		try {
			gather.append(askZipcode);
		} catch (TwiMLException e1) {
			e1.printStackTrace();
		}
		try {
			twiml.append(gather);
		} catch (TwiMLException e) {
			e.printStackTrace();
		}
		System.out.println("Returning" + twiml.toXML());
		return Response.status(200).type(MediaType.TEXT_XML)
				.entity(twiml.toXML()).build();
	}

	// Say the weather in the input zipcode.
	@Path("sayWeather/")
	@GET
	@Produces("application/xml")
	public Response sawWeather(@QueryParam("Digits") String zipcode) {
		log.debug("Inside sayWeather");
		// Generate the TwiML message.
		TwiMLResponse twiml = new TwiMLResponse();
		Pair<String, String> cityAndWeather = getCityAndWeather(zipcode);
		Say sayResult = new Say("The zipcode entered is " + zipcode
				+ ". Weather in " + cityAndWeather.getValue0() + " is " + cityAndWeather.getValue1());
		try {
			twiml.append(sayResult);
		} catch (TwiMLException e) {
			e.printStackTrace();
		}
		System.out.println("Returning" + twiml.toXML());
		return Response.status(200).type(MediaType.TEXT_XML)
				.entity(twiml.toXML()).build();
	}

	private Pair<String, String> getCityAndWeather(String zipcode) {
		UriBuilder builder = UriBuilder.fromPath(WEATHER_API_URL);
		builder.queryParam("zip", zipcode);
		try {
			URL weatherUrl = builder.build().toURL();
			System.out.println("Url is " + weatherUrl.toString());
			HttpURLConnection weatherConnection = (HttpURLConnection) weatherUrl.openConnection();
			weatherConnection.setRequestMethod("GET");
			String response  = readResponse(weatherConnection);
			System.out.println("Response is " + response);
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(response).getAsJsonObject();
			String cityName = obj.get("name").getAsString();
			String weather = obj.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();
			System.out.println("City name is " + cityName + ".Weather is " + weather);
			return Pair.with(cityName, weather);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String readResponse(HttpURLConnection connection) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder output = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				output.append(inputLine);
			}
			in.close();
			return output.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
