package cn.jr.plugin.other;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StringUtils {

    final static String DOCKER_FROM = "${from}";
    final static String JAR_NAME = "${jarName}";
    final static String PORT = " ${port}";
    final static String ENTRYPOINT = "${entrypoint}";
    final static String PROFILE_ACTIVE = "${profiles.active}";
    final static String ARTIFACTID = "${artifactId}";
    final static String REVISION = "${revision}";
    final static String CDATA_START = "<![CDATA[";
    final static String CDATA_END = "]]>";

    public static String replace(String str, Properties properties) {

        String profile = properties.getProperty("profile");
        String jarName = properties.getProperty("jarName");
        String artifactId = properties.getProperty("artifactId");
        String revision = properties.getProperty("revision");
        String from = properties.getProperty("from");
        String port = properties.getProperty("port");
        String entrypoint = properties.getProperty("entrypoint");

        Application.getLog().debug("profile:" + profile);
        Application.getLog().debug("jarName:" + jarName);
        Application.getLog().debug("artifactId:" + artifactId);
        Application.getLog().debug("revision:" + revision);
        Application.getLog().debug("from:" + from);
        Application.getLog().debug("port:" + port);
        Application.getLog().debug("entrypoint:" + entrypoint);

        str = replace(str, JAR_NAME, jarName);
        str = replace(str, PROFILE_ACTIVE, profile);
        str = replace(str, ARTIFACTID, artifactId);
        str = replace(str, REVISION, revision);
        str = replace(str, DOCKER_FROM, from);
        str = replace(str, PORT, port);
        str = replace(str, ENTRYPOINT, entrypoint);
        str = replace(str, CDATA_START, "");
        str = replace(str, CDATA_END, "");

        return str;
    }

    public static String replace(String sh, String regex, String replace) {
        if (replace!=null && !replace.isEmpty()) {
            sh = sh.replace(regex, replace.trim());
        }
        return sh;
    }


    public static String rmSpace(String content) {
        StringBuilder builder = new StringBuilder();
        String[] split = content.split("\n");
        for (String s : split) {
            int length = s.length();
            boolean alwaysSpace = true;
            List<Character> characterList = new ArrayList<Character>();
            for (int i = 0; i < length; i++) {
                char c = s.charAt(i);
                boolean whitespace = Character.isWhitespace(c);
                if (!(whitespace && alwaysSpace)) {
                    characterList.add(c);
                    alwaysSpace = false;
                }
            }

            for (Character character : characterList) {
                builder.append(character);
            }

            builder.append("\n");
        }
        return builder.toString();
    }
}
