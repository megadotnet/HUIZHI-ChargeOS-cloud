package com.hcp.common.core.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;
import com.hcp.common.core.utils.StringUtils;

/**
 * 类型转换器
 *
 * @author vctgo
 */
public class Convert
{
    /**
     * 将对象转换为字符串。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的字符串 (String)，或默认值。
     */
    public static String toStr(Object value, String defaultValue)
    {
        if (null == value)
        {
            return defaultValue;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * 将对象转换为字符串。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的字符串 (String)，或 null。
     */
    public static String toStr(Object value)
    {
        return toStr(value, null);
    }

    /**
     * 将对象转换为字符。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的字符 (Character)，或默认值。
     */
    public static Character toChar(Object value, Character defaultValue)
    {
        if (null == value)
        {
            return defaultValue;
        }
        if (value instanceof Character)
        {
            return (Character) value;
        }

        final String valueStr = toStr(value, null);
        return StringUtils.isEmpty(valueStr) ? defaultValue : valueStr.charAt(0);
    }

    /**
     * 将对象转换为字符。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的字符 (Character)，或 null。
     */
    public static Character toChar(Object value)
    {
        return toChar(value, null);
    }

    /**
     * 将对象转换为 Byte。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Byte 值，或默认值。
     */
    public static Byte toByte(Object value, Byte defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Byte)
        {
            return (Byte) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).byteValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Byte.parseByte(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Byte。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Byte 值，或 null。
     */
    public static Byte toByte(Object value)
    {
        return toByte(value, null);
    }

    /**
     * 将对象转换为 Short。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Short 值，或默认值。
     */
    public static Short toShort(Object value, Short defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Short)
        {
            return (Short) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).shortValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Short.parseShort(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Short。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Short 值，或 null。
     */
    public static Short toShort(Object value)
    {
        return toShort(value, null);
    }

    /**
     * 将对象转换为 Number。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Number 值，或默认值。
     */
    public static Number toNumber(Object value, Number defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Number)
        {
            return (Number) value;
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return NumberFormat.getInstance().parse(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Number。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Number 值，或 null。
     */
    public static Number toNumber(Object value)
    {
        return toNumber(value, null);
    }

    /**
     * 将对象转换为 Integer。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Integer 值，或默认值。
     */
    public static Integer toInt(Object value, Integer defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Integer)
        {
            return (Integer) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Integer。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Integer 值，或 null。
     */
    public static Integer toInt(Object value)
    {
        return toInt(value, null);
    }

    /**
     * 将逗号分隔的字符串转换为 Integer 数组。
     *
     * @param str 被转换的字符串。
     * @return 转换后的 Integer 数组。
     */
    public static Integer[] toIntArray(String str)
    {
        return toIntArray(",", str);
    }

    /**
     * 将逗号分隔的字符串转换为 Long 数组。
     *
     * @param str 被转换的字符串。
     * @return 转换后的 Long 数组。
     */
    public static Long[] toLongArray(String str)
    {
        return toLongArray(",", str);
    }

    /**
     * 将分隔符分隔的字符串转换为 Integer 数组。
     *
     * @param split 分隔符。
     * @param str 被转换的字符串。
     * @return 转换后的 Integer 数组。
     */
    public static Integer[] toIntArray(String split, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new Integer[] {};
        }
        String[] arr = str.split(split);
        final Integer[] ints = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            final Integer v = toInt(arr[i], 0);
            ints[i] = v;
        }
        return ints;
    }

    /**
     * 将分隔符分隔的字符串转换为 Long 数组。
     *
     * @param split 分隔符。
     * @param str 被转换的字符串。
     * @return 转换后的 Long 数组。
     */
    public static Long[] toLongArray(String split, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new Long[] {};
        }
        String[] arr = str.split(split);
        final Long[] longs = new Long[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            final Long v = toLong(arr[i], null);
            longs[i] = v;
        }
        return longs;
    }

    /**
     * 将逗号分隔的字符串转换为 String 数组。
     *
     * @param str 被转换的字符串。
     * @return 转换后的 String 数组。
     */
    public static String[] toStrArray(String str)
    {
        return toStrArray(",", str);
    }

    /**
     * 将分隔符分隔的字符串转换为 String 数组。
     *
     * @param split 分隔符。
     * @param str 被转换的字符串。
     * @return 转换后的 String 数组。
     */
    public static String[] toStrArray(String split, String str)
    {
        return str.split(split);
    }

    /**
     * 将对象转换为 Long。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Long 值，或默认值。
     */
    public static Long toLong(Object value, Long defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Long)
        {
            return (Long) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            // 支持科学计数法
            return new BigDecimal(valueStr.trim()).longValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Long。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Long 值，或 null。
     */
    public static Long toLong(Object value)
    {
        return toLong(value, null);
    }

    /**
     * 将对象转换为 Double。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Double 值，或默认值。
     */
    public static Double toDouble(Object value, Double defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Double)
        {
            return (Double) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).doubleValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            // 支持科学计数法
            return new BigDecimal(valueStr.trim()).doubleValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Double。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Double 值，或 null。
     */
    public static Double toDouble(Object value)
    {
        return toDouble(value, null);
    }

    /**
     * 将对象转换为 Float。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Float 值，或默认值。
     */
    public static Float toFloat(Object value, Float defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Float)
        {
            return (Float) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).floatValue();
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Float.parseFloat(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Float。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Float 值，或 null。
     */
    public static Float toFloat(Object value)
    {
        return toFloat(value, null);
    }

    /**
     * 将对象转换为 Boolean。
     * 支持的值：true, false, yes, ok, no, 1, 0。
     * 如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Boolean 值，或默认值。
     */
    public static Boolean toBool(Object value, Boolean defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Boolean)
        {
            return (Boolean) value;
        }
        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        valueStr = valueStr.trim().toLowerCase();
        switch (valueStr)
        {
            case "true":
            case "yes":
            case "ok":
            case "1":
                return true;
            case "false":
            case "no":
            case "0":
                return false;
            default:
                return defaultValue;
        }
    }

    /**
     * 将对象转换为 Boolean。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 Boolean 值，或 null。
     */
    public static Boolean toBool(Object value)
    {
        return toBool(value, null);
    }

    /**
     * 将对象转换为 Enum 对象。如果转换失败或对象为 null，则返回默认值。
     *
     * @param clazz Enum 的 Class 类型。
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 Enum 对象，或默认值。
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (clazz.isAssignableFrom(value.getClass()))
        {
            @SuppressWarnings("unchecked")
            E myE = (E) value;
            return myE;
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Enum.valueOf(clazz, valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 Enum 对象。如果转换失败或对象为 null，则返回 null。
     *
     * @param clazz Enum 的 Class 类型。
     * @param value 被转换的值。
     * @return 转换后的 Enum 对象，或 null。
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value)
    {
        return toEnum(clazz, value, null);
    }

    /**
     * 将对象转换为 BigInteger。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 BigInteger 值，或默认值。
     */
    public static BigInteger toBigInteger(Object value, BigInteger defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof BigInteger)
        {
            return (BigInteger) value;
        }
        if (value instanceof Long)
        {
            return BigInteger.valueOf((Long) value);
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigInteger(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 BigInteger。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 BigInteger 值，或 null。
     */
    public static BigInteger toBigInteger(Object value)
    {
        return toBigInteger(value, null);
    }

    /**
     * 将对象转换为 BigDecimal。如果转换失败或对象为 null，则返回默认值。
     *
     * @param value 被转换的值。
     * @param defaultValue 转换失败或 value 为 null 时的默认值。
     * @return 转换后的 BigDecimal 值，或默认值。
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof BigDecimal)
        {
            return (BigDecimal) value;
        }
        if (value instanceof Long)
        {
            return new BigDecimal((Long) value);
        }
        if (value instanceof Double)
        {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Integer)
        {
            return new BigDecimal((Integer) value);
        }
        final String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为 BigDecimal。如果转换失败或对象为 null，则返回 null。
     *
     * @param value 被转换的值。
     * @return 转换后的 BigDecimal 值，或 null。
     */
    public static BigDecimal toBigDecimal(Object value)
    {
        return toBigDecimal(value, null);
    }

    /**
     * 将对象转换为 UTF-8 编码的字符串。
     * 1、Byte 数组和 ByteBuffer 会被转换为对应字符串。
     * 2、对象数组会调用 Arrays.toString 方法。
     *
     * @param obj 被转换的对象。
     * @return 转换后的字符串。
     */
    public static String utf8Str(Object obj)
    {
        return str(obj, CharsetKit.CHARSET_UTF_8);
    }

    /**
     * 将对象转换为指定字符集的字符串。
     *
     * @param obj 被转换的对象。
     * @param charsetName 字符集名称。
     * @return 转换后的字符串。
     */
    public static String str(Object obj, String charsetName)
    {
        return str(obj, Charset.forName(charsetName));
    }

    /**
     * 将对象转换为指定字符集的字符串。
     * 1、Byte 数组和 ByteBuffer 会被转换为对应字符串。
     * 2、对象数组会调用 Arrays.toString 方法。
     *
     * @param obj 被转换的对象。
     * @param charset 字符集对象。
     * @return 转换后的字符串。
     */
    public static String str(Object obj, Charset charset)
    {
        if (null == obj)
        {
            return null;
        }

        if (obj instanceof String)
        {
            return (String) obj;
        }
        else if (obj instanceof byte[] || obj instanceof Byte[])
        {
            if (obj instanceof byte[])
            {
                return str((byte[]) obj, charset);
            }
            else
            {
                Byte[] bytes = (Byte[]) obj;
                int length = bytes.length;
                byte[] dest = new byte[length];
                for (int i = 0; i < length; i++)
                {
                    dest[i] = bytes[i];
                }
                return str(dest, charset);
            }
        }
        else if (obj instanceof ByteBuffer)
        {
            return str((ByteBuffer) obj, charset);
        }
        return obj.toString();
    }

    /**
     * 将 byte 数组转换为指定字符集的字符串。
     *
     * @param bytes byte 数组。
     * @param charset 字符集名称。如果为空，则使用系统默认字符集。
     * @return 转换后的字符串。
     */
    public static String str(byte[] bytes, String charset)
    {
        return str(bytes, StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 将 byte 数组解码为字符串。
     *
     * @param data byte 数组。
     * @param charset 字符集对象。如果为空，则使用系统默认字符集。
     * @return 解码后的字符串。
     */
    public static String str(byte[] data, Charset charset)
    {
        if (data == null)
        {
            return null;
        }

        if (null == charset)
        {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 将 ByteBuffer 数据转换为指定字符集的字符串。
     *
     * @param data ByteBuffer 数据。
     * @param charset 字符集名称。如果为空，则使用当前系统默认字符集。
     * @return 转换后的字符串。
     */
    public static String str(ByteBuffer data, String charset)
    {
        if (data == null)
        {
            return null;
        }

        return str(data, Charset.forName(charset));
    }

    /**
     * 将 ByteBuffer 数据转换为指定字符集的字符串。
     *
     * @param data ByteBuffer 数据。
     * @param charset 字符集对象。如果为空，则使用当前系统默认字符集。
     * @return 转换后的字符串。
     */
    public static String str(ByteBuffer data, Charset charset)
    {
        if (null == charset)
        {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    // ----------------------------------------------------------------------- 全角半角转换
    /**
     * 将半角字符串转换为全角字符串。
     *
     * @param input 半角字符串。
     * @return 全角字符串。
     */
    public static String toSBC(String input)
    {
        return toSBC(input, null);
    }

    /**
     * 将半角字符串转换为全角字符串，排除指定的字符。
     *
     * @param input 半角字符串。
     * @param notConvertSet 不进行转换的字符集合。
     * @return 全角字符串。
     */
    public static String toSBC(String input, Set<Character> notConvertSet)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (null != notConvertSet && notConvertSet.contains(c[i]))
            {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == ' ')
            {
                c[i] = '\u3000';
            }
            else if (c[i] < '\177')
            {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 将全角字符串转换为半角字符串。
     *
     * @param input 全角字符串。
     * @return 半角字符串。
     */
    public static String toDBC(String input)
    {
        return toDBC(input, null);
    }

    /**
     * 将全角字符串转换为半角字符串，排除指定的字符。
     *
     * @param text 全角字符串。
     * @param notConvertSet 不进行转换的字符集合。
     * @return 转换后的半角字符串。
     */
    public static String toDBC(String text, Set<Character> notConvertSet)
    {
        char[] c = text.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (null != notConvertSet && notConvertSet.contains(c[i]))
            {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == '\u3000')
            {
                c[i] = ' ';
            }
            else if (c[i] > '\uFF00' && c[i] < '\uFF5F')
            {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 将数字转换为中文大写金额字符串。
     *
     * @param n 数字金额。
     * @return 中文大写金额字符串。
     */
    public static String digitUppercase(double n)
    {
        String[] fraction = { "角", "分" };
        String[] digit = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String[][] unit = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++)
        {
            // 优化double计算精度丢失问题
            BigDecimal nNum = new BigDecimal(n);
            BigDecimal decimal = new BigDecimal(10);
            BigDecimal scale = nNum.multiply(decimal).setScale(2, RoundingMode.HALF_EVEN);
            double d = scale.doubleValue();
            s += (digit[(int) (Math.floor(d * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1)
        {
            s = "整";
        }
        int integerPart = (int) Math.floor(n);

        for (int i = 0; i < unit[0].length && integerPart > 0; i++)
        {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++)
            {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }
}
