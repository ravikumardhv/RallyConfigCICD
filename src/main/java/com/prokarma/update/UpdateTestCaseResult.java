package com.prokarma.update;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.response.CreateResponse;

public class UpdateTestCaseResult {
	private static String server = "https://rally1.rallydev.com";
    private static  String userName = "mgadiraju@prokarma.com";
    private static String password = "Prokarma@123";
    
    private String workSpaceName="Prokarma";
    private String projectName="Sample Project";
    private String iterationName="Iteration 1";
    
    private RallyRestApi restApi;
	
	public UpdateTestCaseResult() {
		
	}
	
	private void getRallyAPI() throws URISyntaxException{
		 restApi=new RallyRestApi(new URI(server), userName, password);
		 
		
	}
	
	public void updateTestCaseWithResult(String workSpaceRef,String projectRef,String testCaseRef,String status) throws IOException, URISyntaxException{
		getRallyAPI();
		JsonObject testResult=new JsonObject();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		testResult.addProperty("Build", 1);
		testResult.addProperty("Date", nowAsISO);
		testResult.addProperty("Verdict", status);
		testResult.addProperty("TestCase", removeExtraWSURL(testCaseRef));
		
		
		CreateRequest createTestCaseResult=new CreateRequest("TestCaseResult", testResult);
		CreateResponse create = restApi.create(createTestCaseResult);
		String[] errors = create.getErrors();
		for(String error:errors){
			System.out.println("err "+error);
		}
		
		restApi.close();
	}
	
	private static String removeExtraWSURL(String refURL){
		refURL = refURL.replace("https://rally1.rallydev.com/slm/webservice/v2.0", "");
		refURL = refURL.replace("\"", "");
		
		return refURL;
	}

}
