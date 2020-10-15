package cn.jr.plugin.mojo;

import cn.jr.plugin.other.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;

import java.io.File;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM,
        requiresDependencyCollection = ResolutionScope.RUNTIME_PLUS_SYSTEM)
@Execute(phase = LifecyclePhase.CLEAN)
public class CleanMojo extends AbstractMojo {
    /**
     * 基础目录
     */
    @Parameter(defaultValue = "${basedir}")
    private File basedir;
    /**
     * 目标目录，默认是当前构建文件夹下
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File targetDir;

    @Override
    public void execute() throws MojoExecutionException {
        Log log = getLog();

        log.info("基础目录" + basedir.getAbsolutePath());
        log.info("目标目录" + targetDir.getAbsolutePath());

        if (targetDir.exists()) {
            log.info("目标文件夹存在，开始删除......");
            FileUtil.removeAllFilesAndLogs(targetDir,log);
        }
    }


}
