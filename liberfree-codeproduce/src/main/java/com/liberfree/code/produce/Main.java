package com.liberfree.code.produce;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhangchao
 * @time: 2018-09-12 15:50
 **/
public class Main {
    private static final String TEMPLATE_PATH = "src/main/java/com/liberfree/code/template";
    private static final String PATH = "target/";


    static String[]  templates = new String[]{
            "controller.ftl",
            "repository.ftl",
            "repositoryQuery.ftl",
            "service.ftl",
            "serviceImpl.ftl"
    };

    public static void main(String[] args) throws Exception {
        // step1 创建freeMarker配置实例
        Configuration configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File(TEMPLATE_PATH));
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("name", "AdvanceBankDraft");
        dataMap.put("idType", "Integer");
        dataMap.put("dao", "com.cupsc.dz.portal.biz.repository.bank");
        dataMap.put("service", "com.cupsc.dz.portal.biz.service");
        dataMap.put("controller", "com.cupsc.dz.portal.web.admin.controller");

        Main.out(configuration.getTemplate(templates[0]),dataMap.get("name")+"Controller.java",dataMap);
        Main.out(configuration.getTemplate(templates[1]),dataMap.get("name")+"Repository.java",dataMap);
        Main.out(configuration.getTemplate(templates[2]),dataMap.get("name")+"QueryRepository.java",dataMap);
        Main.out(configuration.getTemplate(templates[3]),dataMap.get("name")+"Service.java",dataMap);
        Main.out(configuration.getTemplate(templates[4]),dataMap.get("name")+"ServiceImpl.java",dataMap);

    }

    public static void out(Template template,String fileName,Map dataMap) throws Exception{
        // step5 生成数据
        File docFile = new File(PATH + fileName);
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
        // step6 输出文件
        template.process(dataMap, out);
    }
}
