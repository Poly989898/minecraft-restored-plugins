package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.interaction.projectile.ArrowPotionEffectArrayItem;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrowPotionEffects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> {
   public ArrowPotionEffects() {
      super(
         "ARROW_POTION_EFFECTS",
         Material.TIPPED_ARROW,
         "Arrow Potion Effects",
         new String[]{"The effects to be applied when", "entities are shot by this bow"},
         new String[]{"bow", "crossbow"}
      );
   }

   public RandomPotionEffectListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new RandomPotionEffectListData((ConfigurationSection)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.ARROW_POTION_EFFECTS)
            .enable("Write in the chat the potion effect you want to add.", ChatColor.AQUA + "Format: [POTION_EFFECT] [DURATION] [AMPLIFIER]");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("arrow-potion-effects")) {
         Set<String> keys = editionInventory.getEditedSection().getConfigurationSection("arrow-potion-effects").getKeys(false);
         String text = Arrays.asList(keys.toArray(new String[0])).get(keys.size() - 1);
         editionInventory.getEditedSection().set("arrow-potion-effects." + text, null);
         if (keys.size() <= 1) {
            editionInventory.getEditedSection().set("arrow-potion-effects", null);
         }

         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Successfully removed "
                  + text.substring(0, 1).toUpperCase()
                  + text.substring(1).toLowerCase()
                  + ChatColor.GRAY
                  + "."
            );
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String[] texts = text.split(" ");
      Validate.isTrue(
         texts.length == 3, text + " is not a valid [POTION_EFFECT] [DURATION] [AMPLIFIER]. Example: 'FAST_DIGGING 30 3' stands for Haste 3 for 30 seconds."
      );
      PotionEffectType potionEffectType = null;

      for (PotionEffectType effectType : PotionEffectType.values()) {
         if (effectType != null && effectType.getName().equalsIgnoreCase(texts[0].replace("-", "_"))) {
            potionEffectType = effectType;
            break;
         }
      }

      Validate.notNull(potionEffectType, texts[0] + " is not a valid potion effect.");
      double value = MMOUtils.parseDouble(texts[1]);
      int count = (int)MMOUtils.parseDouble(texts[2]);
      ConfigurationSection section = editionInventory.getEditedSection().createSection("arrow-potion-effects." + potionEffectType.getName());
      section.set("duration", value);
      section.set("amplifier", count);
      editionInventory.getEditedSection().set("arrow-potion-effects." + potionEffectType.getName(), section);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + potionEffectType.getName() + " " + count + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomPotionEffectListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         RandomPotionEffectListData randomPotionEffectListData = (RandomPotionEffectListData)optional.get();

         for (RandomPotionEffectData effect : randomPotionEffectListData.getEffects()) {
            texts.add(
               ChatColor.GRAY
                  + "* "
                  + ChatColor.GREEN
                  + UtilityMethods.caseOnWords(effect.getType().getName().toLowerCase().replace("_", " "))
                  + ChatColor.GRAY
                  + " Level: "
                  + ChatColor.GREEN
                  + effect.getAmplifier()
                  + ChatColor.GRAY
                  + " Duration: "
                  + ChatColor.GREEN
                  + effect.getDuration()
            );
         }
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add an effect.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last effect.");
   }

   @NotNull
   public PotionEffectListData getClearStatData() {
      return new PotionEffectListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull PotionEffectListData potionEffectListData) {
      ArrayList list = new ArrayList();
      String generalStatFormat = this.getGeneralStatFormat();
      potionEffectListData.getEffects()
         .forEach(
            var2x -> list.add(
               generalStatFormat.replace(
                  "{effect}",
                  MMOItems.plugin.getLanguage().getPotionEffectName(var2x.getType())
                     + " "
                     + MMOUtils.intToRoman(var2x.getLevel())
                     + "("
                     + MythicLib.plugin.getMMOConfig().decimal.format(var2x.getDuration())
                     + "s)"
               )
            )
         );
      itemStackBuilder.getLore().insert(this.getPath(), list);
      itemStackBuilder.addItemTag(this.getAppliedNBT(potionEffectListData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData potionEffectListData) {
      ArrayList list = new ArrayList();
      JsonArray jsonArray = new JsonArray();
      potionEffectListData.getEffects().forEach(var1x -> {
         JsonObject var2x = new JsonObject();
         var2x.addProperty("type", var1x.getType().getName());
         var2x.addProperty("level", var1x.getLevel());
         var2x.addProperty("duration", var1x.getDuration());
         jsonArray.add(var2x);
      });
      list.add(new ItemTag(this.getNBTPath(), jsonArray.toString()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      PotionEffectListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public PotionEffectListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath == null) {
         return null;
      }

      PotionEffectListData potionEffectListData = new PotionEffectListData();

      for (ArrowPotionEffectArrayItem arrowPotionEffectArrayItem : (ArrowPotionEffectArrayItem[])MythicLib.plugin
         .getJson()
         .parse((String)tagAtPath.getValue(), ArrowPotionEffectArrayItem[].class)) {
         potionEffectListData.add(new PotionEffectData(PotionEffectType.getByName(arrowPotionEffectArrayItem.type), arrowPotionEffectArrayItem.duration, arrowPotionEffectArrayItem.level));
      }

      return potionEffectListData;
   }
}
