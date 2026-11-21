package com.nexomc.nexo.mechanics.misc.custom.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.misc.custom.fields.CustomEvent;
import com.nexomc.nexo.utils.actions.ClickAction;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.Duration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\r*\u00020\u000eH\u0007b\u0002\b\u000f¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/listeners/EquipListener;", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "itemID", "", "cooldown", "Lkotlin/time/Duration;", "event", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "<init>", "(Ljava/lang/String;Lkotlin/time/Duration;Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;Lcom/nexomc/nexo/utils/actions/ClickAction;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "onEquipArmor", "", "Lcom/destroystokyo/paper/event/player/PlayerArmorChangeEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class EquipListener extends CustomListener {
   private EquipListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction) {
      super(itemID, cooldown, event, clickAction);
   }

   @EventHandler
   public final void onEquipArmor(@NotNull PlayerArmorChangeEvent playerArmorChangeEvent) {
      if (Intrinsics.areEqual(this.getItemID(), NexoItems.idFromItem(playerArmorChangeEvent.getNewItem()))) {
         Player player = playerArmorChangeEvent.getPlayer();
         this.perform(player, playerArmorChangeEvent.getNewItem());
      }
   }

   /** Kotlin default-constructor bridge, kept for binary compatibility with classes that were not recompiled. */
   public EquipListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction, kotlin.jvm.internal.DefaultConstructorMarker marker) {
      this(itemID, cooldown, event, clickAction);
   }
}
