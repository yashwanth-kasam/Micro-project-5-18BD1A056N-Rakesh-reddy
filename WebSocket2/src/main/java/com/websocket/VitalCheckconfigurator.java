package com.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.*;

public class VitalCheckconfigurator extends ServerEndpointConfig.Configurator 
{
	public void modifyHandshake(ServerEndpointConfig sec,HandshakeRequest request,HandshakeResponse response)
	{
		sec.getUserProperties().put("username",(String)((HttpSession)request.getHttpSession()).getAttribute("username"));
	}
}