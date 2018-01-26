package com.youliang.service;

import com.youliang.enums.FileTypeEnum;
import com.youliang.model.EImail;
import com.youliang.model.SendResult;
import com.youliang.model.Sender;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

/**
 * Created by youliang.cheng on 2018/1/22.
 */
@Service("mailService")
public class MailServiceImpl implements MailService {
    private static final Logger LOG = Logger.getLogger(MailServiceImpl.class);
    public static String myEmailSMTPHost = "smtp.yeah.net";

    @Override
    public SendResult sendMail(Sender sender, EImail eImail) {
        LOG.info("===>>> sender:" +sender + "get a request: "+eImail);
        SendResult result = new SendResult();
        if(sender == null) {
            result.setResult(false);
            result.setErrMsg("Sender is null.");
            return result;
        }
        if(eImail == null) {
            result.setResult(false);
            result.setErrMsg("EImail is null.");
            return result;
        }
        Properties props = setMailProperties();
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(false);
        MimeMessage message = null;
        try {
            message = createMimeMessage(session, sender, eImail);
            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();
            transport.connect(sender.getSender(), sender.getPassword());
            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            //transport.sendMessage(message, message.getAllRecipients());
            result.setResult(true);
            // 7. 关闭连接
            transport.close();
        } catch (Exception e) {
            result.setResult(false);
            result.setErrMsg(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String testMsg(String msg) {
        System.out.println("get a msg:" + msg);
        return "hi, get msg:" + msg;
    }


    private Properties setMailProperties() {
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        return props;
    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sender
     * @param eImail
     * @return
     * @throws Exception
     */
    private static MimeMessage createMimeMessage(Session session, Sender sender, EImail eImail) throws Exception {

        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）

        message.setFrom(new InternetAddress(sender.getSender(), sender.getNickName(), "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        if(eImail.getReceiver().size() > 1) {
            int max = eImail.getReceiver().size();
            InternetAddress[] its = new InternetAddress[max];
            for(int i=0; i < max; i++ ) {
                String receiver = eImail.getReceiver().get(i);
                its[i] = new InternetAddress(receiver, receiver, "UTF-8");
                System.out.println(i+": receiver:"+receiver);
            }
            message.setRecipients(MimeMessage.RecipientType.TO, its);
        } else {
            if(eImail.getReceiver().get(0) != null)
                message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(eImail.getReceiver().get(0), eImail.getReceiver().get(0), "UTF-8"));
        }

        // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
        message.setSubject(eImail.getTopic(), "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
        //message.setContent("XX用户你好, 今天全场5折, 快来抢购, 错过今天再等一年。。。", "text/html;charset=UTF-8");

        // 创建图片的一个表示用于显示在邮件中显示
        // 创建邮件的正文
        MimeBodyPart text = new MimeBodyPart();
        MimeMultipart mm = new MimeMultipart("mixed");
        // setContent(“邮件的正文内容”,”设置邮件内容的编码方式”)
/*        text.setContent(eImail.getContent(),
                "text/html;charset=gb2312");*/
        StringBuffer picStr = new StringBuffer();
        if(eImail.getFiles().size() > 1 ) {
            int max = eImail.getFiles().size();
            for(int i=0; i<max; i++) {
                if(eImail.getFiles().get(i).getFileType() == FileTypeEnum.PIC &&
                        eImail.getFiles().get(i).isShow()) {
                    picStr.append("<img src=\"cid:"+ eImail.getFiles().get(i).getFileName() +"\">");
                }
                // 创建图片
                MimeBodyPart img = new MimeBodyPart();
                /*
                 * JavaMail API不限制信息只为文本,任何形式的信息都可能作茧自缚MimeMessage的一部分.
                 * 除了文本信息,作为文件附件包含在电子邮件信息的一部分是很普遍的. JavaMail
                 * API通过使用DataHandler对象,提供一个允许我们包含非文本BodyPart对象的简便方法.
                 */
                DataHandler dh = new DataHandler(new FileDataSource(eImail.getFiles().get(i).getFilePath()));//图片路径
                img.setDataHandler(dh);
                img.setContentID(eImail.getFiles().get(i).getFileName());
                img.setFileName(MimeUtility.encodeText(eImail.getFiles().get(i).getFileName()));
                mm.addBodyPart(img);
            }
            text.setContent("<h1>"+ eImail.getContent() +"</h1>"
                            + picStr,
                    "text/html;charset=gb2312");
            mm.addBodyPart(text);
        } else {
            MimeBodyPart img = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource(eImail.getFiles().get(0).getFilePath()));//图片路径
            if(eImail.getFiles().get(0) != null && eImail.getFiles().get(0).getFileType() == FileTypeEnum.PIC
                    && eImail.getFiles().get(0).isShow()) {

                picStr.append("<img src=\"cid:"+ eImail.getFiles().get(0).getFileName() +"\">");
                // 创建图片
                img.setDataHandler(dh);
                img.setContentID(eImail.getFiles().get(0).getFileName());
                img.setFileName(MimeUtility.encodeText(eImail.getFiles().get(0).getFileName()));
            }

            /*
             * JavaMail API不限制信息只为文本,任何形式的信息都可能作茧自缚MimeMessage的一部分.
             * 除了文本信息,作为文件附件包含在电子邮件信息的一部分是很普遍的. JavaMail
             * API通过使用DataHandler对象,提供一个允许我们包含非文本BodyPart对象的简便方法.
             */
            text.setContent("<h1>"+ eImail.getContent() +"</h1>"
                            + picStr,
                    "text/html;charset=gb2312");
            mm.addBodyPart(text);
            mm.addBodyPart(img);
        }
        //mm.setSubType("related");// 设置正文与图片之间的关系
        // 图班与正文的 body
        MimeBodyPart all = new MimeBodyPart();
        all.setContent(mm);
        // 附件与正文（text 和 img）的关系
        MimeMultipart mm2 = new MimeMultipart();
        mm2.addBodyPart(all);
        //mm2.setSubType("mixed");// 设置正文与附件之间的关系
        message.setContent(mm2);
        message.saveChanges(); // 保存修改
        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
}
