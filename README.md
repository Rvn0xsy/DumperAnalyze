## DumperAnalyze

> 最近审计了一套产品代码，发现使用了JavaAgent+Javassist技术在类加载后进行替换一部分被加密的方法，感觉也可以通过这个技术应用到其他地方，因此写了这个小工具，原理比较简单。

通过JavaAgent与Javassist技术对JVM加载的类对象进行动态插桩，可以做一些破解、加密验证的绕过等操作

使用方法：

```
java -javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=./config.properties -jar SpringApp.jar
```

JavaAgent可以通过`=`传递参数，`./config.properties` 是配置文件。


### 配置文件


```properties
# 需要插桩修改的类
class=online.payloads.main.Main
# 需要插桩修改的方法
method=getMain
# 在方法执行前想要执行的代码 （需要Base64编码）
# System.out.println("Inject");
before_code=U3lzdGVtLm91dC5wcmludGxuKCJJbmplY3QiKTsK
```

除了`before_code`还有如下几个方式：

- before_code 在执行原方法之前插入字节码
- after_code  在原方法执行完毕（return）之前插入字节码
- body_code   覆盖原方法

