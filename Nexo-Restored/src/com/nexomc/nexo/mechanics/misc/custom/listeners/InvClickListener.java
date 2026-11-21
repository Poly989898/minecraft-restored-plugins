package com.nexomc.nexo.mechanics.misc.custom.listeners;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.misc.custom.fields.CustomEvent;
import com.nexomc.nexo.utils.actions.ClickAction;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.Duration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ&\u0010\u000f\u001a\u00020\u0010*\u00020\u0011H\u0007b\u0018\b\u0012\u0012\n\b\u0013\u0012\u0006\b\n0\u00148\u0015\u0012\b\b\u0016\u0012\u0004\b\u0007\u0010\u0002R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/listeners/InvClickListener;", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "itemID", "", "cooldown", "Lkotlin/time/Duration;", "event", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "<init>", "(Ljava/lang/String;Lkotlin/time/Duration;Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;Lcom/nexomc/nexo/utils/actions/ClickAction;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "clickTypes", "", "Lorg/bukkit/event/inventory/ClickType;", "onInvClick", "", "Lorg/bukkit/event/inventory/InventoryClickEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "com.nexomc:core"}
)
public final class InvClickListener extends CustomListener {
   @NotNull
   private final Set<ClickType> clickTypes;

   private InvClickListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction) {
      super(itemID, cooldown, event, clickAction);
      this.clickTypes = (Set)(new LinkedHashSet());
      Pair pair = TuplesKt.to(CollectionsKt.getOrNull(event.getParams(), 0), CollectionsKt.getOrNull(event.getParams(), 1));
      String type = (String)pair.component1();
      String subType = (String)pair.component2();
      if (type != null) {
         switch (type.hashCode()) {
            case -1074341483:
               if (!type.equals("middle")) {
                  throw new IllegalStateException("Unexpected value: " + type);
               }

               ((Collection)this.clickTypes).add(ClickType.MIDDLE);
               return;
            case 96673:
               if (!type.equals("all")) {
                  throw new IllegalStateException("Unexpected value: " + type);
               }
               break;
            case 3317767:
               if (!type.equals("left")) {
                  throw new IllegalStateException("Unexpected value: " + type);
               }

               if (Intrinsics.areEqual(subType, "all")) {
                  ((Collection)this.clickTypes).add(ClickType.LEFT);
                  ((Collection)this.clickTypes).add(ClickType.SHIFT_LEFT);
                  return;
               } else {
                  if (Intrinsics.areEqual(subType, "shift")) {
                     ((Collection)this.clickTypes).add(ClickType.SHIFT_LEFT);
                  } else {
                     ((Collection)this.clickTypes).add(ClickType.LEFT);
                  }

                  return;
               }
            case 108511772:
               if (type.equals("right")) {
                  if (Intrinsics.areEqual(subType, "all")) {
                     ((Collection)this.clickTypes).add(ClickType.RIGHT);
                     ((Collection)this.clickTypes).add(ClickType.SHIFT_RIGHT);
                     return;
                  } else {
                     if (Intrinsics.areEqual(subType, "shift")) {
                        ((Collection)this.clickTypes).add(ClickType.SHIFT_RIGHT);
                     } else {
                        ((Collection)this.clickTypes).add(ClickType.RIGHT);
                     }

                     return;
                  }
               }

               throw new IllegalStateException("Unexpected value: " + type);
            default:
               throw new IllegalStateException("Unexpected value: " + type);
         }
      }

      if (Intrinsics.areEqual(subType, "all")) {
         ((Collection)this.clickTypes).add(ClickType.RIGHT);
         ((Collection)this.clickTypes).add(ClickType.SHIFT_RIGHT);
         ((Collection)this.clickTypes).add(ClickType.LEFT);
         ((Collection)this.clickTypes).add(ClickType.SHIFT_LEFT);
         ((Collection)this.clickTypes).add(ClickType.MIDDLE);
      } else if (Intrinsics.areEqual(subType, "shift")) {
         ((Collection)this.clickTypes).add(ClickType.SHIFT_RIGHT);
         ((Collection)this.clickTypes).add(ClickType.SHIFT_LEFT);
      } else {
         ((Collection)this.clickTypes).add(ClickType.RIGHT);
         ((Collection)this.clickTypes).add(ClickType.LEFT);
         ((Collection)this.clickTypes).add(ClickType.MIDDLE);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onInvClick(@NotNull InventoryClickEvent inventoryClickEvent) {
      ItemStack clicked = inventoryClickEvent.getCurrentItem();
      if (this.clickTypes.contains(inventoryClickEvent.getClick())) {
         if (clicked != null && Intrinsics.areEqual(this.getItemID(), NexoItems.idFromItem(clicked))) {
            HumanEntity whoClicked = inventoryClickEvent.getWhoClicked();
            if (this.perform((Player)whoClicked, clicked)) {
               if (this.getEvent().getCancelEvent()) {
                  inventoryClickEvent.setCancelled(true);
               }

            }
         }
      }
   }

   /** Kotlin default-constructor bridge, kept for binary compatibility with classes that were not recompiled. */
   public InvClickListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction, kotlin.jvm.internal.DefaultConstructorMarker marker) {
      this(itemID, cooldown, event, clickAction);
   }
}
