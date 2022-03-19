package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.EmailResponse;

@Service
public interface EmailService extends FRService {

	public EmailResponse getEmailDetails(User user, long monitorIdemailIdL) throws Exception;
}
