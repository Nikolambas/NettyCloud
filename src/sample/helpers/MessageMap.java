package sample.helpers;

import sample.helpers.classesFunctions.*;

import java.util.HashMap;
import java.util.Map;

public  class MessageMap {
    public  Map<MessageType,MessageFunctions> messageMap;

    public MessageMap() {
        this.messageMap=setMessageMap();
    }

    public Map<MessageType, MessageFunctions> getMessageMap() {
        return messageMap;
    }


    public HashMap setMessageMap(){
        Map<MessageType, MessageFunctions> messageMap=new HashMap<>();
        messageMap.put(MessageType.DELETE, new DeleteFunctions());
        messageMap.put(MessageType.FILECOME, new FileComeFunctions());
        messageMap.put(MessageType.FILEGET, new FileGetFunctions());
        messageMap.put(MessageType.LISTVIEW, new ListViewFunctions());
        messageMap.put(MessageType.USER, new UserFunctions());
        messageMap.put(MessageType.USERREG, new UserRegFunctions());
        return (HashMap) messageMap;
    }
}
