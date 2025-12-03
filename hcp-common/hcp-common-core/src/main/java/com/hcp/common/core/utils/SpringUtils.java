package com.hcp.common.core.utils;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * spring工具类 方便在非spring管理环境中获取bean
 *
 * @author vctgo
 */
@Component
public final class SpringUtils implements BeanFactoryPostProcessor
{
    /** Spring应用上下文环境 */
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        SpringUtils.beanFactory = beanFactory;
    }

    /**
     * 根据 Bean 名称获取 Bean 实例。
     *
     * @param name Bean 的名称。
     * @return 以给定名称注册的 Bean 实例 (Object)。
     * @throws org.springframework.beans.BeansException 如果 Bean 无法被获取。
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException
    {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 根据 Bean 类型获取 Bean 实例。
     *
     * @param clz Bean 的类型。
     * @return 匹配给定类型的 Bean 实例。
     * @throws org.springframework.beans.BeansException 如果 Bean 无法被获取。
     */
    public static <T> T getBean(Class<T> clz) throws BeansException
    {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 判断 BeanFactory 中是否包含与给定名称匹配的 Bean 定义。
     *
     * @param name Bean 的名称。
     * @return 如果包含该 Bean 定义，则返回 true；否则返回 false。
     */
    public static boolean containsBean(String name)
    {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断给定名称的 Bean 是否为单例 (Singleton)。
     *
     * @param name Bean 的名称。
     * @return 如果是单例，则返回 true；否则返回 false。
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 如果找不到给定名称的 Bean 定义。
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.isSingleton(name);
    }

    /**
     * 获取给定名称的 Bean 的类型。
     *
     * @param name Bean 的名称。
     * @return 注册对象的类型 (Class)。
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 如果找不到给定名称的 Bean 定义。
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getType(name);
    }

    /**
     * 获取给定 Bean 名称的别名。
     *
     * @param name Bean 的名称。
     * @return 别名数组 (String[])。如果定义中没有别名，则返回空数组。
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException 如果找不到给定名称的 Bean 定义。
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取当前对象的 AOP 代理对象。
     *
     * @param invoker 目标对象（尽管在实现中未被直接使用，通常用于泛型推断）。
     * @return 当前的 AOP 代理对象。
     * @throws IllegalStateException 如果当前没有 AOP 代理正在运行。
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker)
    {
        return (T) AopContext.currentProxy();
    }
}
