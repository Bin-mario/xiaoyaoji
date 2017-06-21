package cn.com.xiaoyaoji.core.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页
 *
 * @author: zhoujingjie
 * @Date: 16/5/4
 */
public class Pagination {
    private int page = 1;
    private int limit = 10;
    private int total;
    private Map<String, Object> params = new HashMap<>();

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getStart() {
        int temp = (page - 1) * limit;
        if (temp < 0) {
            temp = 0;
        }
        return temp;
    }

    public int getPages() {
        return total % limit == 0 ? total / limit : (total / limit + 1);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Pagination putParams(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Pagination putParams(Map<String, String> paramsMap) {
        params.putAll(paramsMap);
        return this;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static Pagination build(Map<String, String> params) {
        Pagination pagination = new Pagination();
        if (params.get("page") != null) {
            pagination.setPage(Integer.parseInt(params.get("page")));
        }
        if (params.get("limit") != null) {
            pagination.setLimit(Integer.parseInt(params.get("limit")));
        }
        pagination.putParams(params);
        return pagination;
    }
}
