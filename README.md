# liberfree-http:1.0-RELEASE
## API 
`GET`
`POST`
`DELETE`
`PUT`
`PATCH` 
## 使用方法 
``` java

    public static void main(String[] args) { 

        Request.Response response = RequestBuilder
                .build("https://www.sojson.com/open/api/weather/json.shtml")
                .addParams("city", "成都")
                .get();

        logger.info(response.getStatus());
        logger.info(response.getContentString());
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            logger.info(entry.getKey()+":"+entry.getValue());
        }

    }

``` java
