package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemSet;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ItemSetStat extends StringStat implements GemStoneStat {
   public ItemSetStat() {
      super(
         "SET",
         Material.LEATHER_CHESTPLATE,
         "Item Set",
         new String[]{"Item sets can give to the player extra", "bonuses that depend on how many items", "from the same set your wear."},
         new String[]{"!gem_stone", "!consumable", "!material", "!block", "!miscellaneous", "all"}
      );
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      super.whenClicked(var1, var2);
      if (var2.getAction() != InventoryAction.PICKUP_HALF) {
         var1.getPlayer().sendMessage(ChatColor.GREEN + "Available Item Sets:");
         StringBuilder var3 = new StringBuilder();

         for (ItemSet var5 : MMOItems.plugin.getSets().getAll()) {
            var3.append(ChatColor.GREEN).append(var5.getId()).append(ChatColor.GRAY).append(" (").append(var5.getName()).append(ChatColor.GRAY).append("), ");
         }

         if (var3.length() > 1) {
            var3.setLength(var3.length() - 2);
         }

         var1.getPlayer().sendMessage(var3.toString());
      }
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      ItemSet var3 = MMOItems.plugin.getSets().get(var2.toString());
      if (var3 != null) {
         var1.getLore().insert("set", var3.getLoreTag());
      }

      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringData var1) {
      ItemSet var2 = MMOItems.plugin.getSets().get(var1.toString());
      Validate.notNull(var2, String.format("Could not find item set with ID '%s'", var1));
      ArrayList var3 = new ArrayList();
      var3.add(new ItemTag(this.getNBTPath(), var1.toString()));
      return var3;
   }

   @NotNull
   @Override
   public String getNBTPath() {
      return "MMOITEMS_ITEM_SET";
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      ItemSet var4 = MMOItems.plugin.getSets().get(var2);
      Validate.notNull(var4, String.format("Couldn't find the set named '%s'.", var2));
      super.whenInput(var1, var2, var3);
   }
}
