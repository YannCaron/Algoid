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

import fr.cyann.algoide.AlgoIDEConstant;
import fr.cyann.algoide.runtime.TaskManager;
import static fr.cyann.algoide.runtime.TaskManager.ERR_STACK_OVERFLOW;
import fr.cyann.jasi.exception.MultilingMessage;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * The FileTools class. Creation date: 27 mai 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public final class FileTools implements AlgoIDEConstant {

    public static final String RESSOURCE_SRC_DEFAULT_PATH = "src";
    public static final String RESSOURCE_IMAGE_DEFAULT_PATH = "img";
    public static final String RESSOURCE_IMAGE_DEFAULT_EXT = ".png";

    private static String currentPath;

    private FileTools() {
    }

    public static String readFile(BufferedReader reader) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line = null;
            //String ls = System.getProperty("line.separator");
            String ls = "\n";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            return stringBuilder.toString();
        }
    }

    public static String readFile(File file) {
        try {
            currentPath = file.getParent();
            return readFile(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public static String readFile(InputStream is) {
        return readFile(new BufferedReader(new InputStreamReader(is)));
    }

    public static void saveFile(File file, String source) {
        try {
            currentPath = file.getParent();
            Writer writer = new BufferedWriter(new FileWriter(file));
            saveFile(writer, source);
        } catch (IOException ex) {
            Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveFile(Writer writer, String source) {
        try {
            writer.write(source);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static File getCurrentFile() {
        File file = Paths.get(CURRENT_FILE).toFile();
        currentPath = file.getParent();
        if (file.exists()) {
            return file;
        }

        return null;
    }

    public static void saveToCurrentFile(String source) {
        File file = Paths.get(CURRENT_FILE).toFile();
        try {

            if (!file.exists()) {
                file.createNewFile();
            }
            saveFile(file, source);

        } catch (IOException ex) {
            Logger.getLogger(FileTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static File ensureExtension(File file) {
        if (file.getName().endsWith(FileTools.AL_EXTENSION)) {
            return file;
        } else {
            return new File(file.getPath() + FileTools.AL_EXTENSION);
        }
    }

    public static File findSubRessource(String path, String res, String fileName, String ext) {
        File expected;

        // simple
        expected = new File(path + File.separator + fileName);
        if (expected.exists()) {
            return expected;
        }
        expected = new File(path + File.separator + fileName + ext);
        if (expected.exists()) {
            return expected;
        }

        // ressource
        expected = new File(path + File.separator + res + File.separator + fileName);
        if (expected.exists()) {
            return expected;
        }
        expected = new File(path + File.separator + res + File.separator + fileName + ext);
        if (expected.exists()) {
            return expected;
        }

        // ressource double
        expected = new File(path + File.separator + res + File.separator + res + File.separator + fileName);
        if (expected.exists()) {
            return expected;
        }
        expected = new File(path + File.separator + res + File.separator + res + File.separator + fileName + ext);
        if (expected.exists()) {
            return expected;
        }

        // src / ressource
        expected = new File(path + File.separator + RESSOURCE_SRC_DEFAULT_PATH + File.separator + res + File.separator + fileName);
        if (expected.exists()) {
            return expected;
        }
        expected = new File(path + File.separator + RESSOURCE_SRC_DEFAULT_PATH + File.separator + res + File.separator + fileName + ext);
        if (expected.exists()) {
            return expected;
        }

        return null;
    }

    public static File findRessource(String fileName, String res, String ext) {

        File expected;
        expected = findSubRessource(currentPath, res, fileName, ext);
        if (expected != null) {
            return expected;
        }

        expected = findSubRessource(System.getProperty("user.dir"), res, fileName, ext);
        if (expected != null) {
            return expected;
        }

        expected = findSubRessource(System.getProperty("user.home"), res, fileName, ext);
        if (expected != null) {
            return expected;
        }

        return null;
    }

    public static BufferedImage loadImgRessource(String fileName) throws IOException {
        File file = findRessource(fileName, RESSOURCE_IMAGE_DEFAULT_PATH, RESSOURCE_IMAGE_DEFAULT_EXT);
        if (file != null) {
            return ImageIO.read(file);
        }

        throw new IOException("File not found!");
    }

    public static void copyRessources() {
        File imgRessource = new File(System.getProperty("user.dir") + File.separator + RESSOURCE_IMAGE_DEFAULT_PATH);
        if (!imgRessource.exists()) {
            imgRessource.mkdirs();
            FileUtils.copyResourcesRecursively(FileTools.class.getResource(File.separator + RESSOURCE_IMAGE_DEFAULT_PATH), imgRessource);

            Logger.getLogger(TaskManager.class.getName()).log(Level.WARNING, new MultilingMessage("Image ressource files copied to [%s] !", "Fichier de ressources images copiés dans [%s] !").setArgs(imgRessource).toString());
        }
    }

}
