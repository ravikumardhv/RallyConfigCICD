package com.prokarma.getTestCases;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class RetriveTestCases {
	
	private static String server = "https://rally1.rallydev.com";
    private static  String userName = "prokarmarally@gmail.com";
    private static String password = "Prokarma@123";
    
    private String workSpaceName="Prokarma";
    private String projectName="Sample Project";
    private String iterationName="Iteration 1";
    
    private RallyRestApi restApi;
	
	public RetriveTestCases() {
		
	}
	
	private void getRallyAPI() throws URISyntaxException{
		 restApi=new RallyRestApi(new URI(server), userName, password);
		 
		
	}
	
	private String getWorkSpaceRef() throws IOException{
		QueryRequest workSpaceRequest=new QueryRequest("Workspaces");
		QueryFilter filter=new QueryFilter("Name", "=", workSpaceName);
		filter.and(new QueryFilter("State","=","Open"));
		workSpaceRequest.setFetch(new Fetch("Name", "_ref"));
		workSpaceRequest.setQueryFilter(filter);
		QueryResponse query = restApi.query(workSpaceRequest);
		 JsonArray workSpaceQueryResults = query.getResults();
         JsonElement workSpaceQueryElement = workSpaceQueryResults.get(0);
        // System.out.println(workSpaceQueryElement);
         JsonObject workSpaceQueryObject = workSpaceQueryElement.getAsJsonObject();
         String workSpaceRef = workSpaceQueryObject.get("_ref").toString();
        return workSpaceRef;
		
	}
	
	private String getProjectRef(String workSpaceRef) throws IOException{
		QueryRequest projectsRequest=new QueryRequest("Projects");
		QueryFilter filter=new QueryFilter("Name", "=", projectName);
		filter.and(new QueryFilter("Workspace","=",workSpaceRef));
		projectsRequest.setFetch(new Fetch("Name", "_ref"));
		projectsRequest.setQueryFilter(filter);
		QueryResponse query = restApi.query(projectsRequest);
		JsonArray projectQueryResults = query.getResults();
		 JsonElement projectQueryElement = projectQueryResults.get(0);
	        //System.out.println(projectQueryResults);
	         JsonObject projectQueryObject = projectQueryElement.getAsJsonObject();
		
		String projectRef = projectQueryObject.get("_ref").toString();
		/*System.out.println("projectRef "+projectRef);
		String removeExtraWSURL = removeExtraWSURL(projectRef,"project");
		System.out.println("remove "+removeExtraWSURL);
		GetRequest projectRequest1=new GetRequest(removeExtraWSURL);
		GetResponse workspaceResponse = restApi.get(projectRequest1);
		 JsonObject workspaceObj1 = workspaceResponse.getObject();
		System.out.println(workspaceObj1);*/
		return projectRef;
	}
	
	private String getTestCasesByIteration(String workSpaceRef,String projectRef) throws IOException{
		QueryRequest testCasesRequest=new QueryRequest("TestCases");
		testCasesRequest.setWorkspace(workSpaceRef);
		testCasesRequest.setProject(projectRef);
		/*QueryFilter filter=new QueryFilter("Artifact.Name", "=", iterationName);
		testCasesRequest.setQueryFilter(filter);*/
		QueryResponse query = restApi.query(testCasesRequest);
		JsonArray projectQueryResults = query.getResults();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<projectQueryResults.size();i++){
			JsonElement jsonElement = projectQueryResults.get(i);
			JsonObject jsonQueryObject = jsonElement.getAsJsonObject();
	        String _ref = jsonQueryObject.get("_ref").toString();
	        String notes = jsonQueryObject.get("Notes").toString();
	        if(null!=notes && notes.length()>2 ){
	        	 notes=notes.replaceAll("\\\\", "");
	 	        notes=notes.replaceAll("<div>", "");
	 	        notes=notes.replaceAll("</div>", "");
	 	        notes=notes.replaceAll("&lt;", "<");
	 	        notes=notes.replaceAll("&gt;", ">");
	 	        notes=notes.replaceAll("&nbsp;", "");
	 	        notes=notes.replaceAll("</p>", "");
	 	       notes=notes.replaceAll("</p>", "");
	 	      notes=notes.replaceAll("</span>", "");
	 	     notes=notes.replaceAll("<p class=\"MsoNormal\" style=\"vertical-align: baseline;\">", "");
	 	    notes=notes.replaceAll("<span style=\"line-height: 15.7333px;\">", "");
	 	   notes=notes.replaceAll("<p class=\"MsoNormal rally-rte-class-0b3ec748f\">", "");
	 	    notes=notes.replaceAll("<span class=\"rally-rte-class-0f8e27bcb\">", "");
	 	        notes=notes.replaceFirst(">", "> <parameter name=\"tcNumber\" value="+_ref+"></parameter>");
	 	        //notes=notes.replaceFirst("</test>", " <parameter name=\"tcNumber\" value="+_ref+"></parameter></test>");
	 	       notes=notes.replaceFirst("\"", "");
	 	      notes=notes.substring(0, notes.length()-1);
	 	       sb.append(notes);
	        }
	       
	       
	       
		}
		String string = sb.toString();
		System.out.println(string);
		return string;
		
		
	}
	
	private void writeToFile(String file) throws IOException{
		java.nio.file.Files.write(Paths.get("./src/test/resources/com/automation/Rally/autmation.xml"), file.getBytes());
	}
	
	
	
	private static String removeExtraWSURL(String refURL){
		refURL = refURL.replace("https://rally1.rallydev.com/slm/webservice/v2.0", "");
		refURL = refURL.replace("\"", "");
		
		return refURL;
	}
	
	public static void main(String args[]) throws URISyntaxException, IOException{
		
		java.nio.file.Files.deleteIfExists(Paths.get("./src/test/resources/com/automation/Rally/autmation.xml"));
		RetriveTestCases testCases=new RetriveTestCases();
		
		testCases.getRallyAPI();
		String workSpaceRef = testCases.getWorkSpaceRef();
		System.out.println(workSpaceRef);
		String projectRef = testCases.getProjectRef(workSpaceRef);
		System.out.println(projectRef);
		String testCasesByIteration = testCases.getTestCasesByIteration(removeExtraWSURL(workSpaceRef),removeExtraWSURL(projectRef));
		System.out.println("testcases "+testCasesByIteration);
	//	testCases.updateTestCaseWithResult(removeExtraWSURL(workSpaceRef),removeExtraWSURL(projectRef), testCasesByIteration, "Pass");
		testCases.addSuiteFile(removeExtraWSURL(workSpaceRef),removeExtraWSURL(projectRef),testCasesByIteration);
		testCases.closeConnection();
		
	}
	
	
	public void generateTestNgScripts() throws IOException, URISyntaxException{
		java.nio.file.Files.deleteIfExists(Paths.get("./src/test/resources/com/automation/Rally/autmation.xml"));
		
		getRallyAPI();
		String workSpaceRef = getWorkSpaceRef();
		System.out.println(workSpaceRef);
		String projectRef = getProjectRef(workSpaceRef);
		System.out.println(projectRef);
		String testCasesByIteration = getTestCasesByIteration(removeExtraWSURL(workSpaceRef),removeExtraWSURL(projectRef));
		System.out.println("testcases "+testCasesByIteration);
		addSuiteFile(removeExtraWSURL(workSpaceRef),removeExtraWSURL(projectRef),testCasesByIteration);
		closeConnection();
		
	}
	
	private void closeConnection() throws IOException{
		 restApi.close();
	}
	
	private void addSuiteFile(String workSpaceRef,String prjectRef,String testCaseString) throws IOException{
		
		StringBuffer suite=new StringBuffer();
		String suiteHead="<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
				+ "\n <!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" ><suite name=\"Suite1\">";
		String workSpaceParameter="<parameter name=\"workspaceRefNumber\" value=\""+workSpaceRef+"\"/>";
		String projectParameter="<parameter name=\"projectRefNumber\" value=\""+prjectRef+"\"/>";
		
		suite.append(suiteHead);
		suite.append("\n");
		suite.append(workSpaceParameter);
		suite.append("\n");
		suite.append(projectParameter);
		suite.append("\n");
		suite.append(testCaseString);
		
		String suiteTail="</suite>";
		suite.append(suiteTail);
		writeToFile(suite.toString());
	}

}
