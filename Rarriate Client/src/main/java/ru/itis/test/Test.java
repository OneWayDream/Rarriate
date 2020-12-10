package ru.itis.test;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws Exception{
        String a = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Rarriate!\\app1.properties";
        Path path = Paths.get(a);

        Properties properties = new Properties();

        if (!Files.exists(path)) {
            Files.createFile(path);
            properties.load(new FileInputStream(new File(path.toString())));
            properties.setProperty("TEST", "test");
            properties.store(new FileOutputStream(new File(path.toString())), null);
        }
//        System.out.println(Files.exists(Paths.get(a)));
    }
}
