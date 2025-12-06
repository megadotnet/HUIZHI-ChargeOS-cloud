package com.hcp.common.core.utils.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;

import com.hcp.common.core.utils.ServletUtils;
import com.hcp.common.core.utils.StringUtils;

/**
 * 获取IP方法
 *
 * @author vctgo
 */
public class IpUtils
{
    public final static String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
    // 匹配 ip
    public final static String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";
    public final static String REGX_IP_WILDCARD = "(((\\*\\.){3}\\*)|(" + REGX_0_255 + "(\\.\\*){3})|(" + REGX_0_255 + "\\." + REGX_0_255 + ")(\\.\\*){2}" + "|((" + REGX_0_255 + "\\.){3}\\*))";
    // 匹配网段
    public final static String REGX_IP_SEG = "(" + REGX_IP + "\\-" + REGX_IP + ")";

    /**
     * 获取当前请求的客户端 IP 地址。
     *
     * @return 客户端 IP 地址。
     */
    public static String getIpAddr()
    {
        return getIpAddr(ServletUtils.getRequest());
    }

    /**
     * 获取指定请求的客户端 IP 地址。
     *
     * @param request HTTP 请求对象。
     * @return 客户端 IP 地址。
     */
    public static String getIpAddr(HttpServletRequest request)
    {
        if (request == null)
        {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 检查给定的 IP 地址是否为内部 IP 地址。
     *
     * @param ip IP 地址字符串。
     * @return 如果是内部 IP 地址，则返回 true；否则返回 false。
     */
    public static boolean internalIp(String ip)
    {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    /**
     * 检查给定的字节数组 IP 地址是否为内部 IP 地址。
     *
     * @param addr IP 地址的字节数组表示。
     * @return 如果是内部 IP 地址，则返回 true；否则返回 false。
     */
    private static boolean internalIp(byte[] addr)
    {
        if (StringUtils.isNull(addr) || addr.length < 2)
        {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0)
        {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4)
                {
                    return true;
                }
            case SECTION_5:
                switch (b1)
                {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * 将 IPv4 地址字符串转换为字节数组。
     *
     * @param text IPv4 地址字符串。
     * @return 转换后的字节数组。如果格式不正确，则返回 null。
     */
    public static byte[] textToNumericFormatV4(String text)
    {
        if (text.length() == 0)
        {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try
        {
            long l;
            int i;
            switch (elements.length)
            {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L))
                    {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L))
                    {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L))
                    {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                        {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L))
                    {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                        {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        return bytes;
    }

    /**
     * 获取本地主机的 IP 地址。
     *
     * @return 本地 IP 地址。如果无法获取，返回 "127.0.0.1"。
     */
    public static String getHostIp()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
        }
        return "127.0.0.1";
    }

    /**
     * 获取本地主机的主机名。
     *
     * @return 本地主机名。如果无法获取，返回 "未知"。
     */
    public static String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
        }
        return "未知";
    }

    /**
     * 从多级反向代理中获得第一个非 unknown 的 IP 地址。
     *
     * @param ip 逗号分隔的 IP 地址字符串。
     * @return 第一个非 unknown 的 IP 地址。
     */
    public static String getMultistageReverseProxyIp(String ip)
    {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0)
        {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips)
            {
                if (false == isUnknown(subIp))
                {
                    ip = subIp;
                    break;
                }
            }
        }
        return StringUtils.substring(ip, 0, 255);
    }

    /**
     * 检测给定的字符串是否表示未知状态（如 "unknown"）。
     *
     * @param checkString 被检测的字符串。
     * @return 如果字符串为空或为 "unknown"（忽略大小写），则返回 true；否则返回 false。
     */
    public static boolean isUnknown(String checkString)
    {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }
    /**
     * 判断字符串是否为有效的 IP 地址。
     *
     * @param ip 待检测的字符串。
     * @return 如果是有效的 IP 地址，则返回 true；否则返回 false。
     */
    public static boolean isIP(String ip)
    {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP);
    }

    /**
     * 判断字符串是否为 IP 地址或包含通配符 '*' 的 IP 地址格式。
     *
     * @param ip 待检测的字符串。
     * @return 如果符合格式，则返回 true；否则返回 false。
     */
    public static boolean isIpWildCard(String ip)
    {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP_WILDCARD);
    }

    /**
     * 检测 IP 地址是否匹配带有通配符的规则。
     * 此方法不进行基本的格式检查。
     *
     * @param ipWildCard 带有通配符的 IP 规则（例如 192.168.*.*）。
     * @param ip 待检测的 IP 地址。
     * @return 如果匹配，则返回 true；否则返回 false。
     */
    public static boolean ipIsInWildCardNoCheck(String ipWildCard, String ip)
    {
        String[] s1 = ipWildCard.split("\\.");
        String[] s2 = ip.split("\\.");
        boolean isMatchedSeg = true;
        for (int i = 0; i < s1.length && !s1[i].equals("*"); i++)
        {
            if (!s1[i].equals(s2[i]))
            {
                isMatchedSeg = false;
                break;
            }
        }
        return isMatchedSeg;
    }

    /**
     * 判断字符串是否为 IP 段格式（例如 "10.10.10.1-10.10.10.99"）。
     *
     * @param ipSeg 待检测的字符串。
     * @return 如果符合 IP 段格式，则返回 true；否则返回 false。
     */
    public static boolean isIPSegment(String ipSeg)
    {
        return StringUtils.isNotBlank(ipSeg) && ipSeg.matches(REGX_IP_SEG);
    }

    /**
     * 判断 IP 地址是否在指定的 IP 段内。
     * 此方法不进行基本的格式检查。
     *
     * @param iparea IP 段（例如 "192.168.1.1-192.168.1.10"）。
     * @param ip 待检测的 IP 地址。
     * @return 如果 IP 在段内，则返回 true；否则返回 false。
     */
    public static boolean ipIsInNetNoCheck(String iparea, String ip)
    {
        int idx = iparea.indexOf('-');
        String[] sips = iparea.substring(0, idx).split("\\.");
        String[] sipe = iparea.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i)
        {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe)
        {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    /**
     * 校验 IP 地址是否符合过滤规则。
     * 支持单个 IP、通配符 IP（如 192.168.*.*）和 IP 段（如 10.10.10.1-10.10.10.99）。
     * 多个规则可以用分号分隔。
     *
     * @param filter 过滤规则字符串。
     * @param ip 待校验的 IP 地址。
     * @return 如果 IP 符合任一规则，则返回 true；否则返回 false。
     */
    public static boolean isMatchedIp(String filter, String ip)
    {
        if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(ip))
        {
            return false;
        }
        String[] ips = filter.split(";");
        for (String iStr : ips)
        {
            if (isIP(iStr) && iStr.equals(ip))
            {
                return true;
            }
            else if (isIpWildCard(iStr) && ipIsInWildCardNoCheck(iStr, ip))
            {
                return true;
            }
            else if (isIPSegment(iStr) && ipIsInNetNoCheck(iStr, ip))
            {
                return true;
            }
        }
        return false;
    }
}
