package com.psyb.service.ytx;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.psyb.service.common.Constants;
import com.psyb.service.common.ScriptManager;
import com.psyb.service.common.model.InnerServer;
import com.psyb.service.common.util.DateUtil;
import com.psyb.service.common.util.EncryptUtil;

@Repository("yTXService")
public class YTXService {
	
	private static final int Request_Get = 0;

	private static final int Request_Post = 1;
	
	protected final Logger logger = LogManager.getLogger(getClass().getName());
	
	private BodyType BODY_TYPE = BodyType.Type_XML;
	public String Callsid;
	
	public enum BodyType {
		Type_XML, Type_JSON;
	}

	public enum AccountType {
		Accounts, SubAccounts;
	}
	
	private static final String SubAccounts = "SubAccounts";
	private static final String SMSMessages = "SMS/Messages";
	private static final String TemplateSMS = "SMS/TemplateSMS";
	private static final String Query_SMSTemplate = "SMS/QuerySMSTemplate";
	private static final String LandingCalls = "Calls/LandingCalls";
	private static final String VoiceVerify = "Calls/VoiceVerify";
	private static final String CallBack = "Calls/Callback";
	private static final String IvrDial = "ivr/dial";
	private static final String BillRecords = "BillRecords";
	private static final String queryCallState = "ivr/call";
	private static final String callResult = "CallResult";
	private static final String mediaFileUpload = "Calls/MediaFileUpload";

