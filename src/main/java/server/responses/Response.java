package server.responses;

import com.google.gson.JsonElement;

import java.util.Objects;

public class Response {

    private String response;
    private JsonElement value;
    private String reason;


    public Response() {
    }

    public Response(String response, JsonElement value) {
        this.response = response;
        this.value = value;
    }

    public Response(String response, JsonElement value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (!Objects.equals(this.response, response.response)) return false;
        return Objects.equals(value, response.value);
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

}
