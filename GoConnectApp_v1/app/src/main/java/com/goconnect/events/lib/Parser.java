package com.goconnect.events.lib;
///**
// * 
// */
//package com.emed.events.lib;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//import com.emed.events.model.MyEventsResponse;
//import com.google.gson.Gson;
//
///**
// * @author Reddy
// * 
// */
//public class Parser {
//
//	public static Object parseJSonResponse(String responseString, String responseClassType) throws Exception {
//
//		InputStream source = new ByteArrayInputStream(responseString.getBytes());
//		Gson gson = new Gson();
//		Reader reader = new InputStreamReader(source);
//
//		// return gson.fromJson(reader, Class.forName(responseClassType));
//
//		// workaround for the above dynamic class type passing
//		if (responseClassType.equals("MyEvents"))
//			return gson.fromJson(reader, MyEventsResponse.class);
//		else
//			return null;
//	}
//
//	public static String objectToJSonString(Object request) throws Exception {
//		return new Gson().toJson(request);
//		// return new GsonBuilder().serializeNulls().create().toJson(request);
//	}
//}
