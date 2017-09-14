package com.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class TomcatMonitor implements Runnable{  
    
    String start=""; //ϵͳ���� ����  
    String stop=""; //ϵͳ���� �ر�  
    String testHttp="";  //�������ӵ�ַ  
    int testIntervalTime=1;//�������Ӽ��ʱ�䣬��λΪ��  
    int waitIntervalTime=2; //�ȴ����Լ��ʱ�䣬��λΪ��  
    int testTotalCount=5; //���������ܴ���  
      
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
              
        System.out.println("*******************��ʼ���ɹ�!*******************");  
           
          
        thread=new Thread(this);  
        thread.start();       
    }  
      
    public void run() {  
        System.out.println("���ڼ����...");     
        int testCount=0;  
        while(true){  
            testCount=0;  
            testCount++;              
            boolean isrun=test();  
            System.out.println("����������������,�������Ӵ���Ϊ:"+testCount+",���Ϊ:"+(isrun==false?"ʧ��.":"�ɹ�!"));                 
            while(!isrun){  
                if(testCount>=testTotalCount)break;  
                try {  
                    Thread.sleep(testIntervalTime*1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                testCount++;  
                System.out.println("����������������,�������Ӵ���Ϊ:"+testCount+",���Ϊ:"+(isrun==false?"ʧ��.":"�ɹ�!"));                 
                isrun=test();  
            }  
              
            if(!isrun){               
                try{        
                    //�ر�tomcat����      
                    Process proc = Runtime.getRuntime().exec(stop);  
                    Thread.sleep(5000);  
                    //����tomcat����  
                    System.out.println("��������ʧ��,��������tomcat");  
                    Process p=Runtime.getRuntime().exec(start);   
                    System.out.println("����tomcat�ɹ�");  
                }catch(Exception e){  
                    e.printStackTrace();  
                    System.out.println("����tomcat�쳣,��鿴�ȹش�����Ϣ����������");  
                      
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
            BufferedReader reader = new BufferedReader(new InputStreamReader( urlConn.getInputStream()));            //ʵ����������������ȡ��ҳ����  
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
