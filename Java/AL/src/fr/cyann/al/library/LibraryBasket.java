/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.library;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryBasket<T extends Lib> implements Iterable<T> {

	private final File path;
	private final Class<T> clazz;
	private final List<T> libs;

	public LibraryBasket(Class<T> clazz, File path) {
		this.clazz = clazz;
		this.path = path;
		libs = new ArrayList<T>();

		if (path.isDirectory()) {
			loadLibs();
		} else {
			loadJar(path);
		}
	}

	private void loadLibs() {

		if (!path.exists()) {
			Logger.getLogger(LibraryBasket.class.getName()).log(Level.INFO, null, "Path does not exists !");
			return; // no plugins
		}

		for (File file : path.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".jar");
			}
		})) {
			loadJar(file);
		}

	}

	public final void loadJar(File file) {

		Enumeration enumeration;
		String classPath;
		URLClassLoader classLoader;

		if (file.exists()) {
			try {
				URL url = file.toURI().toURL();
				classLoader = new URLClassLoader(new URL[]{url});
				JarFile jar = new JarFile(file.getAbsoluteFile());
				enumeration = jar.entries();

				while (enumeration.hasMoreElements()) {
					classPath = enumeration.nextElement().toString();
					loadLib(classLoader, classPath);
				}

			} catch (MalformedURLException ex) {
				Logger.getLogger(LibraryBasket.class.getName()).log(Level.SEVERE, null, ex);
			} catch (Exception ex) {
				Logger.getLogger(LibraryBasket.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void loadLib(ClassLoader classLoader, String classPath) {

		String classExt = ".class";

		if (classPath.endsWith(classExt)) {
			try {

				String className = classPath.substring(0, classPath.length() - classExt.length());
				className = className.replace("/", ".");
				className = className.replace("\\", ".");

				Class cls = Class.forName(className, true, classLoader);

				for (Class inter : cls.getInterfaces()) {
					if (inter.equals(clazz)) {
						T lib = (T) cls.newInstance();

						SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
						String message = String.format("Plugin \"%s v%s\" created by %s the %s successfully loaded !", lib.getName(), lib.getVersion(), lib.getAuthor(), f.format(lib.getCreationDate()));
						System.out.println(message);

						libs.add(lib);
					}
				}

			} catch (ClassNotFoundException ex) {
				Logger.getLogger(LibraryBasket.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(LibraryBasket.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(LibraryBasket.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

	}

	@Override
	public Iterator<T> iterator() {
		return libs.iterator();
	}
}
