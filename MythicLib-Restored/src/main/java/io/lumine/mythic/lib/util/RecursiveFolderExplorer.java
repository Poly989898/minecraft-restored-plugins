package io.lumine.mythic.lib.util;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

@Deprecated
public class RecursiveFolderExplorer {
   private final Consumer<File> action;
   private final Consumer<RuntimeException> exceptionHandling;

   @Deprecated
   public RecursiveFolderExplorer(Consumer<File> action, Plugin plugin, String errorLogPrefix) {
      this.action = action;
      this.exceptionHandling = exception -> plugin.getLogger().log(Level.WARNING, errorLogPrefix + ": " + exception.getMessage());
   }

   @Deprecated
   public RecursiveFolderExplorer(Consumer<File> action, Consumer<RuntimeException> exceptionHandling) {
      this.action = action;
      this.exceptionHandling = exceptionHandling;
   }

   @Deprecated
   public void explore(File file) {
      if (file.isDirectory()) {
         Arrays.asList(file.listFiles()).forEach(this::explore);
      } else {
         try {
            this.action.accept(file);
         } catch (RuntimeException exception) {
            this.exceptionHandling.accept(exception);
         }
      }
   }
}
