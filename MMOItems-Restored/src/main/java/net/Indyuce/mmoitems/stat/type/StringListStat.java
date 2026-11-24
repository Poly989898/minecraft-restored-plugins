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
   public StringListStat(String var1, Material var2, String var3, String[] var4, String[] var5, Material... var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringListData var2) {
      if (var2 instanceof StringListData) {
         if (var2.getList().size() != 0) {
            String var3 = String.join(", ", var2.getList());
            String var4 = this.getGeneralStatFormat();
            String var5 = var4.replace("{value}", var3);
            var1.getLore().insert(this.getPath(), var5);
            var1.addItemTag(this.getAppliedNBT(var2));
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData var1) {
      JsonArray var2 = new JsonArray();

      for (String var4 : var1.getList()) {
         var2.add(var4);
      }

      ArrayList var5 = new ArrayList();
      var5.add(new ItemTag(this.getNBTPath(), var2.toString()));
      return var5;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, this).enable("Write in the chat the line you want to add.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains(this.getPath())) {
         List var3 = var1.getEditedSection().getStringList(this.getPath());
         if (var3.isEmpty()) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set(this.getPath(), var3.isEmpty() ? null : var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MythicLib.plugin.parseColors(var4) + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      List var4 = var1.getEditedSection().contains(this.getPath()) ? var1.getEditedSection().getStringList(this.getPath()) : new ArrayList();
      var4.add(var2);
      var1.getEditedSection().set(this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " Stat successfully added.");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      StringListData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public StringListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            String[] var3 = (String[])new Gson().fromJson((String)var2.getValue(), String[].class);
            return new StringListData(var3);
         } catch (JsonSyntaxException | IllegalStateException var4) {
         }
      }

      return null;
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         StringListData var3 = (StringListData)var2.get();
         var3.getList().forEach(var1x -> var1.add(ChatColor.GRAY + MythicLib.plugin.parseColors(var1x)));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a permission.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last permission.");
   }

   @NotNull
   public StringListData getClearStatData() {
      return new StringListData();
   }
}
