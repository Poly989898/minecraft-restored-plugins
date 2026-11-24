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

   public DoubleStat(String var1, Material var2, String var3, String[] var4) {
      this(var1, var2, var3, var4, new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public DoubleStat(String var1, Material var2, String var3, String var4) {
      this(var1, var2, var3, MMOUtils.trimString(50, var4), new String[]{"!miscellaneous", "!block", "all"}, true);
   }

   public DoubleStat(String var1, Material var2, String var3, String[] var4, String[] var5, Material... var6) {
      this(var1, var2, var3, var4, var5, true, var6);
   }

   public DoubleStat(String var1, Material var2, String var3, String[] var4, String[] var5, boolean var6, Material... var7) {
      super(var1, var2, var3, var4, var5, var7);
      this.moreIsBetter = var6;
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

   public NumericStatFormula whenInitialized(Object var1) {
      if (var1 instanceof Number) {
         return new NumericStatFormula(Double.parseDouble(var1.toString()), 0.0, 0.0, 0.0);
      } else if (var1 instanceof ConfigurationSection) {
         return new NumericStatFormula(var1);
      } else {
         throw new IllegalArgumentException("Must specify a number or a config section");
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      double var3 = var2.getValue();
      if (!(var3 < 0.0) || this.handleNegativeStats()) {
         double var5 = 0.0;
         if (UpgradeTemplate.isDisplayingUpgrades() && var1.getMMOItem().getUpgradeLevel() != 0) {
            StatHistory var7 = var1.getMMOItem().getStatHistory(this);
            if (var7 != null) {
               DoubleData var8 = (DoubleData)var7.recalculateUnupgraded();
               var5 = var3 - var8.getValue();
            }
         }

         if (var3 != 0.0 || var5 != 0.0) {
            String var9 = formatPath(this.getId(), this.getGeneralStatFormat(), this.moreIsBetter, var3 * this.multiplyWhenDisplaying());
            if (var5 != 0.0) {
               var9 = var9
                  + UpgradeTemplate.getUpgradeChangeSuffix(
                     plus(true, var5 * this.multiplyWhenDisplaying()) + MythicLib.plugin.getMMOConfig().decimals.format(var5 * this.multiplyWhenDisplaying()),
                     !this.isGood(var5 * this.multiplyWhenDisplaying())
                  );
            }

            var1.getLore().insert(this.getPath(), var9);
         }

         if (var2.getValue() != 0.0) {
            var1.addItemTag(this.getAppliedNBT(var2));
         }
      }
   }

   @Deprecated
   public static String formatPath(@NotNull String var0, boolean var1, double var2) {
      return formatPath("ATTACK_DAMAGE", var0, var1, var2);
   }

   @Deprecated
   public static String formatPath(@NotNull String var0, boolean var1, double var2, double var4) {
      return formatPath("ATTACK_DAMAGE", var0, var1, var2, var4);
   }

   @NotNull
   public static String formatPath(@NotNull String var0, @NotNull String var1, boolean var2, double var3) {
      return formatPath(var0, var1, var2, true, var3);
   }

   @NotNull
   public static String formatPath(@NotNull String var0, @NotNull String var1, boolean var2, boolean var3, double var4) {
      String var6 = StatManager.format(var0, var4);
      String var7 = getColorPrefix(var4 < 0.0 && var2);
      return var1.replace("{value}", var7 + var6).replace("<plus>", var7 + plus(var3, var4));
   }

   @NotNull
   public static String formatPath(@NotNull String var0, @NotNull String var1, boolean var2, double var3, double var5) {
      return formatPath(var0, var1, var2, true, var3, var5);
   }

   @NotNull
   public static String formatPath(@NotNull String var0, @NotNull String var1, boolean var2, boolean var3, double var4, double var6) {
      if (SilentNumbers.round(var4, 2) == SilentNumbers.round(var6, 2)) {
         return formatPath(var0, var1, var2, var3, var4);
      }

      String var8 = plus(var3, var4) + StatManager.format(var0, var4);
      String var9 = (var3 && var4 < 0.0 && var6 > 0.0 ? "+" : "") + StatManager.format(var0, var6);
      String var10 = getColorPrefix(var4 < 0.0 && var2);
      String var11 = getColorPrefix(var6 < 0.0 && var2);
      return var1.replace("<plus>", "")
         .replace("{value}", var10 + var8 + MMOItems.plugin.getConfig().getString("stats-displaying.range-dash", "⎓") + var11 + var9);
   }

   @NotNull
   private static String getColorPrefix(boolean var0) {
      return MMOItems.plugin.getConfig().getString("stats-displaying.color-" + (var0 ? "negative" : "positive"), "");
   }

   @NotNull
   private static String plus(boolean var0, double var1) {
      return var0 && var1 > 0.0 ? "+" : "";
   }

   public void whenPreviewed(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2, @NotNull NumericStatFormula var3) {
      Validate.isTrue(var2 instanceof DoubleData, "Current Data is not Double Data");
      Validate.isTrue(var3 instanceof NumericStatFormula, "Template Data is not Numeric Stat Formula");
      double var4 = var3.calculate(0.0, NumericStatFormula.FormulaInputType.LOWER_BOUND);
      double var6 = var3.calculate(0.0, NumericStatFormula.FormulaInputType.UPPER_BOUND);
      if (!(var6 < 0.0) || this.handleNegativeStats()) {
         if (var4 < 0.0 && !this.handleNegativeStats()) {
            var4 = 0.0;
         }

         var1.addItemTag(this.getAppliedNBT(var2));
         if (var4 != 0.0 || var6 != 0.0) {
            String var8 = formatPath(
               this.getId(), this.getGeneralStatFormat(), this.moreIsBetter(), var4 * this.multiplyWhenDisplaying(), var6 * this.multiplyWhenDisplaying()
            );
            var1.getLore().insert(this.getPath(), var8);
         }
      }
   }

   public boolean isGood(double var1) {
      return this.moreIsBetter() ? var1 >= 0.0 : var1 <= 0.0;
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.getValue()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.DOUBLE));
      }

      DoubleData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new DoubleData(SilentNumbers.round((Double)var2.getValue(), 4)) : null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set(this.getPath(), null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ChatColor.GRAY + ".");
      } else {
         new StatEdition(var1, this)
            .enable(
               "Write in the chat the numeric value you want.",
               "Second Format: {Base} {Scaling Value} {Spread} {Max Spread} {Hard Min} {Hard Max}",
               "Third Format: {Min Value} -> {Max Value}"
            );
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      NumericStatFormula var4 = new NumericStatFormula(var2);
      var4.fillConfigurationSection(var1.getEditedSection(), this.getPath(), NumericStatFormula.FormulaSaveOption.DELETE_IF_ZERO);
      var1.registerTemplateEdition();
      var1.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + this.getName()
               + " successfully changed to {"
               + var4.getBase()
               + " - "
               + var4.getScale()
               + " - "
               + var4.getSpread()
               + " - "
               + var4.getMaxSpread()
               + " - ("
               + var4.getMin()
               + " -> "
               + var4.getMax()
               + ") }"
         );
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<NumericStatFormula> var2) {
      if (var2.isPresent()) {
         NumericStatFormula var3 = (NumericStatFormula)var2.get();
         if (var3.isUniform()) {
            var1.add(
               ChatColor.GRAY
                  + "Uniform: "
                  + ChatColor.GREEN
                  + DECIMAL_FORMAT.format(var3.getMin())
                  + ChatColor.GRAY
                  + " -> "
                  + ChatColor.GREEN
                  + DECIMAL_FORMAT.format(var3.getMax())
            );
         } else {
            var1.add(
               ChatColor.GRAY
                  + "Base Value: "
                  + ChatColor.GREEN
                  + DECIMAL_FORMAT.format(var3.getBase())
                  + (var3.getScale() != 0.0 ? ChatColor.GRAY + " (+" + ChatColor.GREEN + DECIMAL_FORMAT.format(var3.getScale()) + ChatColor.GRAY + "/Lvl)" : "")
            );
            if (var3.getSpread() > 0.0) {
               var1.add(
                  ChatColor.GRAY
                     + "Spread: "
                     + ChatColor.GREEN
                     + DECIMAL_FORMAT.format(var3.getSpread() * 100.0)
                     + "%"
                     + ChatColor.GRAY
                     + " (Max: "
                     + ChatColor.GREEN
                     + DECIMAL_FORMAT.format(var3.getMaxSpread() * 100.0)
                     + "%"
                     + ChatColor.GRAY
                     + ")"
               );
            }

            if (var3.hasMin()) {
               var1.add(ChatColor.GRAY + "Min: " + ChatColor.GREEN + DECIMAL_FORMAT.format(var3.getMin()));
            }

            if (var3.hasMax()) {
               var1.add(ChatColor.GRAY + "Max: " + ChatColor.GREEN + DECIMAL_FORMAT.format(var3.getMax()));
            }
         }
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + "---");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @NotNull
   public DoubleData getClearStatData() {
      return new DoubleData(0.0);
   }

   @NotNull
   @Override
   public UpgradeInfo loadUpgradeInfo(@Nullable Object var1) {
      return DoubleStat.DoubleUpgradeInfo.GetFrom(var1);
   }

   @NotNull
   @Override
   public StatData apply(@NotNull StatData var1, @NotNull UpgradeInfo var2, int var3) {
      int var4 = var3;
      if (var1 instanceof DoubleData && var2 instanceof DoubleStat.DoubleUpgradeInfo) {
         double var5 = ((DoubleData)var1).getValue();
         if (var4 > 0) {
            while (var4 > 0) {
               var5 = ((DoubleStat.DoubleUpgradeInfo)var2).getPMP().apply(var5);
               var4--;
            }
         } else if (var4 < 0) {
            while (var4 < 0) {
               var5 = ((DoubleStat.DoubleUpgradeInfo)var2).getPMP().reverse(var5);
               var4++;
            }
         }

         ((DoubleData)var1).setValue(var5);
      }

      return var1;
   }

   public static class DoubleUpgradeInfo implements UpgradeInfo {
      @NotNull
      PlusMinusPercent pmp;

      @NotNull
      public static DoubleStat.DoubleUpgradeInfo GetFrom(@Nullable Object var0) {
         Validate.notNull(var0, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation must not be null", new String[0]));
         String var1 = var0.toString();
         if (var1.isEmpty()) {
            throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation is empty", new String[0]));
         }

         char var2 = var1.charAt(0);
         if (var2 == 's') {
            var1 = var1.substring(1);
         } else if (var2 != '+' && var2 != '-' && var2 != 'n') {
            var1 = '+' + var1;
         }

         FriendlyFeedbackProvider var3 = new FriendlyFeedbackProvider(FFPMMOItems.get());
         PlusMinusPercent var4 = PlusMinusPercent.getFromString(var1, var3);
         if (var4 == null) {
            throw new IllegalArgumentException(
               ((FriendlyFeedbackMessage)var3.getFeedbackOf(FriendlyFeedbackCategory.ERROR).get(0)).forConsole(var3.getPalette())
            );
         } else {
            return new DoubleStat.DoubleUpgradeInfo(var4);
         }
      }

      public DoubleUpgradeInfo(@NotNull PlusMinusPercent var1) {
         this.pmp = var1;
      }

      @NotNull
      public PlusMinusPercent getPMP() {
         return this.pmp;
      }
   }
}
