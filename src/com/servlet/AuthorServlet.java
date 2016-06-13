package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import com.jwt.Jwt;
@WebServlet(urlPatterns="/author/token",loadOnStartup=1,description="生成token的方法")
public class AuthorServlet extends HttpServlet {

	private static final long serialVersionUID = -8463692428988705309L;
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			String token=request.getHeader("token");
			System.out.println(token);
			Map<String, Object> result=Jwt.validToken(token);
			//转JSON并输出
			PrintWriter out = response.getWriter();
			out.println(new JSONObject(result).toJSONString());
			out.flush();
			out.close();
	}
	
	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String , Object> payload=new HashMap<String, Object>();
		Date date=new Date();
		payload.put("uid", "291969452");//用户id
		payload.put("iat", date.getTime());//生成时间
		payload.put("ext",date.getTime()+1000*60*60);//过期时间1小时
		String token=null;
		token=Jwt.createToken(payload);
		
		response.setContentType("text/html;charset=UTF-8;");
		Cookie cookie=new Cookie("token", token);
		cookie.setMaxAge(3600); 
		response.addCookie(cookie);
		PrintWriter out = response.getWriter();
		out.println(token);
		out.flush();
		out.close();
	}

}
