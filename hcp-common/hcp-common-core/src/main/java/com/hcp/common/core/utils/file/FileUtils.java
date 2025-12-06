package com.hcp.common.core.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import com.hcp.common.core.utils.StringUtils;

/**
 * 文件处理工具类
 *
 * @author vctgo
 */
public class FileUtils
{
    /** 字符常量：斜杠 {@code '/'} */
    public static final char SLASH = '/';

    /** 字符常量：反斜杠 {@code '\\'} */
    public static final char BACKSLASH = '\\';

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 将指定文件的内容写入到输出流中。
     *
     * @param filePath 文件的路径。
     * @param os 输出流。
     * @throws FileNotFoundException 如果文件不存在。
     * @throws IOException 如果读取文件或写入流时发生错误。
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException
    {
        FileInputStream fis = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0)
            {
                os.write(b, 0, length);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除指定路径的文件。
     *
     * @param filePath 文件的路径。
     * @return 如果文件成功删除，则返回 true；否则返回 false。
     */
    public static boolean deleteFile(String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists())
        {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 验证文件名称是否合法。
     *
     * @param filename 文件名称。
     * @return 如果文件名称符合规则，则返回 true；否则返回 false。
     */
    public static boolean isValidFilename(String filename)
    {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否允许下载。
     *
     * @param resource 需要下载的文件名或路径。
     * @return 如果允许下载，则返回 true；否则返回 false。
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 判断是否在允许下载的文件规则内
        return ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource));
    }

    /**
     * 对下载的文件名进行重新编码，以解决不同浏览器下的乱码问题。
     *
     * @param request HTTP 请求对象，用于获取 User-Agent。
     * @param fileName 原始文件名。
     * @return 编码后的文件名。
     * @throws UnsupportedEncodingException 如果编码不支持。
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
    {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE"))
        {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox"))
        {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome"))
        {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else
        {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 从文件路径中获取文件名。
     *
     * @param filePath 文件路径。
     * @return 文件名。
     */
    public static String getName(String filePath)
    {
        if (null == filePath)
        {
            return null;
        }
        int len = filePath.length();
        if (0 == len)
        {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1)))
        {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--)
        {
            c = filePath.charAt(i);
            if (isFileSeparator(c))
            {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 判断字符是否为 Windows 或 Linux (Unix) 的文件分隔符。
     * Windows 平台下分隔符为 '\'，Linux (Unix) 为 '/'。
     *
     * @param c 需要判断的字符。
     * @return 如果是文件分隔符，则返回 true；否则返回 false。
     */
    public static boolean isFileSeparator(char c)
    {
        return SLASH == c || BACKSLASH == c;
    }

    /**
     * 设置下载文件的响应头，对文件名进行编码处理。
     *
     * @param response HTTP 响应对象。
     * @param realFileName 真实文件名。
     * @throws UnsupportedEncodingException 如果编码不支持。
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
    {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 对字符串进行百分号编码。
     *
     * @param s 需要编码的字符串。
     * @return 编码后的字符串。
     * @throws UnsupportedEncodingException 如果编码不支持。
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }
}
