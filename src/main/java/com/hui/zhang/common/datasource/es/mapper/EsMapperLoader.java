package com.hui.zhang.common.datasource.es.mapper;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by zhanghui on 2019-04-18.
 */
@Configuration
public class EsMapperLoader {
    private static final Logger logger = LoggerFactory.getLogger(EsMapperLoader.class);

    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    public EsMapperLoader(){

    }

    @Bean(name ="esMappperBean")
    public EsMappperBean initEsMappperBean() {
        EsMappperBean esMappperBean=new EsMappperBean();

        URL url = EsMapperLoader.class.getClassLoader().getResource("es/");
        if (null!=url){//有文件夹才能解析
            if (url.toString().startsWith("jar")){
                logger.info("parser from jar files");
                try {
                    String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
                    //logger.info("1.#####{}",jarPath);
                    URL jarURL = new URL(jarPath);
                    JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
                    JarFile jarFile = jarCon.getJarFile();
                    Enumeration<JarEntry> jarEntrys = jarFile.entries();
                    //logger.info("2.#####{}",jarEntrys.hasMoreElements());

                    while (jarEntrys.hasMoreElements()) {
                        JarEntry entry = jarEntrys.nextElement();
                        String name = entry.getName();
                        //logger.info("3.#####{}",name);

                        if (name.contains("/classes/es/") && !entry.isDirectory()) {
                            //logger.info("4.@@@@@@@",name);
                            //doWithInputStream(EsMapperLoader.class.getClassLoader().getResourceAsStream(name));
                            InputStream inputStream = EsMapperLoader.class.getClassLoader().getResourceAsStream(name);
                            this.initQuerConfig(esMappperBean,inputStream);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                logger.info("parser from local files");
                try {
                    Resource resource =  this.resourceLoader.getResource("classpath:es");
                    if(resource.exists()){
                        File file =resource.getFile(); //ResourceUtils.getFile("classpath:es");
                        if(file.exists()){
                            File[] files = file.listFiles();
                            if(files != null){
                                for(File childFile:files){
                                    InputStream inputStream=new FileInputStream(childFile);
                                    this.initQuerConfig(esMappperBean,inputStream);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return esMappperBean;
    }

    private void initQuerConfig( EsMappperBean esMappperBean,InputStream inputStream){
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            String namespace=root.attributeValue("namespace");
            List<Element> childElements = root.elements();
            for (Element child : childElements) {
                String elmName=child.getName();
                if (elmName.equals("query")){
                    String queryId = child.attributeValue("id");
                    String keyId=namespace+"."+queryId;
                    if (null!=esMappperBean.getQueryElement(keyId)){
                        throw  new RuntimeException("es mapper load error ,keyId:"+keyId+" has same key name");
                    }
                    esMappperBean.putQueryElement(keyId,child);
                    logger.info("init es mapper keyId:{}",keyId);
                }
            }
        }catch (Exception e){
            logger.error("load es mapper error!:{}",e.getMessage());
            e.printStackTrace();
        }

    }

}
