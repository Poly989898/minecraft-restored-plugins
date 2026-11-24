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

   public RandomElementListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new RandomElementListData((ConfigurationSection)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new ElementsEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("element")) {
         editionInventory.getEditedSection().set("element", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Elements successfully removed.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String elementText = objs[0].toString();
      NumericStatFormula numericStatFormula = new NumericStatFormula(text);
      numericStatFormula.fillConfigurationSection(editionInventory.getEditedSection(), "element." + elementText);
      String elementName = elementText.split("\\.")[0];
      if (editionInventory.getEditedSection().contains("element")) {
         if (editionInventory.getEditedSection().getConfigurationSection("element").contains(elementName)
            && editionInventory.getEditedSection().getConfigurationSection("element." + elementName).getKeys(false).isEmpty()) {
            editionInventory.getEditedSection().set("element." + elementName, null);
         }

         if (editionInventory.getEditedSection().getConfigurationSection("element").getKeys(false).isEmpty()) {
            editionInventory.getEditedSection().set("element", null);
         }
      }

      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + ChatColor.RED
               + UtilityMethods.caseOnWords(elementText.replace(".", " "))
               + ChatColor.GRAY
               + " successfully changed to "
               + ChatColor.GOLD
               + numericStatFormula
               + ChatColor.GRAY
               + "."
         );
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomElementListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         RandomElementListData randomElementListData = (RandomElementListData)optional.get();
         randomElementListData.getKeys()
            .forEach(
               var2x -> texts.add(
                  ChatColor.GRAY
                     + "* "
                     + var2x.getKey().getName()
                     + " "
                     + var2x.getValue().getName()
                     + ": "
                     + ChatColor.RED
                     + randomElementListData.getStat(var2x.getKey(), var2x.getValue())
               )
            );
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to access the elements edition menu.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove all the elements.");
   }

   @NotNull
   public ElementListData getClearStatData() {
      return new ElementListData();
   }

   @Override
   public void loadConfiguration(@NotNull ConfigurationSection section, @NotNull Object obj) {
      if (obj instanceof String) {
         for (ElementStatType elementStatType : ElementStatType.values()) {
            this.statFormat.put(elementStatType, section.getString("elemental-" + elementStatType.lowerCaseName(), "<TranslationNotFound:" + elementStatType.name().toLowerCase() + ">"));
         }
      } else {
         Validate.isTrue(obj instanceof ConfigurationSection, "Must be a config section");
         ConfigurationSection elementSection = (ConfigurationSection)obj;

         for (ElementStatType elementStatType : ElementStatType.values()) {
            this.statFormat.put(elementStatType, elementSection.getString(elementStatType.lowerCaseName(), "<TranslationNotFound:" + elementStatType.lowerCaseName() + ">"));
         }
      }
   }

   @Override
   public String getLegacyTranslationPath() {
      return "elemental-damage";
   }

   @NotNull
   private String formatForLore(Element element, ElementStatType elementStatType) {
      return this.statFormat.get(elementStatType).replace("{color}", element.getColor()).replace("{icon}", element.getLoreIcon()).replace("{element}", element.getName());
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ElementListData elementListData) {
      ArrayList list = new ArrayList();

      for (Pair key : elementListData.getKeys()) {
         String text = this.formatForLore((Element)key.getKey(), (ElementStatType)key.getValue());
         double stat = elementListData.getStat((Element)key.getKey(), (ElementStatType)key.getValue());
         list.add(DoubleStat.formatPath("ELEMENTAL_STAT", text, true, stat));
      }

      if (!list.isEmpty()) {
         itemStackBuilder.getLore().insert("elements", list);
      }

      itemStackBuilder.addItemTag(this.getAppliedNBT(elementListData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ElementListData elementListData) {
      ArrayList list = new ArrayList();

      for (Pair key : elementListData.getKeys()) {
         list.add(
            new ItemTag(
               "MMOITEMS_" + ((ElementStatType)key.getValue()).getConcatenatedTagPath((Element)key.getKey()),
               elementListData.getStat((Element)key.getKey(), (ElementStatType)key.getValue())
            )
         );
      }

      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();

      for (Element element : Element.values()) {
         for (ElementStatType elementStatType : ElementStatType.values()) {
            String text = "MMOITEMS_" + elementStatType.getConcatenatedTagPath(element);
            if (readMMOItem.getNBT().hasTag(text)) {
               list.add(ItemTag.getTagAtPath(text, readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE));
            }
         }
      }

      ElementListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public ElementListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ElementListData elementListData = new ElementListData();

      for (Element element : Element.values()) {
         for (ElementStatType elementStatType : ElementStatType.values()) {
            String text = "MMOITEMS_" + elementStatType.getConcatenatedTagPath(element);
            ItemTag tagAtPath = ItemTag.getTagAtPath(text, itemTags);
            if (tagAtPath != null) {
               elementListData.setStat(element, elementStatType, (Double)tagAtPath.getValue());
            }
         }
      }

      return elementListData.isEmpty() ? null : elementListData;
   }

   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ElementListData elementListData, @NotNull RandomElementListData randomElementListData) {
      Validate.isTrue(elementListData instanceof ElementListData, "Current Data is not ElementListData");
      Validate.isTrue(randomElementListData instanceof RandomElementListData, "Template Data is not RandomElementListData");
      ArrayList list = new ArrayList();

      for (Element element : Element.values()) {
         for (ElementStatType elementStatType : ElementStatType.values()) {
            NumericStatFormula stat = randomElementListData.getStat(element, elementStatType);
            double value = stat.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
            double upperBound = stat.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
            if (value != 0.0 || upperBound != 0.0) {
               String text = DoubleStat.formatPath(elementStatType.getConcatenatedTagPath(element), this.formatForLore(element, elementStatType), true, value, upperBound);
               list.add(text);
            }
         }
      }

      if (!list.isEmpty()) {
         itemStackBuilder.getLore().insert("elements", list);
      }

      itemStackBuilder.addItemTag(this.getAppliedNBT(elementListData));
   }
}
