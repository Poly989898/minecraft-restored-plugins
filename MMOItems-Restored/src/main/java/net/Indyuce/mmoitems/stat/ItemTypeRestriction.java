package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTypeRestriction extends ItemStat<StringListData, StringListData> {
   public ItemTypeRestriction() {
      super(
         "ITEM_TYPE_RESTRICTION",
         Material.EMERALD,
         "Item Type Restriction",
         new String[]{"This option defines the item types", "on which your gem can be applied."},
         new String[]{"gem_stone"}
      );
   }

   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.ITEM_TYPE_RESTRICTION)
            .enable("Write in the chat the item type you want your gem to support.", "Supported formats: WEAPON or BLUNT, PIERCING, SLASHING, OFFHAND, EXTRA.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains(this.getPath())) {
         List list = editionInventory.getEditedSection().getStringList("" + this.getPath());
         if (list.size() < 1) {
            return;
         }

         String text = (String)list.get(list.size() - 1);
         list.remove(text);
         editionInventory.getEditedSection().set("" + this.getPath(), list.size() == 0 ? null : list);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + text + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String text2 = text.toUpperCase().replace(" ", "_").replace("-", "_");
      Validate.isTrue(
         this.isValid(text2),
         text2
            + " is not a valid item type/set. You can enter WEAPON, BLUNT, PIERCING, SLASHING, OFFHAND, EXTRA, as well as other item types here: /mi list type."
      );
      List list = editionInventory.getEditedSection().contains(this.getPath()) ? editionInventory.getEditedSection().getStringList("" + this.getPath()) : new ArrayList();
      list.add(text2);
      editionInventory.getEditedSection().set(this.getPath(), list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Your gem now supports " + text2 + ".");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         StringListData stringListData = (StringListData)optional.get();
         stringListData.getList().forEach(var1x -> texts.add(ChatColor.GRAY + "* " + ChatColor.GREEN + var1x));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "Compatible with any type.");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a supported item type/set.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last element.");
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringListData stringListData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(stringListData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData stringListData) {
      ArrayList list = new ArrayList();
      String text = String.join(",", stringListData.getList());
      list.add(new ItemTag(this.getNBTPath(), text));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      StringListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public StringListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath == null) {
         return null;
      }

      String text = (String)tagAtPath.getValue();
      ArrayList list = new ArrayList();
      if (text.contains(",")) {
         for (String text2 : text.split(",")) {
            if (!text2.isEmpty()) {
               list.add(text2);
            }
         }
      } else {
         list.add(text);
      }

      return new StringListData(list);
   }

   private boolean isValid(String text) {
      if (text.equals("WEAPON")) {
         return true;
      }

      for (Type type : MMOItems.plugin.getTypes().getAll()) {
         if (type.getId().equals(text)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public StringListData getClearStatData() {
      return new StringListData();
   }
}
