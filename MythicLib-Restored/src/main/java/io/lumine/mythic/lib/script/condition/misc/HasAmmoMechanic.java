package io.lumine.mythic.lib.script.condition.misc;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HasAmmoMechanic extends Condition {
   private final Material item;
   private final boolean creativeInfinite;
   private final boolean consumeIfMet;
   private final String itemIgnoreTag;

   public HasAmmoMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("item");
      this.item = Material.valueOf(config.getString("item"));
      this.creativeInfinite = config.getBoolean("creative_infinite", false);
      this.itemIgnoreTag = config.contains("item_ignore_tag") ? config.getString("item_ignore_tag") : null;
      this.consumeIfMet = config.getBoolean("consume_if_met", false);
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      if (meta.getCaster().getPlayer().getGameMode() == GameMode.CREATIVE && this.creativeInfinite) {
         return true;
      }

      if (this.itemIgnoreTag != null
         && !this.itemIgnoreTag.isEmpty()
         && NBTItem.get(meta.getCaster().getPlayer().getInventory().getItem(meta.getCaster().getActionHand().toBukkit())).getBoolean(this.itemIgnoreTag)) {
         return true;
      }

      if (!meta.getCaster().getPlayer().getInventory().containsAtLeast(new ItemStack(this.item), 1)) {
         return false;
      }

      if (this.consumeIfMet) {
         meta.getCaster().getPlayer().getInventory().removeItem(new ItemStack[]{new ItemStack(this.item)});
      }

      return true;
   }
}
