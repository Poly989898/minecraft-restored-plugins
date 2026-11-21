package com.nexomc.nexo.nms.v1_21_R11.packets.transformers;

import com.nexomc.nexo.nms.Transformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u0002H\u0016J\f\u0010\u0007\u001a\u00020\t*\u00020\tH\u0002J\f\u0010\u0007\u001a\u00020\n*\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/packets/transformers/MerchantTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/world/item/trading/MerchantOffers;", "itemTransformer", "Lcom/nexomc/nexo/nms/v1_21_R11/packets/transformers/ItemTransformer;", "<init>", "(Lcom/nexomc/nexo/nms/v1_21_R11/packets/transformers/ItemTransformer;)V", "transform", "input", "Lnet/minecraft/world/item/trading/MerchantOffer;", "Lnet/minecraft/world/item/trading/ItemCost;", "com.nexomc:v1_21_R11"}
)
public final class MerchantTransformer implements Transformer<MerchantOffers> {
   @NotNull
   private final ItemTransformer itemTransformer;

   public MerchantTransformer(@NotNull ItemTransformer itemTransformer) {
      Intrinsics.checkNotNullParameter(itemTransformer, "itemTransformer");
      super();
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
      var10000 = this.itemTransformer;
      ItemStack var5 = $this$transform.itemStack();
      Intrinsics.checkNotNullExpressionValue(var5, "itemStack(...)");
      ItemStack itemStack = var10000.transform(var5);
      return new ItemCost($this$transform.item(), $this$transform.count(), components, itemStack);
   }

   private static final ItemCost transform$lambda$1(MerchantTransformer this$0, ItemCost it) {
      Intrinsics.checkNotNull(it);
      return this$0.transform(it);
   }

   private static final ItemCost transform$lambda$2(Function1 $tmp0, Object p0) {
      return (ItemCost)$tmp0.invoke(p0);
   }
}
