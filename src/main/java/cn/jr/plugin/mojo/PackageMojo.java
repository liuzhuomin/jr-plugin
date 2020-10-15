package cn.jr.plugin.mojo;

import cn.jr.plugin.build.GenerateAllFactory;
import cn.jr.plugin.build.GenerateInterface;
import cn.jr.plugin.build.GenerateInterfaceDocker;
import cn.jr.plugin.build.GenerateInterfaceShell;
import cn.jr.plugin.other.Application;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Package an application into a OCI image using a buildpack.
 *
 * @author Phillip Webb
 * @author Scott Frederick
 * @since 2.3.0
 */
@Mojo(name = "package",
        executionStrategy = "always",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM,
        requiresDependencyCollection = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class PackageMojo extends AbstractCopyFileMojo {

    /**
     * "ALL"都生成, "SHELL"生成SHELL, "DOCKER"生成docker的
     * 'DOCKER'对应{@link GenerateInterfaceDocker}</p>
     * 'SHELL'对应{@link GenerateInterfaceShell}</p>
     * 'ALL'对应所有
     */
    @Parameter
    private Map<String, Properties> generate;
    /**
     * jar包名称,带后缀
     */
    @Parameter
    private String jarName;

    @Parameter(defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Application.setLog(getLog());
        String packaging = project.getPackaging();
        if (!"jar".equals(packaging) || skip) {
            Application.getLog().info("not jar so skip...");
            return;
        }

        //拷贝文件操作
        try {
            super.copyFile();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }

        Log log = Application.getLog();
        log.info("开始生成脚本文件...");
        if (jarName == null || jarName.isEmpty()) {
            log.info("jarName is empty... set jarName is app.jar");
            jarName = "app.jar";
        }

        if (generate != null) {
            Set<Map.Entry<String, Properties>> entries = generate.entrySet();
            for (Map.Entry<String, Properties> entry : entries) {
                List<GenerateInterface> generate = GenerateAllFactory.FILE_FACTORY.generate(entry.getKey());
                for (GenerateInterface generateInterface : generate) {
                    boolean generateResult = false;
                    try {
                        Properties value = entry.getValue();
                        value.put("jarName", jarName);
                        if (StringUtils.isNotBlank(profile)) {
                            value.put("profile", profile);
                        }
                        if (StringUtils.isNotBlank(artifactId)) {
                            value.put("artifactId", artifactId);
                        }
                        if (StringUtils.isNotBlank(revision)) {
                            value.put("revision", revision);
                        }
                        generateResult = generateInterface.generate(targetDir, value);
                    } catch (IOException | SAXException | ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                    log.info("generateResult: " + generateResult);
                }
            }
        }
    }
}
