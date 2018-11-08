package utils;

import java.io.*;

public class FileUtil {
    private static final int BUFF_SIZE = 1024;

    /**
     * 写文件
     *
     * @param content
     * @param fileName
     * @throws IOException
     */
    public static void writeFile(String content, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists())
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(fileName, false);
        // TODO 为压缩空间可以使用压缩算法，为保证数据安全，可以使用加密算法
        writer.write(content);
        writer.close();
    }

    /**
     * 读文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) throws IOException {
        FileReader reader = new FileReader(fileName);
        char[] buff = new char[BUFF_SIZE];
        StringBuilder builder = new StringBuilder();
        int len;
        while ((len = reader.read(buff)) > 0) {
            if (len < BUFF_SIZE) {
                builder.append(buff, 0, len);
            } else {
                builder.append(buff);
            }
        }
        reader.close();
        return builder.toString();
    }

    /**
     * 读取文件指定行
     *
     * @param fileName
     * @param lineNum
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName, int lineNum) throws IOException {
        if (new File(fileName).exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String ret = null;
            int i = 1;
            while (null != (ret = reader.readLine())) {
                if (i == lineNum) {
                    return ret;
                }
                i++;
            }
            reader.close();
            return ret;
        }
        return null;
    }


}
