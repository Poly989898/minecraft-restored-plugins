package net.Indyuce.mmocore.manager.profession;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;

public class AlchemyManager extends SpecificProfessionManager {
   public double splash;
   public double lingering;
   public double upgrade;
   public double extend;
   private final Map<PotionType, Double> base = new HashMap<>();

   public AlchemyManager() {
      super("alchemy-experience");
   }

   @Override
   public void loadProfessionConfiguration(ConfigurationSection config) {
      this.splash = 1.0 + config.getDouble("special.splash") / 100.0;
      this.lingering = 1.0 + config.getDouble("special.lingering") / 100.0;
      this.extend = 1.0 + config.getDouble("special.extend") / 100.0;
      this.upgrade = 1.0 + config.getDouble("special.upgrade") / 100.0;

      for (String key : config.getConfigurationSection("effects").getKeys(false)) {
         try {
            PotionType type = PotionType.valueOf(key.toUpperCase().replace("-", "_").replace(" ", "_"));
            MMOCore.plugin.alchemyManager.registerBaseExperience(type, config.getDouble("effects." + key));
         } catch (IllegalArgumentException exception) {
            MMOCore.log(Level.WARNING, "Could not read potion type from " + key);
         }
      }
   }

   public void registerBaseExperience(PotionType type, double value) {
      this.base.put(type, value);
   }

   public double getBaseExperience(PotionType type) {
      return this.base.get(type);
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.splash = this.lingering = this.upgrade = this.extend = 1.0;
         this.base.clear();
      }
   }
}
