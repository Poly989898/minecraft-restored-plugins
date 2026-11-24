package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.ColorData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DyeColor extends ItemStat<ColorData, ColorData> {
   public DyeColor() {
      super(
         "DYE_COLOR",
         Material.RED_DYE,
         "Dye Color",
         new String[]{"The color of your item", "(for dyeable items).", "In RGB."},
         new String[0],
         Material.LEATHER_HELMET,
         Material.LEATHER_CHESTPLATE,
         Material.LEATHER_LEGGINGS,
         Material.LEATHER_BOOTS,
         Material.LEATHER_HORSE_ARMOR
      );
   }

   public ColorData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof String, "Must specify a string");
      return new ColorData((String)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.DYE_COLOR).enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: {Red} {Green} {Blue}");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set("dye-color", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed Dye Color.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String[] var4 = var2.split(" ");
      Validate.isTrue(var4.length == 3, "Use this format: {Red} {Green} {Blue}.");

      for (String var8 : var4) {
         int var9 = Integer.parseInt(var8);
         Validate.isTrue(var9 >= 0 && var9 < 256, "Color must be between 0 and 255");
      }

      var1.getEditedSection().set("dye-color", var2);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Dye Color successfully changed to " + var2 + ".");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<ColorData> var2) {
      var1.add(ChatColor.GRAY + "Current Value: " + (var2.isPresent() ? ChatColor.GREEN + ((ColorData)var2.get()).toString() : ChatColor.RED + "None"));
      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the dye color.");
   }

   @NotNull
   public ColorData getClearStatData() {
      return new ColorData(0, 0, 0);
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ItemMeta var2 = var1.getNBT().getItem().getItemMeta();
      if (var2 instanceof LeatherArmorMeta) {
         ArrayList var3 = new ArrayList();
         var3.add(new ItemTag(this.getNBTPath(), ((LeatherArmorMeta)var2).getColor()));
         ColorData var4 = this.getLoadedNBT(var3);
         if (var4 != null) {
            var1.setData(this, var4);
         }
      }
   }

   @Nullable
   public ColorData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         Color var3 = (Color)var2.getValue();
         return new ColorData(var3);
      } else {
         return null;
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull ColorData var2) {
      if (var1.getMeta() instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)var1.getMeta()).setColor(var2.getColor());
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ColorData var1) {
      return new ArrayList<>();
   }
}
