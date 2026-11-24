package net.Indyuce.mmoitems.api.crafting;

import io.lumine.mythic.lib.api.crafting.uifilters.UIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemUIFilter implements UIFilter {
   ArrayList<String> validDataments;
   private static boolean reg = true;
   static MMOItemUIFilter global;

   @NotNull
   public String getIdentifier() {
      return "m";
   }

   public boolean matches(@NotNull ItemStack var1, @NotNull String var2, @NotNull String var3, @Nullable FriendlyFeedbackProvider var4) {
      var2 = var2.replace(" ", "_").replace("-", "_").toUpperCase();
      var3 = var3.replace(" ", "_").replace("-", "_").toUpperCase();
      if (!this.isValid(var2, var3, var4)) {
         return false;
      }

      if (this.cancelMatch(var1, var4)) {
         return false;
      }

      NBTItem var5 = NBTItem.get(var1);
      MMOItemTemplate var6 = MMOItems.plugin.getTemplates().getTemplate(var5);
      if (var6 == null) {
         FriendlyFeedbackProvider.log(
            var4, FriendlyFeedbackCategory.FAILURE, "Item $r{0}$b is $fnot a MMOItem$b. ", new String[]{SilentNumbers.getItemName(var1)}
         );
         return false;
      }

      String var7 = "";
      if (var3.contains("{")) {
         var7 = var3.substring(var3.indexOf(123) + 1);
         var3 = var3.substring(0, var3.indexOf(123));
         if (var7.endsWith("}")) {
            var7 = var7.substring(0, var7.length() - 1);
         }
      }

      if (var6.getType().getId().equals(var2) && var6.getId().equals(var3)) {
         if (!var7.isEmpty()) {
            VolatileMMOItem var8 = new VolatileMMOItem(var5);
            QuickNumberRange var9 = SilentNumbers.rangeFromBracketsTab(var7, "LEVEL");
            if (var9 != null) {
               int var10 = var8.getUpgradeLevel();
               if (!var9.inRange(var10)) {
                  FriendlyFeedbackProvider.log(
                     var4,
                     FriendlyFeedbackCategory.FAILURE,
                     "MMOItem $r{0} {1}$b is of level $u{2}$b though $r{3}$b was expected. $fNo Match. ",
                     new String[]{var6.getType().getId(), var6.getId(), String.valueOf(var10), var9.toString()}
                  );
                  return false;
               }
            }
         }

         FriendlyFeedbackProvider.log(
            var4, FriendlyFeedbackCategory.SUCCESS, "Detected $r{0} {1} $sSuccessfully. ", new String[]{var6.getType().getId(), var6.getId()}
         );
         return true;
      } else {
         FriendlyFeedbackProvider.log(
            var4,
            FriendlyFeedbackCategory.FAILURE,
            "MMOItem $r{0} {1}$b is not the expected $r{2} {3}$b. $fNo Match. ",
            new String[]{var6.getType().getId(), var6.getId(), var2, var3}
         );
         return false;
      }
   }

   public boolean isValid(@NotNull String var1, @NotNull String var2, @Nullable FriendlyFeedbackProvider var3) {
      if (reg) {
         return true;
      }

      var1 = var1.replace(" ", "_").replace("-", "_").toUpperCase();
      var2 = var2.replace(" ", "_").replace("-", "_").toUpperCase();
      Type var4 = MMOItems.plugin.getTypes().get(var1);
      if (var4 == null) {
         FriendlyFeedbackProvider.log(var3, FriendlyFeedbackCategory.ERROR, "$fInvalid MMOItem Type $r{0}$f. ", new String[]{var1});
         return false;
      }

      if (var2.contains("{") && var2.contains("}")) {
         var2 = var2.substring(0, var2.indexOf(123));
      }

      if (MMOItems.plugin.getMMOItem(var4, var2) == null) {
         FriendlyFeedbackProvider.log(
            var3, FriendlyFeedbackCategory.ERROR, "$fInvalid MMOItem $r{0} {1}$f: $bNo such MMOItem for Type $e{0}$b. ", new String[]{var1, var2}
         );
         return false;
      } else {
         FriendlyFeedbackProvider.log(var3, FriendlyFeedbackCategory.SUCCESS, "Valid MMOItem $r{0} {1}$b. $snice. ", new String[]{var1, var2});
         return true;
      }
   }

   @NotNull
   public ArrayList<String> tabCompleteArgument(@NotNull String var1) {
      return SilentNumbers.smartFilter(MMOItems.plugin.getTypes().getAllTypeNames(), var1, true);
   }

   @NotNull
   public ArrayList<String> tabCompleteData(@NotNull String var1, @NotNull String var2) {
      Type var3 = MMOItems.plugin.getTypes().get(var1);
      if (var3 != null) {
         String var4 = "";
         String var5 = "";
         if (var2.contains("{")) {
            var4 = var2.substring(var2.indexOf(123) + 1);
            var2 = var2.substring(0, var2.indexOf(123));
            int var6 = 0;
            if (var4.contains(",")) {
               var6 = var4.lastIndexOf(44) + 1;
            }

            var5 = var4.substring(var6);
            var4 = var4.substring(0, var6);
         }

         ArrayList<String> var14 = SilentNumbers.smartFilter(MMOItems.plugin.getTemplates().getTemplateNames(var3), var2, true);
         ArrayList var7 = var14;
         if (!var5.isEmpty()) {
            ArrayList<String> var8 = new ArrayList();
            if (var5.contains("=")) {
               String var9 = var5.substring(0, var5.indexOf(61));
               switch (var9.toLowerCase()) {
                  case "level":
                     SilentNumbers.addAll(var8, new String[]{"level=1..", "level=2..4", "level=..6"});
                     break;
                  default:
                     var8.add(var5);
               }
            } else {
               var8 = SilentNumbers.smartFilter(this.getValidDataments(), var5, true);
            }

            for (String var16 : var14) {
               for (String var12 : var8) {
                  var7.add(var16 + "{" + var4 + var12);
               }
            }
         }

         return var7;
      } else {
         return SilentNumbers.toArrayList(new String[]{"Type_not_found,_check_your_spelling"});
      }
   }

   @NotNull
   public ArrayList<String> getValidDataments() {
      if (this.validDataments != null) {
         return this.validDataments;
      }

      this.validDataments = SilentNumbers.toArrayList(new String[]{"level"});
      return this.validDataments;
   }

   public boolean fullyDefinesItem() {
      return true;
   }

   @Nullable
   public ItemStack getItemStack(@NotNull String var1, @NotNull String var2, @Nullable FriendlyFeedbackProvider var3) {
      if (!this.isValid(var1, var2, var3)) {
         return null;
      }

      var1 = var1.replace(" ", "_").replace("-", "_").toUpperCase();
      var2 = var2.replace(" ", "_").replace("-", "_").toUpperCase();
      return MMOItems.plugin.getItem(var1, var2);
   }

   @NotNull
   public ItemStack getDisplayStack(@NotNull String var1, @NotNull String var2, @Nullable FriendlyFeedbackProvider var3) {
      if (!this.isValid(var1, var2, var3)) {
         return ItemFactory.of(Material.STRUCTURE_VOID).name("§cInvalid MMOItem §e" + var1 + " " + var2).build();
      }

      var1 = var1.replace(" ", "_").replace("-", "_").toUpperCase();
      String var4 = "";
      if (var2.contains("{")) {
         var4 = var2.substring(var2.indexOf(123) + 1);
         var2 = var2.substring(0, var2.indexOf(123));
         if (var4.endsWith("}")) {
            var4 = var4.substring(0, var4.length() - 1);
         }
      }

      var2 = var2.replace(" ", "_").replace("-", "_").toUpperCase();
      MMOItem var5 = new MMOItemBuilder(MMOItems.plugin.getTemplates().getTemplate(MMOItems.plugin.getTypes().get(var1), var2), 0, null, true).build();
      if (!var4.isEmpty() && var5.hasUpgradeTemplate()) {
         QuickNumberRange var6 = SilentNumbers.rangeFromBracketsTab(var4, "level");
         if (var6 != null) {
            UpgradeData var7 = ((UpgradeData)var5.getData(ItemStats.UPGRADE)).clone();
            var7.setLevel(SilentNumbers.floor(var6.getAsDouble(0.0)));
            var5.setData(ItemStats.UPGRADE, var7);
         }
      }

      return var5.newBuilder().buildNBT(true).toItem();
   }

   @NotNull
   public ArrayList<String> getDescription(@NotNull String var1, @NotNull String var2) {
      if (!this.isValid(var1, var2, null)) {
         return SilentNumbers.toArrayList(new String[]{"This MMOItem is $finvalid$b."});
      }

      var1 = var1.replace(" ", "_").replace("-", "_").toUpperCase();
      var2 = var2.replace(" ", "_").replace("-", "_").toUpperCase();
      return SilentNumbers.toArrayList(new String[]{SilentNumbers.getItemName(MMOItems.plugin.getItem(var1, var2))});
   }

   public boolean determinateGeneration() {
      return false;
   }

   public boolean partialDeterminateGeneration(@NotNull String var1, @NotNull String var2) {
      return !this.isValid(var1, var2, null);
   }

   @NotNull
   public String getSourcePlugin() {
      return "MMOItems";
   }

   @NotNull
   public String getFilterName() {
      return "MMOItem";
   }

   @NotNull
   public String exampleArgument() {
      return "CONSUMABLE";
   }

   @NotNull
   public String exampleData() {
      return "MANGO";
   }

   public static void register() {
      global = new MMOItemUIFilter();
      UIFilterManager.registerUIFilter(global);
      VanillaMMOItemCountermatch.enable();
      reg = false;
   }

   @NotNull
   public static MMOItemUIFilter get() {
      return global;
   }
}
