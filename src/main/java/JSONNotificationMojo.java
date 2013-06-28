import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.json.simple.JSONObject;

/**
 * Sends JSON notification of the GAV information to given endpoints
 * 
 * @goal notify
 */

public class JSONNotificationMojo extends AbstractMojo {

	/**
	 * The groupId
	 * 
	 * @parameter expression="${notify.groupId}" default-value="${project.groupId}"
	 */
	private String groupId;

	/**
	 * The artifactId
	 * 
	 * @parameter expression="${notify.artifactId}" default-value="${project.artifactId}"
	 */
	private String artifactId;

	/**
	 * The version
	 * 
	 * @parameter expression="${notify.version}" default-value="${project.version}"
	 */
	private String version;
	
	/**
	 * Fail the build if there is an error connecting to the endpoint
	 * 
	 * @parameter default-value="true"
	 */
	private static Boolean failBuild;

	/**
	 * An array of user defined endpoints
	 * 
	 * @parameter
	 */
	private String[] endpoints;

	public void execute() throws MojoExecutionException {
		if (endpoints != null){
			notify(groupId, artifactId, version, endpoints);
		}
	}

	public static void notify(String groupId, String artifactId,String version, String[] endpoints) {
		String json = generateJSON(groupId, artifactId, version);
		for (String endpoint : endpoints) {
			sendJson(endpoint, json);
		}
	}

	@SuppressWarnings("unchecked")
	protected static String generateJSON(String groupId, String artifactId, String version) {
		JSONObject obj = new JSONObject();
		obj.put("groupId", groupId);
		obj.put("artifactId", artifactId);
		obj.put("version", version);
		obj.put("timestamp", getTimestamp());
		return obj.toJSONString();
	}

	protected static String sendJson(String endpoint, String body) {
		Exception exception = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(endpoint);
		StringEntity input = null;
		String response = null;
		HttpResponse httpResponse = null;
		try {
			input = new StringEntity(body);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			httpResponse = httpClient.execute(postRequest);
			response = IOUtils.toString(new InputStreamReader((httpResponse.getEntity().getContent())));
		} catch (UnsupportedEncodingException e) {
			exception = e;
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			exception = e;
			e.printStackTrace();
		} catch (IOException e) {
			exception = e;
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		if ((failBuild) && (exception  != null)){
			throw new RuntimeException(exception);
		}
		return response;
	}

	private static String getTimestamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
		return simpleDateFormat.format(new Date());
	}

}
