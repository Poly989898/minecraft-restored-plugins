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

   public ColorData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof String, "Must specify a string");
      return new ColorData((String)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.DYE_COLOR).enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: {Red} {Green} {Blue}");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set("dye-color", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed Dye Color.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String[] texts = text.split(" ");
      Validate.isTrue(texts.length == 3, "Use this format: {Red} {Green} {Blue}.");

      for (String text2 : texts) {
         int count = Integer.parseInt(text2);
         Validate.isTrue(count >= 0 && count < 256, "Color must be between 0 and 255");
      }

      editionInventory.getEditedSection().set("dye-color", text);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Dye Color successfully changed to " + text + ".");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<ColorData> optional) {
      texts.add(ChatColor.GRAY + "Current Value: " + (optional.isPresent() ? ChatColor.GREEN + ((ColorData)optional.get()).toString() : ChatColor.RED + "None"));
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the dye color.");
   }

   @NotNull
   public ColorData getClearStatData() {
      return new ColorData(0, 0, 0);
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ItemMeta itemMeta = readMMOItem.getNBT().getItem().getItemMeta();
      if (itemMeta instanceof LeatherArmorMeta) {
         ArrayList list = new ArrayList();
         list.add(new ItemTag(this.getNBTPath(), ((LeatherArmorMeta)itemMeta).getColor()));
         ColorData loadedNBT = this.getLoadedNBT(list);
         if (loadedNBT != null) {
            readMMOItem.setData(this, loadedNBT);
         }
      }
   }

   @Nullable
   public ColorData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         Color color = (Color)tagAtPath.getValue();
         return new ColorData(color);
      } else {
         return null;
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ColorData colorData) {
      if (itemStackBuilder.getMeta() instanceof LeatherArmorMeta) {
         ((LeatherArmorMeta)itemStackBuilder.getMeta()).setColor(colorData.getColor());
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ColorData colorData) {
      return new ArrayList<>();
   }
}
