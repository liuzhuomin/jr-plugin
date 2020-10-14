package cn.jr.plugin.mojo;

import cn.jr.plugin.other.Application;
import cn.jr.plugin.other.FileCopySpecial;
import cn.jr.plugin.other.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractCopyFileMojo extends AbstractMojo {
    /**
     * 基础目录
     */
    @Parameter(defaultValue = "${basedir}")
    protected File basedir;
    /**
     * 需要拷贝的文件集合，会直接拷贝到targetDir
     */
    @Parameter
    protected List<File> copyFiles;

    /**
     * 拷贝文件的单独配置
     */
    @Parameter
    protected List<FileCopySpecial> copySpecials;

    /**
     * 目标目录，默认是当前构建文件夹下
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    protected File targetDir;

    /**
     * Practical reference to the Maven project
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${profiles.active}")
    protected String profile;
    @Parameter(defaultValue = "${project.artifactId}")
    protected String artifactId;
    @Parameter(defaultValue = "${revision}")
    protected String revision;

    protected void copyFile() throws IOException {

        Log log = getLog();
        Application.setLog(log);

        String packaging = project.getPackaging();
        if (!"jar".equals(packaging)) {
            Application.getLog().info("not jar so skip...");
        }

        basedir = new File(basedir.getCanonicalPath());
        targetDir = new File(targetDir.getCanonicalPath());

        log.info("=============绝对路径============");

        log.info("当前基础的目录" + basedir.getAbsolutePath());
        log.info("需要拷贝到目录" + targetDir.getAbsolutePath());

        log.info("=============相对路径============");

        log.info("当前基础的目录" + basedir.getCanonicalPath());
        log.info("需要拷贝到目录" + targetDir.getCanonicalPath());

        if (!targetDir.exists() && targetDir.isDirectory()) {
            log.info("目标文件夹不存在，开始创建......");
            boolean mkdir = targetDir.mkdirs();
            log.info("mkdir" + mkdir);
            if (!mkdir) {
                throw new RuntimeException("创建文件夹失败！");
            }
        }

        try {
            if (copyFiles != null && !copyFiles.isEmpty() && targetDir != null) {
                log.info("通过<copyFiles>标签拷贝文件...");
                log.info("copyFiles" + copyFiles);
                for (File copyFile : copyFiles) {
                    log.info("copyFile.getAbsolutePath()..." + copyFile.getAbsolutePath());
                    log.info("targetDir.getAbsolutePath()..." + targetDir.getAbsolutePath());
                    String copy = FileUtil.copy(copyFile.getAbsolutePath(), targetDir.getAbsolutePath());
                    replaceStr(copy);
                }
                log.info("总共拷贝了" + copyFiles.size() + "个文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("拷贝文件出错!");
        }


        log.info("通过copySpecials拷贝文件");
        log.info("copySpecials" + copySpecials);
        if (copySpecials != null) {
            copySpecials.forEach(v -> {
                String sourcePath = null;
                String targetPath = null;
                try {
                    sourcePath = v.getSource().getCanonicalPath();
                    targetPath = v.getTarget().getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String copy = FileUtil.copy(sourcePath, targetPath);
                replaceStr(copy);
                log.info("sourcePath: " + sourcePath);
                log.info("targetPath: " + targetPath);
            });
        }
    }

    private void replaceStr(String copy) {
        if (StringUtils.isNotBlank(profile)) {
            FileUtil.update(new File(copy), "@profiles.active@", profile);
            FileUtil.update(new File(copy), "${profiles.active}", profile);
        }
        if (StringUtils.isNotBlank(artifactId)) {
            FileUtil.update(new File(copy), "${project.artifactId}", artifactId);
        }
        if (StringUtils.isNotBlank(revision)) {
            FileUtil.update(new File(copy), "${revision}", revision);
        }
    }

}
