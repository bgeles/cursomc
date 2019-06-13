package com.brunoglauco.cursomc.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

import com.brunoglauco.cursomc.domain.Categoria;

public interface EmailService {

	void sendOrderConfirmationEmail(Categoria obj);
	
	void sendEmail(SimpleMailMessage msg);
	
	void sendOrderConfirmationHtmlEmail(Categoria obj);
	
	void sendHtmlEmail(MimeMessage msg);
	
}

