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
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionColor extends ItemStat<ColorData, ColorData> {
   public PotionColor() {
      super(
         "POTION_COLOR",
         Material.POTION,
         "Potion Color",
         new String[]{"The color of your potion.", "(Doesn't impact the effects)."},
         new String[0],
         Material.POTION,
         Material.SPLASH_POTION,
         Material.LINGERING_POTION,
         Material.TIPPED_ARROW
      );
   }

   public ColorData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof String, "Must specify a string");
      return new ColorData((String)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.POTION_COLOR).enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: {Red} {Green} {Blue}");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set("potion-color", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed Potion Color.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String[] texts = text.split(" ");
      Validate.isTrue(texts.length == 3, "Use this format: {Red} {Green} {Blue}. Example: '75 0 130' stands for Purple.");

      for (String text2 : texts) {
         int count = Integer.parseInt(text2);
         Validate.isTrue(count >= 0 && count < 256, "Color must be between 0 and 255");
      }

      editionInventory.getEditedSection().set("potion-color", text);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Potion Color successfully changed to " + text + ".");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta() instanceof PotionMeta) {
         Color color = ((PotionMeta)readMMOItem.getNBT().getItem().getItemMeta()).getColor();
         if (color != null) {
            readMMOItem.setData(this, new ColorData(color));
         }
      }
   }

   @Nullable
   public ColorData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      throw new NotImplementedException();
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<ColorData> optional) {
      texts.add(optional.isPresent() ? ChatColor.GREEN + ((ColorData)optional.get()).toString() : ChatColor.RED + "Uncolored");
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the potion color.");
   }

   @NotNull
   public ColorData getClearStatData() {
      return new ColorData(0, 0, 0);
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ColorData colorData) {
      if (itemStackBuilder.getItemStack().getType().name().contains("POTION") || itemStackBuilder.getItemStack().getType() == Material.TIPPED_ARROW) {
         ((PotionMeta)itemStackBuilder.getMeta()).setColor(colorData.getColor());
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ColorData colorData) {
      throw new NotImplementedException();
   }
}
