package com.nexomc.nexo.nms.v1_21_R4;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.IPluginConverter;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.LinkedHashMap;
import java.util.Map;
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
import kotlin.jvm.internal.Intrinsics;
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
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R4/PluginConverter;", "Lcom/nexomc/nexo/nms/IPluginConverter;", "<init>", "()V", "convertItemsAdder", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "convertOraxen", "entity", "Lorg/bukkit/entity/Entity;", "com.nexomc:v1_21_R4"}
)
public final class PluginConverter implements IPluginConverter {
   public void convertItemsAdder(@NotNull ItemStack itemStack) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      PluginConverter var2 = this;

      Object $this$convertItemsAdder_u24lambda_u240;
      try {
         block: {
            PluginConverter $this$convertItemsAdder_u24lambda_u240 = var2;
            int var4 = 0;
            net.minecraft.world.item.ItemStack var27 = ItemUtils.Companion.toCraftStack(itemStack).handle;
            if (var27 == null) {
               return;
            }

            net.minecraft.world.item.ItemStack nmsItem = var27;
            CustomData var28 = (CustomData)nmsItem.get(DataComponents.CUSTOM_DATA);
            if (var28 != null) {
               CompoundTag var29 = var28.copyTag();
               if (var29 != null) {
                  CompoundTag customDataCopy = var29;
                  String it = customDataCopy.getCompound("itemsadder").getString("id");
                  int var9 = 0;
                  Intrinsics.checkNotNull(it);
                  String var30 = ((CharSequence)it).length() != 0 ? it : null;
                  if (var30 == null) {
                     return;
                  }

                  String itemId = var30;
                  customDataCopy.remove("itemsadder");
                  CompoundTag var11 = customDataCopy.getCompound("PublicBukkitValues");
                  String var12 = "PublicBukkitValues";
                  int it = 0;
                  var11.putString("nexo:id", itemId);
                  Unit var14 = Unit.INSTANCE;
                  customDataCopy.put(var12, (Tag)var11);
                  $this$convertItemsAdder_u24lambda_u240 = ResultShim.ok((CustomData)nmsItem.set(DataComponents.CUSTOM_DATA, CustomData.of(customDataCopy)));
                  break block;
               }
            }

            return;
         }
      } catch (Throwable var15) {
         $this$convertItemsAdder_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(var15));
      }

      Throwable var31 = ResultShim.exceptionOrNull($this$convertItemsAdder_u24lambda_u240);
      if (var31 != null) {
         Throwable it = var31;
         int var18 = 0;
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }

         CustomData var32 = (CustomData)CraftItemStack.asNMSCopy(itemStack).getComponents().get(DataComponents.CUSTOM_DATA);
         if (var32 == null) {
            return;
         }

         CompoundTag var33 = var32.copyTag();
         if (var33 == null) {
            return;
         }

         CompoundTag customDataCopy = var33;
         CompoundTag ia = customDataCopy.getCompound("itemsadder");
         int var21 = 0;
         Pair var22 = TuplesKt.to(ia.getString("namespace"), ia.getString("id"));
         String namespace = (String)var22.component1();
         String id = (String)var22.component2();
         String var34 = (String)NexoPlugin.Companion.instance().converter().getItemsadderConverter().getChangedItemIds().get(namespace + ":" + id);
         if (var34 == null) {
            var34 = id;
         }

         String itemId = var34;
         itemStack.editPersistentDataContainer(PluginConverter::convertItemsAdder$lambda$1$1);
      }

      itemStack.editMeta(PluginConverter::convertItemsAdder$lambda$3);
   }

   public void convertOraxen(@NotNull ItemStack itemStack) {
      Intrinsics.checkNotNullParameter(itemStack, "itemStack");
      itemStack.editPersistentDataContainer(PluginConverter::convertOraxen$lambda$0);
   }

   public void convertOraxen(@NotNull Entity entity) {
      Intrinsics.checkNotNullParameter(entity, "entity");
      if (entity instanceof ItemDisplay) {
         PersistentDataContainer var10000 = ((ItemDisplay)entity).getPersistentDataContainer();
         Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer");
         CraftPersistentDataContainer pdc = (CraftPersistentDataContainer)var10000;
         Iterable $this$associate$iv = (Iterable)pdc.getTagsCloned().entrySet();
         int capacity$iv = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault($this$associate$iv, 10)), 16);
         Map destination$iv$iv = (Map)(new LinkedHashMap(capacity$iv));

         for(Object element$iv$iv : $this$associate$iv) {
            Map.Entry var13 = (Map.Entry)element$iv$iv;
            int var14 = 0;
            String key = (String)var13.getKey();
            Tag tag = (Tag)var13.getValue();
            Intrinsics.checkNotNull(key);
            Pair var17 = TuplesKt.to(StringsKt.replace(key, "oraxen:", "nexo:", false), tag);
            destination$iv$iv.put(var17.getFirst(), var17.getSecond());
         }

         pdc.clear();
         pdc.putAll(destination$iv$iv);
      }
   }

   private static final void convertItemsAdder$lambda$1$1(String $itemId, PersistentDataContainer pdc) {
      pdc.set(NexoItems.INSTANCE.getITEM_ID(), PersistentDataType.STRING, $itemId);
   }

   private static final Unit convertItemsAdder$lambda$2(ItemMeta it) {
      it.displayName((Component)null);
      return Unit.INSTANCE;
   }

   private static final void convertItemsAdder$lambda$3(Function1 $tmp0, Object p0) {
      $tmp0.invoke(p0);
   }

   private static final void convertOraxen$lambda$0(PersistentDataContainer pdc) {
      Intrinsics.checkNotNull(pdc, "null cannot be cast to non-null type org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer");
      CraftPersistentDataContainer pdc = (CraftPersistentDataContainer)pdc;
      Iterable $this$associate$iv = (Iterable)pdc.getTagsCloned().entrySet();
      int capacity$iv = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault($this$associate$iv, 10)), 16);
      Map destination$iv$iv = (Map)(new LinkedHashMap(capacity$iv));

      for(Object element$iv$iv : $this$associate$iv) {
         Map.Entry var12 = (Map.Entry)element$iv$iv;
         int var13 = 0;
         String key = (String)var12.getKey();
         Tag tag = (Tag)var12.getValue();
         Intrinsics.checkNotNull(key);
         Pair var16 = TuplesKt.to(StringsKt.replace(key, "oraxen:", "nexo:", false), tag);
         destination$iv$iv.put(var16.getFirst(), var16.getSecond());
      }

      pdc.clear();
      pdc.putAll(destination$iv$iv);
   }
}
