package io.lumine.mythic.lib.api.crafting.uimanager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.uifilters.UIFilter;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UIFilterManager {
   private static final HashMap<String, UIFilter> loadedUIFilters = new HashMap<>();

   public static void registerUIFilter(@NotNull UIFilter filter) {
      FriendlyFeedbackProvider ffp = new FriendlyFeedbackProvider(FFPMythicLib.get());
      ffp.activatePrefix(true, "UIFilter Registry");
      if (MythicLib.plugin == null) {
         ffp.log(
            FriendlyFeedbackCategory.ERROR,
            "Filter '$u{0}$b' of $u{1}$b is being registered before MythicLib loads. $fDon't do that. ",
            filter.getFilterName(),
            filter.getSourcePlugin()
         );
         ffp.sendAllTo(MythicLib.plugin.getServer().getConsoleSender());
      } else if (loadedUIFilters.containsKey(filter.getIdentifier())) {
         UIFilter aggravate = loadedUIFilters.get(filter.getIdentifier());
         ffp.log(
            FriendlyFeedbackCategory.ERROR,
            "Filter '$u{0}$b' of $u{1}$b has a conflicting key with $e{2}$b's '$e{3}$b' filter. $fIt has been rejected. ",
            filter.getFilterName(),
            filter.getSourcePlugin(),
            aggravate.getSourcePlugin(),
            aggravate.getFilterName()
         );
         ffp.sendAllTo(MythicLib.plugin.getServer().getConsoleSender());
      } else if (!filter.isValid(filter.exampleArgument(), filter.exampleData(), ffp)) {
         ffp.log(
            FriendlyFeedbackCategory.OTHER,
            "Filter '$u{0}$b' of $u{1}$b has $finvalid example argument and data$b: ",
            filter.getFilterName(),
            filter.getSourcePlugin()
         );
         ffp.sendTo(FriendlyFeedbackCategory.OTHER, MythicLib.plugin.getServer().getConsoleSender());
         ffp.clearFeedbackOf(FriendlyFeedbackCategory.OTHER);
         ffp.sendAllTo(MythicLib.plugin.getServer().getConsoleSender());
      } else {
         UIFilterBasicCountermatch tester = new UIFilterBasicCountermatch();
         filter.addMatchOverride(tester);
         filter.matches(new ItemStack(Material.CAULDRON), filter.exampleArgument(), filter.exampleData(), null);
         if (!tester.wasEvaluated()) {
            ffp.log(
               FriendlyFeedbackCategory.ERROR,
               "Filter '$u{0}$b' of $u{1}$b does not check for counter matches. $fRejected$b.",
               filter.getFilterName(),
               filter.getSourcePlugin()
            );
            ffp.sendTo(FriendlyFeedbackCategory.ERROR, MythicLib.plugin.getServer().getConsoleSender());
         } else {
            tester.load();
            loadedUIFilters.put(filter.getIdentifier(), filter);
         }
      }
   }

   @Contract("null,_ -> null")
   @Nullable
   public static ProvidedUIFilter getUIFilter(@Nullable String compound, @Nullable FriendlyFeedbackProvider ffp) {
      if (compound == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "No string was provided to even attempt to read a UIFilter from it. ");
         return null;
      }

      String[] split = compound.split(" ");
      if (split.length < 3) {
         FriendlyFeedbackProvider.log(
            ffp,
            FriendlyFeedbackCategory.ERROR,
            "Provided string '$u{0}$b' was not in the expected format '$ekey argument data amount$b' (where amount is optional). ",
            compound
         );
         return null;
      }

      String amount = null;
      if (split.length >= 4) {
         amount = split[3];
      }

      return getUIFilter(split[0], split[1], split[2], amount, ffp);
   }

   @Nullable
   public static ProvidedUIFilter getUIFilter(
      @NotNull String key, @NotNull String argument, @NotNull String data, @Nullable String amount, @Nullable FriendlyFeedbackProvider ffp
   ) {
      UIFilter ofKey = loadedUIFilters.get(key);
      if (ofKey == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "There is no UIFilter loaded with key $u{0}$b. ", key);
         return null;
      }

      QuickNumberRange amountRange = QuickNumberRange.getFromString(amount);
      if (amount != null && amountRange == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Expected a number or range like $e4..6$b instead of $u{0}$b as an amount. ", amount);
         return null;
      }

      ProvidedUIFilter provided = new ProvidedUIFilter(ofKey, argument, data);
      provided.setAmountRange(amountRange);
      FriendlyFeedbackProvider.log(
         ffp, FriendlyFeedbackCategory.SUCCESS, "Correctly built provided filter $r{0}$b from $u{1} {2} {3}$b. ", ofKey.getFilterName(), key, argument, data
      );
      if (amountRange != null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Amount range: {0}", amountRange.toStringColored());
      }

      return provided;
   }
}
