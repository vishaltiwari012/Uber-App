package com.codingworld.project.uber.uberApp;

import com.codingworld.project.uber.uberApp.services.EmailSenderService;
import com.codingworld.project.uber.uberApp.services.impl.EmailSenderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UberAppApplicationTests {
	@Autowired
	private EmailSenderService emailSenderService;

	@Test
	public void sendEmail() {
		emailSenderService.sendEmail("nalobi9411@ndiety.com", "This is testing email", "Hii, From uber app");
	}

	@Test
	public void sendEmailMultiple() {
		String emails[] = {
				"nalobi9411@ndiety.com",
				"vishaltiwari9267@gmail.com"
		};
		emailSenderService.sendEmail(emails, "This is testing email", "Hii, From uber app");
	}

	@Test
	void contextLoads() {
	}

}
