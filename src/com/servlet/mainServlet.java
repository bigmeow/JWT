package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;
@WebServlet(urlPatterns="/servlet/getInfo",loadOnStartup=1)
public class mainServlet extends HttpServlet {

	private static final long serialVersionUID = -1643121334640537359L;

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("正在调用获取信息的接口");
		//将过滤器中存入的payload数据取出来
		HashMap<String, String> data=(HashMap<String, String>) request.getAttribute("data");
		//payload中的数据可以用来做查询，比如我们在登陆成功时将用户ID存到了payload中，我们可以将它取出来，去数据库查询这个用户的所有信息；
		//而不是用request.getParameter("uid")方法来获取前端传给我们的uid，因为前端的参数时可篡改的不完全可信的，而我们从payload中取出来的数据是从token中
		//解密取出来的，在秘钥没有被破解的情况下，它是绝对可信的；这样可以避免别人用这个接口查询非自己用户ID的相关信息
		JSONObject resp=new JSONObject();
		resp.put("success", true);
		resp.put("msg", "成功");
		resp.put("data", data);
		output(resp.toJSONString(), response);
	}
	
	public void output(String jsonStr,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=UTF-8;");
		PrintWriter out = response.getWriter();
		out.println(jsonStr);
		out.flush();
		out.close();
		
	}
	
}
