package net.Indyuce.mmocore.api.player.attribute;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.ExpCurve;
import net.Indyuce.mmocore.experience.ExperienceObject;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerAttribute implements ExperienceObject {
   private final String id;
   private final String name;
   private final int max;
   private final ExperienceTable expTable;
   private final Set<StatModifier> buffs = new HashSet<>();

   public PlayerAttribute(ConfigurationSection config) {
      Validate.notNull(config, "Could not load config");
      this.id = config.getName().toLowerCase().replace("_", "-").replace(" ", "-");
      this.name = MythicLib.plugin.parseColors(config.getString("name", "MyAttribute"));
      this.max = config.contains("max-points") ? Math.max(1, config.getInt("max-points")) : 0;
      if (config.contains("buff")) {
         for (String key : config.getConfigurationSection("buff").getKeys(false)) {
            try {
               String stat = key.toUpperCase().replace("-", "_").replace(" ", "_");
               this.buffs.add(new StatModifier("attribute." + this.id, stat, config.getString("buff." + key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.log(Level.WARNING, "Could not load buff '" + key + "' from attribute '" + this.id + "': " + exception.getMessage());
            }
         }
      }

      ExperienceTable expTable = null;
      if (config.contains("exp-table")) {
         try {
            expTable = MMOCore.plugin.experience.loadExperienceTable(config.get("exp-table"));
         } catch (RuntimeException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load exp table from attribute '" + this.id + "': " + exception.getMessage());
         }
      }

      this.expTable = expTable;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasMax() {
      return this.max > 0;
   }

   public int getMax() {
      return this.max;
   }

   public Set<StatModifier> getBuffs() {
      return this.buffs;
   }

   @Override
   public String getKey() {
      return "attribute:" + this.getId().replace("-", "_");
   }

   @NotNull
   @Override
   public ExperienceTable getExperienceTable() {
      return Objects.requireNonNull(this.expTable, "Attribute has no exp table");
   }

   @Override
   public boolean hasExperienceTable() {
      return this.expTable != null;
   }

   @Nullable
   @Override
   public ExpCurve getExpCurve() {
      throw new RuntimeException("Attributes don't have experience");
   }

   @Override
   public void giveExperience(PlayerData playerData, double experience, @Nullable Location hologramLocation, @NotNull EXPSource source) {
      throw new RuntimeException("Attributes don't have experience");
   }

   @Override
   public boolean shouldHandle(PlayerData playerData) {
      throw new RuntimeException("Attributes don't have experience");
   }
}
