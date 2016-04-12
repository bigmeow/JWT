package com.jwt;

import java.text.ParseException;
import java.util.HashMap;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.util.Base64;

import net.minidev.json.JSONObject;

public class Test {
	/**
	 * 秘钥
	 */
	static String SECRET="3d990d2276917dfac04467df11fff26d";
	
	public static void main(String[] args) throws KeyLengthException, ParseException {
		String token=createToken();//创建token
		validToken(token);//验证
	}
	
	/**
	 * 验证token
	 * @throws KeyLengthException 
	 */
	public static void validToken(String token) throws KeyLengthException{
		String[] tokenArr=token.split("\\.");
		Payload payload = new Payload(new Base64(tokenArr[1]).decodeToString());
		
		///A
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256,JOSEObjectType.JWT, null, null, null, null, null, null, null, null, null, null, null);
        // 创建一个 JWS object
        JWSObject jwsObject = new JWSObject(header, payload);
        //创建 HMAC signer        
        JWSSigner signer = new MACSigner(SECRET.getBytes());
        try {
			jwsObject.sign(signer);
		} catch (JOSEException e) {
			 System.err.println("签名失败" + e.getMessage());
			e.printStackTrace();
		}
        
        System.out.println("token验证结果: " +  jwsObject.serialize().equals(token));
		
	}
	
	/**
	 * 生成token
	 * @throws KeyLengthException
	 */
	public static String createToken() throws KeyLengthException{
		
				HashMap<String, Object> map=new HashMap<String,Object>();
				map.put("uid", "291969452");
				map.put("iat", 1460361882760l);//生成时间
				map.put("ext",1460365482760l);//过期时间1小时
				JSONObject userInfo = new JSONObject(map);
				///B
				Payload payload = new Payload(userInfo);
				
		        
		        ///A
		        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256,JOSEObjectType.JWT, null, null, null, null, null, null, null, null, null, null, null);
		        // 创建一个 JWS object
		        JWSObject jwsObject = new JWSObject(header, payload);
		        //创建 HMAC signer        
		        JWSSigner signer = new MACSigner(SECRET.getBytes());
		        try {
					jwsObject.sign(signer);
				} catch (JOSEException e) {
					 System.err.println("签名失败" + e.getMessage());
					e.printStackTrace();
				}
		        
		        String token = jwsObject.serialize();
		        System.out.println("最后生成的token: " + token);
		        return token;
		        
		        //eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiIyOTE5Njk0NTIiLCJpYXQiOjE0NjAzNjE4ODI3NjAsImV4dCI6MTQ2MDM2NTQ4Mjc2MH0.3ZjDGmvH4aVxAObRSuESIj4xY8wUk2XDUiSXBJp4OqI
	}
	
}
