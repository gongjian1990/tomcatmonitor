package com.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class TomcatMonitor implements Runnable{  
    
    String start=""; //系统命令 启动  
    String stop=""; //系统命令 关闭  
    String testHttp="";  //测试连接地址  
    int testIntervalTime=1;//测试连接间隔时间，单位为秒  
    int waitIntervalTime=2; //等待测试间隔时间，单位为秒  
    int testTotalCount=5; //测试连接总次数  
      
    Thread thread=null;  
      
    public TomcatMonitor(){  
        InputStream in = TomcatMonitor.class.getResourceAsStream("config.properties");  
        Properties p = new Properties();  
         try {  
            p.load(in);  
            stop=p.getProperty("stop");  
            start=p.getProperty("start");  
            testHttp=p.getProperty("testHttp");  
            testIntervalTime=Integer.parseInt(p.getProperty("testIntervalTime"));  
            waitIntervalTime=Integer.parseInt(p.getProperty("waitIntervalTime")); 
            testTotalCount=Integer.parseInt(p.getProperty("testTotalCount"));             
        } catch (Exception e) {  
                    e.printStackTrace();  
        }  
              
        System.out.println("*******************初始化成功!*******************");  
           
          
        thread=new Thread(this);  
        thread.start();       
    }  
      
    public void run() {  
        System.out.println("正在监控中...");     
        int testCount=0;  
        while(true){  
            testCount=0;  
            testCount++;              
            boolean isrun=test();  
            System.out.println("正在启动测试连接,尝试连接次数为:"+testCount+",结果为:"+(isrun==false?"失败.":"成功!"));                 
            while(!isrun){  
                if(testCount>=testTotalCount)break;  
                try {  
                    Thread.sleep(testIntervalTime*1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                testCount++;  
                System.out.println("正在启动测试连接,尝试连接次数为:"+testCount+",结果为:"+(isrun==false?"失败.":"成功!"));                 
                isrun=test();  
            }  
              
            if(!isrun){               
                try{        
                    //关闭tomcat服务      
                    Process proc = Runtime.getRuntime().exec(stop);  
                    Thread.sleep(5000);  
                    //启动tomcat服务  
                    System.out.println("测试连接失败,正在重启tomcat");  
                    Process p=Runtime.getRuntime().exec(start);   
                    System.out.println("重启tomcat成功");  
                }catch(Exception e){  
                    e.printStackTrace();  
                    System.out.println("重启tomcat异常,请查看先关错误信息。。。。。");  
                      
                }                 
            }  
              
            try {  
                Thread.sleep(waitIntervalTime*1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
              
            isrun=test();  
        }         
    }  
      
    public boolean test(){  
          
        URL url=null;         
        try {  
            url = new URL(testHttp);  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        }  
        try {  
            URLConnection urlConn=url.openConnection();  
            urlConn.setReadTimeout(15000);  
            BufferedReader reader = new BufferedReader(new InputStreamReader( urlConn.getInputStream()));            //实例化输入流，并获取网页代码  
                   String s;                                         
                   while ((s = reader.readLine()) != null) {  
                      return true;     
                   }                          
        } catch (Exception e) {  
          return false;  
        }  
        return false;  
    }  
      
      
    public static void main(String[] args) throws Exception{  
        TomcatMonitor tm=new TomcatMonitor();  
    }  
  
}  
