package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Phil",
   name = "itemissimilar",
   aliases = {"issimilar", "similarto"},
   description = "Checks if the ItemStack is similar"
)
public class ItemIsSimilarCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "slot",
      aliases = {"s"},
      description = "The slot to check.\nValid Slots are:\n- HAND, OFFHAND, HEAD, CHEST, LEGS, FEET\n- 0 - 36\n",
      defValue = "HAND"
   )
   private final String slot;
   @MythicField(
      name = "item",
      aliases = {"i", "material", "m", "mm", "mythicitem"},
      description = "The item/material to check for",
      defValue = "DIRT"
   )
   private AbstractItemStack item;

   public ItemIsSimilarCondition(String line, MythicLineConfig config) {
      super(line);
      this.slot = config.getString(new String[]{"slot", "s"}, "HAND");
      String materialString = config.getString(new String[]{"item", "i", "material", "m", "mm", "mythicitem"}, "DIRT");

      try {
         this.item = getPlugin().getBootstrap().createItem(materialString);
      } catch (IllegalArgumentException e) {
         MythicLogger.errorConditionConfig(this, config, "'" + materialString + "' is not a valid material.");
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (!target.isPlayer()) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Target is not a player, returning false");
         return false;
      } else {
         Player player = BukkitAdapter.adapt(target.asPlayer());

         AbstractItemStack item;
         try {
            int slot = Integer.parseInt(this.slot);
            item = slot < 36 ? BukkitAdapter.adapt(player.getInventory().getItem(slot)) : BukkitAdapter.adapt(player.getInventory().getItem(0));
         } catch (NumberFormatException e) {
            item = EquipSlot.of(this.slot).get(target).orElse(null);
         }

         if (item == null) {
            item = BukkitAdapter.adapt(Material.AIR);
         }

         return this.item.isSimilar(item);
      }
   }
}
