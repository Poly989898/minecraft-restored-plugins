package com.nexomc.nexo.recipes.listeners;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.utils.ItemUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/recipes/listeners/SmithingRecipeEvents;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onSmithingRecipe", "", "Lorg/bukkit/event/inventory/PrepareSmithingEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class SmithingRecipeEvents implements Listener {
   @EventHandler
   public final void onSmithingRecipe(@NotNull PrepareSmithingEvent prepareSmithingEvent) {
      SmithingInventory it = prepareSmithingEvent.getInventory();
      int value = 0;
      ItemStack inputTemplate = it.getInputTemplate();
      if (inputTemplate != null) {
         ItemStack inputMineral = it.getInputMineral();
         if (inputMineral != null) {
            Pair pair = TuplesKt.to(inputTemplate, inputMineral);
            ItemStack template = (ItemStack)pair.component1();
            ItemStack material = (ItemStack)pair.component2();
            inputTemplate = prepareSmithingEvent.getInventory().getInputEquipment();
            if (inputTemplate != null) {
               ItemStack input = inputTemplate;
               if (!template.isEmpty() && !material.isEmpty() && !input.isEmpty()) {
                  ItemStack[] contents = prepareSmithingEvent.getInventory().getContents();
                  Iterable iterable = (Iterable)ArraysKt.filterNotNull(contents);
                  boolean flag;
                  if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                     flag = false;
                  } else {
                     Iterator iterator = iterable.iterator();

                     while(true) {
                        if (!iterator.hasNext()) {
                           flag = false;
                           break;
                        }

                        Object element = iterator.next();
                        ItemStack p0 = (ItemStack)element;
                        int value2 = 0;
                        if (p0.isEmpty()) {
                           flag = true;
                           break;
                        }
                     }
                  }

                  if (!flag) {
                     ItemStack[] itemStacks = prepareSmithingEvent.getInventory().getContents();
                     iterable = (Iterable)ArraysKt.filterNotNull(itemStacks);
                     NexoItems nexoItems = NexoItems.INSTANCE;
                     boolean flag2;
                     if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                        flag2 = true;
                     } else {
                        Iterator iterator2 = iterable.iterator();

                        while(true) {
                           if (!iterator2.hasNext()) {
                              flag2 = true;
                              break;
                           }

                           Object obj = iterator2.next();
                           ItemStack p0 = (ItemStack)obj;
                           int value3 = 0;
                           if (NexoItems.exists(p0)) {
                              flag2 = false;
                              break;
                           }
                        }
                     }

                     if (!flag2) {
                        String str = NexoItems.idFromItem(input);
                        if (str != null) {
                           String nexoItemId = str;
                           if (!ItemUtils.INSTANCE.isAllowedInVanillaRecipes(input)) {
                              Iterator recipeIterator = Bukkit.recipeIterator();
                              Sequence sequence = SequencesKt.asSequence(recipeIterator);
                              int value6 = 0;
                              ObjectArrayList result = new ObjectArrayList();

                              for(Object obj2 : kotlin.sequences.SequencesKt.asIterable(sequence)) {
                                 if (obj2 instanceof SmithingTransformRecipe) {
                                    SmithingTransformRecipe recipe = (SmithingTransformRecipe)obj2;
                                    int value4 = 0;
                                    if (recipe.getTemplate().test(template) && recipe.getAddition().test(material) && recipe.getBase().test(input) && recipe.getBase() instanceof RecipeChoice.ExactChoice) {
                                       result.add(obj2);
                                    }
                                 }
                              }

                              Iterable iterable2 = (Iterable)result;
                              value6 = 0;
                              boolean flag3;
                              if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                                 flag3 = true;
                              } else {
                                 block: {
                                    for(Object obj3 : iterable2) {
                                       SmithingTransformRecipe it_1 = (SmithingTransformRecipe)obj3;
                                       int value5 = 0;
                                       if (!Intrinsics.areEqual(nexoItemId, NexoItems.idFromItem(it_1.getBase().getItemStack()))) {
                                          flag3 = false;
                                          break block;
                                       }
                                    }

                                    flag3 = true;
                                 }
                              }

                              if (!flag3) {
                                 prepareSmithingEvent.setResult((ItemStack)null);
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
