package net.Indyuce.mmocore.experience;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.experience.droptable.ExperienceTable;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Profession implements ExperienceObject, PreloadedObject {
   private final String id;
   private final String name;
   private final int maxLevel;
   private final Map<Profession.ProfessionOption, Boolean> options = new HashMap<>();
   private final ExpCurve expCurve;
   private final ExperienceTable expTable;
   private final LinearValue experience;
   private final PostLoadAction postLoadAction = new PostLoadAction(configx -> MMOCore.plugin.professionManager.loadProfessionConfigurations(this, configx));

   public Profession(String id, ConfigurationSection config) {
      this.postLoadAction.cacheConfig(config);
      this.id = id.toLowerCase().replace("_", "-").replace(" ", "-");
      this.name = config.getString("name");
      Validate.notNull(this.name, "Could not load name");
      this.expCurve = config.contains("exp-curve")
         ? MMOCore.plugin.experience.getCurveOrThrow(config.get("exp-curve").toString().toLowerCase().replace("_", "-").replace(" ", "-"))
         : ExpCurve.DEFAULT;
      this.experience = new LinearValue(config.getConfigurationSection("experience"));
      ExperienceTable expTable = null;
      if (config.contains("exp-table")) {
         try {
            expTable = MMOCore.plugin.experience.loadExperienceTable(config.get("exp-table"));
         } catch (RuntimeException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load exp table from profession '" + id + "': " + exception.getMessage());
         }
      }

      this.expTable = expTable;
      if (config.contains("options")) {
         for (String key : config.getConfigurationSection("options").getKeys(false)) {
            try {
               Profession.ProfessionOption option = Profession.ProfessionOption.valueOf(key.toUpperCase().replace("-", "_").replace(" ", "_"));
               this.options.put(option, config.getBoolean("options." + key));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load option '" + key + "' from profession '" + id + "': " + exception.getMessage());
            }
         }
      }

      this.maxLevel = config.getInt("max-level");
      if (config.contains("exp-sources")) {
         for (String key : config.getStringList("exp-sources")) {
            try {
               MMOCore.plugin.experience.registerSource(MMOCore.plugin.loadManager.loadExperienceSource(new MMOLineConfig(key), this));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not register exp source '" + key + "' from profession '" + id + "': " + exception.getMessage());
            }
         }
      }
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public boolean getOption(Profession.ProfessionOption option) {
      return this.options.getOrDefault(option, option.getDefault());
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String getKey() {
      return "profession_" + this.getId();
   }

   @Override
   public ExpCurve getExpCurve() {
      return this.expCurve;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public boolean hasMaxLevel() {
      return this.maxLevel > 0;
   }

   public LinearValue getExperience() {
      return this.experience;
   }

   @Override
   public boolean hasExperienceTable() {
      return this.expTable != null;
   }

   @NotNull
   @Override
   public ExperienceTable getExperienceTable() {
      return Objects.requireNonNull(this.expTable, "Profession has no exp table");
   }

   @Override
   public void giveExperience(@NotNull PlayerData playerData, double experience, @Nullable Location hologramLocation, @NotNull EXPSource source) {
      playerData.getCollectionSkills().giveExperience(this, experience, EXPSource.SOURCE, hologramLocation, true);
   }

   @Override
   public boolean shouldHandle(PlayerData playerData) {
      return true;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Profession that = (Profession)o;
         return this.id.equals(that.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   public enum ProfessionOption {
      EXP_HOLOGRAMS(true);

      private final boolean def;

      ProfessionOption(boolean def) {
         this.def = def;
      }

      public boolean getDefault() {
         return this.def;
      }
   }
}
