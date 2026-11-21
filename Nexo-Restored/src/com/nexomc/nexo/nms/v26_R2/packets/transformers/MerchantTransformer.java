package com.nexomc.nexo.nms.v26_R2.packets.transformers;

import com.nexomc.nexo.nms.Transformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
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
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/MerchantTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/world/item/trading/MerchantOffers;", "itemTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;", "<init>", "(Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;)V", "transform", "input", "Lnet/minecraft/world/item/trading/MerchantOffer;", "Lnet/minecraft/world/item/trading/ItemCost;", "com.nexomc:v26_R2"}
)
public final class MerchantTransformer implements Transformer<MerchantOffers> {
   @NotNull
   private final ItemTransformer itemTransformer;

   public MerchantTransformer(@NotNull ItemTransformer itemTransformer) {
      super();
      this.itemTransformer = itemTransformer;
   }

   @NotNull
   public MerchantOffers transform(@NotNull MerchantOffers input) {
      MerchantOffers merchantOffers = new MerchantOffers();
      int value = 0;
      Iterable iterable = (Iterable)input;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         MerchantOffer it = (MerchantOffer)item;
         int value2 = 0;
         destination.add(this.transform(it));
      }

      merchantOffers.addAll((Collection)((List)destination));
      return merchantOffers;
   }

   private final MerchantOffer transform(MerchantOffer merchantOffer) {
      ItemCost baseCostA = merchantOffer.baseCostA;
      ItemCost itemCost = this.transform(baseCostA);
      Optional optional = merchantOffer.costB.map((a0_ -> MerchantTransformer.transform$lambda$2((b0_ -> MerchantTransformer.transform$lambda$1(this, (ItemCost)b0_)), a0_)));
      ItemTransformer itemTransformer2 = this.itemTransformer;
      ItemStack result = merchantOffer.getResult();
      MerchantOffer offer = new MerchantOffer(itemCost, optional, itemTransformer2.transform(result), merchantOffer.uses, merchantOffer.maxUses, merchantOffer.xp, merchantOffer.priceMultiplier, merchantOffer.demand, merchantOffer.ignoreDiscounts, merchantOffer.asBukkit());
      offer.resetSpecialPriceDiff();
      offer.addToSpecialPriceDiff(merchantOffer.getSpecialPriceDiff());
      return offer;
   }

   private final ItemCost transform(ItemCost itemCost) {
      ItemTransformer itemTransformer2 = this.itemTransformer;
      DataComponentExactPredicate dataComponentExactPredicate = itemCost.components();
      DataComponentExactPredicate components = itemTransformer2.transform(dataComponentExactPredicate);
      itemTransformer2 = this.itemTransformer;
      ItemStack itemStack2 = itemCost.itemStack();
      ItemStack itemStack = itemTransformer2.transform(itemStack2);
      return new ItemCost(itemCost.item(), itemCost.count(), components, itemStack);
   }

   private static final ItemCost transform$lambda$1(MerchantTransformer merchantTransformer, ItemCost it) {
      return merchantTransformer.transform(it);
   }

   private static final ItemCost transform$lambda$2(Function1 tmp0, Object p0) {
      return (ItemCost)tmp0.invoke(p0);
   }
}
