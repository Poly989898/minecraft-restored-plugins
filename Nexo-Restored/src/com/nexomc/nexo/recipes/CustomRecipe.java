package com.nexomc.nexo.recipes;

import com.nexomc.nexo.utils.CollectionUtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u000e\u0018\u0000 /2\u00020\u0001:\u0001/B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u000e\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\b\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\u0014\u0010\u001f\u001a\u00020\u000b2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004J\n\u0010!\u001a\u00020\"H\u0096\u0080\u0004J(\u0010#\u001a\u00020\u000b2\u000e\u0010$\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\b2\u000e\u0010%\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bH\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0019\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0015\u001a\u00070\u0016¢\u0006\u0002\b\u0017¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u001a\u001a\u00020\u001b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u000e\u0010\u001e\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R \u0010&\u001a\u00070\u0006¢\u0006\u0002\b\u00178BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b(\u0010)\u001a\u0004\b'\u0010\u0012R \u0010*\u001a\u00070\u0006¢\u0006\u0002\b\u00178BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b,\u0010)\u001a\u0004\b+\u0010\u0012R\u0011\u0010-\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b-\u0010.¨\u00060"},
   d2 = {"Lcom/nexomc/nexo/recipes/CustomRecipe;", "", "name", "", "group", "result", "Lorg/bukkit/inventory/ItemStack;", "ingredients", "", "Lorg/bukkit/inventory/RecipeChoice;", "ordered", "", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lorg/bukkit/inventory/ItemStack;Ljava/util/List;Z)V", "getName", "()Ljava/lang/String;", "getGroup", "getResult", "()Lorg/bukkit/inventory/ItemStack;", "getIngredients", "()Ljava/util/List;", "key", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "getKey", "()Lnet/kyori/adventure/key/Key;", "namespacedKey", "Lorg/bukkit/NamespacedKey;", "getNamespacedKey", "()Lorg/bukkit/NamespacedKey;", "isOrdered", "equals", "other", "hashCode", "", "areEqual", "ingredients1", "ingredients2", "dyeTestItem", "getDyeTestItem", "dyeTestItem$delegate", "Lkotlin/Lazy;", "leatherItem", "getLeatherItem", "leatherItem$delegate", "isValidDyeRecipe", "()Z", "Companion", "com.nexomc:core"}
)
public final class CustomRecipe {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String name;
   @NotNull
   private final String group;
   @NotNull
   private final ItemStack result;
   @NotNull
   private final List<RecipeChoice> ingredients;
   @NotNull
   private final Key key;
   @NotNull
   private final NamespacedKey namespacedKey;
   private boolean isOrdered;
   @NotNull
   private final Lazy dyeTestItem$delegate;
   @NotNull
   private final Lazy leatherItem$delegate;

   public CustomRecipe(@NotNull String name, @NotNull String group, @NotNull ItemStack result, @NotNull List<RecipeChoice> ingredients, boolean ordered) {
      super();
      this.name = name;
      this.group = group;
      this.result = result;
      this.ingredients = ingredients;
      Key key2 = Key.key("nexo", this.name);
      this.key = key2;
      this.namespacedKey = new NamespacedKey("nexo", this.name);
      this.isOrdered = ordered;
      this.dyeTestItem$delegate = LazyKt.lazy(CustomRecipe::dyeTestItem_delegate$lambda$0);
      this.leatherItem$delegate = LazyKt.lazy(CustomRecipe::leatherItem_delegate$lambda$0);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final String getGroup() {
      return this.group;
   }

   @NotNull
   public final ItemStack getResult() {
      return this.result;
   }

   @NotNull
   public final List<RecipeChoice> getIngredients() {
      return this.ingredients;
   }

   @NotNull
   public final Key getKey() {
      return this.key;
   }

   @NotNull
   public final NamespacedKey getNamespacedKey() {
      return this.namespacedKey;
   }

   public boolean equals(@Nullable Object other) {
      if (other == null) {
         return false;
      } else if (other == this) {
         return true;
      } else if (!(other instanceof CustomRecipe)) {
         return false;
      } else {
         return Intrinsics.areEqual(this.result, ((CustomRecipe)other).result) && this.areEqual(this.ingredients, ((CustomRecipe)other).ingredients);
      }
   }

   public int hashCode() {
      return this.result.hashCode() / 2 + this.ingredients.hashCode() / 2 + (this.isOrdered ? 1 : 0);
   }

   private final boolean areEqual(List<? extends RecipeChoice> ingredients1, List<? extends RecipeChoice> ingredients2) {
      int index = 0;

      for(int i = ((Collection)ingredients1).size(); index < i; ++index) {
         RecipeChoice ingredient1 = (RecipeChoice)ingredients1.get(index);
         if (this.isOrdered) {
            RecipeChoice ingredient2 = (RecipeChoice)ingredients2.get(index);
            if (ingredient1 != null || ingredient2 != null) {
               if (ingredient1 == null || ingredient2 == null) {
                  return false;
               }

               if (!Intrinsics.areEqual(ingredient1, ingredient2)) {
                  return false;
               }
            }
         } else if (ingredient1 != null) {
            Iterable iterable = (Iterable)ingredients2;
            RecipeChoice recipeChoice = ingredient1;
            boolean flag;
            if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
               flag = true;
            } else {
               Iterator iterator = iterable.iterator();

               while(true) {
                  if (!iterator.hasNext()) {
                     flag = true;
                     break;
                  }

                  Object element = iterator.next();
                  int value = 0;
                  if (recipeChoice.equals(element)) {
                     flag = false;
                     break;
                  }
               }
            }

            if (flag) {
               return false;
            }
         }
      }

      return true;
   }

