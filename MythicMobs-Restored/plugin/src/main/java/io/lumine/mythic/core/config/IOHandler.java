package io.lumine.mythic.core.config;

import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class IOHandler {
   public static List<File> getAllFiles(String directoryName) {
      File directory = new File(directoryName);
      List<File> resultList = new ArrayList<>();
      File[] fList = directory.listFiles();

      for (File file : fList) {
         if (!file.isFile() || !file.getName().endsWith(".yml") && !file.getName().endsWith(".txt")) {
            if (file.isDirectory()) {
               resultList.addAll(getAllFiles(file.getAbsolutePath()));
            }
         } else {
            resultList.add(file);
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loading config file: " + file.getAbsolutePath());
         }
      }

      return resultList;
   }

   public static String getList(String s, File[] list) {
      for (File f : list) {
         s = s + f.getName() + ", ";
      }

      return s;
   }

   public static <T extends JavaPlugin> List<IOLoader<T>> getSaveLoad(T plugin, List<File> itemFiles, String s) {
      List<IOLoader<T>> list = new ArrayList<>();

      for (File f : itemFiles) {
         list.add(new IOLoader<>(plugin, f, s));
      }

      return list;
   }
}
