package com.lzx.frame.core.entity;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ComplainEnum {

    public enum ComplainType {

        QUALITY(1, "维修质量"),
        EFFICIENCY(2, "维修效率"),
        PRICETRANSPARENCY(3, "价格透明度"),
        SERVICEATTITUDE(4, "服务态度"),
        ENVIRONMENTALSCIENCE(5, "店面环境"),
        OTHER(6, "其它");

        ComplainType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        private final int value;
        private final String name;

        public byte getValue() {
            return (byte) value;
        }

        public String getName() {
            return name;
        }

        public static String getDesc(byte value) {
            for (ComplainType e : ComplainType.values())
                if (e.getValue() == value)
                    return e.getName();
            return "";
        }
    }

    public static void main(String[] args) {
        /*List<Map<Byte, String>> complainType = Stream.of(ComplainType.values()).map(x -> {
            Map<Byte, String> map = new HashMap<>();
            map.put(x.getValue(), x.getName());
            return map;
        }).collect(Collectors.toList());
        System.out.println(JsonUtils.toJSONString(complainType));*/

        //上传接口及图片数据封装装换成json格式
        String jsonData = "{\n" +
                "\t\"system\": [{\n" +
                "\t\t\"jczbh\": \"231202008\",\n" +
                "\t\t\"ywlb\": \"ZJZP\"\n" +
                "\t}],\n" +
                "\t\"data_info\": [{\n" +
                "\t\t\"flowId\": \"231202008201912170001\",\n" +
                "\t\t\"imgType\": \"JCJLD\",\n" +
                "\t\t\"imgTime\": \"" + getYMDHMS() + "\",\n" +
                "\t\t\"organ\": \"231202008\",\n" +
                "\t\t\"img\": \"" + image2Base64("http://img.ddqnr.com/shvtspimg//vtsp_imgs/140/2019/12/20191217104619_308.jpg") + "\",\n" +
                "\t\t\"flowcs\": \"1\"\n" +
                "\t}]\n" +
                "}";

        /*String xmlParams = String.join(StringUtils.EMPTY, "<?xml version=\"1.0\" encoding=\"utf-8\"?> ",

                //设置根标签及函数编码
                "<root> <function> <funcode>", "04", "</funcode> <organ>",

                //设置省网检测站编码
                "230112004", "</organ> ", " </function> <param> <jcbgbh>",

                //设置检测报告单编号
                "230112004201911220004", "</jcbgbh> <jycs>",

                //设置检测次数
                "1", "</jycs> ",

                //设置检验类别
                " <jylb>", "技术等级评定", "</jylb> <cphm>",

                //设置车牌号码
                "黑A065MF", "</cphm> <cpys>",

                //设置车牌颜色
               "蓝色", "</cpys> </param> </root>");*/

        //创建动态代理
        JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();

        //获取客户端实体
        Client client = factory.createClient("http://59.110.219.201/rweb/RService.asmx?wsdl");

        //请求结果接收
        Object[] objects;
        try {
            //调用webservice，获取结果
            objects = client.invoke("PushData", jsonData);

            //请求省网地址，上传图片
            String answer = ArrayUtils.isEmpty(objects) ? "{}" : (String) objects[0];

            System.out.println(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将远程图片转换成base64字符串
     *
     * @param imgUrl 远程图片路径
     * @return 图片base64字符串
     */
    private static String image2Base64(String imgUrl) {
        //输入流
        InputStream is = null;

        //字节输出流
        ByteArrayOutputStream outStream = null;

        //http请求Connection
        HttpURLConnection httpUrl = null;

        try {
            //设置远程图片路径
            URL url = new URL(imgUrl);

            //打开连接
            httpUrl = (HttpURLConnection) url.openConnection();

            //获取连接
            httpUrl.connect();

            //获取输入流
            is = httpUrl.getInputStream();

            //创建字节输出流
            outStream = new ByteArrayOutputStream();

            //创建一个Buffer字符串  
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕

            int len;

            //使用一个输入流从buffer里把数据读取出来
            while ((len = is.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }

            //压缩
            Thumbnails.of(is).scale(1f).outputQuality(0.25f).toOutputStream(outStream);

            //对字节数组Base64编码
            return Base64.getEncoder().encodeToString(outStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭输入流
                if (is != null) {
                    is.close();
                }

                //关闭字节输出流
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //关闭htpp连接
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
        return "";
    }

    /**
     * 生成格式 yyyy-MM-dd HH:mm:ss
     */
    private static String getYMDHMS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 控制时间输出格式
        return sdf.format(new Date());
    }
}
