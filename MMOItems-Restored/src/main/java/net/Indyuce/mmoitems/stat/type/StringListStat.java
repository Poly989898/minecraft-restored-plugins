package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.Gson;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringListStat extends ItemStat<StringListData, StringListData> {
   public StringListStat(String text, Material material, String name, String[] texts, String[] compatibleTypes, Material... materials) {
      super(text, material, name, texts, compatibleTypes, materials);
   }

   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringListData stringListData) {
      if (stringListData instanceof StringListData) {
         if (stringListData.getList().size() != 0) {
            String text = String.join(", ", stringListData.getList());
            String generalStatFormat = this.getGeneralStatFormat();
            String formattedLine = generalStatFormat.replace("{value}", text);
            itemStackBuilder.getLore().insert(this.getPath(), formattedLine);
            itemStackBuilder.addItemTag(this.getAppliedNBT(stringListData));
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData stringListData) {
      JsonArray jsonArray = new JsonArray();

      for (String text : stringListData.getList()) {
         jsonArray.add(text);
      }

      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), jsonArray.toString()));
      return list;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, this).enable("Write in the chat the line you want to add.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains(this.getPath())) {
         List list = editionInventory.getEditedSection().getStringList(this.getPath());
         if (list.isEmpty()) {
            return;
         }

         String text = (String)list.get(list.size() - 1);
         list.remove(text);
         editionInventory.getEditedSection().set(this.getPath(), list.isEmpty() ? null : list);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MythicLib.plugin.parseColors(text) + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      List list = editionInventory.getEditedSection().contains(this.getPath()) ? editionInventory.getEditedSection().getStringList(this.getPath()) : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set(this.getPath(), list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " Stat successfully added.");
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
      if (tagAtPath != null) {
         try {
            String[] texts = (String[])new Gson().fromJson((String)tagAtPath.getValue(), String[].class);
            return new StringListData(texts);
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         StringListData stringListData = (StringListData)optional.get();
         stringListData.getList().forEach(var1x -> texts.add(ChatColor.GRAY + MythicLib.plugin.parseColors(var1x)));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a permission.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last permission.");
   }

   @NotNull
   public StringListData getClearStatData() {
      return new StringListData();
   }
}
