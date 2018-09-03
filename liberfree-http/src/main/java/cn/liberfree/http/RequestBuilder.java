package cn.liberfree.http;


import org.slf4j.Logger;

import java.util.Map;

public  class RequestBuilder {

    private Request request ;

    private RequestBuilder(String url) {
        this.request = new Request();
        this.request.setUrl(url);
    }

    public static RequestBuilder build(String url) {
        return new RequestBuilder(url);
    }

    public RequestBuilder addHeader(String name,String value){
        this.request.addHeaders(name,value);
        return this;
    }


    public RequestBuilder setEncoding(String encoding) {
        this.request.setEncoding(encoding);
        return this;
    }

    public RequestBuilder setConnectTimeout(Integer connectTimeout) {
        this.request.setConnectTimeout(connectTimeout);
        return this;
    }

    public RequestBuilder setReadTimeout(Integer readTimeout) {
        this.request.setReadTimeout(readTimeout);
        return this;
    }

    public RequestBuilder setException(RuntimeException exception) {
        this.request.setException(exception);
        return this;
    }

    public RequestBuilder setBody(byte[] body) {
        this.request.setBody(body);
        return this;
    }

    public RequestBuilder setContentType(String contentType) {
        this.request.setContentType(contentType);
        return this;
    }

    public RequestBuilder addParams(String name,String value){
        this.request.addParams(name,value);
        return this;
    }

    public Request.Response get(){
        this.request.setMethod(Request.RequestMethod.GET);
        return this.request.req();
    }
    public Request.Response post(){
        this.request.setMethod(Request.RequestMethod.POST);
        return this.request.req();
    }

    public Request.Response delete(){
        this.request.setMethod(Request.RequestMethod.DELETE);
        return this.request.req();
    }

    public Request.Response put(){
        this.request.setMethod(Request.RequestMethod.PUT);
        return this.request.req();
    }

    public Request.Response patch(){
        this.request.setMethod(Request.RequestMethod.PATCH);
        return this.request.req();
    }







    public static void main(String[] args) {
//        Request request = new Request();
//        request.setUrl("https://www.sojson.com/open/api/weather/json.shtml");
////        request.addParams("city", "成都");
//        request.setBody("city=成都".getBytes());
//        Response req = request.req();
//
//        System.out.println(req.getStatus());
//        System.out.println(req.getContentString());

        Request.Response response = RequestBuilder
                .build("https://www.sojson.com/open/api/weather/json.shtml")
                .addParams("city", "成都")
                .get();

        System.out.println(response.getStatus()+"");
        System.out.println(response.getContentString());
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

}