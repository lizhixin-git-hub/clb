package com.lzx.frame.core.api;

import com.lzx.frame.common.annotation.decrypt.AESDecryptBody;
import com.lzx.frame.common.annotation.encrypt.AESEncryptBody;
import com.lzx.frame.common.annotation.log.Log;
import com.lzx.frame.core.entity.Student;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping({"", "/", "/test"})
public class TestController {

    @RequestMapping({"", "/"})
    public String index() {
        return "index";
    }

    @AESEncryptBody()
    @RequestMapping("/encrypt")
    @Log(describe = "新增学生，学号为#{#student.no},名字为#{#student.name},key为#{#key},电话为#{#student.phone}")
    public Map<String, Object> encrypt(Student student, String key) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("md5", "md5");
        result.put("result", map);
        result.put("timestamp", new Date().getTime());
        result.put("name", student.getName());
        result.put("key", key);
        return result;

    }

    @AESDecryptBody()
    @RequestMapping("/dncrypt")
    @Log(describe = "删除学生，学号为#{#student.no},名字为#{#student.name},key为#{#key},电话为#{#student.phone}")
    public Map<String, Object> dncrypt(@RequestBody Map<String, Object> map) {
        return map;
    }

    @ResponseBody
    @RequestMapping("/upload")
    public void upload(HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        //检查form中是否有enctype="multipart/form-data"
        if (multipartResolver.isMultipart(request)) {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            //获取multiRequest 中所有的文件名
            Iterator iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                //一次遍历所有文件
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    //String path = "E:/springUpload" + file.getOriginalFilename();
                    //上传
                    byte[] imageBytes = compressPicCycle(file.getBytes(), 900, 0.8);
                    compressPicForScale(file.getBytes(), 900);
                    InputStream inputStream = new ByteArrayInputStream(imageBytes);
                    OutputStream outputStream = new FileOutputStream(new File("E://index.jpg"));
                    byte[] by = new byte[inputStream.available()];//此数字不唯一哦；
                    int len;
                    while ((len = inputStream.read(by)) != -1) { //len就是得出的字节流了。
                        outputStream.write(by, 0, len);
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("方法三的运行时间：" + (endTime - startTime) + "ms");
    }

    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     */
    private static byte[] compressPicForScale(byte[] imageBytes, long desFileSize) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        double accuracy = getAccuracy(srcSize / 1024);
        try {
            while (imageBytes.length > desFileSize * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream)
                        .scale(1f)
                        .outputQuality(accuracy)
                        .toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
                System.out.println("====" + imageBytes.length);
            }
            System.out.println("图片原大小=" + srcSize / 1024 + "kb | 压缩后大小=" + imageBytes.length / 1024 + "kb");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("【图片压缩】msg=图片压缩失败!");
        }
        return imageBytes;
    }

    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小
     * @return 图片压缩质量比
     */
    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2047) {
            accuracy = 0.6;
        } else if (size < 3275) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }

    /**
     * @param fileSize 指定图片大小,单位kb
     * @param accuracy 精度,递归压缩的比率,建议小于0.9
     */
    private static byte[] compressPicCycle(byte[] bytes, long fileSize, double accuracy) throws IOException {
        long srcFileSizeJPG = bytes.length;
        // 2、判断大小，如果小于指定kb，不压缩；如果大于等于指定kb，压缩
        if (srcFileSizeJPG <= fileSize * 1024) {
            return bytes;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int width = bim.getWidth();
        int height = bim.getHeight();
        width = new BigDecimal(width).multiply(new BigDecimal(accuracy)).intValue();
        height = new BigDecimal(height).multiply(new BigDecimal(accuracy)).intValue();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        Thumbnails.of(new ByteArrayInputStream(bytes)).scale(width, height).keepAspectRatio(false).outputQuality(accuracy).toOutputStream(byteArrayOutputStream);
        System.out.println("================:" + byteArrayOutputStream.toByteArray().length);
        return compressPicCycle(byteArrayOutputStream.toByteArray(), fileSize, accuracy);
    }
}
