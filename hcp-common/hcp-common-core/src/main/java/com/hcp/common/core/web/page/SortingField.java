package com.hcp.common.core.web.page;

import java.io.Serializable;

/**
 * 排序字段 DTO
 *
 * 类名加了 ing 的原因是，避免和 ES SortField 重名。
 */
public class SortingField implements Serializable {

    /**
     * 顺序 - 升序
     */
    public static final String ORDER_ASC = "asc";
    /**
     * 顺序 - 降序
     */
    public static final String ORDER_DESC = "desc";

    /**
     * 字段
     */
    private String field;
    /**
     * 顺序
     */
    private String order;

    /**
     * 空构造方法，用于解决反序列化问题。
     */
    public SortingField() {
    }

    /**
     * 全参构造方法。
     *
     * @param field 排序字段。
     * @param order 排序顺序（asc 或 desc）。
     */
    public SortingField(String field, String order) {
        this.field = field;
        this.order = order;
    }

    /**
     * 获取排序字段。
     *
     * @return 排序字段。
     */
    public String getField() {
        return field;
    }

    /**
     * 设置排序字段。
     *
     * @param field 排序字段。
     * @return 当前 SortingField 对象，便于链式调用。
     */
    public SortingField setField(String field) {
        this.field = field;
        return this;
    }

    /**
     * 获取排序顺序。
     *
     * @return 排序顺序。
     */
    public String getOrder() {
        return order;
    }

    /**
     * 设置排序顺序。
     *
     * @param order 排序顺序。
     * @return 当前 SortingField 对象，便于链式调用。
     */
    public SortingField setOrder(String order) {
        this.order = order;
        return this;
    }
}
