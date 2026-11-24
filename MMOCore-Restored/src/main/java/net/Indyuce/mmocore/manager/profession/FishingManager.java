package net.Indyuce.mmocore.manager.profession;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.loot.RandomWeightedRoll;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.chest.condition.ConditionInstance;
import net.Indyuce.mmocore.loot.fishing.FishingDropItem;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FishingManager extends SpecificProfessionManager {
   private final Set<FishingManager.FishingDropTable> tables = new LinkedHashSet<>();

   public FishingManager() {
      super("on-fish");
   }

   @Override
   public void loadProfessionConfiguration(ConfigurationSection config) {
      for (String key : config.getKeys(false)) {
         try {
            this.tables.add(new FishingManager.FishingDropTable(config.getConfigurationSection(key)));
         } catch (IllegalArgumentException exception) {
            MMOCore.log(Level.WARNING, "Could not load fishing drop table " + key + ": " + exception.getMessage());
         }
      }

      MMOCore.plugin.statManager.registerProfession("FISHING_STRENGTH", this.getLinkedProfession());
      MMOCore.plugin.statManager.registerProfession("CRITICAL_FISHING_CHANCE", this.getLinkedProfession());
      MMOCore.plugin.statManager.registerProfession("CRITICAL_FISHING_FAILURE_CHANCE", this.getLinkedProfession());
   }

   @NotNull
   public FishingManager.FishingDropTable calculateDropTable(@NotNull Player player, @NotNull FishHook hook) {
      ConditionInstance conditionEntity = new ConditionInstance(player, hook.getLocation());

      for (FishingManager.FishingDropTable table : this.tables) {
         if (table.areConditionsMet(conditionEntity)) {
            return table;
         }
      }

      return null;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.tables.clear();
      }
   }

   public static class FishingDropTable {
      private final Set<Condition> conditions = new HashSet<>();
      private final List<FishingDropItem> items = new ArrayList<>();

      public FishingDropTable(ConfigurationSection section) {
         Validate.notNull(section, "Could not load config");
         String id = section.getName();
         if (section.contains("conditions")) {
            List<String> list = section.getStringList("conditions");
            Validate.notNull(list, "Could not load conditions");

            for (String str : list) {
               try {
                  this.conditions.add(MMOCore.plugin.loadManager.loadCondition(new MMOLineConfig(str)));
               } catch (IllegalArgumentException exception) {
                  MMOCore.plugin
                     .getLogger()
                     .log(Level.WARNING, "Could not load condition '" + str + "' from fishing drop table '" + id + "': " + exception.getMessage());
               }
            }
         }

         List<String> list = section.getStringList("items");
         Validate.notNull(list, "Could not load item list");

         for (String str : list) {
            try {
               FishingDropItem dropItem = new FishingDropItem(new MMOLineConfig(str));
               this.items.add(dropItem);
            } catch (RuntimeException exception) {
               MMOCore.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not load item '" + str + "' from fishing drop table '" + id + "': " + exception.getMessage());
            }
         }

         Validate.notEmpty(this.items, "The item list must not be empty.");
      }

      public boolean areConditionsMet(ConditionInstance entity) {
         for (Condition condition : this.conditions) {
            if (!condition.isMet(entity)) {
               return false;
            }
         }

         return true;
      }

      public Set<Condition> getConditions() {
         return this.conditions;
      }

      public FishingDropItem getRandomItem(PlayerData player) {
         return new RandomWeightedRoll<>(player, this.items, MMOCore.plugin.configManager.fishingDropsChanceWeight).rollItem();
      }
   }
}
