package client.input;

import com.beust.jcommander.Parameter;

public class Input {
    @Parameter(names = "-t")
    private String type;
    @Parameter(names = "-k")
    private String key;
    @Parameter(names = "-v")
    private String value;
    @Parameter(names = "-in")
    private String json;

    public Input() {
    }

    public Input(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Input(String type, String key, String value, String json) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.json = json;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setIndex(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


}
