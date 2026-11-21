package com.nexomc.nexo.nms.v1_21_R5.packets.transformers;

import com.nexomc.nexo.nms.AbstractTransformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0002H\u0016J\f\u0010\t\u001a\u00020\u000b*\u00020\u000bH\u0002J\f\u0010\t\u001a\u00020\f*\u00020\fH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R5/packets/transformers/MerchantTransformer;", "Lcom/nexomc/nexo/nms/AbstractTransformer;", "Lnet/minecraft/world/item/trading/MerchantOffers;", "player", "Lorg/bukkit/entity/Player;", "itemTransformer", "Lcom/nexomc/nexo/nms/v1_21_R5/packets/transformers/ItemTransformer;", "<init>", "(Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/nms/v1_21_R5/packets/transformers/ItemTransformer;)V", "transform", "input", "Lnet/minecraft/world/item/trading/MerchantOffer;", "Lnet/minecraft/world/item/trading/ItemCost;", "com.nexomc:v1_21_R5"}
)
public final class MerchantTransformer extends AbstractTransformer<MerchantOffers> {
   @NotNull
   private final ItemTransformer itemTransformer;

   public MerchantTransformer(@NotNull Player player, @NotNull ItemTransformer itemTransformer) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(itemTransformer, "itemTransformer");
      super(player);
      this.itemTransformer = itemTransformer;
   }

   @NotNull
   public MerchantOffers transform(@NotNull MerchantOffers input) {
      Intrinsics.checkNotNullParameter(input, "input");
      MerchantOffers $this$transform_u24lambda_u240 = new MerchantOffers();
      int var4 = 0;
      Iterable $this$map$iv = (Iterable)input;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         MerchantOffer it = (MerchantOffer)item$iv$iv;
         int var15 = 0;
         Intrinsics.checkNotNull(it);
         destination$iv$iv.add(this.transform(it));
      }

      $this$transform_u24lambda_u240.addAll((Collection)((List)destination$iv$iv));
      return $this$transform_u24lambda_u240;
   }

   private final MerchantOffer transform(MerchantOffer $this$transform) {
      ItemCost var10003 = $this$transform.baseCostA;
      Intrinsics.checkNotNullExpressionValue(var10003, "baseCostA");
      ItemCost var10002 = this.transform(var10003);
      Optional var3 = $this$transform.costB.map(MerchantTransformer::transform$lambda$2);
      ItemTransformer var10004 = this.itemTransformer;
      ItemStack var10005 = $this$transform.result;
      Intrinsics.checkNotNullExpressionValue(var10005, "result");
      MerchantOffer offer = new MerchantOffer(var10002, var3, var10004.transform(var10005), $this$transform.uses, $this$transform.maxUses, $this$transform.xp, $this$transform.priceMultiplier, $this$transform.demand, $this$transform.ignoreDiscounts, $this$transform.asBukkit());
      offer.resetSpecialPriceDiff();
      offer.addToSpecialPriceDiff($this$transform.specialPriceDiff);
      return offer;
   }

   private final ItemCost transform(ItemCost $this$transform) {
      ItemTransformer var10000 = this.itemTransformer;
      DataComponentExactPredicate var10001 = $this$transform.components();
      Intrinsics.checkNotNullExpressionValue(var10001, "components(...)");
      DataComponentExactPredicate components = var10000.transform(var10001);
      Holder var10002 = $this$transform.item();
      int var10003 = $this$transform.count();
      ItemTransformer var10005 = this.itemTransformer;
      ItemStack var10006 = $this$transform.itemStack();
      Intrinsics.checkNotNullExpressionValue(var10006, "itemStack(...)");
      return new ItemCost(var10002, var10003, components, var10005.transform(var10006));
   }

   private static final ItemCost transform$lambda$1(MerchantTransformer this$0, ItemCost it) {
      Intrinsics.checkNotNull(it);
      return this$0.transform(it);
   }

   private static final ItemCost transform$lambda$2(Function1 $tmp0, Object p0) {
      return (ItemCost)$tmp0.invoke(p0);
   }
}
