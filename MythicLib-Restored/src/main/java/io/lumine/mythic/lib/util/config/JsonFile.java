package io.lumine.mythic.lib.util.config;

import io.lumine.mythic.lib.MythicLib;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class JsonFile extends ConfigFile<JsonObject> {
   public JsonFile(@NotNull String name) {
      this(MythicLib.plugin, "", name);
   }

   public JsonFile(@NotNull String folder, @NotNull String name) {
      this(MythicLib.plugin, folder, name);
   }

   public JsonFile(@NotNull Plugin plugin, @NotNull String folder, @NotNull String name) {
      super(plugin, new File(plugin.getDataFolder() + folder, name + ".json"));
      if (!this.getFile().exists()) {
         this.setContent(new JsonObject());
      } else {
         try {
            FileReader reader = new FileReader(this.getFile());
            this.setContent(MythicLib.plugin.getGson().fromJson(reader, JsonObject.class));
            reader.close();
         } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, "Could not load JSON file '" + this.getFile().getName() + "': " + exception.getMessage());
            if (!this.hasContent()) {
               this.setContent(new JsonObject());
            }
         }
      }
   }

   @Override
   public void save() {
      try {
         if (!this.getFile().exists()) {
            this.getFile().getParentFile().mkdir();
         }

         FileWriter writer = new FileWriter(this.getFile());
         writer.write(this.getContent().toString());
         writer.close();
      } catch (IOException exception) {
         this.getPlugin().getLogger().log(Level.SEVERE, "Could not save JSON file '" + this.getFile().getName() + "': " + exception.getMessage());
         exception.printStackTrace();
      }
   }
}
