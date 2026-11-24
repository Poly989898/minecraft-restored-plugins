package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.data.random.RandomBooleanData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanStat extends ItemStat<RandomBooleanData, BooleanData> {
   public BooleanStat(String text, Material material, String name, String[] texts, String[] compatibleTypes, Material... materials) {
      super(text, material, name, texts, compatibleTypes, materials);
   }

   public RandomBooleanData whenInitialized(Object obj) {
      if (obj instanceof Boolean) {
         return new RandomBooleanData((Boolean)obj);
      } else if (obj instanceof Number) {
         return new RandomBooleanData(Double.parseDouble(obj.toString()));
      } else {
         throw new IllegalArgumentException("Must specify a number (chance) or true/false");
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull BooleanData booleanData) {
      if (booleanData.isEnabled()) {
         itemStackBuilder.addItemTag(this.getAppliedNBT(booleanData));
         itemStackBuilder.getLore().insert(this.getPath(), this.getGeneralStatFormat());
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData booleanData) {
      ArrayList list = new ArrayList();
      if (booleanData.isEnabled()) {
         list.add(new ItemTag(this.getNBTPath(), true));
      }

      return list;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         editionInventory.getEditedSection().set(this.getPath(), editionInventory.getEditedSection().getBoolean(this.getPath()) ? null : true);
         editionInventory.registerTemplateEdition();
      } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
         new StatEdition(editionInventory, this).enable("Write in the chat the probability you want (a percentage)");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      double value = MMOUtils.parseDouble(text);
      Validate.isTrue(value >= 0.0 && value <= 100.0, "Chance must be between 0 and 100");
      editionInventory.getEditedSection().set(this.getPath(), value / 100.0);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + this.getName()
               + " successfully changed to "
               + ChatColor.GREEN
               + MythicLib.plugin.getMMOConfig().decimal.format(value)
               + "% Chance"
               + ChatColor.GRAY
               + "."
         );
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.BOOLEAN));
      }

      BooleanData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      return tagAtPath != null ? new BooleanData((Boolean)tagAtPath.getValue()) : null;
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomBooleanData> optional) {
      if (optional.isPresent()) {
         double chance = ((RandomBooleanData)optional.get()).getChance();
         texts.add(
            ChatColor.GRAY
               + "Current Value: "
               + (
                  chance >= 1.0
                     ? ChatColor.GREEN + "True"
                     : (chance <= 0.0 ? ChatColor.RED + "False" : ChatColor.GREEN + MythicLib.plugin.getMMOConfig().decimal.format(chance * 100.0) + "% Chance")
               )
         );
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "False");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to switch this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to choose a probability to have this option.");
   }

   @NotNull
   public BooleanData getClearStatData() {
      return new BooleanData(false);
   }
}
