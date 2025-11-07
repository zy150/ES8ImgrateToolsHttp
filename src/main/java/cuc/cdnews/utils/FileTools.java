package cuc.cdnews.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileTools {


	/**
	 * 文件转为二进制数组 等价于fileToBin
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getFileToByte(File file) {
		byte[] by = new byte[(int) file.length()];
		try {
			InputStream is = new FileInputStream(file);
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			byte[] bb = new byte[2048];
			int ch;
			ch = is.read(bb);
			while (ch != -1) {
				bytestream.write(bb, 0, ch);
				ch = is.read(bb);
			}
			by = bytestream.toByteArray();
			
			is.close();
		} catch (Exception ex) {
			throw new RuntimeException("transform file into bin Array 出错", ex);
		}
		return by;
	}
	/**
    *
    * @param result 音频字节流
    * @param file 存储路径
    */
    public static void byte2File(byte[] result, String file) {
        File audioFile = new File(file);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(audioFile);
            fos.write(result);

        }catch (Exception e){
            //logger.info(e.toString());
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    public static String readFile(File file, String charset){
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
	public static void WriteFileDate(String fileName, String data) {

		try {

			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			BufferedWriter output = new BufferedWriter(new FileWriter(file));

			output.write(data);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void WriteFileDate(String folderName,String fileName, String data) {

		try {
			File folder = new File(folderName);
			if (!folder.exists()) {
				boolean dirCreated = folder.mkdirs();
				if (dirCreated) {
	                System.out.println("Directory created: " + folderName);
	            } else {
	                System.out.println("Failed to create directory: " + folderName);
	            }
			}
			
			File writefile = new File(folderName + File.separator+ fileName);//.createNewFile();
			
			BufferedWriter output = new BufferedWriter(new FileWriter(writefile));

			output.write(data);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 读取文件内容的方法
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lines.remove(0);
        return lines;
    }

    // 将内容写回文件的方法
    public static void writeFile(String filePath, List<String> lines,Boolean isNew) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath,true))) {
            for (String line : lines) {
            	if(isNew)
            	{
            		bw.write(line);
            		bw.newLine(); // 写入新行
            	}
            	else
            	{
            		bw.append(line);
            		bw.newLine(); // 写入新行
            	}
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
