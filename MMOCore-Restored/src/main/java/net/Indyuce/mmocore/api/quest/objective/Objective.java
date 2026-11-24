package net.Indyuce.mmocore.api.quest.objective;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import org.apache.commons.lang.Validate;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;

public abstract class Objective {
   private final String id;
   private final String lore;
   private final BarColor barColor;
   private final List<Trigger> triggers = new ArrayList<>();

   public Objective(ConfigurationSection config) {
      this.id = config.getName();
      this.lore = config.getString("lore");
      Validate.notNull(config.getStringList("triggers"), "Could not load trigger list");
      String format = config.getString("bar-color", "PURPLE");
      this.barColor = BarColor.valueOf(format.toUpperCase().replace("-", "_").replace(" ", "_"));

      for (String key : config.getStringList("triggers")) {
         try {
            this.triggers.add(MMOCore.plugin.loadManager.loadTrigger(new MMOLineConfig(key)));
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load trigger '" + key + "' from objective '" + this.id + "': " + exception.getMessage());
         }
      }
   }

   public String getId() {
      return this.id;
   }

   public BarColor getBarColor() {
      return this.barColor;
   }

   public boolean hasLore() {
      return this.lore != null && !this.lore.isEmpty();
   }

   public String getDefaultLore() {
      return this.lore;
   }

   public List<Trigger> getTriggers() {
      return this.triggers;
   }

   public abstract ObjectiveProgress newProgress(QuestProgress questProgress);
}
