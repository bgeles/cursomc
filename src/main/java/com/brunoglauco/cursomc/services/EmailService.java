package com.brunoglauco.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.brunoglauco.cursomc.domain.Categoria;

public interface EmailService {

	void sendOrderConfirmationEmail(Categoria obj);
	
	void sendEmail(SimpleMailMessage msg);
	
}

