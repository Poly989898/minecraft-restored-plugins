package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.papi.PlaceholderManager;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PAPIPlaceholder;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicPAPIPlaceholder;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPISupport extends PlaceholderManager<MythicBukkit> {
   private PlaceholderAPISupport.PlaceholderTree placeholders = new PlaceholderAPISupport.PlaceholderTree();

   public PlaceholderAPISupport(MythicBukkit plugin) {
      super(plugin);
      this.registerPlaceholders(plugin, "io.lumine.mythic.core.skills.placeholders.papi");
   }

   public void registerPlaceholders(LuminePlugin plugin, String packageName) {
      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, packageName, MythicPAPIPlaceholder.class)) {
         try {
            String name = clazz.getAnnotation(MythicPAPIPlaceholder.class).placeholder();
            if (PAPIPlaceholder.class.isAssignableFrom(clazz)) {
               Constructor<?> constructor = clazz.getConstructor(plugin.getClass());
               this.register(name, (PAPIPlaceholder)constructor.newInstance(plugin));
            }
         } catch (Exception e) {
            MythicLogger.error("Failed to load placeholder {0}", clazz.getCanonicalName());
            e.printStackTrace();
         }
      }
   }

   @Override
   public String parse(String string) {
      return PlaceholderAPI.setPlaceholders(null, string);
   }

   public String parse(String string, AbstractPlayer player) {
      return PlaceholderAPI.setPlaceholders((Player)player.getBukkitEntity(), string);
   }

   public String onRequest(OfflinePlayer player, String identifier) {
      String[] split = identifier.split("_");
      List<String> argsList = new ArrayList<>();
      PlaceholderAPISupport.PlaceholderTree ref = this.placeholders;

      for (int i = 0; i < split.length; i++) {
         if (ref.getSubPlaceholders().containsKey(split[i])) {
            ref = ref.getSubPlaceholders().get(split[i]);
         } else {
            argsList.add(split[i]);
         }
      }

      String[] args = argsList.toArray(new String[0]);
      return ref.getPlaceholder().onPlaceholderRequest(player, args);
   }

   @NotNull
   @Override
   public String getIdentifier() {
      return "mythic";
   }

   @NotNull
   @Override
   public String getVersion() {
      return "5.0.0";
   }

   public void register(String[] keys, PAPIPlaceholder transformer) {
      for (String key : keys) {
         this.register(key, transformer);
      }
   }

   public void register(String key, PAPIPlaceholder transformer) {
      String[] split = key.split("_");
      PlaceholderAPISupport.PlaceholderTree ref = this.placeholders;

      for (int i = 0; i <= split.length; i++) {
         if (i == split.length) {
            ref.setPlaceholder(transformer);
         } else {
            if (!ref.getSubPlaceholders().containsKey(split[i])) {
               ref.getSubPlaceholders().put(split[i], new PlaceholderAPISupport.PlaceholderTree());
            }

            ref = ref.getSubPlaceholders().get(split[i]);
         }
      }
   }

   private class PlaceholderTree {
      private PAPIPlaceholder placeholder = null;
      private Map<String, PlaceholderAPISupport.PlaceholderTree> subPlaceholders = new HashMap<>();

      @Generated
      public PlaceholderTree() {
      }

      @Generated
      public PAPIPlaceholder getPlaceholder() {
         return this.placeholder;
      }

      @Generated
      public Map<String, PlaceholderAPISupport.PlaceholderTree> getSubPlaceholders() {
         return this.subPlaceholders;
      }

      @Generated
      public void setPlaceholder(PAPIPlaceholder placeholder) {
         this.placeholder = placeholder;
      }

      @Generated
      public void setSubPlaceholders(Map<String, PlaceholderAPISupport.PlaceholderTree> subPlaceholders) {
         this.subPlaceholders = subPlaceholders;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PlaceholderAPISupport.PlaceholderTree other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$placeholder = this.getPlaceholder();
            Object other$placeholder = other.getPlaceholder();
            if (this$placeholder == null ? other$placeholder == null : this$placeholder.equals(other$placeholder)) {
               Object this$subPlaceholders = this.getSubPlaceholders();
               Object other$subPlaceholders = other.getSubPlaceholders();
               return this$subPlaceholders == null ? other$subPlaceholders == null : this$subPlaceholders.equals(other$subPlaceholders);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof PlaceholderAPISupport.PlaceholderTree;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $placeholder = this.getPlaceholder();
         result = result * 59 + ($placeholder == null ? 43 : $placeholder.hashCode());
         Object $subPlaceholders = this.getSubPlaceholders();
         return result * 59 + ($subPlaceholders == null ? 43 : $subPlaceholders.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PlaceholderAPISupport.PlaceholderTree(placeholder=" + this.getPlaceholder() + ", subPlaceholders=" + this.getSubPlaceholders() + ")";
      }
   }
}
