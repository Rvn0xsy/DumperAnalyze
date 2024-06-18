## DumperAnalyze

> 最近审计了一套产品代码，发现使用了JavaAgent+Javassist技术在类加载后进行替换一部分被加密的方法，感觉也可以通过这个技术应用到其他地方，因此写了这个小工具，原理比较简单。
> 相关文章：[某安全数据交换系统的漏洞挖掘](https://payloads.online/archivers/2023/09/18/acc369fd-9310-4351-889c-457b12da9c25)

通过JavaAgent与Javassist技术对JVM加载的类对象进行动态插桩，可以做一些破解、加密验证的绕过等操作

使用方法：

```
java -javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=./config.properties -jar SpringApp.jar
```

JavaAgent可以通过`=`传递参数，`./config.properties` 是配置文件，SpringApp.jar 为要在运行时修改的jar包


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
- body_code   直接覆盖原方法，原来的代码不会执行

### 其他应用程序使用该项目示例

#### Jetty

1. 在jetty.sh脚本中编辑JAVA_OPTIONS：

```bash
export DCONFIG_PATH="./config.properties"
export JAVA_OPTIONS="${JAVA_OPTIONS} -javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=${DCONFIG_PATH}"
```
2. 在 start.ini 中也可以添加

```bash
-javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=./config.properties
```

#### Resin

修改 `conf/resin.conf` 或者 `conf/resin.xml` 文件中的`<jvm-args>`：

1. Java 8

```xml
<jvm-arg>-javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=./config.properties</jvm-arg>
```
2. Java 9 或更高

#### Spring Boot 

```
java -javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=./config.properties -jar SpringApp.jar
```

#### Tomcat

修改`CATALINA_BASE/bin/setenv.sh` 或 `CATALINA_BASE/bin/setenv.bat` 文件，如果文件不存在，请创建一个。使用setenv.sh脚本配置您的环境变量：

```bash
export DCONFIG_PATH="./config.properties"
export CATALINA_OPTS="$CATALINA_OPTS -javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=${DCONFIG_PATH}"
```

#### WebLogic

编辑startWebLogic.sh文件，添加如下内容：

```bash
export DCONFIG_PATH="./config.properties"
export JAVA_OPTIONS="${JAVA_OPTIONS} -javaagent:Dumper-Analyze-1.0-SNAPSHOT-jar-with-dependencies.jar=${DCONFIG_PATH}"
```

