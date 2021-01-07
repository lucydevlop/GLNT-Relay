package kr.co.glnt.relay.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class DisplayMessage {
    private String facilityId;
    private List<DisplayMessageInfo> messages;

    @Data
    private static class DisplayMessageInfo {
        private String color;
        private String text;
        private int order;
        private int line;

        public DisplayMessageInfo() {}
    }


    private final List<String> messageFormat = Arrays.asList("", "![000/P0000/Y0004/%s%s!]", "![000/P0001/Y0408/%s%s!]");

    public List<String> generateMessageList() {
        Collections.sort(messages, new Comparator<DisplayMessageInfo>() {
            @Override
            public int compare(DisplayMessageInfo d1, DisplayMessageInfo d2) {
                return d1.getOrder() > d2.getOrder() ? 1 : -1;
            }
        });

        List<String> messageList = new ArrayList<>();
        for(int j = 0; j < messages.size(); j++) {
            DisplayMessageInfo info = messages.get(j);
            String message = String.format(messageFormat.get(info.getLine()), info.getColor(), info.getText());
            messageList.add(message);
        }

        return messageList;
    }
}
