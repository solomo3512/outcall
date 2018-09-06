package com.psyb.service.common;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ming.sample.util.ProtocolUtil;
import org.ming.sample.util.StreamUtil;

import com.psyb.service.common.exception.CCPException;

/**
 * CCP Http Client based Apache HttpClient.
 * 
 * @since 2013-11-18
 * @version 1.0
 */
public final class CCPHttpClient {

	public static final Logger log = LogManager.getLogger(CCPHttpClient.class);
	public static final String TLS = "TLS";
	private int connTimeout = 5;
	private int soTimeout = 10;

	public CCPHttpClient() {
		this(5, 10);
	}

	public CCPHttpClient(int connTimeout, int soTimeout) {
		this.connTimeout = connTimeout;
		this.soTimeout = soTimeout;
	}

	/**
	 * HTTP GET Request API
	 * 
	 * @param url
	 * @return
	 * @throws CCPException
	 */
	public String httpGet(String url) throws CCPException {
		return httpGet(url, null);
	}

	/**
	 *  * HTTP GET Request API.
	 *  
	 * @param url
	 * @param headers
	 * @return
	 * @throws CCPException
	 */
	public String httpGet(String url, HashMap<String, String> headers) throws CCPException {
		return httpGet(url, -1, headers);
	}

	/**
	 * HTTP GET Request API.
	 * 
	 * @param url
	 * @param headers
	 * @return
	 * @throws CCPException
	 */
	public String httpGet(String url, int port, HashMap<String, String> headers) throws CCPException {
		DefaultHttpClient httpClient = null;
		try {
			if (ProtocolUtil.isHttpsUrl(url)) {
				port = (port <= -1 ? 443 : 8883);
				httpClient = registerSSL(ProtocolUtil.getHost(url), TLS, port, "https");
			} else {
				httpClient = new DefaultHttpClient();
			}
			HttpGet httpRequest = new HttpGet(url);

			HttpConnectionParams.setConnectionTimeout(httpRequest.getParams(), connTimeout * 1000);
			HttpConnectionParams.setSoTimeout(httpRequest.getParams(), soTimeout * 1000);

			if (headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = headers.get(key);
					httpRequest.setHeader(key, value);
				}
			} else {
				httpRequest.setHeader("Content-Type", "application/xml");
			}

			log.debug(url);
			HttpResponse response = httpClient.execute(httpRequest);
			log.info("HTTP Get Send Over...");

			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				log.info("Http Get status code: " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					String resp = StreamUtil.readContentByStream(response.getEntity().getContent());
					log.debug(resp);
					return resp;
				} else {
					throw new CCPException(ScriptManager.buildError("112605", "Got error code " + statusCode
							+ " from [" + url + "]"));
				}
			}

