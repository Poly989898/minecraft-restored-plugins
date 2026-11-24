package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
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

public class PermanentEffects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> {
   public PermanentEffects() {
      super(
         "PERM_EFFECTS",
         Material.POTION,
         "Permanent Effects",
         new String[]{"The potion effects your", "item grants to the holder."},
         new String[]{"!miscellaneous", "!block", "all"}
      );
   }

   public RandomPotionEffectListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      RandomPotionEffectListData randomPotionEffectListData = new RandomPotionEffectListData();

      for (String key : section.getKeys(false)) {
         PotionEffectType potionEffectType = PotionEffectType.getByName(key.toUpperCase().replace("-", "_").replace(" ", "_"));
         Validate.notNull(potionEffectType, "Could not find potion effect type named '" + key + "'");
         randomPotionEffectListData.add(new RandomPotionEffectData(potionEffectType, new NumericStatFormula(section.get(key))));
      }

      return randomPotionEffectListData;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.PERM_EFFECTS)
            .enable("Write in the chat the permanent potion effect you want to add.", ChatColor.AQUA + "Format: {Effect Name} {Amplifier Numeric Formula}");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("perm-effects")) {
         Set<String> keys = editionInventory.getEditedSection().getConfigurationSection("perm-effects").getKeys(false);
         String text = new ArrayList<>(keys).get(keys.size() - 1);
         editionInventory.getEditedSection().set("perm-effects." + text, null);
         if (keys.size() <= 1) {
            editionInventory.getEditedSection().set("perm-effects", null);
         }

         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase() + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String[] texts = text.split(" ");
      Validate.isTrue(
         texts.length >= 2,
         "Use this format: {Effect Name} {Effect Amplifier Numeric Formula}. Example: 'speed 1 0.3' stands for Speed 1, plus 0.3 level per item level (rounded up to lower int)"
      );
      PotionEffectType potionEffectType = PotionEffectType.getByName(texts[0].replace("-", "_"));
      Validate.notNull(
         potionEffectType,
         texts[0]
            + " is not a valid potion effect. All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
      );
      NumericStatFormula numericStatFormula = new NumericStatFormula(text.substring(text.indexOf(" ") + 1));
      numericStatFormula.fillConfigurationSection(editionInventory.getEditedSection(), "perm-effects." + potionEffectType.getName());
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + potionEffectType.getName() + " " + numericStatFormula + ChatColor.GRAY + " successfully added.");
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
                  + UtilityMethods.caseOnWords(effect.getType().getName().replace("_", " ").toLowerCase())
                  + " "
                  + effect.getAmplifier().toString()
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
            effect -> list.add(
               generalStatFormat.replace("{effect}", MMOItems.plugin.getLanguage().getPotionEffectName(effect.getType()) + " " + MMOUtils.intToRoman(effect.getLevel()))
            )
         );
      itemStackBuilder.getLore().insert("perm-effects", list);
      itemStackBuilder.addItemTag(this.getAppliedNBT(potionEffectListData));
   }

   @Override
   public String getLegacyTranslationPath() {
      return "perm-effect";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData potionEffectListData) {
      ArrayList list = new ArrayList();
      JsonObject jsonObject = new JsonObject();

      for (PotionEffectData effect : potionEffectListData.getEffects()) {
         jsonObject.addProperty(effect.getType().getName(), effect.getLevel());
      }

      list.add(new ItemTag(this.getNBTPath(), jsonObject.toString()));
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
      if (tagAtPath != null) {
         try {
            PotionEffectListData potionEffectListData = new PotionEffectListData();
            JsonElement jsonElement = new JsonParser().parse((String)tagAtPath.getValue());
            jsonElement.getAsJsonObject()
               .entrySet()
               .forEach(entry -> potionEffectListData.add(new PotionEffectData(PotionEffectType.getByName((String)entry.getKey()), ((JsonElement)entry.getValue()).getAsInt())));
            return potionEffectListData;
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }
}
