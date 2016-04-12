package com.jwt;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

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
/**
 * 提供生成token和校验token两个方法
 * @author running@vip.163.com
 *
 */
public class Jwt {
	/**
	 * 秘钥
	 */
	private static final String SECRET="3d990d2276917dfac04467df11fff26d";
	
	/**
	 * token过期(token失效了)
	 */
	public static Number EXPIRED=-1; 
	
	/**
	 * 校验失败（token不一致）
	 */
	public static Number FAIL=0;
	
	/**
	 * 校验成功
	 */
	public static Number SUCCESS=1;
	
	/**
	 * 代码抛异常（校验token时代码出错）
	 */
	public static Number EXCEPT=2;
	
	/**
	 * 生成token，该方法只在用户登录成功后调用
	 * @param Map集合，主要存储用户id，token生成时间，token过期时间等
	 * @return token字符串
	 * @throws KeyLengthException 
	 */
	public static String createToken(Map<String, Object> playLoad) throws KeyLengthException{
		///B
		JSONObject userInfo = new JSONObject(playLoad);
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
		return jwsObject.serialize();
	}
	
	/**
	 * 校验token是否合法，返回Map集合,集合中主要包含  isSuccess是否成功  status状态码   data鉴权成功后从token中提取的数据
	 * 该方法在过滤器中调用，每次请求API时都校验
	 * @param token
	 * @return
	 * @throws KeyLengthException
	 */
	public static Map<String, Object> validToken(String token) throws KeyLengthException{
		Map<String, Object> resultMap=new HashMap<String, Object>();
		
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
			 resultMap.put("isSuccess", false);
			 resultMap.put("status", EXCEPT);
			e.printStackTrace();
		}
        if( jwsObject.serialize().equals(token)){
        	System.out.println("token校验成功");
        	resultMap.put("isSuccess", true);
        	resultMap.put("status", SUCCESS);
        	resultMap.put("data", payload.toJSONObject());
        }else{
        	resultMap.put("isSuccess", false);
        	resultMap.put("status", FAIL);
        }
		
		return resultMap;
	}
	
	
}
