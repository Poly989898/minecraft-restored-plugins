package com.nexomc.nexo.utils;

import kotlin.Metadata;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\bf\u0018\u00002\u00020\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u0003X¦\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\u0004\u0018\u00010\u0003X¦\u000e¢\u0006\f\u001a\u0004\b\t\u0010\u0005\"\u0004\b\n\u0010\u0007¨\u0006\u000bÀ\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/utils/Colorable;", "", "color", "Lorg/bukkit/Color;", "getColor", "()Lorg/bukkit/Color;", "setColor", "(Lorg/bukkit/Color;)V", "potionContentColor", "getPotionContentColor", "setPotionContentColor", "com.nexomc:core"}
)
public interface Colorable {
   @Nullable
   Color getColor();

   void setColor(@Nullable Color color);

   @Nullable
   Color getPotionContentColor();

   void setPotionContentColor(@Nullable Color color);
}
