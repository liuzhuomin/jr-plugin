package cn.jr.plugin.other;

import org.apache.maven.plugin.logging.Log;

import java.io.*;

public final class FileUtil {

    /**
     * 创建且覆盖文件
     *
     * @param path    绝对路径
     * @param content 文本内容
     */
    public static void createFile(String path, String content) throws IOException {
        File runBatFile = new File(path);
        if (runBatFile.exists()) {
            boolean delete = runBatFile.delete();
        }
        content = content.replace("\t", "");
        FileWriter runBatFileW = new FileWriter(runBatFile);
        runBatFileW.write(content);
        runBatFileW.flush();
        runBatFileW.close();
    }

    /**
     * 拷贝文件到另外一个目录
     *
     * @param sourceFile 源文件完整路径(包括文件名)
     * @param targetPath 目标文件目录(不包括文件名)
     */
    public static String copy(String sourceFile, String targetPath) {
        File file = new File(sourceFile);
        createDir(new File(targetPath));
        try {
            String path = targetPath.endsWith(File.separator)
                    ? targetPath : targetPath + File.separator;
            String targetAbsPath = path + file.getName();

            FileInputStream fis = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(targetAbsPath);
            byte[] datas = new byte[1024 * 8];
            int len;//创建长度
            while ((len = fis.read(datas)) != -1) {
                out.write(datas, 0, len);
            }
            fis.close();//释放资源
            out.flush();
            out.close();//释放资源
            return targetAbsPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void createDir(File tarFile) {
        if (!tarFile.exists()) {
            tarFile.mkdirs();
        }
    }

    /**
     * 删除targetDir所代表目录下的所有文件
     *
     * @param targetDir 必须是个目录
     */
    public static void removeAllFiles(File targetDir) {
        removeAllFilesAndLogs(targetDir, null);
    }

    /**
     * 删除targetDir所代表目录下的所有文件
     *
     * @param targetDir 必须是个目录
     */
    public static void removeAllFilesAndLogs(File targetDir, Log log) {
        if (!targetDir.isDirectory()) {
            throw new IllegalArgumentException("targetDir is not a directory!");
        }
        File[] files = targetDir.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean directory = file.isDirectory();
                if (directory) {
                    removeAllFiles(file);
                    boolean delete = file.delete();
                    if (log != null) {
                        log.info("目录下文件数量为0，目录" + file.getAbsolutePath() + "删除...，结果:" + delete);
                    }
                } else {
                    boolean delete = file.delete();
                    if (log != null) {
                        log.info("删除文件:" + file.getAbsolutePath() + "，结果:" + delete);
                    }
                }
            }
        }
        boolean delete = targetDir.delete();
        if (log != null) {
            log.info("目录下文件数量为0，目录" + targetDir.getAbsolutePath() + "删除...，结果:" + delete);
        }
    }


//    /**
//     * 读取文件为字符串
//     *
//     * @param fileName 文件绝对路径
//     * @return 字符串内容
//     */
//    public static String readFileByString(String fileName) throws FileNotFoundException {
//        FileReader dockerFileReader = new FileReader(new File(fileName));
//        char[] charArray = new char[1024];
//        int offset = 0;
//        int len = 0;
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            while (dockerFileReader.read(charArray, offset, len) != -1) {
//                String s = new String(charArray);
//                stringBuilder.append(s);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return stringBuilder.toString();
//    }


    /**
     * 读取文件为字符串
     *
     * @param fileName 文件绝对路径
     * @return 字符串内容
     */
    public static String readFileByString(String fileName) throws IOException {
        fileName = "/" + fileName;
        InputStream is = FileUtil.class.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            sb.append(s).append("\r\n");
        }
        br.close();
        return sb.toString();
    }

    public static void update(File file, String regex, String profile) {
        try {
            String s = FileUtil.readFileByString(file);
            if(s.contains(regex)){
                String replaceStr = s.replace(regex, profile);
                BufferedWriter bufferedWriter =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                bufferedWriter.write(replaceStr);
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileByString(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            sb.append(s).append("\r\n");
        }
        br.close();
        return sb.toString();
    }

//    public static void main(String[] args) {
//        String source="D:\\work\\new-cms\\cms-projects\\cms-web\\src\\main\\resources\\files\\账号白名单导入模板.xlsx";
//        String target="D:\\work\\new-cms\\cms-projects\\dist\\cms-web-0.1.0914-SNAPSHOT\\files";
//        copy(source,target);
//    }


}
