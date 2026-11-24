package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.element.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.ElementsEdition;
import net.Indyuce.mmoitems.stat.data.ElementListData;
import net.Indyuce.mmoitems.stat.data.random.RandomElementListData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.Previewable;
import net.Indyuce.mmoitems.util.ElementStatType;
import net.Indyuce.mmoitems.util.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Elements extends ItemStat<RandomElementListData, ElementListData> implements Previewable<RandomElementListData, ElementListData> {
   private Map<ElementStatType, String> statFormat = new HashMap<>();

   public Elements() {
      super("ELEMENT", Material.SLIME_BALL, "Elements", new String[]{"The elements of your item."}, new String[]{"equipment", "ornament", "gem_stone"});
   }

   public RandomElementListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new RandomElementListData((ConfigurationSection)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new ElementsEdition(var1.getPlayer(), var1.getEdited()).open(var1);
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("element")) {
         var1.getEditedSection().set("element", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Elements successfully removed.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = var3[0].toString();
      NumericStatFormula var5 = new NumericStatFormula(var2);
      var5.fillConfigurationSection(var1.getEditedSection(), "element." + var4);
      String var6 = var4.split("\\.")[0];
      if (var1.getEditedSection().contains("element")) {
         if (var1.getEditedSection().getConfigurationSection("element").contains(var6)
            && var1.getEditedSection().getConfigurationSection("element." + var6).getKeys(false).isEmpty()) {
            var1.getEditedSection().set("element." + var6, null);
         }

         if (var1.getEditedSection().getConfigurationSection("element").getKeys(false).isEmpty()) {
            var1.getEditedSection().set("element", null);
         }
      }

      var1.registerTemplateEdition();
      var1.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + ChatColor.RED
               + UtilityMethods.caseOnWords(var4.replace(".", " "))
               + ChatColor.GRAY
               + " successfully changed to "
               + ChatColor.GOLD
               + var5
               + ChatColor.GRAY
               + "."
         );
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomElementListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         RandomElementListData var3 = (RandomElementListData)var2.get();
         var3.getKeys()
            .forEach(
               var2x -> var1.add(
                  ChatColor.GRAY
                     + "* "
                     + var2x.getKey().getName()
                     + " "
                     + var2x.getValue().getName()
                     + ": "
                     + ChatColor.RED
                     + var3.getStat(var2x.getKey(), var2x.getValue())
               )
            );
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to access the elements edition menu.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove all the elements.");
   }

   @NotNull
   public ElementListData getClearStatData() {
      return new ElementListData();
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection var1, @NotNull Object var2) {
      if (var2 instanceof String) {
         for (ElementStatType var6 : ElementStatType.values()) {
            this.statFormat.put(var6, var1.getString("elemental-" + var6.lowerCaseName(), "<TranslationNotFound:" + var6.name().toLowerCase() + ">"));
         }
      } else {
         Validate.isTrue(var2 instanceof ConfigurationSection, "Must be a config section");
         ConfigurationSection var8 = (ConfigurationSection)var2;

         for (ElementStatType var7 : ElementStatType.values()) {
            this.statFormat.put(var7, var8.getString(var7.lowerCaseName(), "<TranslationNotFound:" + var7.lowerCaseName() + ">"));
         }
      }
   }

   @Override
   public String getLegacyTranslationPath() {
      return "elemental-damage";
   }

   @NotNull
   private String formatForLore(Element var1, ElementStatType var2) {
      return this.statFormat.get(var2).replace("{color}", var1.getColor()).replace("{icon}", var1.getLoreIcon()).replace("{element}", var1.getName());
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull ElementListData var2) {
      ArrayList var3 = new ArrayList();

      for (Pair var5 : var2.getKeys()) {
         String var6 = this.formatForLore((Element)var5.getKey(), (ElementStatType)var5.getValue());
         double var7 = var2.getStat((Element)var5.getKey(), (ElementStatType)var5.getValue());
         var3.add(DoubleStat.formatPath("ELEMENTAL_STAT", var6, true, var7));
      }

      if (!var3.isEmpty()) {
         var1.getLore().insert("elements", var3);
      }

      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ElementListData var1) {
      ArrayList var2 = new ArrayList();

      for (Pair var4 : var1.getKeys()) {
         var2.add(
            new ItemTag(
               "MMOITEMS_" + ((ElementStatType)var4.getValue()).getConcatenatedTagPath((Element)var4.getKey()),
               var1.getStat((Element)var4.getKey(), (ElementStatType)var4.getValue())
            )
         );
      }

      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();

      for (Element var4 : Element.values()) {
         for (ElementStatType var8 : ElementStatType.values()) {
            String var9 = "MMOITEMS_" + var8.getConcatenatedTagPath(var4);
            if (var1.getNBT().hasTag(var9)) {
               var2.add(ItemTag.getTagAtPath(var9, var1.getNBT(), SupportedNBTTagValues.DOUBLE));
            }
         }
      }

      ElementListData var10 = this.getLoadedNBT(var2);
      if (var10 != null) {
         var1.setData(this, var10);
      }
   }

   @Nullable
   public ElementListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ElementListData var2 = new ElementListData();

      for (Element var4 : Element.values()) {
         for (ElementStatType var8 : ElementStatType.values()) {
            String var9 = "MMOITEMS_" + var8.getConcatenatedTagPath(var4);
            ItemTag var10 = ItemTag.getTagAtPath(var9, var1);
            if (var10 != null) {
               var2.setStat(var4, var8, (Double)var10.getValue());
            }
         }
      }

      return var2.isEmpty() ? null : var2;
   }

   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull ElementListData var2, @NotNull RandomElementListData var3) {
      Validate.isTrue(var2 instanceof ElementListData, "Current Data is not ElementListData");
      Validate.isTrue(var3 instanceof RandomElementListData, "Template Data is not RandomElementListData");
      ArrayList var4 = new ArrayList();

      for (Element var6 : Element.values()) {
         for (ElementStatType var10 : ElementStatType.values()) {
            NumericStatFormula var11 = var3.getStat(var6, var10);
            double var12 = var11.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
            double var14 = var11.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
            if (var12 != 0.0 || var14 != 0.0) {
               String var16 = DoubleStat.formatPath(var10.getConcatenatedTagPath(var6), this.formatForLore(var6, var10), true, var12, var14);
               var4.add(var16);
            }
         }
      }

      if (!var4.isEmpty()) {
         var1.getLore().insert("elements", var4);
      }

      var1.addItemTag(this.getAppliedNBT(var2));
   }
}
