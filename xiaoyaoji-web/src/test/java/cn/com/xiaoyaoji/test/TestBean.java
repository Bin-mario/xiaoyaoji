package cn.com.xiaoyaoji.test;

/**
 * @author zhoujingjie
 *         created on 2017/8/31
 */
public class TestBean {
    private String a,s;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "a='" + a + '\'' +
                ", s='" + s + '\'' +
                '}';
    }
}
