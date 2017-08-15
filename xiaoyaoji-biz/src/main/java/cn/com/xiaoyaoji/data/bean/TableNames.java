package cn.com.xiaoyaoji.data.bean;


import cn.com.xiaoyaoji.util.SqlUtils;

/**
 * @author: zhoujingjie
 * @Date: 16/5/11
 */
public interface TableNames {

    String IMAGE="image";
    String USER = SqlUtils.getTableName(User.class);

    String USER_THIRD = "user_third";

    String DOC = SqlUtils.getTableName(Doc.class);

    String DOC_HISTORY = SqlUtils.getTableName(DocHistory.class);

    String MODULES = SqlUtils.getTableName(Module.class);

    String INTERFACE_FOLDER=SqlUtils.getTableName(Folder.class);

    String TEAM=SqlUtils.getTableName(Team.class);

    String PROJECT=SqlUtils.getTableName(Project.class);

    String PROJECT_LOG=SqlUtils.getTableName(ProjectLog.class);
    //
    String PROJECT_USER=SqlUtils.getTableName(ProjectUser.class);
    //
    String FIND_PASSWORD =SqlUtils.getTableName(FindPassword.class);

    String SHARE =SqlUtils.getTableName(Share.class);

    String PROJECT_GLOBAL = SqlUtils.getTableName(ProjectGlobal.class);

    String ATTACH = SqlUtils.getTableName(Attach.class);
}
