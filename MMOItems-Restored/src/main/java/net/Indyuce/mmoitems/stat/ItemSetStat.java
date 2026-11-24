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
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      super.whenClicked(editionInventory, event);
      if (event.getAction() != InventoryAction.PICKUP_HALF) {
         editionInventory.getPlayer().sendMessage(ChatColor.GREEN + "Available Item Sets:");
         StringBuilder builder = new StringBuilder();

         for (ItemSet itemSet : MMOItems.plugin.getSets().getAll()) {
            builder.append(ChatColor.GREEN).append(itemSet.getId()).append(ChatColor.GRAY).append(" (").append(itemSet.getName()).append(ChatColor.GRAY).append("), ");
         }

         if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
         }

         editionInventory.getPlayer().sendMessage(builder.toString());
      }
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      ItemSet itemSet = MMOItems.plugin.getSets().get(stringData.toString());
      if (itemSet != null) {
         itemStackBuilder.getLore().insert("set", itemSet.getLoreTag());
      }

      itemStackBuilder.addItemTag(this.getAppliedNBT(stringData));
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringData stringData) {
      ItemSet itemSet = MMOItems.plugin.getSets().get(stringData.toString());
      Validate.notNull(itemSet, String.format("Could not find item set with ID '%s'", stringData));
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), stringData.toString()));
      return list;
   }

   @NotNull
   @Override
   public String getNBTPath() {
      return "MMOITEMS_ITEM_SET";
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      ItemSet itemSet = MMOItems.plugin.getSets().get(text);
      Validate.notNull(itemSet, String.format("Couldn't find the set named '%s'.", text));
      super.whenInput(editionInventory, text, objs);
   }
}
