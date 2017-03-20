/*
 * HttpsCertificate.java 
 */

package com.goconnect.events.http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import android.util.Log;

public class HttpsCertificate {
	private final static String TAG = "HttpsCertificate";

	private static TrustManager[] trustManagers;

	public static void allowAllSSL() {
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		javax.net.ssl.SSLContext context = null;
		if (trustManagers == null) {
			trustManagers = new javax.net.ssl.TrustManager[] { new _FakeX509TrustManager() };
		}
		try {
			context = javax.net.ssl.SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.toString());
		} catch (KeyManagementException e) {
			Log.e(TAG, e.toString());
		}
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

	public static class _FakeX509TrustManager implements javax.net.ssl.X509TrustManager {
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		public boolean isClientTrusted(X509Certificate[] chain) {
			return (true);
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return (true);
		}

		public X509Certificate[] getAcceptedIssuers() {
			return (_AcceptedIssuers);
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
		}
	}

}
