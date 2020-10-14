package cn.jr.plugin.build;

import cn.jr.plugin.other.Application;
import cn.jr.plugin.other.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * docker构建
 * <p>
 * 以下是一些配置属性,如果填写了这四个标签,可以默认生成dockerfile
 * <from>基础镜像</from>
 * <jarName>jar包名称</jarName>
 * <port>端口</port>
 * <entrypoint>运行时参数</entrypoint>
 * </p>
 *
 * <dockerFile>完整的docker脚本,如果不为空,则取这个</dockerFile>
 * <sh>sh启动脚本,如果为空,有默认的</sh>
 *
 * @author liuliuliu
 * @since 2020/9/14
 */
public class GenerateInterfaceDocker implements GenerateInterface {
    final String DOCKER_FILE_NAME = "Dockerfile";
    final String DOCKER_SHELL = "docker.sh";

    @Override
    public boolean generate(File rootPath, Properties properties) throws IOException, ParserConfigurationException, SAXException {

        String content = properties.getProperty("dockerFile");
        String sh = properties.getProperty("sh");
        String jarName = properties.getProperty("jarName");
        String from = properties.getProperty("from");
        String port = properties.getProperty("port");
        String entrypoint = properties.getProperty("entrypoint");

        String path = rootPath.getAbsolutePath().endsWith(File.separator)
                ? rootPath.getAbsolutePath() : rootPath.getAbsolutePath() + File.separator;

        String dockerFilePath = path + DOCKER_FILE_NAME;
        String dockerShellPath = path + DOCKER_SHELL;

        Log log = Application.getLog();
        log.info("rootPath = " + rootPath + ", properties = " + properties);
        log.info("content = " + content + ", sh = " + sh);

        boolean shEmpty = sh == null || sh.isEmpty();
        if (StringUtils.isNotBlank(content)) {

            if (shEmpty) {
                throw new IllegalArgumentException("sh must not be empty!");
            }

            sh = cn.jr.plugin.other.StringUtils.replace(sh, properties);
            sh = cn.jr.plugin.other.StringUtils.rmSpace(sh);

            content = cn.jr.plugin.other.StringUtils.replace(content, properties);
            content = cn.jr.plugin.other.StringUtils.rmSpace(content);

            //直接从content中获取脚本
            FileUtil.createFile(dockerFilePath, content);
            FileUtil.createFile(dockerShellPath, sh);

        } else {

            if (StringUtils.isBlank(from)) {
                throw new IllegalArgumentException("from must not be empty");
            }
            if (StringUtils.isBlank(jarName)) {
                throw new IllegalArgumentException("jarName must not be empty");
            }
            if (StringUtils.isBlank(port)) {
                throw new IllegalArgumentException("port must not be empty");
            }
            if (StringUtils.isBlank(entrypoint)) {
                throw new IllegalArgumentException("entrypoint must not be empty");
            }

            content = FileUtil.readFileByString(DOCKER_FILE_NAME);
            content = cn.jr.plugin.other.StringUtils.replace(content, properties);
            content = cn.jr.plugin.other.StringUtils.rmSpace(content);

            if(shEmpty){
                sh = FileUtil.readFileByString(DOCKER_SHELL);
            }
            sh = cn.jr.plugin.other.StringUtils.replace(sh, properties);
            sh = cn.jr.plugin.other.StringUtils.rmSpace(sh);

            //直接从content中获取脚本
            FileUtil.createFile(dockerShellPath, sh);
            FileUtil.createFile(dockerFilePath, content);
        }
        return true;
    }



}
