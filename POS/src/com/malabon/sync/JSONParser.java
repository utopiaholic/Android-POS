package com.malabon.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	private static JSONObject jObj = null;
	private static String json = "";
	private int TIMEOUT_MILLISEC = 10;

	public JSONParser() {
	}

	public JSONObject PullData(String url, List<NameValuePair> params) {

		BufferedReader reader = null;
		InputStream is = null;

		// Making HTTP request
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader = new BufferedReader(
					new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			json = sb.toString();
		} catch (Exception e) {
			Log.e("pos_error", "JSONObject PullData: " + e.toString());
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
				Log.e("pos_error", "JSONObject PullData: " + ex.toString());
			}
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("pos_error", "JSONObject PullData: " + e.toString());
		}

		// return JSON String
		return jObj;
	}

	public JSONObject PushData(URL url, String data) {

		URLConnection conn = null;
		BufferedReader reader = null;

		// Making HTTP request
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

			conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());

			wr.write(data);
			wr.flush();

		} catch (UnsupportedEncodingException e) {
			Log.e("pos_error", "JSONObject PushData: " + e.toString());
		} catch (ClientProtocolException e) {
			Log.e("pos_error", "JSONObject PushData: " + e.toString());
		} catch (IOException e) {
			Log.e("pos_error", "JSONObject PushData: " + e.toString());
		}

		try {
			// Get the response
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			json = sb.toString();
		} catch (Exception e) {
			Log.e("pos_error", "JSONObject PushData: " + e.toString());
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
				Log.e("pos_error", "JSONObject PushData: " + ex.toString());
			}
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("pos_error", "JSONObject PushData: " + e.toString());
		}

		// return JSON String
		return jObj;
	}
}