package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.gui.edition.AbilityListEdition;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.random.RandomAbilityData;
import net.Indyuce.mmoitems.stat.data.random.RandomAbilityListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Abilities extends ItemStat<RandomAbilityListData, AbilityListData> {
   private String generalFormat;
   private String modifierIfAny;
   private String modifierForEach;
   private String modifierSplitter;
   private String abilitySplitter;
   private String modifierNewLine;
   private boolean useAbilitySplitter;
   private int modifiersPerLine;

   public Abilities() {
      super(
         "ABILITY",
         Material.BLAZE_POWDER,
         "Item Abilities",
         new String[]{"Make your item cast amazing abilities", "to kill monsters or buff yourself."},
         new String[]{"!block", "all"}
      );
   }

   public RandomAbilityListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a valid config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      RandomAbilityListData randomAbilityListData = new RandomAbilityListData();

      for (String key : section.getKeys(false)) {
         randomAbilityListData.add(new RandomAbilityData(section.getConfigurationSection(key)));
      }

      return randomAbilityListData;
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection section, @NotNull Object obj) {
      if (obj instanceof ConfigurationSection) {
         ConfigurationSection section2 = (ConfigurationSection)obj;
         this.generalFormat = section2.getString("general-format");
         this.modifierIfAny = section2.getString("modifier-if-any", "");
         this.modifierNewLine = "\n" + section2.getString("modifier-new-line", "");
         this.modifierForEach = section2.getString("modifier-foreach");
         this.modifierSplitter = section2.getString("modifier-splitter");
         this.abilitySplitter = section2.getString("ability-splitter.format");
         this.useAbilitySplitter = section2.getBoolean("ability-splitter.enabled");
         this.modifiersPerLine = section2.getInt("modifiers-per-line");
      } else {
         this.generalFormat = section.getString("ability-format");
         this.modifierIfAny = "\n";
         this.modifierNewLine = "\n";
         this.modifierForEach = section.getString("ability-modifier");
         this.modifierSplitter = "";
         this.abilitySplitter = section.getString("ability-splitter");
         this.useAbilitySplitter = this.abilitySplitter != null && !this.abilitySplitter.isEmpty();
         this.modifiersPerLine = 1;
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull AbilityListData abilityListData) {
      ArrayList list = new ArrayList();
      abilityListData.getAbilities()
         .forEach(
            var3x -> {
               StringBuilder builder = new StringBuilder(
                  this.generalFormat
                     .replace("{trigger}", MMOItems.plugin.getLanguage().getTriggerTypeName(var3x.getTrigger()))
                     .replace("{ability}", var3x.getAbility().getName())
               );
               if (!var3x.getModifiers().isEmpty()) {
                  builder.append(this.modifierIfAny);
               }

               boolean flag = false;
               int count = 0;

               for (String modifier : var3x.getModifiers()) {
                  String text = MythicLib.plugin.getMMOConfig().decimals.format(var3x.getParameter(modifier));
                  itemStackBuilder.getLore().registerPlaceholder("ability_" + var3x.getAbility().getHandler().getId().toLowerCase() + "_" + modifier, text);
                  builder.append(flag ? this.modifierNewLine : this.modifierSplitter);
                  builder.append(this.modifierForEach.replace("{modifier}", var3x.getAbility().getParameterName(modifier)).replace("{value}", text));
                  flag = false;
                  if (this.modifiersPerLine > 0 && ++count >= this.modifiersPerLine) {
                     count = 0;
                     flag = true;
                  }
               }

               list.add(builder.toString());
               if (this.useAbilitySplitter) {
                  list.add(this.abilitySplitter);
               }
            }
         );
      if (this.useAbilitySplitter && !list.isEmpty()) {
         list.remove(list.size() - 1);
      }

      itemStackBuilder.getLore().insert("abilities", list);
      itemStackBuilder.addItemTag(this.getAppliedNBT(abilityListData));
   }

   @Override
   public String getLegacyTranslationPath() {
      return "ability-format";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull AbilityListData abilityListData) {
      ArrayList list = new ArrayList();
      JsonArray jsonArray = new JsonArray();

      for (AbilityData ability : abilityListData.getAbilities()) {
         jsonArray.add(ability.toJson());
      }

      list.add(new ItemTag(this.getNBTPath(), jsonArray.toString()));
      return list;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      new AbilityListEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String text2 = (String)objs[0];
      String text3 = (String)objs[1];
      String text4 = text.toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z0-9_]", "");
      if (text3.equals("ability")) {
         Validate.isTrue(MMOItems.plugin.getSkills().hasSkill(text4), text4 + " is not a valid ability! You may check the ability list using /mi list ability.");
         RegisteredSkill skill = MMOItems.plugin.getSkills().getSkill(text4);
         editionInventory.getEditedSection().set("ability." + text2, null);
         editionInventory.getEditedSection().set("ability." + text2 + ".type", text4);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Successfully set the ability to " + ChatColor.GOLD + skill.getName() + ChatColor.GRAY + ".");
      } else if (text3.equals("mode")) {
         TriggerType triggerType = TriggerType.valueOf(text4);
         editionInventory.getEditedSection().set("ability." + text2 + ".mode", triggerType.name());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Successfully set the trigger to " + ChatColor.GOLD + triggerType.getName() + ChatColor.GRAY + ".");
      } else {
         new NumericStatFormula(text)
            .fillConfigurationSection(editionInventory.getEditedSection(), "ability." + text2 + "." + text3, NumericStatFormula.FormulaSaveOption.NONE);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix() + ChatColor.GOLD + UtilityMethods.caseOnWords(text3.replace("-", " ")) + ChatColor.GRAY + " successfully added."
            );
      }
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomAbilityListData> optional) {
      texts.add(ChatColor.GRAY + "Current Abilities: " + ChatColor.GOLD + (optional.isPresent() ? ((RandomAbilityListData)optional.get()).getAbilities().size() : 0));
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to edit the item abilities.");
   }

   @NotNull
   public AbilityListData getClearStatData() {
      return new AbilityListData();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      AbilityListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public AbilityListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            AbilityListData abilityListData = new AbilityListData();

            for (JsonElement asJson : new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonArray()) {
               if (asJson.isJsonObject()) {
                  JsonObject asJsonObject = asJson.getAsJsonObject();
                  abilityListData.add(new AbilityData(asJsonObject));
               }
            }

            return abilityListData;
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }
}
