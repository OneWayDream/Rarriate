package ru.itis.test;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws Exception{
//        String a = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\Rarriate!\\app1.properties";
//        Path path = Paths.get(a);
//
//        Properties properties = new Properties();
//
//        if (!Files.exists(path)) {
//            Files.createFile(path);
//            properties.load(new FileInputStream(new File(path.toString())));
//            properties.setProperty("TEST", "test");
//            properties.store(new FileOutputStream(new File(path.toString())), null);
//        }
////        System.out.println(Files.exists(Paths.get(a)));
        String[] abc = new String[5];
        abc[0] = "0";
        abc[1] = "1";
        abc[2] = "2";
        abc[3] = "3";
        abc[4] = "4";
//        for (String string: abc) {
//            System.out.println(string);
//        }
//
//        abc = Arrays.copyOfRange(abc, 1, 5);
//        for (String string: abc) {
//            System.out.println(string);
//        }
//        abc = Arrays.copyOf(abc, abc.length+1);
//        for (String string: abc) {
//            System.out.println(string);
//        }
////        System.out.println(abc[abc.length-1]);
        abc = Arrays.copyOf(Arrays.copyOfRange(abc, 1, 5), abc.length);
        abc[abc.length-1] = "123";
        for (String string: abc) {
            System.out.println(string);
        }
    }
}
