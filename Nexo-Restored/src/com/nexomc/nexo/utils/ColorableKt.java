package com.nexomc.nexo.utils;

import com.nexomc.nexo.nms.NMSHandlers;
import kotlin.Metadata;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002¨\u0006\u0003"},
   d2 = {"asColorable", "Lcom/nexomc/nexo/utils/Colorable;", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class ColorableKt {
   @Nullable
   public static final Colorable asColorable(@NotNull ItemStack itemStack) {
      return itemStack.isEmpty() ? null : NMSHandlers.handler().itemUtils().asColorable(itemStack);
   }
}
