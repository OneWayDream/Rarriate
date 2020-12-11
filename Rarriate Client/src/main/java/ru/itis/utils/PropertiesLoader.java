package ru.itis.utils;

import ru.itis.exceptions.PropertiesFileNotFoundException;
import ru.itis.start.RarriateStart;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesLoader {

    private static PropertiesLoader instance;
    private Properties properties;
    private String path;

    private PropertiesLoader() {
        properties = new Properties();
        path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Rarriate!\\app.properties";

        try {
            if (!Files.exists(Paths.get(path))){
                createProperties();
            } else {
                properties.load(new FileInputStream(new File(path)));
            }
        } catch (IOException e) {
            RarriateStart.showError(new PropertiesFileNotFoundException("File with properties not found", e));
        }
    }

    public static PropertiesLoader getInstance() {
        if (instance == null) {
            instance = new PropertiesLoader();
        }
        return instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
            properties.setProperty(key, value);
            properties.store(fileOutputStream, null);
        } catch (IOException e) {
            RarriateStart.showError(e);
        }
    }

    private void createProperties() throws IOException{
        if (!Files.exists(Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Rarriate!"))) {
            Files.createDirectory(Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Rarriate!"));
        }
        Files.createFile(Paths.get(path));
        properties.load(new FileInputStream(new File(path)));

        properties.setProperty("WINDOW_WIDTH", "1280");
        properties.setProperty("WINDOW_HEIGHT", "720");
        properties.setProperty("FULLSCREEN", "FALSE");
        properties.setProperty("MUSIC_VOLUME", "20");
        properties.setProperty("SOUND_VOLUME", "100");
        properties.setProperty("PLAYER_NAME", "PLAYER");

        properties.store(new FileOutputStream(new File(path)), null);
    }
}
