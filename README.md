#JWT

本仓库依赖于下面jar包：
+ nimbus-jose-jwt-4.13.1.jar (一款开源的成熟的JSON WEB TOKEN 解决方法，本仓库的代码是对其的进一步封装)
+ json-smart-2.0-RC2.jar,asm-1.0-RC1.jar (依赖jar包，主要用于JSONObject序列化)


核心类Jwt.java结构：
> 2个静态方法createToken和validToken，分别用于生成TOKEN和校验TOKEN;
> 定义了4个静态常量状态，用于表示验证token时的结果：
   * EXPIRED  token过期
   * FAIL     token不一致
   * SUCCESS  token校验成功
   * EXCEPT   代码抛异常（校验token时代码出错）