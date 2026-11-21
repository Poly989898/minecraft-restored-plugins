package com.nexomc.nexo.mechanics.misc.custom.fields;

import com.nexomc.nexo.mechanics.misc.custom.listeners.CustomListener;
import com.nexomc.nexo.utils.actions.ClickAction;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.time.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0011B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType;", "", "constructor", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType$CustomListenerConstructor;", "<init>", "(Ljava/lang/String;ILcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType$CustomListenerConstructor;)V", "getConstructor", "()Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType$CustomListenerConstructor;", "BREAK", "CLICK", "INV_CLICK", "DROP", "DROP_ALL", "PICKUP", "EQUIP", "UNEQUIP", "DEATH", "CustomListenerConstructor", "com.nexomc:core"}
)
public enum CustomEventType {
   BREAK(CustomEventType$1.INSTANCE),
   CLICK(CustomEventType$2.INSTANCE),
   INV_CLICK(CustomEventType$3.INSTANCE),
   DROP(CustomEventType$4.INSTANCE),
   DROP_ALL(CustomEventType$5.INSTANCE),
   PICKUP(CustomEventType$6.INSTANCE),
   EQUIP(CustomEventType$7.INSTANCE),
   UNEQUIP(CustomEventType$8.INSTANCE),
   DEATH(CustomEventType$9.INSTANCE);

   @NotNull
   private final CustomListenerConstructor constructor;

   private CustomEventType(CustomListenerConstructor constructor) {
      this.constructor = constructor;
   }

   @NotNull
   public final CustomListenerConstructor getConstructor() {
      return this.constructor;
   }


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u00002\u00020\u0001J1\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH&¢\u0006\u0002\b\f¨\u0006\rÀ\u0006\u0003"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType$CustomListenerConstructor;", "", "create", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "itemID", "", "cooldown", "Lkotlin/time/Duration;", "event", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "create-hhJSO8g", "com.nexomc:core"}
   )
   public interface CustomListenerConstructor {
      @NotNull
      CustomListener create_hhJSO8g/* $FF was: create-hhJSO8g*/(@Nullable String str, @Nullable Duration duration, @NotNull CustomEvent customEvent, @NotNull ClickAction clickAction);
   }
}
