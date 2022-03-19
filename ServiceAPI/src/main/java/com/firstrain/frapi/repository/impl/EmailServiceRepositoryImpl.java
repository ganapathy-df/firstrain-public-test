package com.firstrain.frapi.repository.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.EmailScheduleDbAPI;
import com.firstrain.db.api.ItemsDbAPI;
import com.firstrain.db.api.MailDocsDbAPI;
import com.firstrain.db.api.MailLogDbAPI;
import com.firstrain.db.api.TemplateDbAPI;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.MailDocs;
import com.firstrain.db.obj.MailLog;
import com.firstrain.db.obj.Template;
import com.firstrain.frapi.repository.EmailServiceRepository;
import com.firstrain.frapi.util.ServicesAPIUtil;

@Service
@Qualifier("emailServiceRepositoryImpl")
public class EmailServiceRepositoryImpl extends EntityBaseServiceRepositoryImpl implements EmailServiceRepository {

	private final Logger LOG = Logger.getLogger(EmailServiceRepositoryImpl.class);
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;

	@Override
	public List<MailDocs> getEmailById(long emailId) throws Exception {

		Transaction txn = null;
		try {

			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			List<MailDocs> mailDocsList = MailDocsDbAPI.fetchEmailDocLogByMailId(emailId);

			// fallback on write DB
			if (mailDocsList == null || mailDocsList.isEmpty()) {
				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				mailDocsList = MailDocsDbAPI.fetchEmailDocLogByMailId(emailId);
				if (mailDocsList != null) {
					LOG.debug("Email " + emailId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("Email " + emailId + " not available in both Read/Write DBs");
				}
			}

			return mailDocsList;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}


	@Override
	public MailLog getEmailLogById(long emailId) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			MailLog mailLog = MailLogDbAPI.fetchUserEmailLogById(txn, emailId);

			// fallback on write DB
			if (mailLog == null) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				mailLog = MailLogDbAPI.fetchUserEmailLogById(txn, emailId);
				if (mailLog != null) {
					LOG.debug("Email " + emailId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("Email " + emailId + " not available in both Read/Write DBs");
				}
			}

			return mailLog;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public Items getSearchById(long searchId) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			Items items = ItemsDbAPI.getItemById(txn, searchId);

			// fallback on write DB
			if (items == null) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				items = ItemsDbAPI.getItemById(txn, searchId);
				if (items != null) {
					LOG.debug("Email item " + searchId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("Email item " + searchId + " not available in both Read/Write DBs");
				}
			}

			return items;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public List<MailLog> getMailLog(long scheduleId, Timestamp sentTime, int sentStatus) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			List<MailLog> mailLogs = MailLogDbAPI.fetchUserEmailLogByScheduleId(scheduleId, sentTime, sentStatus);

			// fallback on write DB
			if (mailLogs == null || mailLogs.isEmpty() || mailLogs.isEmpty()) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				mailLogs = MailLogDbAPI.fetchUserEmailLogByScheduleId(scheduleId, sentTime, sentStatus);
				if (mailLogs != null) {
					LOG.debug("MailLogs for scheudle " + scheduleId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("MailLogs for scheudle " + scheduleId + " not available in both Read/Write DBs");
				}
			}
			return mailLogs;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public EmailSchedule getEmailSchedule(long scheduleId) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			EmailSchedule emailSchedule = EmailScheduleDbAPI.fetchEmailScheduleById(txn, scheduleId);

			// fallback on write DB
			if (emailSchedule == null) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				emailSchedule = EmailScheduleDbAPI.fetchEmailScheduleById(txn, scheduleId);
				if (emailSchedule != null) {
					LOG.debug("EmailSchedule " + scheduleId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("EmailSchedule " + scheduleId + " not available in both Read/Write DBs");
				}
			}
			return emailSchedule;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public Template getTemplate(long templateID) throws Exception {
		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			Template template = TemplateDbAPI.getTemplateByTemplateID(templateID);

			// fallback on write DB
			if (template == null) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				template = TemplateDbAPI.getTemplateByTemplateID(templateID);
				if (template != null) {
					LOG.debug("EmailTemplate " + templateID + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("EmailTemplate " + templateID + " not available in both Read/Write DBs");
				}
			}
			return template;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

}
