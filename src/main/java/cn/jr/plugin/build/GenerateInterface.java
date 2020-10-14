package cn.jr.plugin.build;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public interface GenerateInterface {
    /**
     * 根据配置项生成配置文件
     *
     * @param properties 配置项
     * @return true生成成功/false生成失败
     */
    boolean generate(File rootPath, Properties properties) throws IOException, ParserConfigurationException, SAXException;

}
