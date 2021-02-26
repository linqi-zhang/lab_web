package com.hotstrip.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2019/9/6.
 * 文件处理工具类
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static String OS = System.getProperty("os.name").toLowerCase();

    private static String Windows = "windows";

    /**
     * @description  根据文件名  获取文件的拓展名
     * 例如 test.jpg ====> .jpg 就是后缀名
     * @param myFileName
     */
    public static String getExtName(String myFileName) {
        if(myFileName == null || "".equals(myFileName))
            return null;
        int index = myFileName.lastIndexOf(".");
        //文件没有拓展名
        if(index == -1) {
            return null;
        }
        //获取文件拓展名
        return myFileName.substring(index+1, myFileName.length());
    }

    /**
     * @description 判断当前系统是否是windows
     * @return
     */
    public static boolean isWindows(){
        return OS.indexOf(Windows) >= 0;
    }

    /**
     * @description 拷贝文件流到文件
     * @param multipartFile
     * @param tempPartFile
     */
    public static void copyInputStreamToFile(MultipartFile multipartFile, File tempPartFile) {
        File file = null;
        try {
            file = convert(multipartFile);  //multipartFile 转 file
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(tempPartFile);
            byte[] b = new byte[102400];
            int n;
            while ((n = fis.read(b)) != -1) {
                fos.write(b, 0, n);
            }
            fis.close();
            fos.flush();
            fos.close();
        }catch (Exception e){
            logger.error("拷贝文件流到文件......message: {}...caused: {}", e.getMessage(), e.getCause());
        }finally {
            //用上面的 multipartFile 转 file方法  会在磁盘里面生成一个文件   干掉它
            if(file != null && file.exists()){
                file.delete();
            }
        }
    }

    /**
     * @description multipartFile 转 file   使用该方法会在磁盘里生成一个文件(通常是在项目根路径下)
     * @param file
     * @return
     */
    public static File convert(MultipartFile file) {
        File convertFile = new File(file.getOriginalFilename());
        try {
            if(convertFile.createNewFile()){
                FileOutputStream fos = new FileOutputStream(convertFile);
                fos.write(file.getBytes());
                fos.close();
            }else logger.error("转换文件失败......");
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return convertFile;
    }

    // 随机数
    public static String randomValue(int seed){
        return String.valueOf((int)((Math.random()*9+1)*seed));
    }
}