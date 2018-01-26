package com.cl.user.serviceImpl;

import com.cl.user.servicei.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("testUserService")
@Component
public class UserServiceImpl implements UserService{
	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
	public String sayHello(String  id) {
		LOG.info("hello world----------------------------");
		StringBuffer sb=new StringBuffer();;
		for (int i = 0; i < 10; i++) {
			sb=sb.append("hello world-->"+i+"===="+id+"\n");
		}
		return sb.toString();
	}

	public String test(int a,int b) {
		return (a+b)+"";
	}

	public String test2() {
		String str="hello dubbo";
		return str;
	}

	public String login(String username, String password) {
		if ("cyl".equals(username)&&password.equals("111")) {
			LOG.info(username +" 登录成功");
            return "loginSuccess";
        }else{
        	return "loginError";
        }
	}
}
