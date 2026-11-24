package net.Indyuce.mmocore.loot.droptable;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.chest.condition.ConditionInstance;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DropTable implements PreloadedObject {
   private final String id;
   private final double capacity;
   private final boolean shuffle;
   private final List<DropItem> drops = new ArrayList<>();
   private final List<Condition> conditions = new ArrayList<>();
   private final PostLoadAction postLoadAction = this.generatePostLoadAction();

   public DropTable(ConfigurationSection config) {
      this.postLoadAction.cacheConfig(config);
      this.id = config.getName();
      this.shuffle = config.getBoolean("shuffle");
      this.capacity = config.getDouble("capacity", LootBuilder.DEFAULT_CAPACITY);
      Validate.isTrue(this.capacity >= 0.0, "Capacity must be positive");
   }

   public DropTable(String id) {
      this.id = id;
      this.capacity = 100.0;
      this.shuffle = false;
   }

   private PostLoadAction generatePostLoadAction() {
      return new PostLoadAction(config -> {
         List<String> itemsList = config.getStringList("items");
         List<String> conditionsList = config.getStringList("conditions");
         Validate.notNull(itemsList, "Could not find drop item list");

         for (String key : itemsList) {
            try {
               this.drops.add(MMOCore.plugin.loadManager.loadDropItem(new MMOLineConfig(key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load drop item '" + key + "' from table '" + this.id + "': " + exception.getMessage());
            }
         }

         for (String key : conditionsList) {
            try {
               this.conditions.add(MMOCore.plugin.loadManager.loadCondition(new MMOLineConfig(key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load condition '" + key + "' from table '" + this.id + "': " + exception.getMessage());
            }
         }
      });
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public String getId() {
      return this.id;
   }

   public void registerDropItem(DropItem item) {
      Validate.notNull(item);
      this.drops.add(item);
   }

   public double getCapacity() {
      return this.capacity;
   }

   @NotNull
   public List<DropItem> getDrops() {
      return this.drops;
   }

   @NotNull
   public List<ItemStack> collect(LootBuilder builder) {
      List<DropItem> items;
      if (this.shuffle) {
         items = new ArrayList<>(this.drops);
         Collections.shuffle(items);
      } else {
         items = this.drops;
      }

      for (DropItem item : items) {
         if (item.rollChance(builder.getEntity()) && builder.getCapacity() >= item.getWeight()) {
            item.collect(builder);
            builder.reduceCapacity(item.getWeight());
         }
      }

      return builder.getLoot();
   }

   @NotNull
   public List<Condition> getConditions() {
      return this.conditions;
   }

   public boolean areConditionsMet(ConditionInstance entity) {
      for (Condition condition : this.conditions) {
         if (!condition.isMet(entity)) {
            return false;
         }
      }

      return true;
   }
}
