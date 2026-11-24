package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.gson.JsonArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class NBTTags extends StringListStat {
   static String extraneousTag = "EXTMI_";

   public NBTTags() {
      super("CUSTOM_NBT", Material.NAME_TAG, "NBT Tags", new String[]{"Custom NBT Tags."}, new String[0]);
   }

   @Override
   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.NBT_TAGS).enable("Write in the chat the NBT tag you want to add.", ChatColor.AQUA + "Format: {Tag Name} {Tag Value}");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("custom-nbt")) {
         List customNbt = editionInventory.getEditedSection().getStringList("custom-nbt");
         if (customNbt.size() < 1) {
            return;
         }

         String text = (String)customNbt.get(customNbt.size() - 1);
         customNbt.remove(text);
         editionInventory.getEditedSection().set("custom-nbt", customNbt);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + text + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      Validate.isTrue(text.split(" ").length > 1, "Use this format: {Tag Name} {Tag Value}");
      List list = editionInventory.getEditedSection().contains("custom-nbt") ? editionInventory.getEditedSection().getStringList("custom-nbt") : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set("custom-nbt", list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "StringListStat successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         StringListData stringListData = (StringListData)optional.get();
         stringListData.getList().forEach(var1x -> texts.add(ChatColor.GRAY + var1x));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a tag.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last tag.");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData stringListData) {
      JsonArray jsonArray = new JsonArray();
      ArrayList list = new ArrayList();
      HashMap<String, String> map = new HashMap<>();
      HashMap<String, ItemTag> map2 = new HashMap<>();

      for (String text : stringListData.getList()) {
         if (!text.startsWith(extraneousTag)) {
            jsonArray.add(text);
            String text2 = text.substring(0, text.indexOf(32));
            String text3 = text.substring(text.indexOf(32) + 1);
            map.put(text2, extraneousTag + text);
            map2.put(text2, new ItemTag(text2, this.calculateObjectType(text3)));
         }
      }

      for (String text : stringListData.getList()) {
         if (text.startsWith(extraneousTag)) {
            String text2 = text.substring(extraneousTag.length());
            String text3 = text2.substring(0, text2.indexOf(32));
            String text4 = text2.substring(text2.indexOf(32) + 1);
            if (map.containsKey(text3)) {
               map.put(text3, text);
            } else {
               if (text4.isEmpty()) {
                  map2.remove(text3);
               } else {
                  map2.put(text3, new ItemTag(text3, this.calculateObjectType(text4)));
               }

               map.remove(text3);
            }
         }
      }

      for (String key : map2.keySet()) {
         list.add((ItemTag)map2.get(key));
         jsonArray.add((String)map.get(key));
      }

      list.add(new ItemTag(this.getNBTPath(), jsonArray.toString()));
      return list;
   }

   public Object calculateObjectType(String text) {
      if (text.equalsIgnoreCase("true")) {
         return true;
      }

      if (text.equalsIgnoreCase("false")) {
         return false;
      }

      try {
         return Integer.parseInt(text);
      } catch (NumberFormatException exception) {
         try {
            return Double.parseDouble(text);
         } catch (NumberFormatException exception2) {
            if (text.contains("[") && text.contains("]")) {
               ArrayList list = new ArrayList();

               for (String text2 : text.replace("[", "").replace("]", "").split(",")) {
                  list.add(text2.replace("\"", ""));
               }

               return list;
            } else {
               return text;
            }
         }
      }
   }
}
