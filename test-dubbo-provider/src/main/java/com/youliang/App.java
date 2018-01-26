package com.youliang;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App {
    public static final App instance = new App();
    public static final String serverName = "serverName";

        public static void main(String[] args) throws Exception {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:dubbo-provider.xml");
            context.start();
            System.out.println("====================================");
            Properties senderPrope = loadSenderPrope();
            System.out.println(senderPrope.getProperty(serverName) + " started.");
            System.in.read();
        }


    private static Properties loadSenderPrope() {
        Properties pro = new Properties();
        InputStream in;
        try {
            in = instance.getClass().getClassLoader().getResourceAsStream("dubbo.properties");
            pro.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pro;
    }
}
