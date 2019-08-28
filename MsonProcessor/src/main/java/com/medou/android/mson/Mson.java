package com.medou.android.mson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Mson {

    public final static String CLASSTARGET = "$$Mson";
    public final static String FROMJSON = "fromJsonString";
    public final static String TOJSON = "toJsonString";
//    public final static String TOJSON_ = ".toJsonString()";
//    public final static String FROMJSON_ = ".fromJsonString()";

    public static <T> T fromJson(String json, Class<T> clz){
        try {
            Class clz2 = Class.forName(clz.getName() + CLASSTARGET);
            Method method = clz2.getDeclaredMethod(FROMJSON, String.class);
            if(method != null){
                return (T) method.invoke(clz2.newInstance(), json);
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (NoSuchMethodException e){
            return null;
        } catch (ClassNotFoundException e){
            return null;
        }
        return null;
    }

    public static String toJson(Object obj){
        try {
            String kls = obj.getClass().getName() + CLASSTARGET;
            Class clz = Class.forName(kls);
            Class[] argsClass = new Class[]{obj.getClass()};
            Method method = clz.getMethod(TOJSON, argsClass);
            if(method != null){
                return method.invoke(clz.newInstance(), obj).toString();
            }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            return null;
        } catch (NoSuchMethodException e){
            return null;
        } catch (ClassNotFoundException e){
            return null;
        }
        return null;
    }

}
