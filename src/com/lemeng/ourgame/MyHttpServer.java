package com.lemeng.ourgame;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;  
import com.sun.net.httpserver.HttpHandler;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.spi.HttpServerProvider;


public class MyHttpServer {  
    //启动服务，监听来自客户端的请求  
    public static void httpserverService() throws IOException {  
        HttpServerProvider provider = HttpServerProvider.provider();  
        //HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(8809), 100);//监听端口6666,能同时接 受100个请求 
        HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(8889), 100);//监听端口6666,能同时接 受100个请求 
        httpserver.createContext("/ourgame", new MyHttpHandler());   
        httpserver.setExecutor(null);  
        httpserver.start();       
        MyDebug.log("server started");  
    }  
    //Http请求处理类  
    static class MyHttpHandler implements HttpHandler {  
        public void handle(HttpExchange httpExchange) throws IOException {  
            String responseMsg = "ok";   //响应信息  
            //InputStream in = httpExchange.getRequestBody(); //获得输入流 
            String queryString =  getParm(httpExchange.getRequestBody());  
            MyDebug.log("request:\n"+queryString);  
          
            responseMsg = SqlManager.getIntance().dealMessage(queryString);
            MyDebug.log("responseMsg:\n"+responseMsg);
            httpExchange.sendResponseHeaders(200,responseMsg.getBytes().length); //设置响应头属性及响应信息的长度  
            OutputStream out = httpExchange.getResponseBody();  //获得输出流  
            out.write(responseMsg.getBytes());  
            out.flush();  
            out.close();
            out = null;
            httpExchange.close();    
            httpExchange = null;
        }  
    }  
    
    public static  Map<String,String> jsonToMap(String str){
    	Map<String,String> result = new HashMap<>();
    	
    	try {
    		JSONObject jb = new JSONObject(str);
        	if(jb.has("user")) {
        		result.put("user", jb.getString("user"));
        	}
        	if(jb.has("orderid")) {
        		result.put("orderid",jb.getInt("orderid")+"");
        	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

    	return result;
    }
    

	public static String getParm(InputStream request) {
        BufferedReader br = null;
        InputStreamReader ir = null;
      
        try {
        	ir = new InputStreamReader(request, "UTF-8");
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();	
        	 return null;
        }
                  
        br = new BufferedReader(ir);
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            ir.close();
            ir = null;
            br.close();
            br = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	if(ir != null ) {
        		try {
        			ir.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	
        	if(br != null ) {
        		try {
					br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
            e.printStackTrace();
            return null;
        }
       
        try {
			return URLDecoder.decode(sb.toString(),"utf8") ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    
    public static Map<String,String> formData2Dic(String formData ) {
        Map<String,String> result = new HashMap<>();
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        for(String item: items) {
            String[] keyAndVal = item.split("=");
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                    result.put(key,val);
                }catch (UnsupportedEncodingException e) {}
            }
        }
        return result;
    }
    
    public static void main(String[] args) throws IOException {  

		 try {
	            // The newInstance() call is a work around for some
	            // broken Java implementations

	            //Class.forName("com.mysql.cj.jdbc.Driver");com.mysql.jdbc.
	            Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("加载成功");
	        } catch (Exception ex) {
	        	System.out.println("加载失败");
	            // handle the error
	        }
        httpserverService();  
    }  
}  