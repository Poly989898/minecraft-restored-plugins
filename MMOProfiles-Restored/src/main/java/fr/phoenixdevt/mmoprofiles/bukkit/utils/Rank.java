package fr.phoenixdevt.mmoprofiles.bukkit.utils;

import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Rank {
   private final int slots;
   private final String name;
   private final String permission;

   public Rank(@NotNull ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      this.permission = Objects.requireNonNull(config.getString("permission"), "Permission cannot be null");
      this.name = config.getString("name", config.getName());
      this.slots = config.getInt("slots");
      Validate.isTrue(this.slots > 0, "Slots must be positive");
   }

   public int getMaxSlots() {
      return this.slots;
   }

   public String getName() {
      return this.name;
   }

   public String getPermission() {
      return this.permission;
   }
}
