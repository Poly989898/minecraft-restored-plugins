package me.TechsCode.UltraPermissions.permissionDatabase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public class PermissionDatabase {
   private HashMap<String, PermissionInfo> map = new HashMap<>();
   public static final String githubLink = "https://raw.githubusercontent.com/TechsCode-Team/PluginPermissions/master/Permissions/Database.updb";

   public PermissionDatabase(UltraPermissions ultraPermissions) {
      Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)ultraPermissions.getBootstrap(), () -> {
         ultraPermissions.log("Loading Permission Database...");

         for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            for (Permission permission : plugin.getDescription().getPermissions()) {
               this.map.put(permission.getName(), new PermissionInfo(permission.getName(), plugin.getName(), permission.getDescription(), new String[0], "Extracted from .jar"));
            }
         }

         ultraPermissions.log("- " + this.map.size() + " Permissions extracted from .jars");
         File file = new File(ultraPermissions.getPluginFolder().getAbsolutePath() + "/Main.updb");
         boolean flag = false;

         try {
            FileUtils.copyURLToFile(new URL("https://raw.githubusercontent.com/TechsCode-Team/PluginPermissions/master/Permissions/Database.updb"), file);
            flag = true;
         } catch (IOException exception) {
            ultraPermissions.log("Failed to load Permissions from the Cloud");
         }

         if (file.exists()) {
            try {
               int count = 0;

               for (String line : FileUtils.readLines(file, "UTF-8")) {
                  if (!line.startsWith("#")) {
                     String[] texts = line.split("[+]");
                     if (texts.length < 2) {
                        ultraPermissions.log("Invalid line in " + file.getName() + ":");
                        ultraPermissions.log(line);
                     } else {
                        String text = texts[0];
                        String permission = texts[1];
                        String description = "";
                        String[] aliases = new String[0];
                        if (texts.length >= 3) {
                           description = texts[2];
                        }

                        if (texts.length >= 4) {
                           aliases = texts[3].split(",");
                        }

                        this.map.put(permission, new PermissionInfo(permission, text, description, aliases, "Permission Database (" + file.getName() + ")"));
                        count++;
                     }
                  }
               }

               if (flag) {
                  ultraPermissions.log("- " + count + " Permissions downloaded from the Cloud");
               } else {
                  ultraPermissions.log("- " + count + " Permissions loaded from the Cache");
               }
            } catch (IOException exception) {
               exception.printStackTrace();
            }
         } else {
            ultraPermissions.log("Connect this Server to the internet to load the Permission Database");
         }
      }, 20L);
   }

   public PermissionInfo getInfo(String text) {
      if (this.map.containsKey(text)) {
         return this.map.get(text);
      }

      for (PermissionInfo permissionInfo : this.map.values()) {
         if (permissionInfo.isThisPermission(text)) {
            return permissionInfo;
         }
      }

      return null;
   }

   public List<String> getSupportedPlugins() {
      return this.map.entrySet().stream().map(entry -> entry.getValue().getPlugin()).distinct().collect(Collectors.toList());
   }

   public PermissionInfoList getPermissionInfosFromPlugin(String text) {
      return this.map.values().stream().filter(permissionInfo -> permissionInfo.getPlugin().equalsIgnoreCase(text)).collect(Collectors.toCollection(PermissionInfoList::new));
   }

   public PermissionInfoList getAllPermissionInfos() {
      return new PermissionInfoList(this.map.values());
   }
}
