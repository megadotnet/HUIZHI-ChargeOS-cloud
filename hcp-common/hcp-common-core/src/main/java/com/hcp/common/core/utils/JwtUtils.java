package com.hcp.common.core.utils;

import java.util.Map;
import com.hcp.common.core.constant.SecurityConstants;
import com.hcp.common.core.constant.TokenConstants;
import com.hcp.common.core.text.Convert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Jwt工具类
 *
 * @author vctgo
 */
public class JwtUtils
{
    public static String secret = TokenConstants.SECRET;

    /**
     * 根据数据声明生成 JWT 令牌。
     *
     * @param claims 数据声明 Map，包含需要封装在令牌中的信息。
     * @return 生成的 JWT 令牌 (String)。
     */
    public static String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 解析 JWT 令牌并提取其中的数据声明。
     *
     * @param token JWT 令牌。
     * @return 令牌中的数据声明 (Claims)。
     * @throws io.jsonwebtoken.JwtException 如果令牌无效、过期或解析失败。
     */
    public static Claims parseToken(String token)
    {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 从 JWT 令牌中提取用户标识 (User Key)。
     *
     * @param token JWT 令牌。
     * @return 用户标识 (String)。
     */
    public static String getUserKey(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.USER_KEY);
    }

    /**
     * 从数据声明中提取用户标识 (User Key)。
     *
     * @param claims 数据声明。
     * @return 用户标识 (String)。
     */
    public static String getUserKey(Claims claims)
    {
        return getValue(claims, SecurityConstants.USER_KEY);
    }

    /**
     * 从 JWT 令牌中提取部门 ID。
     *
     * @param token JWT 令牌。
     * @return 部门 ID (String)。
     */
    public static String getDeptId(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_DEPT_ID);
    }

    /**
     * 从数据声明中提取部门 ID。
     *
     * @param claims 数据声明。
     * @return 部门 ID (String)。
     */
    public static String getDeptId(Claims claims)
    {
        return getValue(claims, SecurityConstants.DETAILS_DEPT_ID);
    }

    /**
     * 从 JWT 令牌中提取用户 ID。
     *
     * @param token JWT 令牌。
     * @return 用户 ID (String)。
     */
    public static String getUserId(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_USER_ID);
    }

    /**
     * 从数据声明中提取用户 ID。
     *
     * @param claims 数据声明。
     * @return 用户 ID (String)。
     */
    public static String getUserId(Claims claims)
    {
        return getValue(claims, SecurityConstants.DETAILS_USER_ID);
    }

    /**
     * 从 JWT 令牌中提取用户名。
     *
     * @param token JWT 令牌。
     * @return 用户名 (String)。
     */
    public static String getUserName(String token)
    {
        Claims claims = parseToken(token);
        return getValue(claims, SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 从数据声明中提取用户名。
     *
     * @param claims 数据声明。
     * @return 用户名 (String)。
     */
    public static String getUserName(Claims claims)
    {
        return getValue(claims, SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 从数据声明中根据键获取对应的值。
     *
     * @param claims 数据声明。
     * @param key 键名。
     * @return 对应的值 (String)。如果键不存在，则返回空字符串。
     */
    public static String getValue(Claims claims, String key)
    {
        return Convert.toStr(claims.get(key), "");
    }
}
