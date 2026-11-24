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

   public RandomAbilityListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a valid config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      RandomAbilityListData var3 = new RandomAbilityListData();

      for (String var5 : var2.getKeys(false)) {
         var3.add(new RandomAbilityData(var2.getConfigurationSection(var5)));
      }

      return var3;
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection var1, @NotNull Object var2) {
      if (var2 instanceof ConfigurationSection) {
         ConfigurationSection var3 = (ConfigurationSection)var2;
         this.generalFormat = var3.getString("general-format");
         this.modifierIfAny = var3.getString("modifier-if-any", "");
         this.modifierNewLine = "\n" + var3.getString("modifier-new-line", "");
         this.modifierForEach = var3.getString("modifier-foreach");
         this.modifierSplitter = var3.getString("modifier-splitter");
         this.abilitySplitter = var3.getString("ability-splitter.format");
         this.useAbilitySplitter = var3.getBoolean("ability-splitter.enabled");
         this.modifiersPerLine = var3.getInt("modifiers-per-line");
      } else {
         this.generalFormat = var1.getString("ability-format");
         this.modifierIfAny = "\n";
         this.modifierNewLine = "\n";
         this.modifierForEach = var1.getString("ability-modifier");
         this.modifierSplitter = "";
         this.abilitySplitter = var1.getString("ability-splitter");
         this.useAbilitySplitter = this.abilitySplitter != null && !this.abilitySplitter.isEmpty();
         this.modifiersPerLine = 1;
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull AbilityListData var2) {
      ArrayList var3 = new ArrayList();
      var2.getAbilities()
         .forEach(
            var3x -> {
               StringBuilder var4 = new StringBuilder(
                  this.generalFormat
                     .replace("{trigger}", MMOItems.plugin.getLanguage().getTriggerTypeName(var3x.getTrigger()))
                     .replace("{ability}", var3x.getAbility().getName())
               );
               if (!var3x.getModifiers().isEmpty()) {
                  var4.append(this.modifierIfAny);
               }

               boolean var5 = false;
               int var6 = 0;

               for (String var8 : var3x.getModifiers()) {
                  String var9 = MythicLib.plugin.getMMOConfig().decimals.format(var3x.getParameter(var8));
                  var1.getLore().registerPlaceholder("ability_" + var3x.getAbility().getHandler().getId().toLowerCase() + "_" + var8, var9);
                  var4.append(var5 ? this.modifierNewLine : this.modifierSplitter);
                  var4.append(this.modifierForEach.replace("{modifier}", var3x.getAbility().getParameterName(var8)).replace("{value}", var9));
                  var5 = false;
                  if (this.modifiersPerLine > 0 && ++var6 >= this.modifiersPerLine) {
                     var6 = 0;
                     var5 = true;
                  }
               }

               var3.add(var4.toString());
               if (this.useAbilitySplitter) {
                  var3.add(this.abilitySplitter);
               }
            }
         );
      if (this.useAbilitySplitter && !var3.isEmpty()) {
         var3.remove(var3.size() - 1);
      }

      var1.getLore().insert("abilities", var3);
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @Override
   public String getLegacyTranslationPath() {
      return "ability-format";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull AbilityListData var1) {
      ArrayList var2 = new ArrayList();
      JsonArray var3 = new JsonArray();

      for (AbilityData var5 : var1.getAbilities()) {
         var3.add(var5.toJson());
      }

      var2.add(new ItemTag(this.getNBTPath(), var3.toString()));
      return var2;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      new AbilityListEdition(var1.getPlayer(), var1.getEdited()).open(var1);
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = (String)var3[0];
      String var5 = (String)var3[1];
      String var6 = var2.toUpperCase().replace("-", "_").replace(" ", "_").replaceAll("[^A-Z0-9_]", "");
      if (var5.equals("ability")) {
         Validate.isTrue(MMOItems.plugin.getSkills().hasSkill(var6), var6 + " is not a valid ability! You may check the ability list using /mi list ability.");
         RegisteredSkill var8 = MMOItems.plugin.getSkills().getSkill(var6);
         var1.getEditedSection().set("ability." + var4, null);
         var1.getEditedSection().set("ability." + var4 + ".type", var6);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Successfully set the ability to " + ChatColor.GOLD + var8.getName() + ChatColor.GRAY + ".");
      } else if (var5.equals("mode")) {
         TriggerType var7 = TriggerType.valueOf(var6);
         var1.getEditedSection().set("ability." + var4 + ".mode", var7.name());
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Successfully set the trigger to " + ChatColor.GOLD + var7.getName() + ChatColor.GRAY + ".");
      } else {
         new NumericStatFormula(var2)
            .fillConfigurationSection(var1.getEditedSection(), "ability." + var4 + "." + var5, NumericStatFormula.FormulaSaveOption.NONE);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix() + ChatColor.GOLD + UtilityMethods.caseOnWords(var5.replace("-", " ")) + ChatColor.GRAY + " successfully added."
            );
      }
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomAbilityListData> var2) {
      var1.add(ChatColor.GRAY + "Current Abilities: " + ChatColor.GOLD + (var2.isPresent() ? ((RandomAbilityListData)var2.get()).getAbilities().size() : 0));
      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to edit the item abilities.");
   }

   @NotNull
   public AbilityListData getClearStatData() {
      return new AbilityListData();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      AbilityListData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public AbilityListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            AbilityListData var3 = new AbilityListData();

            for (JsonElement var6 : new JsonParser().parse((String)var2.getValue()).getAsJsonArray()) {
               if (var6.isJsonObject()) {
                  JsonObject var7 = var6.getAsJsonObject();
                  var3.add(new AbilityData(var7));
               }
            }

            return var3;
         } catch (JsonSyntaxException | IllegalStateException var8) {
         }
      }

      return null;
   }
}
