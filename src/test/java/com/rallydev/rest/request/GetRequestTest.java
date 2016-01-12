package com.rallydev.rest.request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.client.BasicAuthClient;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;

public class GetRequestTest {
	
	 private static String server = "https://rally1.rallydev.com";
	    private static  String userName = "mgadiraju@prokarma.com";
	    private static String password = "Prokarma@123";
	    private static BasicAuthClient client;
	
	public static void main(String args[]) throws URISyntaxException, IOException{
		
		RallyRestApi restApi=new RallyRestApi(new URI(server), userName, password);
		 // BasicAuthClient client = new BasicAuthClient(new URI(server), userName, password);
		  
		  QueryRequest subscriptionRequest = new QueryRequest("Subscriptions");
          subscriptionRequest.setFetch(new Fetch("Name", "SubscriptionID", "Workspaces", "Name"));

          QueryResponse subscriptionQueryResponse = restApi.query(subscriptionRequest);
          JsonArray subscriptionQueryResults = subscriptionQueryResponse.getResults();
          JsonElement subscriptionQueryElement = subscriptionQueryResults.get(0);
          JsonObject subscriptionQueryObject = subscriptionQueryElement.getAsJsonObject();
          System.out.println("test "+subscriptionQueryObject);
          String subID = subscriptionQueryObject.get("SubscriptionID").toString();


          System.out.println("Read Subscription: " + subID);

          // Grab Workspaces Collection
          JsonElement jsonElement = subscriptionQueryObject.get("Workspaces");
          System.out.println("workspaces "+jsonElement);
		JsonArray myWorkspaces = jsonElement.getAsJsonArray();

          // Initialize Project counter
          int numberProjects = 0;

          for (int i=0; i<myWorkspaces.size(); i++) {
                  JsonObject workspaceObject = myWorkspaces.get(i).getAsJsonObject();
                  String workspaceRef = workspaceObject.get("_ref").getAsString();                

              GetRequest workspaceRequest = new GetRequest(workspaceRef);
              workspaceRequest.setFetch(new Fetch("Name", "Projects"));
              GetResponse workspaceResponse = restApi.get(workspaceRequest);
              JsonObject workspaceObj = workspaceResponse.getObject();

              String workspaceName = workspaceObj.get("Name").getAsString();
              System.out.printf("Workspace %d ==> %s\n", i, workspaceName);

              JsonArray myProjects = workspaceObj.get("Projects").getAsJsonArray();

              for (int j=0; j<myProjects.size(); j++)
              {
                  JsonObject projectObject = myProjects.get(j).getAsJsonObject();
                  String projectRef = projectObject.get("_ref").getAsString();
                  GetRequest projectRequest = new GetRequest(projectRef);
                  projectRequest.setFetch(new Fetch("Name"));
                  GetResponse projectResponse = restApi.get(projectRequest);              

                  JsonObject projectObj = projectResponse.getObject();
                  String projectName = projectObj.get("Name").getAsString();
                  System.out.printf("==> Project %d ==> %s\n", j, projectName);

                  numberProjects++;
              }
          }

          String numberWorkspacesStr =  String.valueOf(myWorkspaces.size());
          String numberProjectsStr = String.valueOf(numberProjects);

          System.out.println("Total Workspaces: " + numberWorkspacesStr);
          System.out.println("Total Projects: " + numberProjectsStr);

          restApi.close();

		  
		  
	}
    
    @Test
    public void shouldReturnCorrectUrlWithAbsoluteRef() {
        GetRequest req = new GetRequest("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlWithRelativeRef() {
        GetRequest req = new GetRequest("/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlWithFetchParams() {
        GetRequest req = new GetRequest("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js");
        req.setFetch(new Fetch("Name", "Description"));
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=Name%2CDescription");
    }

    @Test
    public void shouldReturnCorrectUrlForUser() {
        Assert.assertEquals(new GetRequest("User").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("user").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("/user").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("/user.js").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("/user/12345.js").toUrl(), "/user/12345.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlForSubscription() {
        Assert.assertEquals(new GetRequest("Subscription").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("subscription").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("/subscription").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("/subscription.js").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("/subscription/12345.js").toUrl(), "/subscription/12345.js?fetch=true");
    }
}
