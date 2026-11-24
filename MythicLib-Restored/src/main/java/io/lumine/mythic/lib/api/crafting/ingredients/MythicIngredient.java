package io.lumine.mythic.lib.api.crafting.ingredients;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.uifilters.IngredientUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicIngredient implements Cloneable {
   @NotNull
   final String name;
   @NotNull
   final ArrayList<ProvidedUIFilter> substitutes;
   boolean acceptsAir;
   static final HashMap<String, MythicIngredient> enabledIngredients = new HashMap<>();
   boolean definesItem = false;

   @NotNull
   public String getName() {
      return this.name;
   }

   public MythicIngredient(@NotNull String name, @NotNull ArrayList<ProvidedUIFilter> ingredient) {
      this.substitutes = ingredient;
      this.name = name;
      this.quantifySubstitutes();
   }

   public MythicIngredient(@NotNull String name, @NotNull ProvidedUIFilter... ingredient) {
      this.substitutes = new ArrayList<>();

      for (ProvidedUIFilter puff : ingredient) {
         if (puff != null) {
            this.substitutes.add(puff);
            if (puff.isAir()) {
               this.acceptsAir = true;
            }
         }
      }

      this.name = name;
      this.quantifySubstitutes();
   }

   public void addSubstitute(@NotNull ProvidedUIFilter substitute) {
      if (!substitute.hasAmount()) {
         substitute.setAmount(1);
      }

      if (!substitute.getParent().useInventoryMatch()) {
         if (!(substitute.getParent() instanceof IngredientUIFilter)) {
            if (substitute.getParent().fullyDefinesItem()) {
               this.definesItem = true;
            }

            this.substitutes.add(substitute);
            if (substitute.isAir()) {
               this.acceptsAir = true;
            }
         }
      }
   }

   @NotNull
   public ArrayList<ProvidedUIFilter> getSubstitutes() {
      return new ArrayList<>(this.substitutes);
   }

   public void clearSubstitutes() {
      this.substitutes.clear();
      this.definesItem = false;
      this.acceptsAir = false;
   }

   void quantifySubstitutes() {
      this.definesItem = false;
      FriendlyFeedbackProvider ffp = new FriendlyFeedbackProvider(FFPMythicLib.get());
      ffp.activatePrefix(true, "Mythic Ingredient $f" + this.getName());

      for (int n = 0; n < this.substitutes.size(); n++) {
         ProvidedUIFilter nbt = this.substitutes.get(n);
         if (nbt == null) {
            this.substitutes.remove(n);
            n--;
            ffp.log(FriendlyFeedbackCategory.ERROR, "$fNull ingredient at {0}. ", this.getName());
         } else {
            if (nbt.getParent().fullyDefinesItem()) {
               this.definesItem = true;
            }

            if (!nbt.hasAmount()) {
               nbt.setAmount(1);
            }

            if (nbt.getParent().useInventoryMatch()) {
               this.substitutes.remove(nbt);
               n--;
               ffp.log(
                  FriendlyFeedbackCategory.ERROR,
                  "You may not use the $u{0}$b ($r{1}$b) UIFilter key to define ingredients: $fInventory-match filters are not supported for ingredients. ",
                  nbt.getParent().getIdentifier(),
                  nbt.getParent().getFilterName(),
                  this.getName()
               );
            } else if (nbt.getParent() instanceof IngredientUIFilter) {
               this.substitutes.remove(nbt);
               n--;
               ffp.log(
                  FriendlyFeedbackCategory.ERROR,
                  "You may not use the $u{0}$b ($r{1}$b) UIFilter key to define ingredients: $fIngredient filter cannot be used to define ingredients. ",
                  nbt.getParent().getIdentifier(),
                  nbt.getParent().getFilterName(),
                  this.getName()
               );
            }
         }
      }

      this.acceptsAir = false;

      for (ProvidedUIFilter substitute : this.substitutes) {
         if (substitute.isAir()) {
            this.acceptsAir = true;
         }
      }

      ffp.sendTo(FriendlyFeedbackCategory.ERROR, MythicLib.plugin.getServer().getConsoleSender());
   }

   public boolean acceptsAir() {
      return this.acceptsAir;
   }

   public boolean matches(@NotNull ItemStack what, @Nullable FriendlyFeedbackProvider ffp) {
      for (ProvidedUIFilter fil : this.substitutes) {
         if (fil.matches(what, false, ffp)) {
            return true;
         }
      }

      return false;
   }

   public boolean matches(
      @NotNull ItemStack what,
      boolean ignoreAmount,
      @Nullable Ref<ArrayList<ProvidedUIFilter>> matchers,
      boolean deepMatch,
      @Nullable FriendlyFeedbackProvider ffp
   ) {
      ArrayList<ProvidedUIFilter> ref = new ArrayList<>();
      boolean oneMatch = false;

      for (ProvidedUIFilter fil : this.substitutes) {
         if (fil.matches(what, ignoreAmount, ffp)) {
            ref.add(fil);
            if (!deepMatch) {
               Ref.setValue(matchers, ref);
               return true;
            }

            oneMatch = true;
         }
      }

      Ref.setValue(matchers, ref);
      return oneMatch;
   }

   public static void enable(@NotNull MythicIngredient ing) {
      if (get(ing.getName()) == null) {
         enabledIngredients.put(ing.getName(), ing);
      }
   }

   @Nullable
   public static MythicIngredient get(@NotNull String name) {
      return enabledIngredients.get(name);
   }

   @NotNull
   public static ArrayList<String> getEnabledIngredients() {
      return new ArrayList<>(enabledIngredients.keySet());
   }

   public static void deserializeFrom(@NotNull ArrayList<ConfigurationSection> configurationSections, @Nullable FriendlyFeedbackProvider ffp) {
      for (ConfigurationSection csConfig : configurationSections) {
         if (csConfig == null) {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Found a null config in the provided list. ");
         } else {
            for (Entry<String, Object> val : csConfig.getValues(false).entrySet()) {
               String tName = val.getKey();
               if (get(tName) != null) {
                  FriendlyFeedbackProvider.log(
                     ffp, FriendlyFeedbackCategory.ERROR, "There is already an ingredient of name '$u{0}$b' defined. $fIgnored. ", tName
                  );
               } else {
                  ArrayList<String> cont = new ArrayList<>();
                  if (csConfig.contains(tName + ".Items")) {
                     cont = new ArrayList<>(csConfig.getStringList(tName + ".Items"));
                  }

                  if (cont.size() > 0) {
                     MythicIngredient deserializedIngredient = deserialize(tName, cont, ffp);
                     if (deserializedIngredient.getSubstitutes().size() > 0) {
                        enable(deserializedIngredient);
                     } else {
                        FriendlyFeedbackProvider.log(
                           ffp, FriendlyFeedbackCategory.ERROR, "Ingredient of name '$u{0}$b' has no items to match. $fIgnored. ", tName
                        );
                     }
                  } else {
                     FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Ingredient of name '$u{0}$b' has no items to match. $fIgnored. ", tName);
                  }
               }
            }
         }
      }
   }

   @NotNull
   public static MythicIngredient deserialize(
      @NotNull String ingredientName, @NotNull ArrayList<String> serializedContents, @Nullable FriendlyFeedbackProvider ffp
   ) {
      ArrayList<ProvidedUIFilter> ret = new ArrayList<>();

      for (String ing : serializedContents) {
         if (ing != null && !ing.isEmpty()) {
            ProvidedUIFilter puff = ProvidedUIFilter.getFromString(ing, ffp);
            if (puff != null) {
               ret.add(puff);
            }
         } else {
            FriendlyFeedbackProvider.log(
               ffp,
               FriendlyFeedbackCategory.ERROR,
               "Expected a UIFilter in the format $ekey argument data amount$b (where amount is optional) instead of a $fnull/empty$b entry."
            );
         }
      }

      return new MythicIngredient(ingredientName, ret);
   }

   public MythicIngredient clone() {
      try {
         super.clone();
      } catch (CloneNotSupportedException var2) {
      }

      return new MythicIngredient(this.getName(), this.getSubstitutes());
   }

   public boolean isDefinesItem() {
      return this.definesItem;
   }

   @Nullable
   public ItemStack getRandomSubstituteItem(@Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isDefinesItem()) {
         return null;
      }

      ItemStack result = null;
      ProvidedUIFilter chosen = this.getRandomSubstitute(true);
      return chosen == null ? null : chosen.getItemStack(ffp);
   }

   @NotNull
   public ItemStack getRandomDisplayItem(@Nullable FriendlyFeedbackProvider ffp) {
      ItemStack result = null;
      ProvidedUIFilter chosen = this.getRandomSubstitute(false);
      return chosen == null
         ? ItemFactory.of(Material.STRUCTURE_VOID)
            .name("§4Internal Error:§3 No Substitute")
            .lore("§6MythicLib §eio.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient.getRandomDisplayItem()")
            .build()
         : chosen.getDisplayStack(ffp);
   }

   @Nullable
   public ProvidedUIFilter getRandomSubstitute(boolean requireItemFullyDefined) {
      ProvidedUIFilter ret = null;
      ArrayList<ProvidedUIFilter> poof = this.getSubstitutes();

      while (ret == null && poof.size() > 0) {
         int chosenIndex = SilentNumbers.floor(SilentNumbers.randomRange(0.0, poof.size() - 0.25));
         ProvidedUIFilter puff = poof.get(chosenIndex);
         if (!requireItemFullyDefined || puff.getParent().fullyDefinesItem()) {
            ret = puff;
         }

         poof.remove(puff);
      }

      return ret;
   }
}
