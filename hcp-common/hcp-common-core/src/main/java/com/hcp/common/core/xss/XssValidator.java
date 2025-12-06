package com.hcp.common.core.xss;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.hcp.common.core.utils.StringUtils;

/**
 * 自定义xss校验注解实现
 *
 * @author vctgo
 */
public class XssValidator implements ConstraintValidator<Xss, String>
{
    private static final String HTML_PATTERN = "<(\\S*?)[^>]*>.*?|<.*? />";

    /**
     * 校验值是否包含 HTML 标签。
     *
     * @param value 需要校验的字符串值。
     * @param constraintValidatorContext 约束验证器上下文。
     * @return 如果值为空或不包含 HTML 标签，则返回 true；否则返回 false。
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        if (StringUtils.isBlank(value))
        {
            return true;
        }
        return !containsHtml(value);
    }

    /**
     * 检查字符串中是否包含 HTML 标签。
     *
     * @param value 需要检查的字符串。
     * @return 如果包含 HTML 标签，则返回 true；否则返回 false。
     */
    public static boolean containsHtml(String value)
    {
        StringBuilder sHtml = new StringBuilder();
        Pattern pattern = Pattern.compile(HTML_PATTERN);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find())
        {
            sHtml.append(matcher.group());
        }
        return pattern.matcher(sHtml).matches();
    }
}
