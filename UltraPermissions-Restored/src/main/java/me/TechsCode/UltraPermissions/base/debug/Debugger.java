package me.TechsCode.UltraPermissions.base.debug;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import lombok.NonNull;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.util.TimeUtil;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;

public class Debugger {
   public Debugger() {
   }

   public static void debug(String text, String... texts) {
      DebugRegistery debugRegistery = TechPlugin.getInstance().getDebugRegistery();
      if (!debugRegistery.getAvailableDebuggers().contains(text)) {
         debugRegistery.addAvailableDebugger(text);
      }

      if (TechPlugin.isDebugging && TechPlugin.getInstance().getDebugRegistery().isDebugging(text)) {
         for (String line : texts) {
            TechPlugin.getInstance().log("\u00a7e[" + text + "] \u00a7r" + line);
         }
      }
   }

   public static void printStackTrace(String text) {
      StackTraceElement[] stackTrace = new Exception().getStackTrace();
      TechPlugin.getInstance().log("!----------------------------------------------------------------------------------------------------------!");
      TechPlugin.getInstance().log(text);
      TechPlugin.getInstance().log("!----------------------------------------------------------------------------------------------------------!");

      for (int i = 1; i < stackTrace.length; i++) {
         String element = stackTrace[i].toString();
         TechPlugin.getInstance().log("\tat " + element);
      }

      TechPlugin.getInstance().log("--------------------------------------------------------------------------------------------------------end-");
   }

   public static void printStackTrace(@NonNull Throwable exception) {
      if (exception == null) {
         throw new NullPointerException("throwable is marked non-null but is null");
      }

      ArrayList list = new ArrayList();
      if (exception.getCause() != null) {
         Throwable cause = exception.getCause();

         do {
            list.add(cause);
         } while ((cause = cause.getCause()) != null);
      }

      if (exception instanceof TechException && !list.isEmpty()) {
         TechPlugin.getInstance().log(exception.getMessage());
      } else {
         TechPlugin.getInstance().log(exception.toString());
         printStackTraceElements(exception);
      }

      if (!list.isEmpty()) {
         Throwable cause = (Throwable)list.get(list.size() - 1);
         TechPlugin.getInstance().log(cause.toString());
         printStackTraceElements(cause);
      }
   }

   private static void printStackTraceElements(Throwable exception) {
      for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
         String text = stackTraceElement.toString();
         TechPlugin.getInstance().log("\tat " + text);
      }
   }

   public static void saveError(Throwable exception, String... texts) {
      String text;
      if (TechPlugin.getInstance() != null) {
         text = "Running " + Bukkit.getName() + " " + Bukkit.getBukkitVersion() + " and Java " + System.getProperty("java.version");
      } else {
         text = "Running "
            + ProxyServer.getInstance().getName()
            + " "
            + ProxyServer.getInstance().getVersion()
            + " and Java "
            + System.getProperty("java.version");
      }

      try {
         ArrayList list = new ArrayList();
         String message = TechPlugin.getInstance().getName() + " " + TechPlugin.getInstance().getVersion() + " encountered " + exception.getClass().getSimpleName();
         fill(
            list,
            "------------------------------------[ " + TimeUtil.getFormattedDate() + " ]-----------------------------------",
            message,
            text,
            "Plugins: "
               + joinListWithCommas(
                  Arrays.asList(
                     SpigotTechPlugin.getInstance() != null
                        ? Bukkit.getPluginManager().getPlugins()
                        : new Object[]{ProxyServer.getInstance().getPluginManager().getPlugins()}
                  )
               ),
            "----------------------------------------------------------------------------------------------"
         );
         if (texts != null && !String.join("", texts).isEmpty()) {
            fill(list, "\nMore Information: ");
            fill(list, texts);
         }

         do {
            fill(
               list,
               exception == null
                  ? "Unknown error"
                  : exception.getClass().getSimpleName() + " " + getOrDefault(exception.getMessage(), getOrDefault(exception.getLocalizedMessage(), "(Unknown cause)"))
            );
            int count = 0;

            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
               count++;
               String element = stackTraceElement.toString();
               if (!element.contains("sun.reflect")) {
                  if (count > 6 && element.startsWith("net.minecraft.server")) {
                     break;
                  }

                  fill(list, "\t at " + stackTraceElement.toString());
               }
            }
         } while ((exception = exception.getCause()) != null);

         fill(list, "----------------------------------------------------------------------------------------------", System.lineSeparator());
         if (TechPlugin.getInstance() != null) {
            Bukkit.getLogger().severe(message + "! Please check your error.log and report this issue with the information in that file. " + text);
         } else {
            ProxyServer.getInstance()
               .getLogger()
               .severe(message + "! Please check your error.log and report this issue with the information in that file. " + text);
         }

         File file = new File(TechPlugin.getInstance().getPluginFolder(), "error.log");
         if (!file.exists()) {
            file.createNewFile();
         }

         FileWriter fileWriter = new FileWriter(file, true);
         Iterator iterator = list.iterator();

         while (iterator.hasNext()) {
            String line = (String)iterator.next();
            fileWriter.write(line + (iterator.hasNext() ? System.lineSeparator() : ""));
         }

         fileWriter.close();
      } catch (Throwable saveException) {
         if (TechPlugin.getInstance() != null) {
            Bukkit.getLogger().log(Level.SEVERE, "Got error when saving another error! Saving error:", saveException);
            Bukkit.getLogger().log(Level.SEVERE, "Original error that is not saved:", exception);
         } else {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Got error when saving another error! Saving error:", saveException);
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Original error that is not saved:", exception);
         }

         saveError(saveException, "Original error that is not saved:");
      }
   }

   private static void fill(List<String> texts, String... additional) {
      texts.addAll(Arrays.asList(additional));
   }

   public static String joinListWithCommas(List<?> list) {
      if (list != null && !list.isEmpty()) {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).toString());
            if (i < list.size() - 1) {
               builder.append(", ");
            }
         }

         return builder.toString();
      } else {
         return "";
      }
   }

   public static <T> T getOrDefault(T value, T fallback) {
      return (T)(!(value instanceof String) || !"none".equalsIgnoreCase((String)value) && !"".equals(value) ? getOrDefaultStrict((T)value, (T)fallback) : fallback);
   }

   public static <T> T getOrDefaultStrict(T value, T fallback) {
      return (T)(value != null ? value : fallback);
   }
}
