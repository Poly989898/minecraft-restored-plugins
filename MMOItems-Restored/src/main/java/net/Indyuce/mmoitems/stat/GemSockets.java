package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.data.GemstoneData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemSockets extends ItemStat<GemSocketsData, GemSocketsData> {
   private String emptyGemSocketFormat;
   private String filledGemSocketFormat;

   public GemSockets() {
      super(
         "GEM_SOCKETS",
         Material.EMERALD,
         "Gem Sockets",
         new String[]{"The amount of gem", "sockets your weapon has."},
         new String[]{"!gem_stone", "equipment", "ornament"}
      );
   }

   public GemSocketsData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new GemSocketsData((List<String>)var1);
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection var1, @NotNull Object var2) {
      if (var2 instanceof String) {
         this.emptyGemSocketFormat = var1.getString("empty-gem-socket");
         this.filledGemSocketFormat = var1.getString("filled-gem-socket");
      } else {
         Validate.isTrue(var2 instanceof ConfigurationSection, "Must be a config section");
         ConfigurationSection var3 = (ConfigurationSection)var2;
         this.emptyGemSocketFormat = var3.getString("empty");
         this.filledGemSocketFormat = var3.getString("filled");
      }
   }

   @Override
   public String getLegacyTranslationPath() {
      return "empty-gem-socket";
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull GemSocketsData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
      ArrayList var3 = new ArrayList();

      for (GemstoneData var5 : var2.getGemstones()) {
         String var6 = var5.getName();
         if (var1.getMMOItem().hasUpgradeTemplate()) {
            int var7 = var1.getMMOItem().getUpgradeLevel();
            if (var7 != 0) {
               Integer var8 = var5.getLevel();
               if (var8 != null) {
                  int var9 = var7 - var8;
                  var6 = DisplayName.appendUpgradeLevel(var6, var9);
               }
            }
         }

         var3.add(this.filledGemSocketFormat.replace("{name}", var6));
      }

      var2.getEmptySlots().forEach(var2x -> var3.add(this.emptyGemSocketFormat.replace("{name}", var2x)));
      var1.getLore().insert("gem-stones", var3);
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull GemSocketsData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.toJson().toString()));
      return var2;
   }

   @NotNull
   @Override
   public String getNBTPath() {
      return "MMOITEMS_GEM_STONES";
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      GemSocketsData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public GemSocketsData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            JsonObject var3 = new JsonParser().parse((String)var2.getValue()).getAsJsonObject();
            GemSocketsData var4 = new GemSocketsData(var3.getAsJsonArray("EmptySlots"));
            JsonArray var5 = var3.getAsJsonArray("Gemstones");
            var5.forEach(var1x -> var4.add(new GemstoneData(var1x.getAsJsonObject())));
            return var4;
         } catch (JsonSyntaxException | IllegalStateException var6) {
         }
      }

      return null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.GEM_SOCKETS).enable("Write in the chat the COLOR of the gem socket you want to add.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains(this.getPath())) {
         List var3 = var1.getEditedSection().getStringList("" + this.getPath());
         if (var3.size() < 1) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set("" + this.getPath(), var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + var4 + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      List var4 = var1.getEditedSection().contains(this.getPath()) ? var1.getEditedSection().getStringList("" + this.getPath()) : new ArrayList();
      var4.add(var2);
      var1.getEditedSection().set("" + this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + var2 + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<GemSocketsData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         GemSocketsData var3 = (GemSocketsData)var2.get();
         var3.getEmptySlots().forEach(var1x -> var1.add(ChatColor.GRAY + "* " + ChatColor.GREEN + var1x + " Gem Socket"));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "No Sockets");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a gem socket.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the socket.");
   }

   @NotNull
   public GemSocketsData getClearStatData() {
      return new GemSocketsData(new ArrayList<>());
   }
}
