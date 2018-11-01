# liberfree-http:1.1-RELEASE
# liberfree-sftp:1.1-RELEASE
 
### 功能
- [x] GET请求
- [x] POST请求
- [x] PUT请求
- [x] PATCH请求
- [x] DELETE请求 
- [ ] 缓存
- [x] https(单向)
- [ ] https(双向) 


### maven maven.apache.org
```
    <dependency>
        <groupId>cn.liberfree</groupId>
        <artifactId>http</artifactId>
        <version>1.0-RELEASE</version>
    </dependency>
     <dependency>
        <groupId>cn.liberfree</groupId>
        <artifactId>sftp</artifactId>
        <version>1.1-RELEASE</version>
    </dependency>
```

### Gradle gradle.org
```
    compile 'cn.liberfree:http:1.0-RELEASE'
```

### Gradle Kotlin DSL

```
    compile(group = "cn.liberfree", name = "http", version = "1.0-RELEASE")
```

## API 

`GET`
`POST`
`DELETE`
`PUT`
`PATCH` 
## 网络请求 
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
```
## sftp
``` java  
    public static void main(String[] args) { 

    	SftpConfigration sftpConfigration = new SftpConfigration("username", "password","ip", port,null);
		SftpConnectionPool connectionPool = new DefaultSftpConnectionPool(sftpConfigration);
		System.out.println(SFTPUtil.exist(connectionPool, "/dz/images/8bbd933d-c724-4429-897e-547837e9040a.jpg"));
    } 
```
	
