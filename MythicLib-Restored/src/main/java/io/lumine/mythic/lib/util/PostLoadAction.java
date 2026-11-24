package io.lumine.mythic.lib.util;

import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostLoadAction {
   @Nullable
   private ConfigurationSection config;
   @Nullable
   private Consumer<ConfigurationSection> action;
   private final boolean persistent;

   public PostLoadAction(@NotNull Consumer<ConfigurationSection> action) {
      this(false, action);
   }

   public PostLoadAction(boolean persistent, @NotNull Consumer<ConfigurationSection> action) {
      this.persistent = persistent;
      this.action = Objects.requireNonNull(action, "Action cannot be null");
   }

   public void cacheConfig(@Nullable ConfigurationSection config) {
      if (this.action == null) {
         throw new PostLoadException("Object already post loaded");
      }

      Validate.isTrue(this.config == null, "Config already cached");
      if (config != null) {
         this.config = config;
      }
   }

   @Nullable
   public ConfigurationSection getCachedConfig() {
      if (this.action == null) {
         throw new PostLoadException("Object already post loaded");
      } else {
         return this.config;
      }
   }

   public void performAction() {
      if (this.action == null) {
         throw new PostLoadException("Object already post loaded");
      }

      Consumer<ConfigurationSection> action = this.action;
      if (!this.persistent) {
         this.action = null;
      }

      if (this.config != null) {
         ConfigurationSection config = this.config;
         this.config = null;
         action.accept(config);
      }
   }
}
