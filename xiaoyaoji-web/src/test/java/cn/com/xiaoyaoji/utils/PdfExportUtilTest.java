package cn.com.xiaoyaoji.utils;

import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import org.junit.Test;

import java.io.FileOutputStream;

import static org.junit.Assert.*;

/**
 * Created by luofei on 2017/6/6.
 */
public class PdfExportUtilTest {

    public static final String DEST = "target/project_test.pdf";

    @Test
    public void export() throws Exception {

        Project project = ServiceFactory.instance().getProject("1mS2Hv6k1No");
        PdfExportUtil.export(project, new FileOutputStream(DEST));
    }

}