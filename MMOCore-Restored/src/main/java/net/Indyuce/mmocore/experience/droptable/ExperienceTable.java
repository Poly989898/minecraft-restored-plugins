package net.Indyuce.mmocore.experience.droptable;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.ExperienceObject;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ExperienceTable {
   private final String id;
   private final List<ExperienceItem> items = new ArrayList<>();

   public ExperienceTable(ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      this.id = config.getName();

      for (String str : config.getKeys(false)) {
         try {
            Validate.isTrue(config.isConfigurationSection(str), "Key '" + str + "' is not a configuration section");
            this.items.add(new ExperienceItem(config.getConfigurationSection(str)));
         } catch (RuntimeException exception) {
            throw new RuntimeException("Could not load item '" + str + "': " + exception.getMessage());
         }
      }
   }

   public String getId() {
      return this.id;
   }

   public List<ExperienceItem> getItems() {
      return this.items;
   }

   public void claim(@NotNull PlayerData levelingUp, int professionLevel, @NotNull ExperienceObject object) {
      for (ExperienceItem item : this.items) {
         int timesClaimed = levelingUp.getClaims(object, item);
         if (item.roll(professionLevel, timesClaimed)) {
            levelingUp.setClaims(object, item, timesClaimed + 1);
            item.applyTriggers(levelingUp);
         }
      }
   }

   public void unclaim(@NotNull PlayerData playerData, @NotNull ExperienceObject object, boolean reset) {
      for (ExperienceItem item : this.items) {
         for (int i = 0; i < playerData.getClaims(object, item); i++) {
            item.removeTriggers(playerData);
         }

         if (reset) {
            playerData.setClaims(object, item, 0);
         }
      }
   }

   public void applyTemporaryTriggers(@NotNull PlayerData data, @NotNull ExperienceObject object) {
      for (ExperienceItem item : this.items) {
         int timesClaimed = data.getClaims(object, item);

         for (int i = 0; i < timesClaimed; i++) {
            item.applyTemporaryTriggers(data);
         }
      }
   }
}
