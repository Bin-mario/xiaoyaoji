package cn.com.xiaoyaoji.test;

import cn.com.xiaoyaoji.util.JdbcUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import java.sql.*;
import java.util.List;

/**
 * @author zhoujingjie
 *         created on 2017/8/31
 */
public class TestObjectArray {

    @Test
    public void test() throws SQLException {
        Connection connect = JdbcUtils.getConnect();
        String sql ="select name a, status s from project limit 10";
        QueryRunner qr = new QueryRunner();
        List<TestBean> query = qr.query(connect, sql, new BeanListHandler<>(TestBean.class));
        System.out.println(query);
    }
}
