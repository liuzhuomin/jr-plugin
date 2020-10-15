## maven构建自定义插件

### 什么是maven插件?

以下是[官方](http://maven.apache.org/plugin-developers/index.html)描述:
    
“Maven”实际上只是一组Maven插件的核心框架。换句话说，插件是执行许多实际操作的地方，插件用于:创建jar文件、创建war文件、编译代码、
单元测试代码、创建项目文档，等等。您可以想到在项目上执行的几乎任何操作都是作为Maven插件实现的。
插件是Maven的核心特性，它允许跨多个项目重用公共构建逻辑。他们通过在项目描述的上下文中——项目对象模型(POM)——执行一个“动作”(即创建
一个WAR文件或编译单元测试)来实现这一点。插件行为可以通过一组唯一的参数来定制，这些参数通过每个插件目标(或Mojo)的描述公开。

### 环境准备

创建一个空的`maven`项目，其`groupId`为`jr.cn.plugin`,`artifactId`为`jr-maven-plugin`,`version`为`1.0-SNAPSHOT`,`packaging`为`maven-plugin`。

pom文件以下:

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>jr.cn.plugin</groupId>
    <artifactId>jr-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!-- 打包方式为maven-plugin -->
    <packaging>maven-plugin</packaging>

    <name>jr-plugin</name>

    <dependencies>

        <!-- 依赖官方plugin的api -->
        <dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>maven-plugin-api</artifactId>
            <version>3.0</version>
        </dependency>

        <!-- 依赖官方plugin的注解，可以通过注解方式开发 -->
        <dependency>
            <groupId>org.apache.maven.plugin-tools</groupId>
            <artifactId>maven-plugin-annotations</artifactId>
            <version>3.4</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 创建一个简易的mojo

在maven项目中创建包`cn.jr.plugin`且在此包下创建以下类:

```
package cn.jr.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;


@Mojo(name = "sayhi")
public class GreetingMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Hello, world.");
    }
}
```

maven-plugin的生命周期

|  生命周期    | 释义  |
|  ----       | ----  |
| compile     | 编译插件的Java代码 |
| process-classes  | 处理类 |
| test  | 运行插件的单元测试 |
| package | 构建插件jar |
| install  | 在本地存储库中安装插件jar |
| deploy  | 将插件jar部署到远程存储库 |

### 执行mojo
在其他的maven项目中添加以下构建依赖:

```
<!-- 构建打包相关的配置 -->
<build>
    <!-- 插件集 -->
    <plugins>
           <!-- 具体的插件依赖和maven普通依赖一样，以gav标识 -->
           <plugin>
                    <groupId>jr.cn.plugin</groupId>
                    <artifactId>jr-maven-plugin</artifactId>
                    <version>1.0-SNAPSHOT</version>
            </plugin>
    </plugins>
</build>
```

* 类`org.apache.maven.plugin.AbstractMojo`提供了实现`mojo`所需的大部分基础设施，但`execute`方法除外。
* `@Mojo`是必需的，它控制如何以及何时执行`mojo`。 
* `execute`方法可以抛出两个异常:
    * org.apache.maven.plugin。MojoExecutionException，如果出现意外问题。抛出此异常会导致显示“生成错误”消息。
    *  org.apache.maven.plugin。MojoFailureException，如果出现预期的问题(比如编译失败)。抛出此异常会导致显示“构建失败”消息。
* `getLog`方法(定义在`AbstractMojo`中)返回一个类似于`log4j`的日志对象，它允许插件创建`调试`、`信息`、`警告`和`错误`级别的消息
。这个日志记录器会在使用此插件的时候打印信息到控制台。


执行的话就很简单了:
```
mvn groupId:artifactId:version:goal
```

`gav`全部替换成插件的`gav`，`goal`指的就是`mojo`的`name`，替换以后为:
```
mvn  jr.cn.plugin:jr-maven-plugin:1.0-SNAPSHOT:sayhi
```

在命令行执行，执行完毕后就能够看到代码中的`Hello, world.`打印了。
需要注意的是一个plugin项目可不仅仅只有一个mojo，是允许创建多个的

----

tips:

如果需要运行安装在本地存储库中的插件的最新版本，可以省略其版本号（mvn  jr.cn.plugin:jr-maven-plugin:sayhi）。

你可以给你的插件分配一个简短的前缀，比如`mvn jr:sayhi`。如果遵循使用`${prefix}-maven-plugin`(或者`maven-${prefix}-plugin`(如果插
件是Apache maven项目的一部分)的约定，这将自动完成。你也可以通过附加的配置来分配—更多信息请参见插件前缀[映射](http://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html)介绍。

最后，还可以将插件的groupId添加到默认搜索的groupId列表中。为此，需要将以下内容添加到${user.home}/.m2/settings.xml文件中,
当然如果是idea，settings.xml位置修改了就改那一个就行。

搞完以上的配置之后，你可以运行mojo `mvn jr:sayhi`。

----

您还可以配置插件，将特定的目标附加到构建生命周期的特定阶段。下面是一个例子:
```
<build>
    <plugins>
      <plugin>
        <groupId>jr.cn.plugin</groupId>
        <artifactId>jr-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <phase>compile</phase>
            <goals>
              <goal>sayhi</goal>
            </goals>
          </execution>
    </executions>
  </plugin>
</plugins>
</build>
```

这将导致在Java代码编译时执行简单的mojo。有关将mojo绑定到生命周期阶段的更多信息，请参考构建[生命周期文档](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)。
也就是说在您执行`mvn compile`的时候，会自动执行您的插件，因为当前插件与maven的生命周期绑定了。

### mojo参数配置

如果没有参数，mojo不太可能非常有用。参数提供了一些非常重要的功能:

* 它提供了钩子来允许用户调整插件的操作以适应他们的需要。
* 它提供了一种方法，可以轻松地从POM提取元素的值，而不需要导航对象。

---
以下参数是配置在`GreetingMojo`类的。
`property`是属性名，`defaultValue`是默认值


#### String

java代码中这么添加
```
@Parameter( property = "sayhi.greeting", defaultValue = "Hello World!" )
private String greeting;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
 <configuration>
    <greeting>Welcome</greeting>
  </configuration>
```
在configuration部分中，元素名`greeting`是参数名，元素的内容`Welcome`是要分配给参数的值。

这包括类型为char、Character、StringBuffer和String的变量。当读取配置时，XML文件中的文本被用作分配给参数的值。对于char和字符参数，只使用文本的第一个字符。

double、boolean、integer等的配置都是和string一样的。

以下举例几个特殊的:

#### Dates
这包括类型为Date的变量。读取配置时，XML文件中的文本使用以下日期格式之一转换:"yyyy-MM-dd HH:mm:ss。a”
(样本日期为“2005- 06 2:22:55.1 PM”)或“yyyy-MM-dd HH:mm:ssa”(样本日期为“2005- 06 2:22:55PM”)。注意，解析是使用
DateFormat.parse()完成的，它允许格式化方面的一些宽大处理。如果方法可以解析指定的日期和时间，即使它不完全匹配上面的模式，它也会这样做。例子:

java代码中这么添加
```
/**
 * My Date.
 */
@Parameter
private Date myDate;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
 <configuration>
        <myDate>2005-10-06 2:22:55.1 PM</myDate>
  </configuration>
```


#### Files and Directories
这包括变量类型文件。当读取配置时，XML文件中的文本被用作所需文件或目录的路径。如果路径是相对的(不以/或像C:这样的驱动器号开头)，
则该路径是相对于包含POM的目录的。例子:

java代码中这么添加
```
/**
 * My File.
 */
@Parameter
private File myFile;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
   <myFile>c:\temp</myFile>
</configuration>
```

#### URLs
这包括变量类型URL。当读取配置时，XML文件中的文本被用作URL。格式必须遵循RFC 2396准则，并且看起来像任何web浏览器的URL (scheme://host:port/path/to/file)。在转换URL时，对URL的任何部分的内容没有任何限制。

java代码中这么添加
```
/**
 * My URL.
 */
@Parameter
private URL myURL;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
   <myURL>http://maven.apache.org</myURL>
</configuration>
```

#### Enums
还可以使用枚举类型参数。首先你需要定义你的枚举类型，然后你可以在参数定义中使用枚举类型:

java代码中这么添加
```
public enum Color {
  GREEN,
  RED,
  BLUE
}

/**
 * My Enum
 */
@Parameter(defaultValue = "GREEN")
private Color myColor;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
  <myColor>GREEN</myColor>
</configuration>
```


#### Arrays
数组类型参数通过多次指定参数来配置。例子:

java代码中这么添加
```
/**
 * My Array.
 */
@Parameter
private String[] myArray;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
    <myArray>
      <param>value1</param>
      <param>value2</param>
    </myArray>
</configuration>
```


#### Collections
这个类别涵盖了实现java.util的任何类。集合，例如ArrayList或HashSet。这些参数是通过多次指定参数来配置的，就像数组一样。例子::

java代码中这么添加
```
/**
 * My List.
 */
@Parameter
private List myList;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
   <myList>
     <param>value1</param>
     <param>value2</param>
   </myList>
</configuration>
```

#### Maps
这个类别涵盖了实现java.util.Map的任何子类。地图上往下
java代码中这么添加
```
/**
 * My Map.
 */
@Parameter
private Map myMap;
```

依赖插件的pom中这么配置
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
  <myMap>
    <key1>value1</key1>
    <key2>value2</key2>
  </myMap>
</configuration>
```

#### Properties
这个类别涵盖了实现java.util.Properties的所有映射。通过在参数配置中以myName myValue 的形式包含XML标记来配置这些参数。例子:
java代码中这么添加
```
/**
* My Map.
@    /**
* My Properties.
*/
@Parameter
private Properties myProperties;
```

依赖插件的pom中这么配置:
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
<myProperties>
  <property>
    <name>propertyName1</name>
    <value>propertyValue1</value>
  <property>
  <property>
    <name>propertyName2</name>
    <value>propertyValue2</value>
  <property>
</myProperties>
</configuration>
```


#### Other Object Classes
这个类别涵盖了实现java.util.Properties的所有映射。通过在参数配置中以myName myValue 的形式包含XML标记来配置这些参数。例子:
java代码中这么添加
```
/**
* My Object.
*/
@Parameter
private MyObject myObject;
```

依赖插件的pom中这么配置:
```
<groupId>jr.cn.plugin</groupId>
<artifactId>jr-maven-plugin</artifactId>
<version>1.0-SNAPSHOT</version>
<configuration>
<myObject>
  <myField>test</myField>
</myObject>
</configuration>
```

```
public class MyQueryMojo
    extends AbstractMojo
{
    @Parameter(property="url")
    private String _url;
 
    @Parameter(property="timeout")
    private int _timeout;
 
    @Parameter(property="options")
    private String[] _options;
 
    public void setUrl( String url )
    {
        _url = url;
    }
 
    public void setTimeout( int timeout )
    {
        _timeout = timeout;
    }
 
    public void setOptions( String[] options )
    {
        _options = options;
    }
 
    public void execute()
        throws MojoExecutionException
    {
        ...
    }
}
```

----
至此，maven plugin的初步构建讲解就算完成了。


