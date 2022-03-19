package com.firstrain.frapi.repository.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.FRAPIAuthDbAPI;
import com.firstrain.db.obj.APIAccount;
import com.firstrain.db.obj.APIAuthKey;
import com.firstrain.db.obj.APIDefinition;
import com.firstrain.frapi.repository.AuthServiceRepository;

@Repository
public class AuthServiceRepositoryImpl implements AuthServiceRepository {

	private final Logger LOG = Logger.getLogger(AuthServiceRepositoryImpl.class);
	private final int MULTI_HASH_COUNT = 5;
	private SecureRandom sr = null;

	@PostConstruct
	public void init() throws Exception {

		try {
			sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (Exception e) {
			LOG.error("Could not initialize java.security.SecureRandom instance", e);
			throw e;
		}
	}

	@Override
	public APIAccount getAccount(String authName, String authPasswordParam, String apiVersion) throws Exception {

		String authPassword = authPasswordParam;
		APIAccount apiAccount = FRAPIAuthDbAPI.getAccountDetails(authName, apiVersion);

		if (apiAccount != null) {
			LOG.debug("API account salt :" + apiAccount.getSalt());
			authPassword = getEncryptedPassword(authPassword, apiAccount.getSalt());
			LOG.debug("encrypted password :" + authPassword);
			if (!apiAccount.getAuthPassword().equals(authPassword)) {
				LOG.debug("passwords not equal");
				return null;
			}
		}
		return apiAccount;
	}

	private String getEncryptedPassword(String authPassword, String saltKey) throws Exception {

		byte[] btPass = doDigest(saltKey, authPassword, MULTI_HASH_COUNT);

		return Base64.encodeBase64String(btPass);
	}

	@Override
	public void persistAuthKey(APIAuthKey apiAuthKey) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			FRAPIAuthDbAPI.persistAuthKey(txn, apiAuthKey);
			txn.commit();
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public APIAuthKey getAuthKeyDetails(String authKey) {
		return FRAPIAuthDbAPI.getAuthKeyDetails(authKey);
	}

	@Override
	public APIAccount getAccountById(long accountId) throws Exception {
		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			return FRAPIAuthDbAPI.getAccountById(txn, accountId);
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public String generateAuthKey() {

		byte[] authKeyBytes = new byte[32];
		sr.nextBytes(authKeyBytes);
		return Hex.encodeHexString(authKeyBytes);
	}

	public static void main(String args[]) {
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");

			String authPassword = "changeme";

			byte[] salt = new byte[32];
			sr.nextBytes(salt);
			String encodedSalt = Base64.encodeBase64String(salt);
			System.out.println("salt:" + encodedSalt);

			byte[] btPass = doDigest(encodedSalt, authPassword, 5);

			String pwd1 = Base64.encodeBase64String(btPass);
			System.out.println("enc pwd:" + pwd1);



			// digest.reset();
			// digest.update(encodedSalt.getBytes());
			//
			// btPass = digest.digest(authPassword.getBytes("UTF-8"));
			//
			// for (int i = 0; i < 5; i++) {
			// digest.reset();
			// btPass = digest.digest(btPass);
			// }
			// String pwd2 = Base64.encodeBase64String(btPass);
			// System.out.println("enc pwd again:" + pwd2);
			//
			// if(pwd1.equals(pwd2)){
			// System.out.println("true");
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static byte[] doDigest(final String encodedSalt, final String authPassword, final int count) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(encodedSalt.getBytes());
		
		byte[] btPass = digest.digest(authPassword.getBytes("UTF-8"));
		
		for (int i = 0; i < count; i++) {
			digest.reset();
			btPass = digest.digest(btPass);
		}
		return btPass;
	}

	@Override
	public List<APIDefinition> getAPIDefinitionList() {
		return FRAPIAuthDbAPI.getAPIDefinitionList();
	}
}
