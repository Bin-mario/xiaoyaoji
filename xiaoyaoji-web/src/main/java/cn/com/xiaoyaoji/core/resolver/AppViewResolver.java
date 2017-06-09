package cn.com.xiaoyaoji.core.resolver;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Locale;

/**
 * @author: zhoujingjie
 * @Date: 17/4/8
 */
public class AppViewResolver extends InternalResourceViewResolver {
    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        return super.buildView(viewName);
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return super.resolveViewName(viewName, locale);
    }
}
