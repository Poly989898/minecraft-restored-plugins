package com.nexomc.nexo.recipes.loaders;

import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.potion.PotionMix;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/recipes/loaders/BrewingLoader;", "Lcom/nexomc/nexo/recipes/loaders/RecipeLoader;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "registerRecipe", "", "com.nexomc:core"}
)
public final class BrewingLoader extends RecipeLoader {
   public BrewingLoader(@NotNull ConfigurationSection section) {
      super(section);
   }

   public void registerRecipe() {
      RecipeChoice recipeChoice;
      block: {
         ConfigurationSection configurationSection = NexoYamlKt.section(this.getSection(), "input");
         if (configurationSection != null) {
            ConfigurationSection p0 = configurationSection;
            int value = 0;
            RecipeChoice recipeChoice2 = this.recipeChoice(p0);
            if (recipeChoice2 != null) {
               RecipeChoice it = recipeChoice2;
               value = 0;
               recipeChoice = PotionMix.createPredicateChoice((org.bukkit.inventory.ItemStack stack) -> it.test(stack));
               break block;
            }
         }

         recipeChoice = null;
      }

      RecipeChoice input;
      block: {
         input = recipeChoice;
         ConfigurationSection configurationSection2 = NexoYamlKt.section(this.getSection(), "ingredient");
         if (configurationSection2 != null) {
            ConfigurationSection p0 = configurationSection2;
            int value2 = 0;
            RecipeChoice recipeChoice3 = this.recipeChoice(p0);
            if (recipeChoice3 != null) {
               RecipeChoice it = recipeChoice3;
               value2 = 0;
               recipeChoice = PotionMix.createPredicateChoice((org.bukkit.inventory.ItemStack stack) -> it.test(stack));
               break block;
            }
         }

         recipeChoice = null;
      }

      RecipeChoice ingredient = recipeChoice;
      Bukkit.getServer().getPotionBrewer().removePotionMix(this.getKey());
      Bukkit.getServer().getPotionBrewer().addPotionMix(new PotionMix(this.getKey(), this.getResult(), input, ingredient));
   }

   private static final boolean registerRecipe$lambda$0$0(RecipeChoice it, ItemStack i) {
      return it.test(i);
   }

   private static final boolean registerRecipe$lambda$0$1(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }

   private static final boolean registerRecipe$lambda$2$0(RecipeChoice it, ItemStack i) {
      return it.test(i);
   }

   private static final boolean registerRecipe$lambda$2$1(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }
}