	public HashMap<String, Object> sendVoiceNotice(String accountid, String authtoken, String Appid, 
			String disnum, int loop, String to,String mediaTxt, String mediaName, String userdata)
	{
		HashMap<String, Object> validate = accountValidate(accountid, authtoken, Appid);
		if (validate != null)
			return validate;
		if (isEmpty(to))
			throw new IllegalArgumentException("必选参数:"
					+ (isEmpty(to) ? " 被叫号码 " : "") + "为空");
		DefaultHttpClient httpclient = null;
		InnerServer inServer = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_REST);
		if(inServer == null)
		{
			logger.error("sendTemplateSMS RestServer Not Config! <" + Constants.SERVER_REST + ">");
			return validate;
		}
		try {
//			if(PROTOCOL.equals("https://"))
//			{
//				httpclient = registerSSL(SERVER_IP, "TLS",Integer.parseInt(SERVER_PORT), "https");
//			}
			if(inServer.getScheme().equalsIgnoreCase("https"))
			{
				httpclient = registerSSL(inServer.getHost(), "TLS",inServer.getPort(), "https");
			}
			else
			{
				httpclient = new DefaultHttpClient();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("初始化httpclient异常" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getAccountHttpRequestBase(inServer.getBaseUrl(), Request_Post,true, accountid, authtoken, LandingCalls);
			StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n<LandingCall>\n");
			sb.append("<appId>").append(Appid).append("</appId>\n")
					.append("<to>").append(to).append("</to>\n");
			if (!(isEmpty(mediaName)))
			{
				sb.append("<mediaName>").append(mediaName).append("</mediaName>\n");
			}
			else
			{
				if (!(isEmpty(mediaTxt)))
					sb.append("<mediaTxt>").append(mediaTxt).append("</mediaTxt>\n");
			}
				
			
			if (loop > 0)
				sb.append("<playTimes>").append(loop).append("</playTimes>\n");
			if (!(isEmpty(disnum)))
				sb.append("<displayNum>").append(disnum).append("</displayNum>\n");
			
			InnerServer resultserver = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_CALLRESULT);
			if(resultserver != null)
			{
				sb.append("<respUrl>").append(resultserver.getBaseUrl()).append("</respUrl>\n");
			}
			
			if (!(isEmpty(userdata)))
				sb.append("<userData>").append(userdata).append("</userData>\n");
			sb.append("</LandingCall>\n").toString();
			String requsetbody = sb.toString();
			logger.info("landingCalls Request body = : " + requsetbody);
			
			System.out.println("请求的包体："+requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			System.out.println("Https请求返回状态码："+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172001", "网络错误");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172002", "无返回");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		logger.info("landingCall response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "返回包体错误");
		} 
	}
	
	public HashMap<String, Object> sendIVRDial(String accountid, String authtoken, String Appid, 
			String disnum, String to,String userdata)
	{
		HashMap<String, Object> validate = accountValidate(accountid, authtoken, Appid);
		if (validate != null)
			return validate;
		if (isEmpty(to))
			throw new IllegalArgumentException("必选参数:"
					+ (isEmpty(to) ? " 被叫号码 " : "") + "为空");
		DefaultHttpClient httpclient = null;
		InnerServer inServer = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_REST);
		if(inServer == null)
		{
			logger.error("sendIVRDial RestServer Not Config! <" + Constants.SERVER_REST + ">");
			return validate;
		}
		try {
			if(inServer.getScheme().equalsIgnoreCase("https"))
			{
				httpclient = registerSSL(inServer.getHost(), "TLS",inServer.getPort(), "https");
			}
			else
			{
				httpclient = new DefaultHttpClient();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("初始化httpclient异常" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getAccountHttpRequestBase(inServer.getBaseUrl(), Request_Post,true, accountid, authtoken, IvrDial);
			StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n<Request>\n");
			sb.append("<Appid>").append(Appid).append("</Appid>\n")
					.append("<Dial number='").append(to).append("'");
			if (!(isEmpty(disnum)))
			{
				sb.append(" disnumber='").append(disnum).append("'");
			}
			if (!(isEmpty(userdata)))
			{
				sb.append(" userdata='").append(userdata).append("'");
			}
			sb.append("></Dial>\n</Request>\n").toString();
			String requsetbody = sb.toString();
			logger.info("sendIVRDial Request body = : " + requsetbody);
			
			System.out.println("请求的包体："+requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			System.out.println("Https请求返回状态码："+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172001", "网络错误");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172002", "无返回");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		logger.info("sendIVRDial response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "返回包体错误");
		} 
	}
	
	public HashMap<String, Object> sendCallBack(String subaccountid, String authtoken, String Appid, 
			String from, String to,String fromser, String toSer)
	{
		HashMap<String, Object> validate = accountValidate(subaccountid, authtoken, Appid);
		if (validate != null)
			return validate;
		if (isEmpty(to))
			throw new IllegalArgumentException("必选参数:"
					+ (isEmpty(to) ? " 被叫号码 " : "") + "为空");
		DefaultHttpClient httpclient = null;
		InnerServer inServer = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_REST);
		if(inServer == null)
		{
			logger.error("sendCallBack RestServer Not Config! <" + Constants.SERVER_REST + ">");
			return validate;
		}
		try {
			if(inServer.getScheme().equalsIgnoreCase("https"))
			{
				httpclient = registerSSL(inServer.getHost(), "TLS",inServer.getPort(), "https");
			}
			else
			{
				httpclient = new DefaultHttpClient();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("初始化httpclient异常" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getAccountHttpRequestBase(inServer.getBaseUrl(), Request_Post,false, subaccountid, authtoken, CallBack);
			StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n<CallBack>\n");
			sb.append("<appId>").append(Appid).append("</appId>\n")
					.append("<from>").append(from).append("</from>\n")
					.append("<to>").append(to).append("</to>\n");
			if (!(isEmpty(fromser)))
			{
				sb.append("<fromSerNum>").append(fromser).append("</fromSerNum>\n");
			}
				
			
			if (!(isEmpty(toSer)))
				sb.append("<customerSerNum>").append(toSer).append("</customerSerNum>\n");
			
			sb.append("</CallBack>\n").toString();
			String requsetbody = sb.toString();
			logger.info("sendCallBack Request body = : " + requsetbody);
			
			System.out.println("请求的包体："+requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			System.out.println("Https请求返回状态码："+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172001", "网络错误");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172002", "无返回");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		logger.info("landingCall response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "返回包体错误");
		} 
	}
	
	/**
	 * 发送短信模板请求
	 * 
	 * @param to
	 *            必选参数 短信接收端手机号码集合，用英文逗号分开，每批发送的手机号数量不得超过100个
	 * @param templateId
	 *            必选参数 模板Id
	 * @param datas
	 *            可选参数 内容数据，用于替换模板中{序号}
	 * @return
	 */
	public HashMap<String, Object> sendTemplateSMS(String accountid, String authtoken, String Appid, String to, String templateId, String[] datas) {
		HashMap<String, Object> validate = accountValidate(accountid, authtoken, Appid);
		if(validate!=null) 
			return validate;
		if ((isEmpty(to)) || (isEmpty(Appid)) || (isEmpty(templateId)))
			throw new IllegalArgumentException("必选参数:" + (isEmpty(to) ? " 手机号码 " : "") + (isEmpty(templateId) ? " 模板Id " : "") + "为空");
		DefaultHttpClient httpclient = null;
		InnerServer inServer = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_REST);
		if(inServer == null)
		{
			logger.error("sendTemplateSMS RestServer Not Config! <" + Constants.SERVER_REST + ">");
			return validate;
		}
		try {
			if(inServer.getScheme().equalsIgnoreCase("https"))
			{
				httpclient = registerSSL(inServer.getHost(), "TLS",inServer.getPort(), "https");
			}
			else
			{
				httpclient = new DefaultHttpClient();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("初始化httpclient异常" + e1.getMessage());
		}
		String result = "";
		try {
			
			HttpPost httppost = (HttpPost) getAccountHttpRequestBase(inServer.getBaseUrl(), Request_Post,true, accountid, authtoken, TemplateSMS);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", Appid);
				json.addProperty("to", to);
				json.addProperty("templateId", templateId);
				if (datas != null) {
					StringBuilder sb = new StringBuilder("[");
					for (String s : datas) {
						sb.append("\"" + s + "\"" + ",");
					}
					sb.replace(sb.length() - 1, sb.length(), "]");
					JsonParser parser = new JsonParser();
					JsonArray Jarray = parser.parse(sb.toString()).getAsJsonArray();
					json.add("datas", Jarray);
				}
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
				sb.append("<appId>").append(Appid).append("</appId>").append("<to>").append(to).append("</to>").append("<templateId>").append(templateId)
						.append("</templateId>");
				if (datas != null) {
					sb.append("<datas>");
					for (String s : datas) {
						sb.append("<data>").append(s).append("</data>");
					}
					sb.append("</datas>");
				}
				sb.append("</TemplateSMS>").toString();
				requsetbody = sb.toString();
			}

			logger.info("sendTemplateSMS Request body =  " + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172001", "网络错误");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172002", "无返回");
		}  finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		logger.info("sendTemplateSMS response body = " + result);
		
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {
			   
			return getMyError("172003", "返回包体错误");
		}
	}
	
	/**
	 * 创建子账号
	 * 
	 * @return
	 */
	public HashMap<String, Object> createSubAccounts(String accountid, String authtoken, String Appid, String name) {
		HashMap<String, Object> validate = accountValidate(accountid, authtoken, Appid);
		if(validate!=null) 
			return validate;
		DefaultHttpClient httpclient = null;
		InnerServer inServer = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_REST);
		if(inServer == null)
		{
			logger.error("createSubAccounts RestServer Not Config! <" + Constants.SERVER_REST + ">");
			return validate;
		}
		try {
			if(inServer.getScheme().equalsIgnoreCase("https"))
			{
				httpclient = registerSSL(inServer.getHost(), "TLS",inServer.getPort(), "https");
			}
			else
			{
				httpclient = new DefaultHttpClient();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("初始化httpclient异常" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getAccountHttpRequestBase(inServer.getBaseUrl(), Request_Post,true, accountid, authtoken, SubAccounts);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", Appid);
				json.addProperty("friendlyName", name);
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?>\n<SubAccount>\n");
				sb.append("<appId>").append(Appid).append("</appId>").append("<friendlyName>").append(name).append("</friendlyName>\n").append("</SubAccount>").toString();
				requsetbody = sb.toString();
			}

			logger.info("createSubAccounts Request body =  " + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172001", "网络错误");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return getMyError("172002", "无返回");
		}  finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		logger.info("sendTemplateSMS response body = " + result);
		
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {
			   
			return getMyError("172003", "返回包体错误");
		}
	}
	

	
	private HttpRequestBase getAccountHttpRequestBase(StringBuffer baseurl, int get, boolean acc, String account, String token, String action) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String timestamp = DateUtil.dateToStr(new Date(),
				DateUtil.DATE_TIME_NO_SLASH);
		String signature="", url="";
		if(acc)
		{
			signature = EncryptUtil.md5(account + token + timestamp);
			url = baseurl.append("/Accounts/")
					.append(account).append("/" + action + "?sig=")
					.append(signature).toString();
		}
		else
		{
			signature = EncryptUtil.md5(account + token + timestamp);
			url = baseurl.append("/SubAccounts/")
					.append(account).append("/" + action + "?sig=")
					.append(signature).toString();
		}
		
		if (callResult.equals(action)) {
			url = url + "&callsid=" + Callsid;
		}
		if (queryCallState.equals(action)) {
			url = url + "&callid=" + Callsid;
		}
		logger.info(getmethodName(action) + " url = " + url);
		HttpRequestBase mHttpRequestBase = null;
		if (get == Request_Get)
			mHttpRequestBase = new HttpGet(url);
		else if (get == Request_Post)
			mHttpRequestBase = new HttpPost(url);
		if (IvrDial.equals(action)) {
			setHttpHeaderXML(mHttpRequestBase);
		} else if (mediaFileUpload.equals(action)) {
			setHttpHeaderMedia(mHttpRequestBase);
		} else {
			setHttpHeader(mHttpRequestBase);
		}

		String src = account + ":" + timestamp;

		String auth = EncryptUtil.base64Encoder(src);
		mHttpRequestBase.setHeader("Authorization", auth);
		System.out.println("请求的Url："+mHttpRequestBase);//打印Url
		return mHttpRequestBase;
		
	}

	private String getmethodName(String action) {
		if (action.equals(LandingCalls)) {
			return "landingCalls";
		} else if (action.equals(VoiceVerify)) {
			return "voiceVerify";
		} else if (action.equals(IvrDial)) {
			return "ivrDial";
		} else if (action.equals(BillRecords)) {
			return "billRecords";
		} else if (action.equals(CallBack)) {
			return "callBack";
		} else if(action.equals(TemplateSMS)){
			 return "sendTemplateSMS";
		} else if(action.equals(SubAccounts)){
			return "SubAccounts";
		} else {
			return "";
		}
	}
	
	public DefaultHttpClient registerSSL(String hostname, String protocol, int port, String scheme)
			throws NoSuchAlgorithmException, KeyManagementException
			{
		DefaultHttpClient httpclient = new DefaultHttpClient();

		SSLContext ctx = SSLContext.getInstance(protocol);

		X509TrustManager tm = new X509TrustManager()
		{
			public void checkClientTrusted(X509Certificate[] chain,String authType)
					throws java.security.cert.CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,String authType)
					throws java.security.cert.CertificateException {
				if ((chain == null) || (chain.length == 0))
					throw new IllegalArgumentException("null or zero-length certificate chain");
				if ((authType == null) || (authType.length() == 0))
					throw new IllegalArgumentException("null or zero-length authentication type");

				boolean br = false;
				Principal principal = null;
				for (X509Certificate x509Certificate : chain) {   
					principal = x509Certificate.getSubjectX500Principal();
					if (principal != null) {
						br = true;
						return;
					}
				}
				if (!(br))
					throw new CertificateException("服务端证书验证失败！");
			}

			public X509Certificate[] getAcceptedIssuers()
			{
				return new X509Certificate[0];
			}

		};
		ctx.init(null, new TrustManager[] { tm }, new SecureRandom());

		SSLSocketFactory socketFactory = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme sch = new Scheme(scheme, port, socketFactory);

		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		return httpclient;
	}
	
	private HashMap<String, Object> jsonToMap(String result) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		JsonParser parser = new JsonParser();
		JsonObject asJsonObject = parser.parse(result).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
		HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

		for (Map.Entry<String, JsonElement> m : entrySet) {
			if ("statusCode".equals(m.getKey())
					|| "statusMsg".equals(m.getKey()))
				hashMap.put(m.getKey(), m.getValue().getAsString());
			else {
				if ("SubAccount".equals(m.getKey())
						|| "totalCount".equals(m.getKey())
						|| "smsTemplateList".equals(m.getKey())
						|| "token".equals(m.getKey())
						|| "callSid".equals(m.getKey())
						|| "state".equals(m.getKey())
						|| "downUrl".equals(m.getKey())) {
					if (!"SubAccount".equals(m.getKey())
							&& !"smsTemplateList".equals(m.getKey()))
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					else {
						try {
							if ((m.getValue().toString().trim().length() <= 2)
									&& !m.getValue().toString().contains("[")) {
								hashMap2.put(m.getKey(), m.getValue()
										.getAsString());
								hashMap.put("data", hashMap2);
								break;
							}
							if (m.getValue().toString().contains("[]")) {
								hashMap2.put(m.getKey(), new JsonArray());
								hashMap.put("data", hashMap2);
								continue;
							}
							JsonArray asJsonArray = parser.parse(
									m.getValue().toString()).getAsJsonArray();
							ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
							for (JsonElement j : asJsonArray) {
								Set<Entry<String, JsonElement>> entrySet2 = j
										.getAsJsonObject().entrySet();
								HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
								for (Map.Entry<String, JsonElement> m2 : entrySet2) {
									hashMap3.put(m2.getKey(), m2.getValue()
											.getAsString());
								}
								arrayList.add(hashMap3);
							}
							hashMap2.put(m.getKey(), arrayList);
						} catch (Exception e) {
							JsonObject asJsonObject2 = parser.parse(
									m.getValue().toString()).getAsJsonObject();
							Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2
									.entrySet();
							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Map.Entry<String, JsonElement> m2 : entrySet2) {
								hashMap3.put(m2.getKey(), m2.getValue()
										.getAsString());
							}
							hashMap2.put(m.getKey(), hashMap3);
							hashMap.put("data", hashMap2);
						}

					}
					hashMap.put("data", hashMap2);
				} else {

					JsonObject asJsonObject2 = parser.parse(
							m.getValue().toString()).getAsJsonObject();
					Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2
							.entrySet();
					HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
					for (Map.Entry<String, JsonElement> m2 : entrySet2) {
						hashMap3.put(m2.getKey(), m2.getValue().getAsString());
					}
					if (hashMap3.size() != 0) {
						hashMap2.put(m.getKey(), hashMap3);
					} else {
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					}
					hashMap.put("data", hashMap2);
				}
			}
		}
		return hashMap;
	}

	/**
	 * @description 将xml字符串转换成map
	 * @param xml
	 * @return Map
	 */
	private HashMap<String, Object> xmlToMap(String xml) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
			ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
			for (Iterator i = rootElt.elementIterator(); i.hasNext();) {
				Element e = (Element) i.next();
				if ("statusCode".equals(e.getName())
						|| "statusMsg".equals(e.getName()))
				{
					map.put(e.getName(), e.getText());
					
				} else {
					if ("SubAccount".equals(e.getName())
							|| "TemplateSMS".equals(e.getName())
							|| "totalCount".equals(e.getName())
							|| "token".equals(e.getName())
							|| "callSid".equals(e.getName())
							|| "state".equals(e.getName())
							|| "downUrl".equals(e.getName())) {
						if (!"SubAccount".equals(e.getName())&&!"TemplateSMS".equals(e.getName())) {
							hashMap2.put(e.getName(), e.getText());
						} else if ("SubAccount".equals(e.getName())){

							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2
									.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
							}
							arrayList.add(hashMap3);
							hashMap2.put("SubAccount", arrayList);
							
						}else if ("TemplateSMS".equals(e.getName())){

							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2
									.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
							}
							arrayList.add(hashMap3);
							hashMap2.put("TemplateSMS", arrayList);
						}
						map.put("data", hashMap2);
					}
					else if("LandingCall".equals(e.getName()))
					{
						for (Iterator i2 = e.elementIterator(); i2
								.hasNext();) {
							Element e2 = (Element) i2.next();
							hashMap2.put(e2.getName(), e2.getText());
						}
						map.put("LandingCall", hashMap2);
					}
					else {

						HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
						for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
							Element e2 = (Element) i2.next();
							// hashMap2.put(e2.getName(),e2.getText());
							hashMap3.put(e2.getName(), e2.getText());
						}
						if (hashMap3.size() != 0) {
							hashMap2.put(e.getName(), hashMap3);
						} else {
							hashMap2.put(e.getName(), e.getText());
						}
						map.put("data", hashMap2);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}
	
	private HashMap<String, Object> accountValidate(String accountid, String authtoken, String appid) {
		InnerServer inServer = ScriptManager.getScriptManager().getInnerServer(Constants.SERVER_REST);
		if(inServer == null)
		{
			return getMyError("112004", "CCP RestServer配置为空");
		}
		if ((isEmpty(accountid))) {
			return getMyError("112006", "主帐号为空");
		}
		if ((isEmpty(authtoken))) {
			return getMyError("112007", "主帐号TOKEN为空");
		}
		if ((isEmpty(appid))) {
			return getMyError("112012", "应用ID为空");
		}
		return null;
	}
	
	private void setHttpHeaderXML(AbstractHttpMessage httpMessage) {
		httpMessage.setHeader("Accept", "application/xml");
		httpMessage.setHeader("Content-Type", "application/xml;charset=utf-8");
	}
	
	private void setHttpHeaderMedia(AbstractHttpMessage httpMessage) {
		if (BODY_TYPE == BodyType.Type_JSON) {
			httpMessage.setHeader("Accept", "application/json");
			httpMessage.setHeader("Content-Type", "application/octet-stream;charset=utf-8;");
		} else {
			httpMessage.setHeader("Accept", "application/xml");
			httpMessage.setHeader("Content-Type", "application/octet-stream;charset=utf-8;");
		}
	}

	private void setHttpHeader(AbstractHttpMessage httpMessage) {
		if (BODY_TYPE == BodyType.Type_JSON) {
			httpMessage.setHeader("Accept", "application/json");
			httpMessage.setHeader("Content-Type",
					"application/json;charset=utf-8");
			
		} else {
			httpMessage.setHeader("Accept", "application/xml");
			httpMessage.setHeader("Content-Type",
					"application/xml;charset=utf-8");
		}
	}
	
	private HashMap<String, Object> getMyError(String code, String msg) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("statusCode", code);
		hashMap.put("statusMsg", msg);
		return hashMap;
	}
	
	private boolean isEmpty(String str) {
		return (("".equals(str)) || (str == null));
	}
	
	public HttpServletResponse sendIVR2YTX(String ivrbody)
	{
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {

            HttpPost httpPost = new HttpPost("http://192.168.21.48:8500/ivrcmd");
//            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//            nvps.add(new BasicNameValuePair("username", "vip"));
//            nvps.add(new BasicNameValuePair("password", "secret"));
            
//            httpPost.setHeader("username", "vip");
//            httpPost.setHeader("password", "secret");
            System.out.println("Send Request Body:\n"+ivrbody);
            StringEntity entity = new StringEntity(ivrbody.toString(), "utf-8");
            
//            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);

            try {
                System.out.println("Receive Response:\n"+response.getStatusLine());
                HttpEntity entity2 = response.getEntity();
                System.out.println(EntityUtils.toString(entity2));
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
            	response.close();
            }
        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;  
	}
}
