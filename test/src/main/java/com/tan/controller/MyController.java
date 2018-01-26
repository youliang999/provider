package com.tan.controller;


import com.cl.user.servicei.UserService;
import com.youliang.enums.FileTypeEnum;
import com.youliang.model.EImail;
import com.youliang.model.MailFiles;
import com.youliang.model.SendResult;
import com.youliang.model.Sender;
import com.youliang.service.MailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@Controller
public class MyController {
	private static final Logger LOG = Logger.getLogger(MyController.class);
	public static final MyController instance = new MyController();
	public static final String senderMail = "emailAccount";
	public static final String senderMailPwd = "emailPassword";
	public static final String nickName = "MyMailServer";
	// 收件人邮箱（替换为自己知道的有效邮箱）
	public static final String receiveMailAccount = "youliang.cheng@dajie-inc.com";

	@Resource(name="testUserService")
	private UserService userService;

	@Autowired
	@Qualifier("mailService")
	private MailService mailService;

	@RequestMapping(method=RequestMethod.GET, value="/test/{id}",produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String say(@PathVariable String id){
		return userService.sayHello("4");
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value="/test1/{id}",produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String test(@PathVariable String id){
		return userService.test(2,3);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/test2",produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String test2(){
		return userService.test2();
	}
	
	@RequestMapping("login.do")
	@ResponseBody
    public ModelAndView login(String username,String password){
		String result=userService.login(username, password);
		LOG.info("===>登陆结果："+result);
		MailFiles files = new MailFiles();
		files.setFilePath("D:\\test20171124.jpg");
		files.setFileName("test20171124.jpg");
		files.setFileType(FileTypeEnum.PIC);
		files.setShow(true);
		Properties senderPrope = loadSenderPrope();
		Sender s = new Sender(senderPrope.getProperty(senderMail), nickName, senderPrope.getProperty(senderMailPwd));
		EImail e = new EImail("testMail", Arrays.asList(receiveMailAccount),
				"~~~~~~~~~~~~~~~~~message~~~~~~~~~~~~~~~~", Arrays.asList(files));

		//SendResult result1 = mailService.sendMail(s, e);

/*		System.out.println("===>>> send...");
		if(result1.isResult()) {
			LOG.info("邮件发送成功!");
		} else {
			LOG.info("err: "+result1.getErrMsg());
		}*/
        return new ModelAndView(result);
    }

	private static Properties loadSenderPrope() {
		Properties pro = new Properties();
		InputStream in;
		try {
			in = instance.getClass().getClassLoader().getResourceAsStream("sender.properties");
			pro.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pro;
	}
}
