package app;

import java.util.ArrayList;

public class Message {
    private String type;
    private String from;
    private String to;
    private String content;
    private ArrayList<String> stringArray;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getStringArray() {
        return stringArray;
    }

    public void setStringArray(ArrayList<String> strArr) {
        this.stringArray = strArr;
    }
}
