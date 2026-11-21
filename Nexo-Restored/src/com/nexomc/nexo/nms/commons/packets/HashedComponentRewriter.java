package com.nexomc.nexo.nms.commons.packets;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.common.hash.HashCode;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.HashOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J?\u0010\t\u001a\u00020\n\"\b\b\u0000\u0010\u000b*\u00020\u00012\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\r2\b\u0010\u000e\u001a\u0004\u0018\u0001H\u000b2\b\u0010\u000f\u001a\u0004\u0018\u0001H\u000b2\u0006\u0010\u0010\u001a\u00020\u0011¢\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014J\u0010\u0010\u0016\u001a\u00020\u0011*\u0006\u0012\u0002\b\u00030\rH\u0002J/\u0010\u0017\u001a\u0004\u0018\u00010\u0018\"\b\b\u0000\u0010\u000b*\u00020\u00012\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\r2\u0006\u0010\u0019\u001a\u0002H\u000bH\u0002¢\u0006\u0002\u0010\u001aR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter;", "", "<init>", "()V", "hashOps", "Lcom/mojang/serialization/DynamicOps;", "Lcom/google/common/hash/HashCode;", "transformedToRaw", "Lit/unimi/dsi/fastutil/ints/Int2IntLinkedOpenHashMap;", "record", "", "T", "type", "Lnet/minecraft/core/component/DataComponentType;", "raw", "transformed", "rawPatched", "", "(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;Ljava/lang/Object;Z)V", "rewrite", "Lnet/minecraft/network/HashedStack;", "hashedStack", "isText", "hash", "", "value", "(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Integer;", "Companion", "com.nexomc:commons"}
)
public final class HashedComponentRewriter {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final DynamicOps<HashCode> hashOps;
   @NotNull
   private final Int2IntLinkedOpenHashMap transformedToRaw;
   private static final int REMOVED = Integer.MIN_VALUE;
   private static final int MAX_ENTRIES = 8192;

   public HashedComponentRewriter() {
      RegistryOps registryOps = NexoCommonsHandler.Companion.getRegistryAccess().createSerializationContext((DynamicOps)HashOps.CRC32C_INSTANCE);
      this.hashOps = (DynamicOps)registryOps;
      this.transformedToRaw = new Int2IntLinkedOpenHashMap(64);
   }

   public final <T> void record(@NotNull DataComponentType<T> type, @Nullable T raw, @Nullable T transformed, boolean rawPatched) {
      if (transformed != null && !Intrinsics.areEqual(raw, transformed)) {
         Integer number = this.hash(type, transformed);
         if (number != null) {
            int transformedHash = number;
            int value;
            if (raw != null && rawPatched) {
               Integer number2 = this.hash(type, raw);
               if (number2 == null) {
                  return;
               }

               value = number2;
            } else {
               value = Integer.MIN_VALUE;
            }

            int rawHash = value;
            if (this.transformedToRaw.size() >= 8192 && !this.transformedToRaw.containsKey(transformedHash)) {
               this.transformedToRaw.removeFirstInt();
            }

            this.transformedToRaw.putAndMoveToLast(transformedHash, rawHash);
         }
      }
   }

   @NotNull
   public final HashedStack rewrite(@NotNull HashedStack hashedStack) {
      HashedStack.ActualItem actualItem = hashedStack instanceof HashedStack.ActualItem ? (HashedStack.ActualItem)hashedStack : null;
      if ((hashedStack instanceof HashedStack.ActualItem ? (HashedStack.ActualItem)hashedStack : null) == null) {
         return hashedStack;
      } else {
         HashedStack.ActualItem item = actualItem;
         Map added = item.components().addedComponents();
         boolean changed = false;
         HashMap rewritten = new HashMap(added.size());

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)added.entrySet()) {
            DataComponentType type = (DataComponentType)entry.getKey();
            Integer hash = (Integer)entry.getValue();
            Integer number;
            if (this.isText(type)) {
               Int2IntLinkedOpenHashMap int2IntLinkedOpenHashMap = this.transformedToRaw;
               number = int2IntLinkedOpenHashMap.getOrDefault(hash, hash);
            } else {
               number = hash;
            }

            Integer raw = number;
            if (Intrinsics.areEqual(raw, hash)) {
               ((Map)rewritten).put(type, hash);
            } else {
               int value = Integer.MIN_VALUE;
               if (raw != null) {
                  if (raw == value) {
                     changed = true;
                     continue;
                  }
               }

               ((Map)rewritten).put(type, raw);
               changed = true;
            }
         }

         if (!changed) {
            return hashedStack;
         } else {
            return (HashedStack)(new HashedStack.ActualItem(item.item(), item.count(), new HashedPatchMap((Map)rewritten, item.components().removedComponents())));
         }
      }
   }

   private final boolean isText(DataComponentType<?> dataComponentType) {
      return dataComponentType == DataComponents.ITEM_NAME || dataComponentType == DataComponents.CUSTOM_NAME || dataComponentType == DataComponents.LORE;
   }

   private final <T> Integer hash(DataComponentType<T> type, T value) {
      HashedComponentRewriter hashedComponentRewriter = this;

      Object obj;
      try {
         HashedComponentRewriter hashedComponentRewriter2 = hashedComponentRewriter;
         int value2 = 0;
         obj = ResultShim.ok(((HashCode)(new TypedDataComponent(type, value)).encodeValue(hashedComponentRewriter2.hashOps).getOrThrow()).asInt());
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (Integer)(ResultShim.isFailure(obj) ? null : obj);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/nms/commons/packets/HashedComponentRewriter$Companion;", "", "<init>", "()V", "REMOVED", "", "MAX_ENTRIES", "com.nexomc:commons"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
