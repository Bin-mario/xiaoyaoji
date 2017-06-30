package cn.com.xiaoyaoji.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.service.ServiceFactory;

public class TestExportImportJson {

	@Test
	public void exportJson(){
		
		System.out.println(ServiceFactory.instance().exportJson("demo"));
	}
	
	@Test
	public void importJson() throws IOException{
		
		String content = FileUtils.readFileToString(new File("src/test/resources/testimp.json"));
		User user = new User();
		user.setId("1kiQ78HJJzk");
		ServiceFactory.instance().importJson(user, content);
	}
}
