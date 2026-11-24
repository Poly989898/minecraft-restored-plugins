package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItemExperienceSource extends ExperienceSource<Void> {
   private final List<Enchantment> enchants = new ArrayList<>();

   public EnchantItemExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser);
      if (config.contains("enchant")) {
         for (String key : config.getString("enchant").split(",")) {
            this.enchants.add(MythicLib.plugin.getVersion().getWrapper().getEnchantmentFromString(key.toLowerCase().replace("-", "_")));
         }
      }
   }

   public boolean matchesParameter(PlayerData player, Void v) {
      return true;
   }

   @Override
   public ExperienceSourceManager<EnchantItemExperienceSource> newManager() {
      return new EnchantItemExperienceSource.Manager();
   }

   private static class Manager extends ExperienceSourceManager<EnchantItemExperienceSource> {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void a(EnchantItemEvent event) {
         PlayerData player = PlayerData.get(event.getEnchanter());

         for (EnchantItemExperienceSource source : this.getSources()) {
            if (source.matches(player, null)) {
               Map<Enchantment, Integer> applicableEnchants = new HashMap<>(event.getEnchantsToAdd());
               if (!source.enchants.isEmpty()) {
                  applicableEnchants.keySet().removeIf(enchantment -> !source.enchants.contains(enchantment));
               }

               if (!applicableEnchants.isEmpty()) {
                  double exp = 0.0;

                  for (Entry<Enchantment, Integer> entry : applicableEnchants.entrySet()) {
                     exp += MMOCore.plugin.enchantManager.getBaseExperience(entry.getKey()) * entry.getValue().intValue();
                  }

                  source.getDispenser().giveExperience(player, exp, event.getEnchantBlock().getLocation(), EXPSource.SOURCE);
               }
            }
         }
      }
   }
}
