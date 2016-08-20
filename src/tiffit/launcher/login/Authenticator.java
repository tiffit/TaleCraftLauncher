package tiffit.launcher.login;

import java.net.Proxy;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import tiffit.launcher.Main;
import tiffit.launcher.filedata.AuthStorageData;

public class Authenticator {

	public static final class Tokens {
		public String accessToken;	
		public String clientToken;
	}
	
	public static UserAuthentication login(String login, String password){
		YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, AuthStorageData.AUTH_STORAGE_DATA.getClientToken());
		UserAuthentication auth = service.createUserAuthentication(Agent.MINECRAFT);
		auth.setPassword(password);
		auth.setUsername(login);
		try {
			auth.logIn();
		} catch (AuthenticationException e) {
			JOptionPane.showMessageDialog(Main.frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		auth.loadFromStorage(auth.saveForStorage());
		return auth;
	}
	
	public static UserAuthentication fromMap(Map<String, Object> map){
		if(map == null) return null;
		YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, AuthStorageData.AUTH_STORAGE_DATA.getClientToken());
		UserAuthentication auth = service.createUserAuthentication(Agent.MINECRAFT);
		auth.loadFromStorage(map);
		return auth;
	}
	
	public static String postRequest(String method, String json) throws Exception{
		HttpClient client = new HttpClient();
	    client.getHostConfiguration().setHost("authserver.mojang.com", 443, "https");

	    PostMethod postMethod = null;

	    try {
	        postMethod = new PostMethod(method);
	        postMethod.setRequestHeader("Content-Type", "application/json" );
	        postMethod.setRequestEntity(new StringRequestEntity(json, "application/json", "utf-8"));

	        int status = client.executeMethod(postMethod);

	        if (status != 200){
	        	System.out.println(postMethod.getResponseBodyAsString());
	            throw new Exception("Request failed with message: " + postMethod.getStatusText() + "; Error: " + postMethod.getResponseBodyAsString());
	        }
	        String str = postMethod.getResponseBodyAsString();
			return str;
	    } finally {
	        postMethod.releaseConnection();
	    }
	}
	
}
