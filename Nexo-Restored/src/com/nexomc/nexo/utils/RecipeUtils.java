package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0014\u0010\u0004\u001a\u00020\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/utils/RecipeUtils;", "", "<init>", "()V", "recipeChoice", "Lorg/bukkit/inventory/RecipeChoice;", "material", "Lorg/bukkit/Material;", "tag", "Lorg/bukkit/Tag;", "com.nexomc:core"}
)
public final class RecipeUtils {
   @NotNull
   public static final RecipeUtils INSTANCE = new RecipeUtils();

   private RecipeUtils() {
   }

   @NotNull
   public final RecipeChoice recipeChoice(@NotNull Material material) {
      try {
      RecipeUtils recipeUtils = this;

      try {
         RecipeUtils recipeUtils2 = recipeUtils;
         int value = 0;
         ItemType asItemType = material.asItemType();
         RecipeChoice.ItemTypeChoice itemTypeChoice = RecipeChoice.itemType(asItemType, new ItemType[0]);
         return (RecipeChoice)itemTypeChoice;
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
         if (ResultShim.exceptionOrNull(obj2) == null) {
            throw new KotlinNothingValueException();
         } else {
            int value2 = 0;
            Class[] clazzs = new Class[]{Material.class};
            Constructor constructor = RecipeChoice.MaterialChoice.class.getConstructor(clazzs);
            Object[] objs = new Object[]{material};
            Object obj = constructor.newInstance(objs);
            return (RecipeChoice)obj;
         }
      }
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   @NotNull
   public final RecipeChoice recipeChoice(@NotNull Tag<Material> tag) {
      try {
      RecipeUtils recipeUtils = this;

      try {
         RecipeUtils recipeUtils2 = recipeUtils;
         int value = 0;
         Set values = tag.getValues();
         Iterable iterable = (Iterable)values;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value2 = 0;
            Material it = (Material)element;
            int value3 = 0;
            ItemType asItemType = it.asItemType();
            if (asItemType != null) {
               Object obj3 = asItemType;
               int value4 = 0;
               destination.add(obj3);
            }
         }

         List types = (List)destination;
         ItemType itemType = (ItemType)CollectionsKt.first(types);
         Collection collection = (Collection)CollectionsKt.drop((Iterable)types, 1);
         ItemType[] itemTypes = (ItemType[])collection.toArray(new ItemType[0]);
         RecipeChoice.ItemTypeChoice itemTypeChoice = RecipeChoice.itemType(itemType, (ItemType[])Arrays.copyOf(itemTypes, itemTypes.length));
         return (RecipeChoice)itemTypeChoice;
      } catch (Throwable e) {
         Object obj = ResultShim.ok(ResultKt.createFailure(e));
         if (ResultShim.exceptionOrNull(obj) == null) {
            throw new KotlinNothingValueException();
         } else {
            int value5 = 0;
            Class[] clazzs = new Class[]{Tag.class};
            Constructor constructor = RecipeChoice.MaterialChoice.class.getConstructor(clazzs);
            Object[] objs = new Object[]{tag};
            Object obj2 = constructor.newInstance(objs);
            return (RecipeChoice)obj2;
         }
      }
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }
}