			throw new CCPException(ScriptManager.buildError("112600", "Unable to connect to server. [" + url + "]"));
		} catch (KeyManagementException e) {
			throw new CCPException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new CCPException(e);
		} catch (IOException e) {
			throw new CCPException(e);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * HTTP POST Request API
	 * 
	 * @param url
	 * @param requestBody
	 * @return
	 * @throws CCPException
	 */
	public String httpPost(String url, String requestBody) throws CCPException {
		return httpPost(url, null, requestBody);
	}

	/**
	 * HTTP POST Request API
	 * 
	 * @param url
	 * @param requestBody
	 * @return
	 * @throws CCPException
	 */
	public String httpPost(String url, HashMap<String, String> headers, String requestBody) throws CCPException {
		return httpPost(url, 8883, headers, requestBody);
	}

	/**
	 * HTTP POST Request API
	 * 
	 * @param url
	 * @param headers
	 * @param requestBody
	 * @return
	 * @throws CCPException
	 */
	public String httpPost(String url, int port, HashMap<String, String> headers, String requestBody)
			throws CCPException {
		DefaultHttpClient httpClient = null;
		try {
			if (ProtocolUtil.isHttpsUrl(url)) {
				port = (port < 0 ? 443 : port);
				httpClient = registerSSL(ProtocolUtil.getHost(url), TLS, port, "https");
			} else {
				httpClient = new DefaultHttpClient();
			}

			HttpPost httpRequest = new HttpPost(url);
			// 设置请求超时时间
			HttpConnectionParams.setConnectionTimeout(httpRequest.getParams(), connTimeout * 1000);
			// 设置等待数据超时时间
			HttpConnectionParams.setSoTimeout(httpRequest.getParams(), soTimeout * 1000);

			if (headers != null && headers.size() > 0) {
				Iterator<String> keys = headers.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = headers.get(key);
					httpRequest.setHeader(key, value);
					log.debug(key + ": " + value);
				}
			} else {
				httpRequest.setHeader("Content-Type", "text/xml");
			}

			log.debug(url);
			log.debug(requestBody + "\r\n");

			byte[] requestByte = ProtocolUtil.getUTF8EncodingContent(requestBody);
			HttpEntity entity = new ByteArrayEntity(requestByte);
			httpRequest.setEntity(entity);

			HttpResponse response = httpClient.execute(httpRequest);
			log.info("Http Post Send Over...");

			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				log.info("Http Post status code: " + statusCode + "\r\n");
				if (statusCode == HttpStatus.SC_OK) {
					String resp = StreamUtil.readContentByStream(response.getEntity().getContent());
					log.debug(resp);
					return resp;
				} else {
					throw new CCPException(ScriptManager.buildError("112605", "Got error code " + statusCode
							+ " from [" + url + "]"));
				}
			}
			throw new CCPException(ScriptManager.buildError("112600", "Unable to connect to server. [" + url + "]"));

		} catch (NoSuchAlgorithmException nsaio) {
			throw new CCPException(nsaio);
		} catch (KeyManagementException ke) {
			throw new CCPException(ke);
		} catch (IOException ioe) {
			throw new CCPException(ioe);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * 注册SSL连接
	 * 
	 * @param hostname
	 *            请求的主机名（IP或者域名）
	 * @param protocol
	 *            请求协议名称（TLS-安全传输层协议）
	 * @param port
	 *            端口号
	 * @param scheme
	 *            协议名称
	 * @return HttpClient实例
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	DefaultHttpClient registerSSL(String hostname, String protocol, int port, String scheme)
			throws NoSuchAlgorithmException, KeyManagementException {

		// 创建一个默认的HttpClient
		DefaultHttpClient httpclient = new DefaultHttpClient();
		// 创建SSL上下文实例
		SSLContext ctx = SSLContext.getInstance(protocol);
		// 服务端证书验证
		X509TrustManager tm = new X509TrustManager() {

			/**
			 * 验证客户端证书
			 */
			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				// 这里跳过客户端证书验证
			}

			/**
			 * 验证服务端证书
			 * 
			 * @param chain
			 *            证书链
			 * @param authType
			 *            使用的密钥交换算法，当使用来自服务器的密钥时authType为RSA
			 */
			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				if (chain == null || chain.length == 0)
					throw new IllegalArgumentException("null or zero-length certificate chain");
				if (authType == null || authType.length() == 0)
					throw new IllegalArgumentException("null or zero-length authentication type");
				log.info("authType: " + authType);
				boolean br = false;
				Principal principal = null;
				for (X509Certificate x509Certificate : chain) {
					principal = x509Certificate.getSubjectX500Principal();
					if (principal != null) {
						log.info("服务器证书信息: " + principal.getName());
						br = true;
						return;
					}
				}
				if (!br) {
					throw new CertificateException("服务端证书验证失败！");
				}
			}

			/**
			 * 返回CA发行的证书
			 */
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		// 初始化SSL上下文
		ctx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
		// 创建SSL连接
		SSLSocketFactory socketFactory = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme sch = new Scheme(scheme, port, socketFactory);
		// 注册SSL连接
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		return httpclient;
	}
	
	
	/**
	 * 小米话单通知失，则存入数据库继续发送话单
	 * 
	 * @param url
	 * @param port
	 * @param headers
	 * @param requestBody
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws CCPException
	 */
	public int sendPost(String url, int port, HashMap<String, String> headers,
			String requestBody) throws KeyManagementException,
			NoSuchAlgorithmException, ClientProtocolException, IOException {
		DefaultHttpClient httpClient = null;
		int statusCode = 0;
		if (ProtocolUtil.isHttpsUrl(url)) {
			port = (port < 0 ? 443 : port);
			httpClient = registerSSL(ProtocolUtil.getHost(url), "TLS", port,
					"https");
		} else {
			httpClient = new DefaultHttpClient();
		}

		HttpPost httpRequest = new HttpPost(url);
		// 设置请求超时时间
		HttpConnectionParams.setConnectionTimeout(httpRequest.getParams(),
				5 * 1000);
		// 设置等待数据超时时间
		HttpConnectionParams.setSoTimeout(httpRequest.getParams(), 10 * 1000);

		if (headers != null && headers.size() > 0) {
			Iterator<String> keys = headers.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				String value = headers.get(key);
				httpRequest.setHeader(key, value);
				log.debug(key + ": " + value);
			}
		} else {
			httpRequest.setHeader("Content-Type", "text/xml");
		}

		log.info(url);
		log.info(requestBody + "\r\n");

		byte[] requestByte = ProtocolUtil.getUTF8EncodingContent(requestBody);
		HttpEntity entity = new ByteArrayEntity(requestByte);
		httpRequest.setEntity(entity);

		HttpResponse response = httpClient.execute(httpRequest);
		log.info("Http Post Send Over...");
		String resp = StreamUtil.readContentByStream(response.getEntity()
				.getContent());
		log.info("miuiServer -> restServer:"+resp);
		if (response != null) {
			statusCode = response.getStatusLine().getStatusCode();
			log.info("Http Post status code: " + statusCode + "\r\n");
		}
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
		return statusCode;
	}

}
