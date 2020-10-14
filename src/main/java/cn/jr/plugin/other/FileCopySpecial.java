package cn.jr.plugin.other;

import java.io.File;

public class FileCopySpecial {
    private File source;
    private File target;

    public FileCopySpecial() {
    }

    public FileCopySpecial(File source, File target) {
        this.source = source;
        this.target = target;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public File getTarget() {
        return target;
    }

    public void setTarget(File target) {
        this.target = target;
    }

    public void set(File source){
        System.out.println("sourceFile"+source);
    }
    @Override
    public String toString() {
        return "CopyFile{" +
                "source=" + source +
                ", target=" + target +
                '}';
    }
}
