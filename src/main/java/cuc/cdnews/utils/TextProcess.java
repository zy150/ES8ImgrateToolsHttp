package cuc.cdnews.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;


public class TextProcess {

	public static String readFile(String fileName, String charset){
		
		File file = new File(fileName);
        //设置默认编码
        if(charset == null){
            charset = "UTF-8";
        }
         
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                 
                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    sb.append(text);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                // TODO: handle exception
            	e.printStackTrace();
            }
        }
        return null;
    }
	public static void writeFile(String path,String content){
	     
	    try {
	        //String content = "测试使用字符串";
	        File file = new File(path);
	        if(!file.exists()){
	            FileWriter fw = new FileWriter(file,false);
	            BufferedWriter bw = new BufferedWriter(fw);
	            bw.write(content);
	            bw.close(); fw.close();
	            System.out.println("test1 done!");
	        }
	         
	    } catch (Exception e) {
	        // TODO: handle exception
	    }
	}
	//这种方式简单方便，代码简单。顺带一提的是：上述代码是清空文件重写，要想追加写入，则将FileWriter构造函数中第二个参数变为true。

	 
	//文件不存在时候，主动创建文件。
	public static void writeToFile2(String path){    
	    try {
	        String content = "测试使用字符串";
	        File file = new File("./File/test2.txt");
	        //文件不存在时候，主动穿件文件。
	        if(!file.exists()){
	            file.createNewFile();
	        }
	        FileWriter fw = new FileWriter(file,false);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(content);
	        bw.close(); fw.close();
	        System.out.println("test2 done!");
	         
	    } catch (Exception e) {
	        // TODO: handle exception
	    }
	}
	 //关键性的话语在于file.createNewFile();

	//使用FileOutputStream来写入txt文件。
	public static void writeToFile3(){    
	   String content = "测试使用字符串";
	   FileOutputStream fileOutputStream = null;
	   File file = new File("./File/test3.txt");
	    
	   try {
	       if(file.exists()){
	           file.createNewFile();
	       }
	        
	       fileOutputStream = new FileOutputStream(file);
	       fileOutputStream.write(content.getBytes());
	       fileOutputStream.flush();
	       fileOutputStream.close();
	   } catch (Exception e) {
	       // TODO: handle exception
	   }
	   System.out.println("test3 done");
	 
	}
	public static void writeFile02(String path,Map<String, Integer> oriMap) throws IOException {

        //写入中文字符时解决中文乱码问题
        FileOutputStream fos=new FileOutputStream(new File(path));
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter  bw=new BufferedWriter(osw);
        //简写如下：
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        //        new FileOutputStream(new File("E:/phsftp/evdokey/evdokey_201103221556.txt")), "UTF-8"));

        
        int i=0;
        for(String ks:oriMap.keySet())
		{
			System.out.println(ks + ":" +oriMap.get(ks));
			 bw.write(i+"、 "+ks + ":" +oriMap.get(ks)+"\t\n");
			 i++;
		}
        //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        bw.close();
        osw.close();
        fos.close();
    }
	
	public static void writeFile02(String path,List<String> list) throws IOException {

        //写入中文字符时解决中文乱码问题
        FileOutputStream fos=new FileOutputStream(new File(path));
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter  bw=new BufferedWriter(osw);
        //简写如下：
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        //        new FileOutputStream(new File("E:/phsftp/evdokey/evdokey_201103221556.txt")), "UTF-8"));

        
        for(String ks:list)
		{
			 bw.write(ks+"\r\n");
		}
        //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
        bw.close();
        osw.close();
        fos.close();
        
        
        System.out.println(path);
    }
	
	public static void writeOverideFile(String path,String content){
	     
	    try {
	        //String content = "测试使用字符串";
	        File file = new File(path);
	        //if(file.exists()){
	            FileWriter fw = new FileWriter(file,false);
	            BufferedWriter bw = new BufferedWriter(fw);
	            bw.write(content);
	            bw.close(); fw.close();
	            //System.out.println("覆盖文件");
	        //}
	         
	    } catch (Exception e) {
	        // TODO: handle exception
	    }
	}
}
