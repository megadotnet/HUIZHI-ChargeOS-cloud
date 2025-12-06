package com.hcp.common.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 错误信息处理类。
 *
 * @author vctgo
 */
public class ExceptionUtil
{
    /**
     * 获取异常的完整堆栈跟踪信息。
     *
     * @param e 异常对象。
     * @return 异常的堆栈跟踪信息 (String)。
     */
    public static String getExceptionMessage(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    /**
     * 获取异常的根源错误信息。
     *
     * @param e 异常对象。
     * @return 根源异常的错误信息 (String)。如果异常没有根源，则返回异常本身的信息。如果信息为 null，返回 "null"。
     */
    public static String getRootErrorMessage(Exception e)
    {
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null)
        {
            return "";
        }
        String msg = root.getMessage();
        if (msg == null)
        {
            return "null";
        }
        return StringUtils.defaultString(msg);
    }
}
