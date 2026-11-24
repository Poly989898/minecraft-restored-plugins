package io.lumine.mythic.lib.util.config;

import io.lumine.mythic.lib.MythicLib;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigFile<T> {
   private final Plugin plugin;
   private final File file;
   private T content;

   public ConfigFile(@NotNull Plugin plugin, @NotNull File file) {
      this.plugin = plugin;
      this.file = file;
   }

   @NotNull
   public T getContent() {
      return this.content;
   }

   public boolean hasContent() {
      return this.content != null;
   }

   public void setContent(T t) {
      this.content = Objects.requireNonNull(t, "Content cannot be null");
   }

   @NotNull
   public Plugin getPlugin() {
      return this.plugin;
   }

   @NotNull
   public File getFile() {
      return this.file;
   }

   public void delete() {
      if (this.file.exists() && !this.file.delete()) {
         MythicLib.plugin.getLogger().log(Level.SEVERE, "Could not delete '" + this.file.getName() + "'");
      }
   }

   public abstract void save();
}
