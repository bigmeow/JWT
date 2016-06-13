package com.jwt;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

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
    private static final byte[] SECRET="3d990d2276917dfac04467df11fff26d".getBytes();
    
    /**
     * 初始化head部分的数据为
     * {
     * 		"alg":"HS256",
     * 		"type":"JWT"
     * }
     */
    private static final JWSHeader header=new JWSHeader(JWSAlgorithm.HS256, JOSEObjectType.JWT, null, null, null, null, null, null, null, null, null, null, null);
    
	/**
	 * 生成token，该方法只在用户登录成功后调用
	 * 
	 * @param Map集合，可以存储用户id，token生成时间，token过期时间等自定义字段
	 * @return token字符串,若失败则返回null
	 */
	public static String createToken(Map<String, Object> playLoad) {
		String tokenString=null;
		// 创建一个 JWS object
		JWSObject jwsObject = new JWSObject(header, new Payload(new JSONObject(playLoad)));
		try {
			// 将jwsObject 进行HMAC签名
			jwsObject.sign(new MACSigner(SECRET));
			tokenString=jwsObject.serialize();
		} catch (JOSEException e) {
			System.err.println("签名失败:" + e.getMessage());
			e.printStackTrace();
		}
		return tokenString;
	}
    
    
    
    /**
     * 校验token是否合法，返回Map集合,集合中主要包含    state状态码   data鉴权成功后从token中提取的数据
     * 该方法在过滤器中调用，每次请求API时都校验
     * @param token
     * @return  Map<String, Object>
     */
	public static Map<String, Object> validToken(String token) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			JWSObject jwsObject = JWSObject.parse(token);
			Payload payload = jwsObject.getPayload();
			JWSVerifier verifier = new MACVerifier(SECRET);

			if (jwsObject.verify(verifier)) {
				JSONObject jsonOBj = payload.toJSONObject();
				// token校验成功（此时没有校验是否过期）
				resultMap.put("state", TokenState.VALID.toString());
				// 若playload包含ext字段，则校验是否过期
				if (jsonOBj.containsKey("ext")) {
					long extTime = (long) jsonOBj.get("ext");
					long curTime = new Date().getTime();
					// 过期了
					if (curTime > extTime) {
						resultMap.clear();
						resultMap.put("state", TokenState.EXPIRED.toString());
					}
				}
				resultMap.put("data", jsonOBj);

			} else {
				// 校验失败
				resultMap.put("state", TokenState.INVALID.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			// token格式不合法导致的异常
			resultMap.clear();
			resultMap.put("state", TokenState.INVALID.toString());
		}
		return resultMap;
	}	
    
}

/**
 * 枚举，定义token的三种状态
 * @author running@vip.163.com
 *
 */
 enum TokenState {  
	 /**
	  * 过期
	  */
	EXPIRED("EXPIRED"),
	/**
	 * 无效(token不合法)
	 */
	INVALID("INVALID"), 
	/**
	 * 有效的
	 */
	VALID("VALID");  
	
    private String  state;  
      
    private TokenState(String state) {  
        this.state = state;  
    }
    
    /**
     * 根据状态字符串获取token状态枚举对象
     * @param tokenState
     * @return
     */
    public static TokenState getTokenState(String tokenState){
    	TokenState[] states=TokenState.values();
    	TokenState ts=null;
    	for (TokenState state : states) {
			if(state.toString().equals(tokenState)){
				ts=state;
				break;
			}
		}
    	return ts;
    }
    public String toString() {
    	return this.state;
    }
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
    
}  
