package org.wilson.world.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import net.sf.json.JSONObject;

public class SecurityUtils {
	public static String genToken(String secret) throws Exception {
		String time = String.valueOf(System.currentTimeMillis());
		
		String hash = getBase64HashFromHmacSHA256(secret, time);
		
		JSONObject obj = new JSONObject();
		obj.put("msg_mac", hash);
		obj.put("time_created", time);
		
		return obj.toString();
	}
	
	public static String getBase64HashFromHmacSHA256(String secret, String message) throws Exception {
		String algorithm = "HmacSHA256";
		Mac sha256_HMAC = Mac.getInstance(algorithm);
		SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), algorithm);
		sha256_HMAC.init(secret_key);
		String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
		
		return hash;
	}
}
