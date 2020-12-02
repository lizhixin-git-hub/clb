package com.lzx.frame.common.toolkit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * json工具类
 */
public class JsonUtils {

    private JsonUtils() {

    }

    /**
     * 通过json格式，返回JavaBean对象
     *
     * @param json json字符串
     * @param cls  目标类型
     * @param <T>  目标类型
     * @return 目标对象
     */
    public static <T> T jsonToObject(String json, Class<T> cls) {
        return !StringUtils.checkValNotNull(json) ? null : JSON.parseObject(json, cls);
    }

    /**
     * json转bean或list或map
     *
     * @param json json字符串
     * @param <T>  目标类型
     * @return 目标对象
     */
    public static <T> T jsonToObject(String json, TypeReference<T> typeReference) {
        return !StringUtils.checkValNotNull(json) ? null : JSON.parseObject(json, typeReference);
    }

    /**
     * 传入对象直接返回Json
     *
     * @param object 对象
     * @return 目标字符串
     */
    public static <T> String toJSONString(T object) {
        return !StringUtils.checkValNotNull(object) ? null : JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

}
