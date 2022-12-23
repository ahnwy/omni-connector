package aicc.omni.omniconnector.model.meta;

import lombok.Data;

@Data
public class ApMsgDto {
    public chatListItem chatList;
    public chatViewItem chatView;


    public static class chatListItem{
        public String channelSeq;
        public String customer;
        public String msg;
        public String status;
    }


    public static class chatViewItem{

        public String msgWrtId;
        public String msgSeq;
        public String msg;
        public String msgWrtTime;
        public String msgContentType;
        public String filePath;
        public String fileName;
        public String fileSize;
        public String isOpen;
    }

    public ApMsgDto(String channelSeq, String customer, String msg, String status,
                    String msgWrtId, String msgSeq, String msgWrtTime, String msgContentType,
                    String filePath, String fileName, String fileSize, String isOpen) {
        chatList = new chatListItem();
        chatList.channelSeq = channelSeq;
        chatList.customer = customer;
        chatList.msg = msg;
        chatList.status = status;
        chatView = new chatViewItem();
        chatView.msgWrtId = msgWrtId;
        chatView.msgSeq = msgSeq;
        chatView.msg = msg;
        chatView.msgWrtTime = msgWrtTime;
        chatView.msgContentType = msgContentType;
        chatView.filePath = filePath;
        chatView.fileName = fileName;
        chatView.fileSize = fileSize;
        chatView.isOpen = isOpen;

    }
}
