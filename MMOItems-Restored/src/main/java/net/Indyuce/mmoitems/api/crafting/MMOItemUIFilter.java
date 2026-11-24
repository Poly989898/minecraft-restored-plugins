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

   public boolean matches(@NotNull ItemStack item, @NotNull String text, @NotNull String itemId, @Nullable FriendlyFeedbackProvider friendlyFeedbackProvider) {
      text = text.replace(" ", "_").replace("-", "_").toUpperCase();
      itemId = itemId.replace(" ", "_").replace("-", "_").toUpperCase();
      if (!this.isValid(text, itemId, friendlyFeedbackProvider)) {
         return false;
      }

      if (this.cancelMatch(item, friendlyFeedbackProvider)) {
         return false;
      }

      NBTItem nbtItem = NBTItem.get(item);
      MMOItemTemplate template = MMOItems.plugin.getTemplates().getTemplate(nbtItem);
      if (template == null) {
         FriendlyFeedbackProvider.log(
            friendlyFeedbackProvider, FriendlyFeedbackCategory.FAILURE, "Item $r{0}$b is $fnot a MMOItem$b. ", new String[]{SilentNumbers.getItemName(item)}
         );
         return false;
      }

      String filterData = "";
      if (itemId.contains("{")) {
         filterData = itemId.substring(itemId.indexOf(123) + 1);
         itemId = itemId.substring(0, itemId.indexOf(123));
         if (filterData.endsWith("}")) {
            filterData = filterData.substring(0, filterData.length() - 1);
         }
      }

      if (template.getType().getId().equals(text) && template.getId().equals(itemId)) {
         if (!filterData.isEmpty()) {
            VolatileMMOItem volatileMMOItem = new VolatileMMOItem(nbtItem);
            QuickNumberRange quickNumberRange = SilentNumbers.rangeFromBracketsTab(filterData, "LEVEL");
            if (quickNumberRange != null) {
               int upgradeLevel = volatileMMOItem.getUpgradeLevel();
               if (!quickNumberRange.inRange(upgradeLevel)) {
                  FriendlyFeedbackProvider.log(
                     friendlyFeedbackProvider,
                     FriendlyFeedbackCategory.FAILURE,
                     "MMOItem $r{0} {1}$b is of level $u{2}$b though $r{3}$b was expected. $fNo Match. ",
                     new String[]{template.getType().getId(), template.getId(), String.valueOf(upgradeLevel), quickNumberRange.toString()}
                  );
                  return false;
               }
            }
         }

         FriendlyFeedbackProvider.log(
            friendlyFeedbackProvider, FriendlyFeedbackCategory.SUCCESS, "Detected $r{0} {1} $sSuccessfully. ", new String[]{template.getType().getId(), template.getId()}
         );
         return true;
      } else {
         FriendlyFeedbackProvider.log(
            friendlyFeedbackProvider,
            FriendlyFeedbackCategory.FAILURE,
            "MMOItem $r{0} {1}$b is not the expected $r{2} {3}$b. $fNo Match. ",
            new String[]{template.getType().getId(), template.getId(), text, itemId}
         );
         return false;
      }
   }

   public boolean isValid(@NotNull String text, @NotNull String itemId, @Nullable FriendlyFeedbackProvider friendlyFeedbackProvider) {
      if (reg) {
         return true;
      }

      text = text.replace(" ", "_").replace("-", "_").toUpperCase();
      itemId = itemId.replace(" ", "_").replace("-", "_").toUpperCase();
      Type type = MMOItems.plugin.getTypes().get(text);
      if (type == null) {
         FriendlyFeedbackProvider.log(friendlyFeedbackProvider, FriendlyFeedbackCategory.ERROR, "$fInvalid MMOItem Type $r{0}$f. ", new String[]{text});
         return false;
      }

      if (itemId.contains("{") && itemId.contains("}")) {
         itemId = itemId.substring(0, itemId.indexOf(123));
      }

      if (MMOItems.plugin.getMMOItem(type, itemId) == null) {
         FriendlyFeedbackProvider.log(
            friendlyFeedbackProvider, FriendlyFeedbackCategory.ERROR, "$fInvalid MMOItem $r{0} {1}$f: $bNo such MMOItem for Type $e{0}$b. ", new String[]{text, itemId}
         );
         return false;
      } else {
         FriendlyFeedbackProvider.log(friendlyFeedbackProvider, FriendlyFeedbackCategory.SUCCESS, "Valid MMOItem $r{0} {1}$b. $snice. ", new String[]{text, itemId});
         return true;
      }
   }

   @NotNull
   public ArrayList<String> tabCompleteArgument(@NotNull String text) {
      return SilentNumbers.smartFilter(MMOItems.plugin.getTypes().getAllTypeNames(), text, true);
   }

   @NotNull
   public ArrayList<String> tabCompleteData(@NotNull String text, @NotNull String itemId) {
      Type type = MMOItems.plugin.getTypes().get(text);
      if (type != null) {
         String completedData = "";
         String currentData = "";
         if (itemId.contains("{")) {
            completedData = itemId.substring(itemId.indexOf(123) + 1);
            itemId = itemId.substring(0, itemId.indexOf(123));
            int count = 0;
            if (completedData.contains(",")) {
               count = completedData.lastIndexOf(44) + 1;
            }

            currentData = completedData.substring(count);
            completedData = completedData.substring(0, count);
         }

         ArrayList<String> texts = SilentNumbers.smartFilter(MMOItems.plugin.getTemplates().getTemplateNames(type), itemId, true);
         ArrayList list = texts;
         if (!currentData.isEmpty()) {
            ArrayList<String> dataSuggestions = new ArrayList();
            if (currentData.contains("=")) {
               String dataKey = currentData.substring(0, currentData.indexOf(61));
               switch (dataKey.toLowerCase()) {
                  case "level":
                     SilentNumbers.addAll(dataSuggestions, new String[]{"level=1..", "level=2..4", "level=..6"});
                     break;
                  default:
                     dataSuggestions.add(currentData);
               }
            } else {
               dataSuggestions = SilentNumbers.smartFilter(this.getValidDataments(), currentData, true);
            }

            for (String templateName : texts) {
               for (String suggestion : dataSuggestions) {
                  list.add(templateName + "{" + completedData + suggestion);
               }
            }
         }

         return list;
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
   public ItemStack getItemStack(@NotNull String text, @NotNull String itemId, @Nullable FriendlyFeedbackProvider friendlyFeedbackProvider) {
      if (!this.isValid(text, itemId, friendlyFeedbackProvider)) {
         return null;
      }

      text = text.replace(" ", "_").replace("-", "_").toUpperCase();
      itemId = itemId.replace(" ", "_").replace("-", "_").toUpperCase();
      return MMOItems.plugin.getItem(text, itemId);
   }

   @NotNull
   public ItemStack getDisplayStack(@NotNull String text, @NotNull String itemId, @Nullable FriendlyFeedbackProvider friendlyFeedbackProvider) {
      if (!this.isValid(text, itemId, friendlyFeedbackProvider)) {
         return ItemFactory.of(Material.STRUCTURE_VOID).name("§cInvalid MMOItem §e" + text + " " + itemId).build();
      }

      text = text.replace(" ", "_").replace("-", "_").toUpperCase();
      String filterData = "";
      if (itemId.contains("{")) {
         filterData = itemId.substring(itemId.indexOf(123) + 1);
         itemId = itemId.substring(0, itemId.indexOf(123));
         if (filterData.endsWith("}")) {
            filterData = filterData.substring(0, filterData.length() - 1);
         }
      }

      itemId = itemId.replace(" ", "_").replace("-", "_").toUpperCase();
      MMOItem mmoItem = new MMOItemBuilder(MMOItems.plugin.getTemplates().getTemplate(MMOItems.plugin.getTypes().get(text), itemId), 0, null, true).build();
      if (!filterData.isEmpty() && mmoItem.hasUpgradeTemplate()) {
         QuickNumberRange quickNumberRange = SilentNumbers.rangeFromBracketsTab(filterData, "level");
         if (quickNumberRange != null) {
            UpgradeData upgradeData = ((UpgradeData)mmoItem.getData(ItemStats.UPGRADE)).clone();
            upgradeData.setLevel(SilentNumbers.floor(quickNumberRange.getAsDouble(0.0)));
            mmoItem.setData(ItemStats.UPGRADE, upgradeData);
         }
      }

      return mmoItem.newBuilder().buildNBT(true).toItem();
   }

   @NotNull
   public ArrayList<String> getDescription(@NotNull String text, @NotNull String itemId) {
      if (!this.isValid(text, itemId, null)) {
         return SilentNumbers.toArrayList(new String[]{"This MMOItem is $finvalid$b."});
      }

      text = text.replace(" ", "_").replace("-", "_").toUpperCase();
      itemId = itemId.replace(" ", "_").replace("-", "_").toUpperCase();
      return SilentNumbers.toArrayList(new String[]{SilentNumbers.getItemName(MMOItems.plugin.getItem(text, itemId))});
   }

   public boolean determinateGeneration() {
      return false;
   }

   public boolean partialDeterminateGeneration(@NotNull String text, @NotNull String itemId) {
      return !this.isValid(text, itemId, null);
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
