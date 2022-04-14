package com.payloads.online;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import java.util.Base64;
import java.io.IOException;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class DefineTransformer implements ClassFileTransformer {

    private Properties propertiesLoader;

    public DefineTransformer(String configPath) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(configPath));
        propertiesLoader = new Properties();
        propertiesLoader.load(in);
    }

    private String getProperties(String key){
        String value = propertiesLoader.getProperty(key);
        if (value == null){
            return new String();
        }
        return value;
    }

    private String base64decode(String str) {
        byte[] decodedBytes = Base64.getDecoder().decode(str);
        return new String(decodedBytes);
    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("加载类：" + className);
        String packageName = className.replaceAll("/",".");

        if (!packageName.equals(getProperties("class"))){
            return null;
        }

        ClassPool classPool = ClassPool.getDefault();
        System.out.println("创建 ClassPool ... 处理类 : " + className);

        try {
            CtClass clazz = classPool.get(packageName);
            CtMethod[] Methods = clazz.getDeclaredMethods();

            // 获取方法名称
            String methodName = getProperties("method");
            if (methodName == null){
                System.out.println("获取不到方法名，请检查配置文件");
                return null;
            }

            // 遍历所有的对象方法
            for (CtMethod method : Methods){
                if (methodName.equals(method.getName())){
                    String afterCode = getProperties("after_code");
                    String bodyCode = getProperties("body_code");
                    String beforeCode = getProperties("before_code");
                    System.out.println("处理方法：" + method.getName());

                    // 在执行原方法之前插入字节码
                    if (beforeCode.length() > 0){
                        method.insertBefore(this.base64decode(beforeCode));
                    }
                    // 在原方法执行完毕之前插入字节码
                    if (afterCode.length() > 0){
                        method.insertAfter(this.base64decode(afterCode));
                    }
                    // 覆盖原方法
                    if (bodyCode.length() > 0){
                        method.setBody(this.base64decode(bodyCode));
                    }

                    break;
                }
            }
            byte[] byteCode = clazz.toBytecode();
            clazz.detach();
            return byteCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

