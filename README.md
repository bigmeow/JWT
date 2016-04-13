#JWT
##项目介绍
```
.
├── README.md
├── src
|    └── com
|        |── filter
|        |   └── CrossOriginResourceFilter.java 
|        ├── jwt
|        │   └── Jwt.java
|        │   └── Test.java
|        |
|        └── servlet
|            └── AuthorServlet.java
├── WebRoot
|   |── WEB-INFO
|   |── index.jsp
|   └── jquery-2.1.0.js
```
##由于使用了servlet3.0语法，运行环境要求JDK7以及以上，Tomcat7以及以上（根目录下附带降级版本，支持jdk1.6,tomcat6）
本项目依赖于下面jar包：
+ nimbus-jose-jwt-4.13.1.jar (一款开源的成熟的JSON WEB TOKEN 解决方法，本仓库的代码是对其的进一步封装)
+ json-smart-2.0-RC2.jar和asm-1.0-RC1.jar (依赖jar包，主要用于JSONObject序列化)
+ cors-filter-2.2.1.jar和java-property-utils-1.9.1.jar（用于处理跨域ajax请求）


核心类Jwt.java结构：
> 2个静态方法createToken和validToken，分别用于生成TOKEN和校验TOKEN;
> 定义了4个静态常量状态，用于表示验证token时的结果：
   * EXPIRED  token过期
   * FAIL     token不一致
   * SUCCESS  token校验成功
   * EXCEPT   代码抛异常（校验token时代码出错）
   
   
##使用示例
###获取token

```Java
Map<String , Object> payload=new HashMap<String, Object>();
Date date=new Date();
payload.put("uid", "291969452");//用户id
payload.put("iat", date.getTime());//生成时间
payload.put("ext",date.getTime()+1000*60*60);//过期时间1小时
String token=null;
try {
	token=Jwt.createToken(payload);
} catch (KeyLengthException e) {
	e.printStackTrace();
}
System.out.println("token:"+token);

```

###校验token
```Java
String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiIyOTE5Njk0NTIiLCJpYXQiOjE0NjA0MzE4ODk2OTgsImV4dCI6MTQ2MDQzNTQ4OTY5OH0.RAa71BnklRMPyPhYBbxsfJdtXBnXeWevxcXLlwC2PrY";
Map<String, Object> result=Jwt.validToken(token);
Boolean isSuccess=(Boolean) result.get("isSuccess");;//校验是否成功
Number statusr=(Number) result.get("status");//状态码 -1过期失效   0token不一致   1校验成功   2代码异常
JSONObject object= (JSONObject) result.get("data");//取出token中保存的数据，注意，该JSONObject的完整包名是net.minidev.json.JSONObject


```
