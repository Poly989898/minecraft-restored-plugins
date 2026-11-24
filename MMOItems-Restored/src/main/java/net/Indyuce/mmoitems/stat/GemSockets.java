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

   public GemSocketsData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new GemSocketsData((List<String>)obj);
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection section, @NotNull Object obj) {
      if (obj instanceof String) {
         this.emptyGemSocketFormat = section.getString("empty-gem-socket");
         this.filledGemSocketFormat = section.getString("filled-gem-socket");
      } else {
         Validate.isTrue(obj instanceof ConfigurationSection, "Must be a config section");
         ConfigurationSection socketSection = (ConfigurationSection)obj;
         this.emptyGemSocketFormat = socketSection.getString("empty");
         this.filledGemSocketFormat = socketSection.getString("filled");
      }
   }

   @Override
   public String getLegacyTranslationPath() {
      return "empty-gem-socket";
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull GemSocketsData gemSocketsData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(gemSocketsData));
      ArrayList list = new ArrayList();

      for (GemstoneData gemstone : gemSocketsData.getGemstones()) {
         String name = gemstone.getName();
         if (itemStackBuilder.getMMOItem().hasUpgradeTemplate()) {
            int upgradeLevel = itemStackBuilder.getMMOItem().getUpgradeLevel();
            if (upgradeLevel != 0) {
               Integer level = gemstone.getLevel();
               if (level != null) {
                  int count = upgradeLevel - level;
                  name = DisplayName.appendUpgradeLevel(name, count);
               }
            }
         }

         list.add(this.filledGemSocketFormat.replace("{name}", name));
      }

      gemSocketsData.getEmptySlots().forEach(socket -> list.add(this.emptyGemSocketFormat.replace("{name}", socket)));
      itemStackBuilder.getLore().insert("gem-stones", list);
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull GemSocketsData gemSocketsData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), gemSocketsData.toJson().toString()));
      return list;
   }

   @NotNull
   @Override
   public String getNBTPath() {
      return "MMOITEMS_GEM_STONES";
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      GemSocketsData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public GemSocketsData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            JsonObject asJsonObject = new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject();
            GemSocketsData gemSocketsData = new GemSocketsData(asJsonObject.getAsJsonArray("EmptySlots"));
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("Gemstones");
            asJsonArray.forEach(element -> gemSocketsData.add(new GemstoneData(element.getAsJsonObject())));
            return gemSocketsData;
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.GEM_SOCKETS).enable("Write in the chat the COLOR of the gem socket you want to add.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains(this.getPath())) {
         List list = editionInventory.getEditedSection().getStringList("" + this.getPath());
         if (list.size() < 1) {
            return;
         }

         String text = (String)list.get(list.size() - 1);
         list.remove(text);
         editionInventory.getEditedSection().set("" + this.getPath(), list);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + text + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      List list = editionInventory.getEditedSection().contains(this.getPath()) ? editionInventory.getEditedSection().getStringList("" + this.getPath()) : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set("" + this.getPath(), list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + text + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<GemSocketsData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         GemSocketsData gemSocketsData = (GemSocketsData)optional.get();
         gemSocketsData.getEmptySlots().forEach(socket -> texts.add(ChatColor.GRAY + "* " + ChatColor.GREEN + socket + " Gem Socket"));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "No Sockets");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a gem socket.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the socket.");
   }

   @NotNull
   public GemSocketsData getClearStatData() {
      return new GemSocketsData(new ArrayList<>());
   }
}
