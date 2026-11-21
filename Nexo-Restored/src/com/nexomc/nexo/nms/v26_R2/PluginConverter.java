package com.nexomc.nexo.nms.v26_R2;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.IPluginConverter;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.optionals.OptionalsKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/PluginConverter;", "Lcom/nexomc/nexo/nms/IPluginConverter;", "<init>", "()V", "convertItemsAdder", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "convertOraxen", "entity", "Lorg/bukkit/entity/Entity;", "com.nexomc:v26_R2"}
)
public final class PluginConverter implements IPluginConverter {
   public void convertItemsAdder(@NotNull ItemStack itemStack) {
      PluginConverter pluginConverter = this;

      Object obj2;
      try {
         block: {
            PluginConverter pluginConverter2 = pluginConverter;
            int value = 0;
            net.minecraft.world.item.ItemStack handle = ItemUtils.Companion.toCraftStack(itemStack).handle;
            if (handle == null) {
               return;
            }

            net.minecraft.world.item.ItemStack nmsItem = handle;
            CustomData customData = (CustomData)nmsItem.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
               CompoundTag copyTag = customData.copyTag();
               if (copyTag != null) {
                  CompoundTag customDataCopy = copyTag;
                  Optional compound = customDataCopy.getCompound("itemsadder");
                  CompoundTag orNull = (CompoundTag)OptionalsKt.getOrNull(compound);
                  if (orNull != null) {
                     Optional string = orNull.getString("id");
                     if (string != null) {
                        Optional it = string;
                        int value2 = 0;
                        string = !it.isEmpty() ? it : null;
                        if (string != null) {
                           String str = (String)OptionalsKt.getOrNull(string);
                           if (str != null) {
                              String itemId = str;
                              customDataCopy.remove("itemsadder");
                              Object obj = customDataCopy.getCompound("PublicBukkitValues").orElse(new CompoundTag());
                              CompoundTag compoundTag3 = (CompoundTag)obj;
                              String str2 = "PublicBukkitValues";
                              int value3 = 0;
                              compoundTag3.putString("nexo:id", itemId);
                              Unit unit = Unit.INSTANCE;
                              customDataCopy.put(str2, (Tag)obj);
                              obj2 = ResultShim.ok((CustomData)nmsItem.set(DataComponents.CUSTOM_DATA, CustomData.of(customDataCopy)));
                              break block;
                           }
                        }
                     }
                  }

                  return;
               }
            }

            return;
         }
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value4 = 0;
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(it), false);
         }

         CustomData customData2 = (CustomData)CraftItemStack.asNMSCopy(itemStack).getComponents().get(DataComponents.CUSTOM_DATA);
         if (customData2 == null) {
            return;
         }

         CompoundTag compoundTag = customData2.copyTag();
         if (compoundTag == null) {
            return;
         }

         CompoundTag customDataCopy = compoundTag;
         Optional optional = customDataCopy.getCompound("itemsadder");
         CompoundTag compoundTag2 = (CompoundTag)OptionalsKt.getOrNull(optional);
         if (compoundTag2 == null) {
            return;
         }

         CompoundTag ia = compoundTag2;
         int it_1 = 0;
         Optional optional2 = ia.getString("namespace");
         String str3 = (String)OptionalsKt.getOrNull(optional2);
         if (str3 == null) {
            return;
         }

         Optional optional3 = ia.getString("id");
         String str4 = (String)OptionalsKt.getOrNull(optional3);
         if (str4 == null) {
            return;
         }

         Pair pair = TuplesKt.to(str3, str4);
         String namespace = (String)pair.component1();
         String id = (String)pair.component2();
         str3 = (String)NexoPlugin.Companion.instance().converter().getItemsadderConverter().getChangedItemIds().get(namespace + ":" + id);
         if (str3 == null) {
            str3 = id;
         }

         if (str3 == null) {
            return;
         }

         String itemId = str3;
         itemStack.editPersistentDataContainer((a0_ -> PluginConverter.convertItemsAdder$lambda$1$1(itemId, (PersistentDataContainer)a0_)));
      }

      itemStack.editMeta((a0_ -> PluginConverter.convertItemsAdder$lambda$2((ItemMeta)a0_)));
   }

   public void convertOraxen(@NotNull ItemStack itemStack) {
      itemStack.editPersistentDataContainer(PluginConverter::convertOraxen$lambda$0);
   }

   public void convertOraxen(@NotNull Entity entity) {
      if (entity instanceof ItemDisplay) {
         PersistentDataContainer persistentDataContainer = ((ItemDisplay)entity).getPersistentDataContainer();
         CraftPersistentDataContainer pdc = (CraftPersistentDataContainer)persistentDataContainer;
         Iterable entrySet = (Iterable)pdc.getTagsCloned().entrySet();
         int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(entrySet, 10)), 16);
         Map destination = (Map)(new LinkedHashMap(capacity));

         for(Object element : entrySet) {
            Map.Entry entry = (Map.Entry)element;
            int value = 0;
            String key = (String)entry.getKey();
            Tag tag = (Tag)entry.getValue();
            Pair pair = TuplesKt.to(StringsKt.replace(key, "oraxen:", "nexo:", false), tag);
            destination.put(pair.getFirst(), pair.getSecond());
         }

         pdc.clear();
         pdc.putAll(destination);
      }
   }

   private static final void convertItemsAdder$lambda$1$1(String itemId, PersistentDataContainer pdc) {
      pdc.set(NexoItems.INSTANCE.getITEM_ID(), PersistentDataType.STRING, itemId);
   }

   private static final Unit convertItemsAdder$lambda$2(ItemMeta it) {
      it.displayName((Component)null);
      return Unit.INSTANCE;
   }

   private static final void convertItemsAdder$lambda$3(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }

   private static final void convertOraxen$lambda$0(PersistentDataContainer pdc) {
      Iterable entrySet = (Iterable)((CraftPersistentDataContainer)pdc).getTagsCloned().entrySet();
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(entrySet, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : entrySet) {
         Map.Entry entry = (Map.Entry)element;
         int value = 0;
         String key = (String)entry.getKey();
         Tag tag = (Tag)entry.getValue();
         Pair pair = TuplesKt.to(StringsKt.replace(key, "oraxen:", "nexo:", false), tag);
         destination.put(pair.getFirst(), pair.getSecond());
      }

      ((CraftPersistentDataContainer)pdc).clear();
      ((CraftPersistentDataContainer)pdc).putAll(destination);
   }
}
