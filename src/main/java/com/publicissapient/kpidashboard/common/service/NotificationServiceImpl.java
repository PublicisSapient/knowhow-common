package com.publicissapient.kpidashboard.common.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

import com.knowhow.retro.notifications.model.EmailEvent;
import com.knowhow.retro.notifications.producer.EmailProducer;
import com.knowhow.retro.notifications.utils.TemplateParserHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring6.SpringTemplateEngine;


import com.publicissapient.kpidashboard.common.model.application.EmailServerDetail;
import com.publicissapient.kpidashboard.common.model.application.GlobalConfig;

import com.publicissapient.kpidashboard.common.repository.application.GlobalConfigRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final GlobalConfigRepository globalConfigRepository;

	private final TemplateParserHelper templateParserHelper;

	private final SpringTemplateEngine templateEngine;

	private final ObjectProvider<EmailProducer> emailProducer;

	@Override
	public void sendNotificationEvent(List<String> emailAddresses, Map<String, String> customData, String notSubject,
			boolean notificationSwitch, String templateKey) {

		if (StringUtils.isNotBlank(notSubject)) {
			EmailServerDetail emailServerDetail = getEmailServerDetail();
			if (emailServerDetail != null) {
				EmailEvent emailEvent = new EmailEvent(emailServerDetail.getFromEmail(), emailAddresses, null, null,
						notSubject, null, customData, emailServerDetail.getEmailHost(),
						emailServerDetail.getEmailPort());
				EmailProducer emailProvider = emailProducer.getIfAvailable();
				if (emailProvider != null) {
                    String fileName = "templates/" + templateKey;
                    emailEvent.setBody(templateParserHelper.getContentFromFile(fileName));
                    emailProvider.sendEmail(emailEvent);
					emailProvider.sendEmail(emailEvent);
				} else {
					sentFromJavaMail(emailEvent, notificationSwitch,templateKey);
				}
			} else {
				log.error("Notification Event not sent : notification emailServer Details not found in db");
			}
		} else {
			log.error("Notification Event not sent : notification subject for {} not found in properties file",
					notSubject);
		}

	}

	public void sentFromJavaMail(EmailEvent emailEvent, boolean notificationSwitch, String templateKey) {
		if (notificationSwitch) {
			JavaMailSenderImpl javaMailSender = getJavaMailSender(emailEvent);
			MimeMessage message = javaMailSender.createMimeMessage();
			try {
				sentMailViaJavaMail(templateKey, emailEvent, javaMailSender, message);
			} catch (MessagingException me) {
				log.error("Email not sent for the key : {}", templateKey);
			} catch (TemplateInputException tie) {
				log.error("Template not found for the key : {}", templateKey);
				throw new RecoverableDataAccessException("Template not found for the key :" + templateKey);
			} catch (TemplateProcessingException tpe) {
				throw new RecoverableDataAccessException("Template not parsed for the key :" + templateKey);
			}

		} else {
			log.info(
					"Notification Switch is Off. If want to send notification set true for notification.switch in property");
		}
	}

	private void sentMailViaJavaMail(String templateKey, EmailEvent emailEvent, JavaMailSenderImpl javaMailSender,
			MimeMessage message) throws MessagingException {
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());
		Context context = new Context();
		Map<String, String> customData = emailEvent.getCustomData();
		if (MapUtils.isNotEmpty(customData)) {
			customData.forEach((k, value) -> {
				BiConsumer<String, String> setVariable = context::setVariable;
				setVariable.accept(k, value);
			});
		}
		String html = templateEngine.process(templateKey, context);
		if (StringUtils.isNotEmpty(html)) {
			helper.setTo(emailEvent.getTo().stream().toArray(String[]::new));
			helper.setText(html, true);
			helper.setSubject(emailEvent.getSubject());
			helper.setFrom(emailEvent.getFrom());
			addAttachments(customData, helper);
			javaMailSender.send(message);
			log.info("Email successfully sent for the key : {}", templateKey);
		}
	}

	private void addAttachments(Map<String, String> customData, MimeMessageHelper helper) throws MessagingException {
		if (customData.containsKey("pdf_attachment")) {
			String base64Pdf = customData.get("pdf_attachment");
			byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
			helper.addAttachment("KnowHOW.pdf", new ByteArrayResource(pdfBytes));
		}
	}

	private EmailServerDetail getEmailServerDetail() {
		List<GlobalConfig> globalConfigs = globalConfigRepository.findAll();
		GlobalConfig globalConfig = CollectionUtils.isEmpty(globalConfigs) ? null : globalConfigs.get(0);
		return globalConfig == null ? null : globalConfig.getEmailServerDetail();
	}

	private JavaMailSenderImpl getJavaMailSender(EmailEvent emailEvent) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailEvent.getEmailHost());
		mailSender.setPort(emailEvent.getEmailPort());
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.debug", "true");
		props.put("mail.smtp.ssl.checkserveridentity", "false");
		return mailSender;
	}
}
