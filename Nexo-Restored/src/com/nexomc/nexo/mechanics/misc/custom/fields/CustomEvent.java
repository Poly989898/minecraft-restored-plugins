package com.nexomc.nexo.mechanics.misc.custom.fields;

import com.nexomc.nexo.mechanics.misc.custom.listeners.CustomListener;
import com.nexomc.nexo.utils.actions.ClickAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ'\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00032\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u0019¢\u0006\u0002\b\u001aR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\tR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "", "action", "", "isOneUsage", "", "cancelEvent", "<init>", "(Ljava/lang/String;ZZ)V", "()Z", "getCancelEvent", "type", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType;", "getType", "()Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEventType;", "params", "", "getParams", "()Ljava/util/List;", "getListener", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "itemID", "cooldown", "Lkotlin/time/Duration;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "getListener-Kx4hsE0", "com.nexomc:core"}
)
public final class CustomEvent {
   private final boolean isOneUsage;
   private final boolean cancelEvent;
   @NotNull
   private final CustomEventType type;
   @NotNull
   private final List<String> params;

   public CustomEvent(@NotNull String action, boolean isOneUsage, boolean cancelEvent) {
      List list;
      block: {
         /* implicit super() */
         this.isOneUsage = isOneUsage;
         this.cancelEvent = cancelEvent;
         this.params = (List)(new ArrayList());
         CharSequence charSequence = (CharSequence)action;
         String[] strs = new String[]{":"};
         List list2 = StringsKt.split(charSequence, strs, false, 0);
         if (!list2.isEmpty()) {
            ListIterator listIterator = list2.listIterator(list2.size());

            while(listIterator.hasPrevious()) {
               String p0 = (String)listIterator.previous();
               int value = 0;
               if (((CharSequence)p0).length() != 0) {
                  list = CollectionsKt.take((Iterable)list2, listIterator.nextIndex() + 1);
                  break block;
               }
            }
         }

         list = CollectionsKt.emptyList();
      }

      List actionParams = list;
      this.type = CustomEventType.valueOf((String)actionParams.get(0));
      CollectionsKt.addAll((Collection)this.params, (Iterable)CollectionsKt.drop((Iterable)actionParams, 1));
   }

   public final boolean isOneUsage() {
      return this.isOneUsage;
   }

   public final boolean getCancelEvent() {
      return this.cancelEvent;
   }

   @NotNull
   public final CustomEventType getType() {
      return this.type;
   }

   @NotNull
   public final List<String> getParams() {
      return this.params;
   }

   @NotNull
   public final CustomListener getListener_Kx4hsE0/* $FF was: getListener-Kx4hsE0*/(@Nullable String itemID, @Nullable Duration cooldown, @NotNull ClickAction clickAction) {
      return this.type.getConstructor().create_hhJSO8g(itemID, cooldown, this, clickAction);
   }
}
