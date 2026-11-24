package net.Indyuce.mmocore.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.player.stats.StatInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatManager implements MMOCoreManager {
   private final Map<String, StatInfo> loaded = new HashMap<>();
   private final Set<String> usedStats = new HashSet<>();

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.loaded.clear();
         this.usedStats.clear();
      }

      FileConfiguration config = new ConfigFile("stats").getConfig();

      for (String key : config.getConfigurationSection("default").getKeys(false)) {
         this.registerDefaultFormula(key, new LinearValue(config.getConfigurationSection("default." + key)));
      }
   }

   public Collection<StatInfo> getLoaded() {
      return this.loaded.values();
   }

   @NotNull
   public Set<String> getRegistered() {
      return this.usedStats;
   }

   @Nullable
   public StatInfo getInfo(String stat) {
      return this.loaded.get(stat);
   }

   public void registerProfession(String stat, Profession profession) {
      this.compute(stat).profession = profession;
   }

   public void registerDefaultFormula(String stat, LinearValue defaultFormula) {
      this.compute(stat).defaultInfo = defaultFormula;
      this.usedStats.add(stat);
   }

   @NotNull
   private StatInfo compute(String stat) {
      return this.loaded.computeIfAbsent(stat, StatInfo::new);
   }
}
