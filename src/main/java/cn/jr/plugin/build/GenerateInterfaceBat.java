package cn.jr.plugin.build;

import cn.jr.plugin.other.Application;
import cn.jr.plugin.other.FileUtil;
import cn.jr.plugin.other.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;


/**
 * 创建bat脚本
 */
public class GenerateInterfaceBat implements GenerateInterface {

    @Override
    public boolean generate(File rootPath, Properties properties) throws IOException, ParserConfigurationException, SAXException {

        String path = rootPath.getAbsolutePath().endsWith(File.separator)
                ? rootPath.getAbsolutePath() : rootPath.getAbsolutePath() + File.separator;
        String dockerFilePath = path + "start.bat";
        String bat = FileUtil.readFileByString("start.bat");
        bat = StringUtils.replace(bat, properties);

        Application.getLog().info("当前bat执行完毕后的脚本为:" + bat);
        //直接从content中获取脚本
        FileUtil.createFile(dockerFilePath, bat);
        return true;
    }

}
