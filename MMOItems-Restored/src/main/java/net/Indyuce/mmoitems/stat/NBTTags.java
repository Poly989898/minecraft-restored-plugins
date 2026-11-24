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
   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.NBT_TAGS).enable("Write in the chat the NBT tag you want to add.", ChatColor.AQUA + "Format: {Tag Name} {Tag Value}");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("custom-nbt")) {
         List var3 = var1.getEditedSection().getStringList("custom-nbt");
         if (var3.size() < 1) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set("custom-nbt", var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + var4 + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      Validate.isTrue(var2.split(" ").length > 1, "Use this format: {Tag Name} {Tag Value}");
      List var4 = var1.getEditedSection().contains("custom-nbt") ? var1.getEditedSection().getStringList("custom-nbt") : new ArrayList();
      var4.add(var2);
      var1.getEditedSection().set("custom-nbt", var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "StringListStat successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         StringListData var3 = (StringListData)var2.get();
         var3.getList().forEach(var1x -> var1.add(ChatColor.GRAY + var1x));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a tag.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last tag.");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData var1) {
      JsonArray var2 = new JsonArray();
      ArrayList var3 = new ArrayList();
      HashMap<String, String> var4 = new HashMap<>();
      HashMap<String, ItemTag> var5 = new HashMap<>();

      for (String var7 : var1.getList()) {
         if (!var7.startsWith(extraneousTag)) {
            var2.add(var7);
            String var8 = var7.substring(0, var7.indexOf(32));
            String var9 = var7.substring(var7.indexOf(32) + 1);
            var4.put(var8, extraneousTag + var7);
            var5.put(var8, new ItemTag(var8, this.calculateObjectType(var9)));
         }
      }

      for (String var13 : var1.getList()) {
         if (var13.startsWith(extraneousTag)) {
            String var15 = var13.substring(extraneousTag.length());
            String var16 = var15.substring(0, var15.indexOf(32));
            String var10 = var15.substring(var15.indexOf(32) + 1);
            if (var4.containsKey(var16)) {
               var4.put(var16, var13);
            } else {
               if (var10.isEmpty()) {
                  var5.remove(var16);
               } else {
                  var5.put(var16, new ItemTag(var16, this.calculateObjectType(var10)));
               }

               var4.remove(var16);
            }
         }
      }

      for (String var14 : var5.keySet()) {
         var3.add((ItemTag)var5.get(var14));
         var2.add((String)var4.get(var14));
      }

      var3.add(new ItemTag(this.getNBTPath(), var2.toString()));
      return var3;
   }

   public Object calculateObjectType(String var1) {
      if (var1.equalsIgnoreCase("true")) {
         return true;
      }

      if (var1.equalsIgnoreCase("false")) {
         return false;
      }

      try {
         return Integer.parseInt(var1);
      } catch (NumberFormatException var8) {
         try {
            return Double.parseDouble(var1);
         } catch (NumberFormatException var7) {
            if (var1.contains("[") && var1.contains("]")) {
               ArrayList var2 = new ArrayList();

               for (String var6 : var1.replace("[", "").replace("]", "").split(",")) {
                  var2.add(var6.replace("\"", ""));
               }

               return var2;
            } else {
               return var1;
            }
         }
      }
   }
}
