package com.hui.zhang.common.util;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by zhanghui on 2018/2/7.
 */
public class HessianEncoder {
    private static final Logger logger = LoggerFactory.getLogger(HessianEncoder.class);

    /**
     * 序列化成byte
     * @param t
     * @param <T>
     * @return
     */
    public static <T>  byte[] encoderByte(T t){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(t);
        } catch (IOException e) {
            logger.error("hessian序列化异常：{}",e);
        }
        byte[] bytes = os.toByteArray();
        return bytes;
    }

    /**
     * 序列化成string
     * @param t
     * @param <T>
     * @return
     */
    public static <T>  String encoderStr(T t){
        byte[] bytes =encoderByte(t);
        String bstr=JsonEncoder.DEFAULT.encode(bytes);
        return bstr;
    }

    /**
     * byte反序列化成对象
     * @param bytes
     * @param cls
     * @param <T>
     * @return
     */
    public static<T> T decodeByte(byte [] bytes,Class<T> cls){
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        T t = null;
        try {
            t = (T) hi.readObject(cls);
        } catch (Exception e) {
            logger.error("hessian反序列化异常：{}",e);
        }
        return  t;
    }

    /**
     * string 反序列化成对象
     * @param str
     * @param cls
     * @param <T>
     * @return
     */
    public static<T> T decodeStr(String str,Class<T> cls){
        byte[] bytes=JsonEncoder.DEFAULT.decode(str,byte[].class);
        T t=decodeByte(bytes,cls);
        return  t;
    }
}
