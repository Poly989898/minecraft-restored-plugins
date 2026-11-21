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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\u000f\u001a\u00020\u0010*\u00020\u0011H\u0007b\u0002\b\u0012R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/listeners/ClickListener;", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "itemID", "", "cooldown", "Lkotlin/time/Duration;", "event", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "<init>", "(Ljava/lang/String;Lkotlin/time/Duration;Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;Lcom/nexomc/nexo/utils/actions/ClickAction;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "interactActions", "", "Lorg/bukkit/event/block/Action;", "onClicked", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class ClickListener extends CustomListener {
   @NotNull
   private final Set<Action> interactActions;

   private ClickListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction) {
      super(itemID, cooldown, event, clickAction);
      this.interactActions = (Set)(new LinkedHashSet());
      Pair pair = TuplesKt.to(CollectionsKt.getOrNull(event.getParams(), 0), CollectionsKt.getOrNull(event.getParams(), 1));
      String type = (String)pair.component1();
      String subType = (String)pair.component2();
      if (type != null) {
         switch (type.hashCode()) {
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
                  ((Collection)this.interactActions).add(Action.LEFT_CLICK_AIR);
                  ((Collection)this.interactActions).add(Action.LEFT_CLICK_BLOCK);
                  return;
               } else {
                  if (Intrinsics.areEqual(subType, "block")) {
                     ((Collection)this.interactActions).add(Action.LEFT_CLICK_BLOCK);
                  } else {
                     ((Collection)this.interactActions).add(Action.LEFT_CLICK_AIR);
                  }

                  return;
               }
            case 108511772:
               if (type.equals("right")) {
                  if (Intrinsics.areEqual(subType, "all")) {
                     ((Collection)this.interactActions).add(Action.RIGHT_CLICK_AIR);
                     ((Collection)this.interactActions).add(Action.RIGHT_CLICK_BLOCK);
                     return;
                  } else {
                     if (Intrinsics.areEqual(subType, "block")) {
                        ((Collection)this.interactActions).add(Action.RIGHT_CLICK_BLOCK);
                     } else {
                        ((Collection)this.interactActions).add(Action.RIGHT_CLICK_AIR);
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
         ((Collection)this.interactActions).add(Action.RIGHT_CLICK_AIR);
         ((Collection)this.interactActions).add(Action.RIGHT_CLICK_BLOCK);
         ((Collection)this.interactActions).add(Action.LEFT_CLICK_AIR);
         ((Collection)this.interactActions).add(Action.LEFT_CLICK_BLOCK);
      } else if (Intrinsics.areEqual(subType, "block")) {
         ((Collection)this.interactActions).add(Action.RIGHT_CLICK_BLOCK);
         ((Collection)this.interactActions).add(Action.LEFT_CLICK_BLOCK);
      } else {
         ((Collection)this.interactActions).add(Action.RIGHT_CLICK_AIR);
         ((Collection)this.interactActions).add(Action.LEFT_CLICK_AIR);
      }

   }

   @EventHandler
   public final void onClicked(@NotNull PlayerInteractEvent playerInteractEvent) {
      if (this.interactActions.contains(playerInteractEvent.getAction())) {
         if (Intrinsics.areEqual(this.getItemID(), NexoItems.idFromItem(playerInteractEvent.getItem()))) {
            Player player = playerInteractEvent.getPlayer();
            ItemStack item = playerInteractEvent.getItem();
            if (this.perform(player, item)) {
               if (this.getEvent().getCancelEvent()) {
                  playerInteractEvent.setCancelled(true);
               }

            }
         }
      }
   }

   /** Kotlin default-constructor bridge, kept for binary compatibility with classes that were not recompiled. */
   public ClickListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction, kotlin.jvm.internal.DefaultConstructorMarker marker) {
      this(itemID, cooldown, event, clickAction);
   }
}
