package com.th.learningenglish.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("email_cua_ban@gmail.com");
		message.setTo(toEmail);
		message.setSubject("Yêu cầu khôi phục mật khẩu - Learn English Web");
		message.setText("Mã OTP khôi phục mật khẩu của bạn là: " + otp + ". Mã này có hiệu lực trong 5 phút.");

		mailSender.send(message);
	}

	public void sendResetLink(String toEmail, String link) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Khôi phục mật khẩu - Learn English Web");
		message.setText("Click vào liên kết để đặt lại mật khẩu (hết hạn trong 5 phút): " + link);

		mailSender.send(message);
	}
}