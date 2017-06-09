package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import cn.com.xiaoyaoji.data.bean.Attach;
import cn.com.xiaoyaoji.data.bean.TableNames;
import cn.com.xiaoyaoji.data.bean.User;
import cn.com.xiaoyaoji.extension.file.FileUtils;
import cn.com.xiaoyaoji.extension.file.MetaData;
import cn.com.xiaoyaoji.core.common.Result;
import cn.com.xiaoyaoji.core.common._HashMap;
import cn.com.xiaoyaoji.service.ServiceFactory;
import cn.com.xiaoyaoji.service.ServiceTool;
import cn.com.xiaoyaoji.utils.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: zhoujingjie
 * @Date: 17/5/2
 */
@RestController
@RequestMapping("/attach")
public class AttachController {
    private static Set<String> excludes;
    static {
        excludes = new HashSet<>();
    }

    @PostMapping
    public Object upload(@RequestParam("file") List<MultipartFile> files,@RequestParam String relateId,@RequestParam String projectId) throws IOException {
        AssertUtils.notNull(relateId,"missing relatedId");
        AssertUtils.isTrue(files!=null && files.size()>0,"请上传文件");
        for(MultipartFile file:files){
            String contentType = file.getContentType();
            //判断类型
            for(String exclude:excludes){
                if(exclude.matches(contentType)){
                    return new Result<>(false,"不允许上传该文件类型:"+exclude);
                }
            }
        }
        for(MultipartFile file:files){
            MetaData md = FileUtils.upload(file);
            String path = md.getPath();
            Attach temp = new Attach();
            temp.setId(StringUtils.id());
            temp.setUrl(path);
            temp.setType(md.getType().name());
            temp.setSort(10);
            temp.setCreateTime(new Date());
            temp.setRelatedId(relateId);
            temp.setFileName(file.getName());
            temp.setProjectId(projectId);
            ServiceFactory.instance().create(temp);
        }
        return true;
    }

    @Ignore
    @GetMapping("/{relatedId}")
    public Object get(@PathVariable String relatedId, @RequestParam String projectId,User user){
        ServiceTool.checkUserHasAccessPermission(projectId,user);
        List<Attach> attaches= ServiceFactory.instance().getAttachsByRelatedId(relatedId);
        return new _HashMap<>()
                .add("fileAccess", ConfigUtils.getFileAccessURL())
                .add("attachs",attaches);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable String id ){
        //权限检查
        Attach attach = ServiceFactory.instance().getById(id,Attach.class);
        AssertUtils.isTrue(attach!=null,"无效ID");
        int rs =ServiceFactory.instance().delete(TableNames.ATTACH,id);
        try {
            FileUtils.delete(attach.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
