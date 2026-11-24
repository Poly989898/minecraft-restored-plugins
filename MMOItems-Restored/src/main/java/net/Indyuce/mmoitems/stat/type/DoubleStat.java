package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackMessage;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.PlusMinusPercent;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.manager.StatManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.UpgradeTemplate;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.data.type.UpgradeInfo;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleStat extends ItemStat<NumericStatFormula, DoubleData> implements Upgradable, Previewable<NumericStatFormula, DoubleData> {
   private final boolean moreIsBetter;
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####");

   public DoubleStat(String text, Material material, String text2, String[] texts) {
      this(text, material, text2, texts, new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public DoubleStat(String text, Material material, String text2, String text3) {
      this(text, material, text2, MMOUtils.trimString(50, text3), new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public DoubleStat(String text, Material material, String text2, String[] texts, String[] texts2, Material... materials) {
      this(text, material, text2, texts, texts2, true, materials);
   }

   public DoubleStat(String text, Material material, String text2, String[] texts, String[] texts2, boolean flag, Material... materials) {
      super(text, material, text2, texts, texts2, materials);
      this.moreIsBetter = flag;
   }

   public boolean handleNegativeStats() {
      return true;
   }

   public double multiplyWhenDisplaying() {
      return 1.0;
   }

   public boolean moreIsBetter() {
      return this.moreIsBetter;
   }

   public NumericStatFormula whenInitialized(Object obj) {
      if (obj instanceof Number) {
         return new NumericStatFormula(Double.parseDouble(obj.toString()), 0.0, 0.0, 0.0);
      } else if (obj instanceof ConfigurationSection) {
         return new NumericStatFormula(obj);
      } else {
         throw new IllegalArgumentException("Must specify a number or a config section");
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      double value = doubleData.getValue();
      if (!(value < 0.0) || this.handleNegativeStats()) {
         double value2 = 0.0;
         if (UpgradeTemplate.isDisplayingUpgrades() && itemStackBuilder.getMMOItem().getUpgradeLevel() != 0) {
            StatHistory statHistory = itemStackBuilder.getMMOItem().getStatHistory(this);
            if (statHistory != null) {
               DoubleData doubleData2 = (DoubleData)statHistory.recalculateUnupgraded();
               value2 = value - doubleData2.getValue();
            }
         }

         if (value != 0.0 || value2 != 0.0) {
            String text = formatPath(this.getId(), this.getGeneralStatFormat(), this.moreIsBetter, value * this.multiplyWhenDisplaying());
            if (value2 != 0.0) {
               text = text
                  + UpgradeTemplate.getUpgradeChangeSuffix(
                     plus(true, value2 * this.multiplyWhenDisplaying()) + MythicLib.plugin.getMMOConfig().decimals.format(value2 * this.multiplyWhenDisplaying()),
                     !this.isGood(value2 * this.multiplyWhenDisplaying())
                  );
            }

            itemStackBuilder.getLore().insert(this.getPath(), text);
         }

         if (doubleData.getValue() != 0.0) {
            itemStackBuilder.addItemTag(this.getAppliedNBT(doubleData));
         }
      }
   }

   @Deprecated
   public static String formatPath(@NotNull String text, boolean flag, double value) {
      return formatPath("ATTACK_DAMAGE", text, flag, value);
   }

   @Deprecated
   public static String formatPath(@NotNull String text, boolean flag, double value, double value2) {
      return formatPath("ATTACK_DAMAGE", text, flag, value, value2);
   }

   @NotNull
   public static String formatPath(@NotNull String text, @NotNull String text2, boolean flag, double value) {
      return formatPath(text, text2, flag, true, value);
   }

   @NotNull
   public static String formatPath(@NotNull String text, @NotNull String text2, boolean flag, boolean flag2, double value) {
      String text3 = StatManager.format(text, value);
      String colorPrefix = getColorPrefix(value < 0.0 && flag);
      return text2.replace("{value}", colorPrefix + text3).replace("<plus>", colorPrefix + plus(flag2, value));
   }

   @NotNull
   public static String formatPath(@NotNull String text, @NotNull String text2, boolean flag, double value, double value2) {
      return formatPath(text, text2, flag, true, value, value2);
   }

   @NotNull
   public static String formatPath(@NotNull String text, @NotNull String text2, boolean flag, boolean flag2, double value, double value2) {
      if (SilentNumbers.round(value, 2) == SilentNumbers.round(value2, 2)) {
         return formatPath(text, text2, flag, flag2, value);
      }

      String text3 = plus(flag2, value) + StatManager.format(text, value);
      String text4 = (flag2 && value < 0.0 && value2 > 0.0 ? "+" : "") + StatManager.format(text, value2);
      String colorPrefix = getColorPrefix(value < 0.0 && flag);
      String colorPrefix2 = getColorPrefix(value2 < 0.0 && flag);
      return text2.replace("<plus>", "")
         .replace("{value}", colorPrefix + text3 + MMOItems.plugin.getConfig().getString("stats-displaying.range-dash", "⎓") + colorPrefix2 + text4);
   }

   @NotNull
   private static String getColorPrefix(boolean flag) {
      return MMOItems.plugin.getConfig().getString("stats-displaying.color-" + (flag ? "negative" : "positive"), "");
   }

   @NotNull
   private static String plus(boolean flag, double value) {
      return flag && value > 0.0 ? "+" : "";
   }

   public void whenPreviewed(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData, @NotNull NumericStatFormula numericStatFormula) {
      Validate.isTrue(doubleData instanceof DoubleData, "Current Data is not Double Data");
      Validate.isTrue(numericStatFormula instanceof NumericStatFormula, "Template Data is not Numeric Stat Formula");
      double value = numericStatFormula.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
      double value2 = numericStatFormula.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
      if (!(value2 < 0.0) || this.handleNegativeStats()) {
         if (value < 0.0 && !this.handleNegativeStats()) {
            value = 0.0;
         }

         itemStackBuilder.addItemTag(this.getAppliedNBT(doubleData));
         if (value != 0.0 || value2 != 0.0) {
            String text = formatPath(
               this.getId(), this.getGeneralStatFormat(), this.moreIsBetter(), value * this.multiplyWhenDisplaying(), value2 * this.multiplyWhenDisplaying()
            );
            itemStackBuilder.getLore().insert(this.getPath(), text);
         }
      }
   }

   public boolean isGood(double value) {
      return this.moreIsBetter() ? value >= 0.0 : value <= 0.0;
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData doubleData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), doubleData.getValue()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE));
      }

      DoubleData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      return tagAtPath != null ? new DoubleData(SilentNumbers.round((Double)tagAtPath.getValue(), 4)) : null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set(this.getPath(), null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ChatColor.GRAY + ".");
      } else {
         new StatEdition(editionInventory, this)
            .enable(
               "Write in the chat the numeric value you want.",
               "Second Format: {Base} {Scaling Value} {Spread} {Max Spread} {Hard Min} {Hard Max}",
               "Third Format: {Min Value} -> {Max Value}"
            );
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      NumericStatFormula numericStatFormula = new NumericStatFormula(text);
      numericStatFormula.fillConfigurationSection(editionInventory.getEditedSection(), this.getPath(), NumericStatFormula.FormulaSaveOption.DELETE_IF_ZERO);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + this.getName()
               + " successfully changed to {"
               + numericStatFormula.getBase()
               + " - "
               + numericStatFormula.getScale()
               + " - "
               + numericStatFormula.getSpread()
               + " - "
               + numericStatFormula.getMaxSpread()
               + " - ("
               + numericStatFormula.getMin()
               + " -> "
               + numericStatFormula.getMax()
               + ") }"
         );
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<NumericStatFormula> optional) {
      if (optional.isPresent()) {
         NumericStatFormula numericStatFormula = (NumericStatFormula)optional.get();
         if (numericStatFormula.isUniform()) {
            texts.add(
               ChatColor.GRAY
                  + "Uniform: "
                  + ChatColor.GREEN
                  + DECIMAL_FORMAT.format(numericStatFormula.getMin())
                  + ChatColor.GRAY
                  + " -> "
                  + ChatColor.GREEN
                  + DECIMAL_FORMAT.format(numericStatFormula.getMax())
            );
         } else {
            texts.add(
               ChatColor.GRAY
                  + "Base Value: "
                  + ChatColor.GREEN
                  + DECIMAL_FORMAT.format(numericStatFormula.getBase())
                  + (numericStatFormula.getScale() != 0.0 ? ChatColor.GRAY + " (+" + ChatColor.GREEN + DECIMAL_FORMAT.format(numericStatFormula.getScale()) + ChatColor.GRAY + "/Lvl)" : "")
            );
            if (numericStatFormula.getSpread() > 0.0) {
               texts.add(
                  ChatColor.GRAY
                     + "Spread: "
                     + ChatColor.GREEN
                     + DECIMAL_FORMAT.format(numericStatFormula.getSpread() * 100.0)
                     + "%"
                     + ChatColor.GRAY
                     + " (Max: "
                     + ChatColor.GREEN
                     + DECIMAL_FORMAT.format(numericStatFormula.getMaxSpread() * 100.0)
                     + "%"
                     + ChatColor.GRAY
                     + ")"
               );
            }

            if (numericStatFormula.hasMin()) {
               texts.add(ChatColor.GRAY + "Min: " + ChatColor.GREEN + DECIMAL_FORMAT.format(numericStatFormula.getMin()));
            }

            if (numericStatFormula.hasMax()) {
               texts.add(ChatColor.GRAY + "Max: " + ChatColor.GREEN + DECIMAL_FORMAT.format(numericStatFormula.getMax()));
            }
         }
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + "---");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @NotNull
   public DoubleData getClearStatData() {
      return new DoubleData(0.0);
   }

   @NotNull
   @Override
   public UpgradeInfo loadUpgradeInfo(@Nullable Object obj) {
      return DoubleStat.DoubleUpgradeInfo.GetFrom(obj);
   }

   @NotNull
   @Override
   public StatData apply(@NotNull StatData statData, @NotNull UpgradeInfo upgradeInfo, int count) {
      int count2 = count;
      if (statData instanceof DoubleData && upgradeInfo instanceof DoubleStat.DoubleUpgradeInfo) {
         double value = ((DoubleData)statData).getValue();
         if (count2 > 0) {
            while (count2 > 0) {
               value = ((DoubleStat.DoubleUpgradeInfo)upgradeInfo).getPMP().apply(value);
               count2--;
            }
         } else if (count2 < 0) {
            while (count2 < 0) {
               value = ((DoubleStat.DoubleUpgradeInfo)upgradeInfo).getPMP().reverse(value);
               count2++;
            }
         }

         ((DoubleData)statData).setValue(value);
      }

      return statData;
   }

   public static class DoubleUpgradeInfo implements UpgradeInfo {
      @NotNull
      PlusMinusPercent pmp;

      @NotNull
      public static DoubleStat.DoubleUpgradeInfo GetFrom(@Nullable Object obj) {
         Validate.notNull(obj, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation must not be null", new String[0]));
         String text = obj.toString();
         if (text.isEmpty()) {
            throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation is empty", new String[0]));
         }

         char ch = text.charAt(0);
         if (ch == 's') {
            text = text.substring(1);
         } else if (ch != '+' && ch != '-' && ch != 'n') {
            text = '+' + text;
         }

         FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
         PlusMinusPercent plusMinusPercent = PlusMinusPercent.getFromString(text, friendlyFeedbackProvider);
         if (plusMinusPercent == null) {
            throw new IllegalArgumentException(
               ((FriendlyFeedbackMessage)friendlyFeedbackProvider.getFeedbackOf(FriendlyFeedbackCategory.ERROR).get(0)).forConsole(friendlyFeedbackProvider.getPalette())
            );
         } else {
            return new DoubleStat.DoubleUpgradeInfo(plusMinusPercent);
         }
      }

      public DoubleUpgradeInfo(@NotNull PlusMinusPercent plusMinusPercent) {
         this.pmp = plusMinusPercent;
      }

      @NotNull
      public PlusMinusPercent getPMP() {
         return this.pmp;
      }
   }
}
