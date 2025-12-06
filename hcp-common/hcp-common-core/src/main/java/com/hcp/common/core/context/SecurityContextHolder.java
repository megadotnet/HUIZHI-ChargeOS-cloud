package com.hcp.common.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.hcp.common.core.constant.SecurityConstants;
import com.hcp.common.core.text.Convert;
import com.hcp.common.core.utils.StringUtils;

/**
 * 获取当前线程变量中的 租户id 部门id 用户id、用户名称、Token等信息
 * 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 *
 * @author vctgo
 */
public class SecurityContextHolder
{
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置线程变量。
     *
     * @param key 键。
     * @param value 值。
     */
    public static void set(String key, Object value)
    {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    /**
     * 获取线程变量字符串值。
     *
     * @param key 键。
     * @return 字符串值。
     */
    public static String get(String key)
    {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    /**
     * 获取线程变量指定类型值。
     *
     * @param key 键。
     * @param clazz 值类型。
     * @return 指定类型的值。
     */
    public static <T> T get(String key, Class<T> clazz)
    {
        Map<String, Object> map = getLocalMap();
        return StringUtils.cast(map.getOrDefault(key, null));
    }

    /**
     * 获取本地线程变量 Map。
     *
     * @return 线程变量 Map。
     */
    public static Map<String, Object> getLocalMap()
    {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null)
        {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    /**
     * 设置本地线程变量 Map。
     *
     * @param threadLocalMap 线程变量 Map。
     */
    public static void setLocalMap(Map<String, Object> threadLocalMap)
    {
        THREAD_LOCAL.set(threadLocalMap);
    }

    /**
     * 获取租户 ID。
     *
     * @return 租户 ID。默认返回 9999L。
     */
    public static Long getTenantId()
    {
        return Convert.toLong(get(SecurityConstants.DETAILS_TENANT_ID), 9999L);
    }

    /**
     * 设置租户 ID。
     *
     * @param tenantId 租户 ID。
     */
    public static void setTenantId(String tenantId)
    {
        set(SecurityConstants.DETAILS_TENANT_ID, tenantId);
    }

    /**
     * 获取部门 ID。
     *
     * @return 部门 ID。
     */
    public static Long getDeptId()
    {
        return Convert.toLong(get(SecurityConstants.DETAILS_DEPT_ID));
    }

    /**
     * 设置部门 ID。
     *
     * @param deptId 部门 ID。
     */
    public static void setDeptId(String deptId)
    {
        set(SecurityConstants.DETAILS_DEPT_ID, deptId);
    }

    /**
     * 设置远程头信息。
     *
     * @param account 账户信息。
     */
    public static void setRemoteHeader(Object account)
    {
        set(SecurityConstants.REMOTE_HEADER, account);
    }

    /**
     * 获取用户 ID。
     *
     * @return 用户 ID。默认返回 0L。
     */
    public static Long getUserId()
    {
        return Convert.toLong(get(SecurityConstants.DETAILS_USER_ID), 0L);
    }

    /**
     * 设置用户 ID。
     *
     * @param account 用户 ID。
     */
    public static void setUserId(String account)
    {
        set(SecurityConstants.DETAILS_USER_ID, account);
    }

    /**
     * 获取用户名。
     *
     * @return 用户名。
     */
    public static String getUserName()
    {
        return get(SecurityConstants.DETAILS_USERNAME);
    }

    /**
     * 设置用户名。
     *
     * @param username 用户名。
     */
    public static void setUserName(String username)
    {
        set(SecurityConstants.DETAILS_USERNAME, username);
    }

    /**
     * 获取用户 Key。
     *
     * @return 用户 Key。
     */
    public static String getUserKey()
    {
        return get(SecurityConstants.USER_KEY);
    }

    /**
     * 设置用户 Key。
     *
     * @param userKey 用户 Key。
     */
    public static void setUserKey(String userKey)
    {
        set(SecurityConstants.USER_KEY, userKey);
    }

    /**
     * 获取权限字符串。
     *
     * @return 权限字符串。
     */
    public static String getPermission()
    {
        return get(SecurityConstants.ROLE_PERMISSION);
    }

    /**
     * 设置权限字符串。
     *
     * @param permissions 权限字符串。
     */
    public static void setPermission(String permissions)
    {
        set(SecurityConstants.ROLE_PERMISSION, permissions);
    }

    /**
     * 移除当前线程变量。
     */
    public static void remove()
    {
        THREAD_LOCAL.remove();
    }
}
