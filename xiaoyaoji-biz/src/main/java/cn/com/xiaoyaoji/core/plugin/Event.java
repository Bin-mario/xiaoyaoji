package cn.com.xiaoyaoji.core.plugin;

/**
 * @author zhoujingjie
 *         created on 2017/6/22
 */
public enum Event {
    DOC_EV("doc.ev"),
    DOC_EXPORT("doc.export"),
    DOC_IMPORT("doc.import"),
    LOGIN("login")
    ;

    String value;

    Event(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Event parse(String value){
        if(value==null)
            return null;
        for(Event e:Event.values()){
            if(e.getValue().equals(value))
                return e;
        }
        return null;
    }



}
