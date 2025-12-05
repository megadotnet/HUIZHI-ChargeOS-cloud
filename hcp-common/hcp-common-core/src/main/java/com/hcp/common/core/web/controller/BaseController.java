package com.hcp.common.core.web.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;

import com.hcp.common.core.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.hcp.common.core.utils.DateUtils;

/**
 * web层通用数据处理
 *
 * @author vctgo
 */
@Slf4j
public class BaseController
{

    /**
     * 初始化数据绑定器。
     * 将前台传递过来的日期格式的字符串，自动转化为 Date 类型。
     *
     * @param binder Web 数据绑定器。
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }


    /**
     * 返回成功响应结果。
     *
     * @return 成功的 AjaxResult 对象。
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回带有消息的成功响应结果。
     *
     * @param message 成功消息。
     * @return 带有消息的成功 AjaxResult 对象。
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回带有数据的成功响应结果。
     *
     * @param data 响应数据。
     * @return 带有数据的成功 AjaxResult 对象。
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    /**
     * 返回失败响应结果。
     *
     * @return 失败的 AjaxResult 对象。
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回带有消息的失败响应结果。
     *
     * @param message 失败消息。
     * @return 带有消息的失败 AjaxResult 对象。
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回带有消息的警告响应结果。
     *
     * @param message 警告消息。
     * @return 带有消息的警告 AjaxResult 对象。
     */
    public AjaxResult warn(String message)
    {
        return AjaxResult.warn(message);
    }

    /**
     * 根据影响行数响应返回结果。
     *
     * @param rows 影响行数。
     * @return 如果 rows > 0 返回成功，否则返回失败。
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 根据布尔结果响应返回结果。
     *
     * @param result 操作结果，true 为成功，false 为失败。
     * @return 如果 result 为 true 返回成功，否则返回失败。
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }
}
