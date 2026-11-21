package io.lumine.mythic.core.config;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyScope;
import java.io.File;

public enum Scope implements PropertyScope {
   CONFIG("config"),
   CONFIG_GENERAL("config/config-general"),
   CONFIG_ITEMS("config/config-items"),
   CONFIG_MOBS("config/config-mobs"),
   CONFIG_SKILLS("config/config-skills"),
   CONFIG_SPAWNING("config/config-spawning"),
   PACK("packs"),
   STATS("stats"),
   COSMETICS("config/config-cosmetics"),
   GENERATION("config/config-generation"),
   ENCHANTS("config/config-enchants"),
   ENCHANTS_RARITIES("config/config-enchant-rarities"),
   RPG("config/config-rpg"),
   ARCHETYPES("config/config-archetypes"),
   SPELLS("config/config-spells"),
   ATTRIBUTES("config/config-attributes"),
   TALENT_TREES("config/config-talents");

   private final String scope;

   private Scope(String scope) {
      this.scope = scope;
   }

   @Override
   public String get() {
      return this.scope;
   }

   @Override
   public String toString() {
      return this.get();
   }

   public File getFile() {
      return new File(MythicBukkit.inst().getDataFolder(), this.get() + ".yml");
   }

   public MythicConfig getConfig() {
      return new MythicConfigImpl(this.getFile());
   }
}
