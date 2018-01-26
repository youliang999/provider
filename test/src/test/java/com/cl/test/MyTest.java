package com.cl.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cl.user.servicei.UserService;

public class MyTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "spring/*.xml" });
		UserService service = (UserService) context.getBean("userService");
		for (int i = 0; i < 100; i++) {
			System.out.println(service.sayHello("111"));
			Thread.sleep(2000);
		}
		
	}
}
