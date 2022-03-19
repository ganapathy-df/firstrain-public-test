package com.firstrain.test.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PojoTestUtil {

	private PojoTestUtil() {
		throw new AssertionError();
	}

	public static List<Class> getAllClasses(String pkgDir) {
		try {
			File directory = getPackageHandle(pkgDir);

			if (directory.exists()) {
				return getClassesFromPackage(pkgDir, directory);
			} else {
				return Collections.emptyList();
			}
		} catch (ClassNotFoundException e) {
			return Collections.emptyList();
		}
	}

	private static List<Class> getClassesFromPackage(String pkgDirParam, File directory) throws ClassNotFoundException {
		//List<Class> classes = new ArrayList<>();
		String pkgDir = pkgDirParam;
		List<Class> classes = new ArrayList();
		String[] files = directory.list();
		for (String file : files) {
			pkgDir = pkgDir.replace('/', '.');
			if (isTopLevelClass(file)) {
				classes.add(Class.forName(pkgDir + '.' + stripClassExtension(file)));
			}
		}
		return classes;
	}

	private static File getPackageHandle(String pkgDir) throws ClassNotFoundException {
		File directory = null;
		try {
			directory = new File(Thread.currentThread().getContextClassLoader().getResource(pkgDir).getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pkgDir + " does not appear to be a valid package", x);
		}
		return directory;
	}

	private static boolean isTopLevelClass(String file) {
		return file.endsWith(".class") && !(file.contains("$") || (file.substring(0, file.indexOf(".class")).endsWith("Test")));
	}

	private static String stripClassExtension(String file){
		return file.substring(0, file.length() - 6);
	}
}
