package com.firstrain.frapi.repository;


import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.MailDocs;
import com.firstrain.db.obj.MailLog;
import com.firstrain.db.obj.Template;


@Service
public interface EmailServiceRepository {

	public List<MailDocs> getEmailById(long emailId) throws Exception;

	public Items getSearchById(long searchId) throws Exception;

	public MailLog getEmailLogById(long emailId) throws Exception;

	public List<MailLog> getMailLog(long scheduleId, Timestamp sentTime, int sentStatus) throws Exception;

	public EmailSchedule getEmailSchedule(long scheduleId) throws Exception;

	public Template getTemplate(long templateID) throws Exception;

}
