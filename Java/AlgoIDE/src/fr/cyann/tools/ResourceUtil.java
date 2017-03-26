/* 
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author caronyn
 */
public class ResourceUtil {

	private static void addFileListRecursive(List<File> list, File dir) {
		for (File sub : dir.listFiles()) {
			if (sub.isDirectory()) {
				addFileListRecursive(list, sub);
			} else {
				list.add(sub);
			}
		}
	}

	public static List<String> getResources(Class<?> clazz, String directory, String regex) throws URISyntaxException, UnsupportedEncodingException, IOException {
		List<String> list = new ArrayList<String>();

		// initialize
		URL dirURL = clazz.getClassLoader().getResource(directory);
		Pattern pattern = Pattern.compile(directory + regex);

		if (dirURL == null) throw new IOException("Directory \"" + directory + "\" does not exists !");

		if (dirURL.getProtocol().equals("file")) {

			List<File> files = new ArrayList<File>();
			addFileListRecursive(files, new File(dirURL.toURI()));

			for (File file : files) {
				String name = file.getPath();
				name = name.substring(name.indexOf("classes") + 8).replace("\\", "/");
				if (pattern.matcher(name).matches()) {
					list.add(name);
				}
			}


		} else if (dirURL.getProtocol().equals("jar")) {

			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				String file = entries.nextElement().getName();

				if (pattern.matcher(file).matches() && !file.endsWith("/")) {
					list.add(file);
				}

			}

		}

		return list;
	}
}
