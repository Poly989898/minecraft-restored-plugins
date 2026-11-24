package net.Indyuce.inventory.slot.restriction;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.LineConfig;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreTagRestriction extends SlotRestriction {
   private final String loreTag;
   private final LoreTagRestriction.CheckType checkType;

   public LoreTagRestriction(LineConfig config) {
      config.validate("tag");
      this.loreTag = MythicLib.plugin.parseColors(config.getString("tag"));
      this.checkType = config.contains("check")
         ? LoreTagRestriction.CheckType.valueOf(UtilityMethods.enumName(config.getString("check")))
         : LoreTagRestriction.CheckType.EQUALS;
   }

   @Override
   public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
      if (!item.hasItemMeta()) {
         return false;
      }

      ItemMeta meta = item.getItemMeta();
      if (!meta.hasLore()) {
         return false;
      }

      for (String checked : meta.getLore()) {
         if (this.checkType.operation.check(checked, this.loreTag)) {
            return true;
         }
      }

      return false;
   }

   enum CheckType {
      EQUALS(String::equals),
      CONTAINS(String::contains),
      STARTS_WITH(String::startsWith),
      ENDS_WITH(String::endsWith);

      final LoreTagRestriction.Operation operation;

      CheckType(LoreTagRestriction.Operation operation) {
         this.operation = operation;
      }
   }

   @FunctionalInterface
   interface Operation {
      boolean check(String key, String value);
   }
}
