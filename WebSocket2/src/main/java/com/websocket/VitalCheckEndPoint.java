package com.websocket;

import java.io.StringWriter;
import java.util.*;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.json.*;


@ServerEndpoint(value="/VitalCheckEndPoint",configurator=VitalCheckconfigurator.class)
public class VitalCheckEndPoint {
	static Set<Session> subscribers=Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(EndpointConfig endpointconfig,Session userSession) {
		System.out.println("Connection Opened  for "+endpointconfig.getUserProperties().get("username"));
		userSession.getUserProperties().put("username",endpointconfig.getUserProperties().get("username"));
		subscribers.add(userSession);
	}
	
	@OnMessage
	public void handleMessage(String message,Session userSession)
	{
	   String username=(String)userSession.getUserProperties().get("username");
	   System.out.println(username+"  messaged "+message);
	   
	   if(username!=null && !username.equals("doctor"))
	    {
	       subscribers.stream().forEach(x->{
	            try {
	            	String[] z=message.split(",");
	            	System.out.println("current user is "+x.getUserProperties().get("username"));
		 if(x.getUserProperties().get("username").equals("doctor"))
		 {
		     if(Integer.parseInt(z[0])< 90)
		    	 x.getBasicRemote().sendText(buildJSON(username,z[0]+","+z[1]+","+z[2]));
		  }
		}
		catch(Exception e)
		{
		   e.printStackTrace();
		 }
		 });
		 }
	   else if(username!=null && username.equals("doctor"))
	    {
	     String[] messages=message.split(",");
	     String patient=messages[0];
	     String subject=messages[1];
	     subscribers.stream().forEach(x->{
	     try 
	    {
	        if(subject.equals("ambulance"))
	        {
	        	
	           if(x.getUserProperties().get("username").equals(patient))
	           {
	        	   x.getBasicRemote().sendText(buildJSON("doctor","has summoned an ambulance"));
	           }
	           else if(x.getUserProperties().get("username").equals("ambulance"))
	           {
	        	   x.getBasicRemote().sendText(buildJSON(patient,messages[2]+","+"Requires an ambulance"));
	           }
	        }
	        else if(subject.equals("medication"))
	         {
	             if(x.getUserProperties().get("username").equals(patient))
	             {
	            	 x.getBasicRemote().sendText(buildJSON("doctor",messages[2]+","+messages[3]));
	             }
		
	         }
	 }
	 catch(Exception e)
	 {
	     e.printStackTrace();
	 }
	 });
	 }
	}

	@OnClose
	public void handleClose(Session userSession)
	{
	  subscribers.remove(userSession);
	 }

	 @OnError
	  public void handleError(Throwable t)
	  {	}
		
	  
	 private String buildJSON(String username,String message)
	{
	   JsonObject jsonObject=Json.createObjectBuilder().add("message",username+","+message).build();
	   StringWriter stringWriter=new StringWriter();
	   try(JsonWriter jsonWriter=Json.createWriter(stringWriter))
	     {
	         jsonWriter.write(jsonObject);
	     }
	   String res=stringWriter.toString();
	   System.out.println(res);
	   return res;
	 }

}
