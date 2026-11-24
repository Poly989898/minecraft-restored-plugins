package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Effects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> implements PlayerConsumable {
   public Effects() {
      super("EFFECTS", Material.POTION, "Effects", new String[]{"The potion effects your", "consumable item grants."}, new String[]{"consumable"});
   }

   public RandomPotionEffectListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new RandomPotionEffectListData((ConfigurationSection)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.EFFECTS)
            .enable(
               "Write in the chat the permanent potion effect you want to add.",
               ChatColor.AQUA + "Format: {Potion Effect Name}|{Duration Numeric Formula}|{Amplifier Numeric Formula}",
               ChatColor.DARK_RED + "Note: " + ChatColor.RED + "The '|' lines are literal."
            );
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("effects")) {
         Set<String> keys = editionInventory.getEditedSection().getConfigurationSection("effects").getKeys(false);
         String text = Arrays.asList(keys.toArray(new String[0])).get(keys.size() - 1);
         editionInventory.getEditedSection().set("effects." + text, null);
         if (keys.size() <= 1) {
            editionInventory.getEditedSection().set("effects", null);
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
      String[] texts = text.split("\\|");
      Validate.isTrue(
         texts.length > 1,
         FriendlyFeedbackProvider.quickForConsole(
            FFPMMOItems.get(), "Use this format: $e{Potion Effect Name}|{Duration Numeric Formula}|{Amplifier Numeric Formula}$b.", new String[0]
         )
      );
      PotionEffectType potionEffectType = PotionEffectType.getByName(texts[0].replace("-", "_").replace(" ", "_").toUpperCase());
      Validate.notNull(
         potionEffectType,
         texts[0]
            + FriendlyFeedbackProvider.quickForConsole(
               FFPMMOItems.get(),
               " is not a valid potion effect. All potion effects can be found here:$e https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html",
               new String[0]
            )
      );
      NumericStatFormula numericStatFormula = new NumericStatFormula(texts[1]);
      NumericStatFormula durationFormula = texts.length > 2 ? new NumericStatFormula(texts[2]) : new NumericStatFormula(1.0, 0.0, 0.0, 0.0);
      numericStatFormula.fillConfigurationSection(editionInventory.getEditedSection(), "effects." + potionEffectType.getName() + ".duration");
      durationFormula.fillConfigurationSection(editionInventory.getEditedSection(), "effects." + potionEffectType.getName() + ".amplifier");
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + potionEffectType.getName() + " " + durationFormula + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomPotionEffectListData> optional) {
      optional.ifPresentOrElse(
         effectListData -> {
            texts.add(ChatColor.GRAY + "Current Value:");

            for (RandomPotionEffectData effect : effectListData.getEffects()) {
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
         },
         () -> texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None")
      );
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
            effect -> list.add(
               generalStatFormat.replace(
                     "{effect}",
                     MMOItems.plugin.getLanguage().getPotionEffectName(effect.getType())
                        + (effect.getLevel() < 2 ? "" : " " + MMOUtils.intToRoman(effect.getLevel()))
                  )
                  .replace("{duration}", MythicLib.plugin.getMMOConfig().decimal.format(effect.getDuration()))
            )
         );
      itemStackBuilder.getLore().insert("effects", list);
      itemStackBuilder.addItemTag(this.getAppliedNBT(potionEffectListData));
   }

   @Override
   public String getLegacyTranslationPath() {
      return "effect";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData potionEffectListData) {
      JsonArray jsonArray = new JsonArray();

      for (PotionEffectData effect : potionEffectListData.getEffects()) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("Type", effect.getType().getName());
         jsonObject.addProperty("Duration", effect.getDuration());
         jsonObject.addProperty("Level", effect.getLevel());
         jsonArray.add(jsonObject);
      }

      ArrayList list = new ArrayList();
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

      try {
         PotionEffectListData potionEffectListData = new PotionEffectListData();

         for (JsonElement asJson : new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonArray()) {
            if (asJson.isJsonObject()) {
               JsonObject asJsonObject = asJson.getAsJsonObject();
               potionEffectListData.add(
                  new PotionEffectData(
                     PotionEffectType.getByName(asJsonObject.get("Type").getAsString()), asJsonObject.get("Duration").getAsDouble(), asJsonObject.get("Level").getAsInt()
                  )
               );
            }
         }

         return potionEffectListData;
      } catch (JsonSyntaxException | IllegalStateException exception) {
         return null;
      }
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      if (volatileMMOItem.hasData(ItemStats.EFFECTS)) {
         PotionEffectListData data = (PotionEffectListData)volatileMMOItem.getData(ItemStats.EFFECTS);

         for (PotionEffectData effect : data.getEffects()) {
            if (effect != null) {
               player.removePotionEffect(effect.getType());
               player.addPotionEffect(effect.toEffect());
            }
         }
      }
   }
}
