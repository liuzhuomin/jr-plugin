package cn.jr.plugin.build;

import cn.jr.plugin.other.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * shell构建
 * <p>
 * 以下是一些配置属性,如果填写了这四个标签,可以默认生成dockerfile
 * <start>基础镜像</start>
 * <stop>jar包名称</stop>
 *
 * @author liuliuliu
 * @since 2020/9/14
 */
public class GenerateInterfaceShell implements GenerateInterface {
    final String JAR_NAME = "\\$\\{jarName}";

    @Override
    public boolean generate(File rootPath, Properties properties) throws IOException {
        String start = properties.getProperty("start");
        String stop = properties.getProperty("stop");
        boolean startEmtpy = start == null || start.isEmpty();
        boolean stopEmpty = stop == null || stop.isEmpty();
        if (startEmtpy) {
            start = FileUtil.readFileByString("start.sh");
        }
        if (stopEmpty) {
            stop = FileUtil.readFileByString("stop.sh");
        }

        String jarName = properties.getProperty("jarName");
        start = start.replaceAll(JAR_NAME, jarName);
        stop = stop.replaceAll(JAR_NAME, jarName);

        String path = rootPath.getAbsolutePath().endsWith(File.separator)
                ? rootPath.getAbsolutePath() : rootPath.getAbsolutePath() + File.separator;

        String startPath = path + "start.sh";
        String stopPath = path + "stop.sh";
        //直接从content中获取脚本
        FileUtil.createFile(startPath, start);
        FileUtil.createFile(stopPath, stop);
        return true;
    }

}