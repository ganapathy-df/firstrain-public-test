package com.firstrain.web.service.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.pojo.AuthAPIResponse;

@Service
public class AuthKeyCacheManager {

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Value("${authkeycache.max.entries}")
	private int maxEntries;

	private Map<String, AuthAPIResponse> keyVsAuthObject = new ConcurrentHashMap<String, AuthAPIResponse>();
	private Object lock = new Object();

	public AuthAPIResponse getAuthKey(String key) {
		AuthAPIResponse authKey = this.keyVsAuthObject.get(key);
		return authKey;
	}

	public void setAuthKey(AuthAPIResponse authKeyRes) {
		this.keyVsAuthObject.put(authKeyRes.getAuthKey(), authKeyRes);
		checkSessionMapSize();
	}

	private void checkSessionMapSize() {
		if (keyVsAuthObject.size() < maxEntries) {
			return;
		}
		taskExecutor.submit(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					if (keyVsAuthObject.size() < maxEntries) {
						return;
					}
					List<AuthAPIResponse> list = new ArrayList<AuthAPIResponse>(keyVsAuthObject.values());
					Collections.sort(list, new Comparator<AuthAPIResponse>() {
						@Override
						public int compare(AuthAPIResponse o1, AuthAPIResponse o2) {
							return -o1.getExpiryTime().compareTo(o2.getExpiryTime());
						}
					});
					// remove eldest 10 or all expired entries
					int removed = 0;
					for (AuthAPIResponse authRes : list) {
						if (removed++ < 10) {
							keyVsAuthObject.remove(authRes.getAuthKey());
						} else {
							break;
						}
					}
				}
			}
		});
	}
}
