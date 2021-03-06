/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:47:34 GMT 2018
 */

package com.firstrain.frapi.repository.impl;

import com.firstrain.db.obj.Users;
import com.firstrain.frapi.util.FreemarkerTemplates;
import com.firstrain.frapi.util.MailUtil;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;


public class UserServiceRepositoryImplESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test0() throws Exception {
		UserServiceRepositoryImpl userServiceRepositoryImpl0 = new UserServiceRepositoryImpl();
		MailUtil mailUtil0 = new MailUtil();
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		Injector.inject(mailUtil0, com.firstrain.frapi.util.MailUtil.class, "freemarkerTemplates",
				freemarkerTemplates0);
		Injector.validateBean(mailUtil0, com.firstrain.frapi.util.MailUtil.class);
		Injector
				.inject(userServiceRepositoryImpl0,
						com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class, "mailUtil",
						mailUtil0);
		Injector.validateBean(userServiceRepositoryImpl0,
				com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class);
		Users users0 = new Users();
		userServiceRepositoryImpl0.updateUser(users0, "#Sl.7f(rlR_o!JTk", "#Sl.7f(rlR_o!JTk");
	}

	@Test(timeout = 4000, expected = IllegalStateException.class)
	public void test1() throws Exception {
		UserServiceRepositoryImpl userServiceRepositoryImpl0 = new UserServiceRepositoryImpl();
		MailUtil mailUtil0 = new MailUtil();
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		Injector.inject(mailUtil0, com.firstrain.frapi.util.MailUtil.class, "freemarkerTemplates",
				freemarkerTemplates0);
		Injector.validateBean(mailUtil0, com.firstrain.frapi.util.MailUtil.class);
		Injector
				.inject(userServiceRepositoryImpl0,
						com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class, "mailUtil",
						mailUtil0);
		Injector.validateBean(userServiceRepositoryImpl0,
				com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class);
		Users users0 = new Users();
		userServiceRepositoryImpl0.sendActivationMail(users0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test2() throws Exception {
		UserServiceRepositoryImpl userServiceRepositoryImpl0 = new UserServiceRepositoryImpl();
		MailUtil mailUtil0 = new MailUtil();
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		Injector.inject(mailUtil0, com.firstrain.frapi.util.MailUtil.class, "freemarkerTemplates",
				freemarkerTemplates0);
		Injector.validateBean(mailUtil0, com.firstrain.frapi.util.MailUtil.class);
		Injector
				.inject(userServiceRepositoryImpl0,
						com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class, "mailUtil",
						mailUtil0);
		Injector.validateBean(userServiceRepositoryImpl0,
				com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class);
		userServiceRepositoryImpl0.getUserById(0L);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test3() throws Exception {
		UserServiceRepositoryImpl userServiceRepositoryImpl0 = new UserServiceRepositoryImpl();
		MailUtil mailUtil0 = new MailUtil();
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		Injector.inject(mailUtil0, com.firstrain.frapi.util.MailUtil.class, "freemarkerTemplates",
				freemarkerTemplates0);
		Injector.validateBean(mailUtil0, com.firstrain.frapi.util.MailUtil.class);
		Injector
				.inject(userServiceRepositoryImpl0,
						com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class, "mailUtil",
						mailUtil0);
		Injector.validateBean(userServiceRepositoryImpl0,
				com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class);
		userServiceRepositoryImpl0.deleteUser((-1L));
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test4() throws Exception {
		UserServiceRepositoryImpl userServiceRepositoryImpl0 = new UserServiceRepositoryImpl();
		MailUtil mailUtil0 = new MailUtil();
		FreemarkerTemplates freemarkerTemplates0 = new FreemarkerTemplates();
		Injector.inject(mailUtil0, com.firstrain.frapi.util.MailUtil.class, "freemarkerTemplates",
				freemarkerTemplates0);
		Injector.validateBean(mailUtil0, com.firstrain.frapi.util.MailUtil.class);
		Injector
				.inject(userServiceRepositoryImpl0,
						com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class, "mailUtil",
						mailUtil0);
		Injector.validateBean(userServiceRepositoryImpl0,
				com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl.class);
		Users users0 = new Users();
		userServiceRepositoryImpl0.createUser(users0, users0, "~kebO^J?X!Je");
	}
}
