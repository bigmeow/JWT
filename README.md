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
|        │   └── JwtTestCase.java
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
+ junit.jar（单元测试相关jar包）


核心类Jwt.java结构：
> 2个静态方法createToken和validToken，分别用于生成TOKEN和校验TOKEN;
> 定义了枚举TokenState，用于表示验证token时的结果，用户可根据结果进行不同处理：
   * EXPIRED  token过期
   * INVALID  token无效（包括token不合法，token格式不对，校验时异常）
   * VALID    token有效

   
   
##使用示例
###获取token

```Java
Map<String , Object> payload=new HashMap<String, Object>();
Date date=new Date();
payload.put("uid", "291969452");//用户id
payload.put("iat", date.getTime());//生成时间
payload.put("ext",date.getTime()+1000*60*60);//过期时间1小时
String token=Jwt.createToken(payload);
System.out.println("token:"+token);

```

###校验token
```Java

String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiIyOTE5Njk0NTIiLCJpYXQiOjE0NjA0MzE4ODk2OTgsImV4dCI6MTQ2MDQzNTQ4OTY5OH0.RAa71BnklRMPyPhYBbxsfJdtXBnXeWevxcXLlwC2PrY";
Map<String, Object> result=Jwt.validToken(token);

String state=(String)result.get("state");
switch (TokenState.getTokenState(state)) {
case VALID:
	//To do somethings
	System.out.println("有效token");
	break;
case EXPIRED:
	System.out.println("过期token");
	break;
case INVALID:
	System.out.println("无效的token");
	break;
}

System.out.println("返回结果数据是：" +result.toString());
	


```
