package io.lumine.mythic.lib.api.util;

import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public abstract class PostLoadObject {
   @Nullable
   private ConfigurationSection config;

   @Deprecated
   public PostLoadObject(@Nullable ConfigurationSection config) {
      this.config = config;
   }

   @Deprecated
   @NotNull
   public ConfigurationSection getPostLoadConfig() {
      return Objects.requireNonNull(this.config, "Object already post loaded");
   }

   @Deprecated
   public void postLoad() {
      Validate.notNull(this.config, "Object already post loaded");
      this.whenPostLoaded(this.config);
      this.config = null;
   }

   @Deprecated
   protected abstract void whenPostLoaded(@NotNull ConfigurationSection section);
}