   private final ItemStack getDyeTestItem() {
      Lazy lazy = this.dyeTestItem$delegate;
      return (ItemStack)lazy.getValue();
   }

   private final ItemStack getLeatherItem() {
      Lazy lazy = this.leatherItem$delegate;
      return (ItemStack)lazy.getValue();
   }

   public final boolean isValidDyeRecipe() {
      List ingredients = CollectionsKt.filterNotNull((Iterable)this.ingredients);
      if (ingredients.size() != 2) {
         Iterable iterable = (Iterable)ingredients;
         boolean flag;
         if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
            flag = true;
         } else {
            Iterator it = iterable.iterator();

            while(true) {
               if (!it.hasNext()) {
                  flag = true;
                  break;
               }

               Object element = it.next();
               RecipeChoice it_2 = (RecipeChoice)element;
               int value = 0;
               if (it_2.test(this.getDyeTestItem())) {
                  flag = false;
                  break;
               }
            }
         }

         if (flag) {
            return false;
         }
      }

      Iterable iterable2 = (Iterable)ingredients;
      Collection destination = (Collection)(new ArrayList());

      for(Object obj : iterable2) {
         RecipeChoice it = (RecipeChoice)obj;
         int value2 = 0;
         if (!it.test(this.getDyeTestItem())) {
            destination.add(obj);
         }
      }

      List it = (List)destination;
      int value3 = 0;
      List list = it.size() == 1 ? it : null;
      if (list != null) {
         RecipeChoice recipeChoice2 = (RecipeChoice)CollectionsKt.first(list);
         if (recipeChoice2 != null) {
            RecipeChoice recipeChoice = recipeChoice2;
            return !(recipeChoice instanceof RecipeChoice.ExactChoice) && recipeChoice.test(this.getLeatherItem());
         }
      }

      return false;
   }

   private static final ItemStack dyeTestItem_delegate$lambda$0() {
      return ItemStack.of(Material.RED_DYE);
   }

   private static final ItemStack leatherItem_delegate$lambda$0() {
      return ItemStack.of(Material.LEATHER_HORSE_ARMOR);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/recipes/CustomRecipe$Companion;", "", "<init>", "()V", "fromRecipe", "Lcom/nexomc/nexo/recipes/CustomRecipe;", "recipe", "Lorg/bukkit/inventory/Recipe;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final CustomRecipe fromRecipe(@Nullable Recipe recipe) {
         CustomRecipe customRecipe;
         if (recipe instanceof ShapedRecipe) {
            Iterable iterable = (Iterable)(new IntRange(0, 2));
            Collection destination = (Collection)(new ArrayList());
            Iterator iterator = iterable.iterator();

            while(iterator.hasNext()) {
               int element = ((IntIterator)iterator).nextInt();
               int value = 0;
               String[] shape = ((ShapedRecipe)recipe).getShape();
               String orNull = ArraysKt.getOrNull(shape, element);
               if (orNull == null) {
                  orNull = "";
               }

               CharSequence charSequence = (CharSequence)StringsKt.padEnd(orNull, 3, '_');
               Map choiceMap = ((ShapedRecipe)recipe).getChoiceMap();
               Map map = choiceMap;

               for(int i = 0; i < charSequence.length(); ++i) {
                  Character p0 = charSequence.charAt(i);
                  destination.add((RecipeChoice)map.get(p0));
               }
            }

            List ingredients = (List)destination;
            String str = ((ShapedRecipe)recipe).getKey().getKey();
            String str2 = ((ShapedRecipe)recipe).getGroup();
            ItemStack itemStack = ((ShapedRecipe)recipe).getResult();
            customRecipe = new CustomRecipe(str, str2, itemStack, ingredients, true);
         } else if (recipe instanceof ShapelessRecipe) {
            Collection collection = CollectionUtilsKt.ensureAtleast((Collection)((ShapelessRecipe)recipe).getChoiceList(), 9, (Object)null);
            List ingredients = (List)collection;
            String str3 = ((ShapelessRecipe)recipe).getKey().getKey();
            String str4 = ((ShapelessRecipe)recipe).getGroup();
            ItemStack itemStack2 = ((ShapelessRecipe)recipe).getResult();
            customRecipe = new CustomRecipe(str3, str4, itemStack2, ingredients, false);
         } else if (recipe instanceof CookingRecipe) {
            String str5 = ((CookingRecipe)recipe).getKey().getKey();
            String str6 = ((CookingRecipe)recipe).getGroup();
            ItemStack itemStack3 = ((CookingRecipe)recipe).getResult();
            customRecipe = new CustomRecipe(str5, str6, itemStack3, CollectionsKt.listOf(((CookingRecipe)recipe).getInputChoice()), false);
         } else if (recipe instanceof StonecuttingRecipe) {
            String str7 = ((StonecuttingRecipe)recipe).getKey().getKey();
            String str8 = ((StonecuttingRecipe)recipe).getGroup();
            ItemStack itemStack4 = ((StonecuttingRecipe)recipe).getResult();
            customRecipe = new CustomRecipe(str7, str8, itemStack4, CollectionsKt.listOf(((StonecuttingRecipe)recipe).getInputChoice()), false);
         } else {
            customRecipe = null;
         }

         return customRecipe;
      }
   }
}
