package net.Indyuce.mmocore.manager.profession;

import io.lumine.mythic.lib.MythicLib;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantManager extends SpecificProfessionManager {
   private final Map<Enchantment, Double> base = new HashMap<>();

   public EnchantManager() {
      super("base-enchant-exp");
   }

   @Override
   public void loadProfessionConfiguration(ConfigurationSection config) {
      for (String key : config.getKeys(false)) {
         try {
            Enchantment enchant = MythicLib.plugin.getVersion().getWrapper().getEnchantmentFromString(key.toLowerCase().replace("-", "_"));
            MMOCore.plugin.enchantManager.registerBaseExperience(enchant, config.getDouble(key));
         } catch (IllegalArgumentException exception) {
            MMOCore.log(Level.WARNING, "Could not find enchant with name '" + key + "'");
         }
      }
   }

   public void registerBaseExperience(Enchantment enchant, double value) {
      this.base.put(enchant, value);
   }

   @NotNull
   public double getBaseExperience(Enchantment enchant) {
      Double found = this.base.get(enchant);
      return found == null ? 0.0 : found;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.base.clear();
      }
   }
}
