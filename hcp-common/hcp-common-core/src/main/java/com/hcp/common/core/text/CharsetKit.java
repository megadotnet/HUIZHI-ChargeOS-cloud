package com.hcp.common.core.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.hcp.common.core.utils.StringUtils;

/**
 * 字符集工具类
 * 
 * @author vctgo
 */
public class CharsetKit
{
    /** ISO-8859-1 */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /** UTF-8 */
    public static final String UTF_8 = "UTF-8";
    /** GBK */
    public static final String GBK = "GBK";

    /** ISO-8859-1 */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
    /** UTF-8 */
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8);
    /** GBK */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * 将字符集名称转换为 Charset 对象。
     * 
     * @param charset 字符集名称。如果为空，则返回默认字符集。
     * @return Charset 对象。
     */
    public static Charset charset(String charset)
    {
        return StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * 转换字符串的字符集编码。
     * 
     * @param source 需要转换的字符串。
     * @param srcCharset 源字符集名称，默认 ISO-8859-1。
     * @param destCharset 目标字符集名称，默认 UTF-8。
     * @return 转换后的字符串。
     */
    public static String convert(String source, String srcCharset, String destCharset)
    {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 转换字符串的字符集编码。
     * 
     * @param source 需要转换的字符串。
     * @param srcCharset 源字符集对象，默认 ISO-8859-1。
     * @param destCharset 目标字符集对象，默认 UTF-8。
     * @return 转换后的字符串。
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset)
    {
        if (null == srcCharset)
        {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        if (null == destCharset)
        {
            destCharset = StandardCharsets.UTF_8;
        }

        if (StringUtils.isEmpty(source) || srcCharset.equals(destCharset))
        {
            return source;
        }
        return new String(source.getBytes(srcCharset), destCharset);
    }

    /**
     * 获取系统默认字符集编码。
     *
     * @return 系统默认字符集编码名称 (String)。
     */
    public static String systemCharset()
    {
        return Charset.defaultCharset().name();
    }
}
