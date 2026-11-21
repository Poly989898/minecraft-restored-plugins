package com.nexomc.nexo.mechanics.furniture.connectable;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u0000 \u00062\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0006B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0007"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType;", "", "<init>", "(Ljava/lang/String;I)V", "ITEM_MODEL", "ITEM", "Companion", "com.nexomc:core"}
)
public enum ConnectableItemType {
   ITEM_MODEL,
   ITEM;

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final ConnectableItemType defaultType = ITEM_MODEL;


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType$Companion;", "", "<init>", "()V", "defaultType", "Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType;", "getDefaultType", "()Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableItemType;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ConnectableItemType getDefaultType() {
         return ConnectableItemType.defaultType;
      }
   }
}
