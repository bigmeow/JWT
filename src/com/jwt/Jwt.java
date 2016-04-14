package com.jwt;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.util.Base64;

import net.minidev.json.JSONObject;
/**
 * 
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
    public static final Integer EXPIRED=-1; 
    
    /**
     * 校验失败（token不一致）
     */
    public static final Integer FAIL=0;
    
    /**
     * 校验成功
     */
    public static final Integer SUCCESS=1;
    
    /**
     * 代码抛异常（校验token时代码出错）
     */
    public static final Integer EXCEPT=2;
    
    /**
     * 生成token，该方法只在用户登录成功后调用
     * @param Map集合，主要存储用户id，token生成时间，token过期时间等
     * @return token字符串
     * @throws KeyLengthException 
     */
    public static String createToken(Map<String, Object> playLoad){
        ///B
        JSONObject userInfo = new JSONObject(playLoad);
        Payload payload = new Payload(userInfo);
        
        ///A
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        // 创建一个 JWS object
        JWSObject jwsObject = new JWSObject(header, payload);
         
        try {
        	//创建 HMAC signer
        	JWSSigner signer = new MACSigner(SECRET.getBytes());
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
    public static Map<String, Object> validToken(String token){
        Map<String, Object> resultMap=new HashMap<String, Object>();
        JWSObject jwsObject=null;
        Payload payload=null;
        try {
	        String[] tokenArr=token.split("\\.");
	        payload = new Payload(new Base64(tokenArr[1]).decodeToString());
	        ///A
	        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
	        // 创建一个 JWS object
	        jwsObject = new JWSObject(header, payload);
        
	        //创建 HMAC signer        
	        JWSSigner signer = new MACSigner(SECRET.getBytes());
            jwsObject.sign(signer);
        } catch (Exception e) {
        	e.printStackTrace();
            //异常
            resultMap.put("isSuccess", false);
            resultMap.put("status", EXCEPT);
            return resultMap;
        }
        
         
        if( jwsObject.serialize().equals(token)){
        	JSONObject jsonOBj= payload.toJSONObject();
            long extTime=(long) jsonOBj.get("ext");
            long curTime=new Date().getTime();
            if(curTime>extTime){
    	       	//过期了
    	       	resultMap.put("isSuccess", false);
    			resultMap.put("status", EXPIRED);
    			System.out.println("token过期");
    			return resultMap;
            }else{
            	//没有过期
            	resultMap.put("isSuccess", true);
            	resultMap.put("status", SUCCESS);
            	resultMap.put("data", payload.toJSONObject());
            	return resultMap;
            }
        }else{
        	//校验失败
        	resultMap.put("isSuccess", false);
        	resultMap.put("status", FAIL);
        }
        
        return resultMap;
    }	
    
    //测试
    public static void main(String[] args) {
		
    	
    	//正常生成token----------------------------------------------------------------------------------------------------
    	String token=null;
    	Map<String, Object> payload = new HashMap<String, Object>();
		Date date = new Date();
		payload.put("uid", "291969452");// 用户id
		payload.put("iat", date.getTime());// 生成时间
		payload.put("ext", date.getTime() + 2000 * 60 * 60);// 过期时间2小时
		try {
			token = Jwt.createToken(payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("新生成的token是："+token);
		System.out.println("将新生成的token马上进行校验");
    	Map<String,Object> resultMap= Jwt.validToken(token);
    	System.out.println("校验结果是:"+resultMap.get("isSuccess"));
    	JSONObject obj= (JSONObject) resultMap.get("data");
    	System.out.println("从token中取出的数据是："+obj.toJSONString());
    	
    	
    	/*
    	//校验过期----------------------------------------------------------------------------------------------------
    	String token=null;
    	Map<String, Object> payload = new HashMap<String, Object>();
		Date date = new Date();
		payload.put("uid", "291969452");// 用户id
		payload.put("iat", date.getTime());// 生成时间
		payload.put("ext", date.getTime());// 过期时间就是当前
		try {
			token = Jwt.createToken(payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("新生成的token是："+token);
		System.out.println("将新生成的token马上进行校验");
    	Map<String,Object> resultMap= Jwt.validToken(token);
    	System.out.println("校验结果是:"+resultMap.get("isSuccess"));
    	System.out.println("false的原因是（-1标识过期）:"+resultMap.get("status"));
    	*/
    	
    	/*
    	//校验非法token的情况----------------------------------------------------------------------------------------------------
    	String token=null;
    	Map<String, Object> payload = new HashMap<String, Object>();
		Date date = new Date();
		payload.put("uid", "291969452");// 用户id
		payload.put("iat", date.getTime());// 生成时间
		payload.put("ext", date.getTime());// 过期时间就是当前
		try {
			token = Jwt.createToken(payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("新生成的token是："+token);
		System.out.println("将新生成的token加点新字符串再来进行校验");
		token=token+"a";
    	Map<String,Object> resultMap= Jwt.validToken(token);
    	System.out.println("校验结果是:"+resultMap.get("isSuccess"));
    	System.out.println("false的原因是（0非法token）:"+resultMap.get("status"));
    	*/
    	
    	
    	
    /*	
    	//校验异常的情况----------------------------------------------------------------------------------------------------
    	String token=null;
    	System.out.println("我胡乱传一个token"+token);
    	Map<String,Object> resultMap= Jwt.validToken(token);
    	System.out.println("校验结果是:"+resultMap.get("isSuccess"));
    	System.out.println("false的原因是（2 token不合法导致的程序异常）:"+resultMap.get("status"));*/
    	
	}
	
}
