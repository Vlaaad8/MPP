package org.example.clientfx.Communication;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestType type;
    private Object data;

    public Object data() {
        return this.data;
    }

    public RequestType type() {
        return this.type;
    }

    private void data(Object data) {
        this.data = data;
    }

    private void type(RequestType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }

    public static class Builder {
        private Request request = new Request();

        public Builder type(RequestType type) {
            request.type(type);
            return this;
        }

        public Builder data(Object data) {
            request.data(data);
            return this;
        }
        public Request build() {
            return request;
        }
    }
}
