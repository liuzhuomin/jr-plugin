package cn.jr.plugin.build;

import cn.jr.plugin.other.Application;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class GenerateAllFactory {
    public static final GenerateAllFactory FILE_FACTORY = new GenerateAllFactory();
    private GenerateAllFactory() {
    }

    public List<GenerateInterface> generate(String type) {
        Type[] values = Type.values();
        Type currentType = Type.valueOf(type);
        boolean contains = ArrayUtils.contains(values, currentType);
        if (!contains) {
            Application.getLog().info("not contains any type label by " + values);
            return Arrays.stream(values).map(Type::getClazz).collect(Collectors.toList());
        } else {
            List<GenerateInterface> objects = new ArrayList<>();
            objects.add(currentType.getClazz());
            return objects;
        }
    }
}
