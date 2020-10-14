package cn.jr.plugin.build;

/**
 * 类型转换
 *
 * @author liuliuliu
 * @since 2020/09/25
 */
public enum Type {
//    All(GenerateInterfaceShell.class, GenerateInterfaceDocker.class, GenerateInterfaceBat.class),
    /**
     * shell类型脚本
     */
    SHELL(new GenerateInterfaceShell()),
    /**
     * docker相关脚本
     */
    DOCKER(new GenerateInterfaceDocker()),
    /**
     * bat相关脚本
     */
    BAT(new GenerateInterfaceBat());
    GenerateInterface clazz;

    Type(GenerateInterface clazz) {
        this.clazz = clazz;
    }

    public GenerateInterface getClazz() {
        return clazz;
    }
}
