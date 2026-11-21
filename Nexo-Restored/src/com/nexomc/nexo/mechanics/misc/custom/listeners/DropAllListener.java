package com.nexomc.nexo.mechanics.misc.custom.listeners;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.misc.custom.fields.CustomEvent;
import com.nexomc.nexo.utils.actions.ClickAction;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.Duration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\r*\u00020\u000eH\u0007b\u0002\b\u000fJ\u0014\u0010\u0010\u001a\u00020\u0011*\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u0003H\u0002¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/listeners/DropAllListener;", "Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "itemID", "", "cooldown", "Lkotlin/time/Duration;", "event", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "<init>", "(Ljava/lang/String;Lkotlin/time/Duration;Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;Lcom/nexomc/nexo/utils/actions/ClickAction;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "onDropped", "", "Lorg/bukkit/event/player/PlayerDropItemEvent;", "Lorg/bukkit/event/EventHandler;", "containsItemWithId", "", "Lorg/bukkit/inventory/PlayerInventory;", "com.nexomc:core"}
)
public final class DropAllListener extends CustomListener {
   private DropAllListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction) {
      super(itemID, cooldown, event, clickAction);
   }

   @EventHandler
   public final void onDropped(@NotNull PlayerDropItemEvent playerDropItemEvent) {
      ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
      ItemStack item = itemStack;
      if (Intrinsics.areEqual(this.getItemID(), NexoItems.idFromItem(item))) {
         if (this.getItemID() != null) {
            PlayerInventory inventory = playerDropItemEvent.getPlayer().getInventory();
            if (this.containsItemWithId(inventory, this.getItemID())) {
               return;
            }
         }

         Player player = playerDropItemEvent.getPlayer();
         if (this.perform(player, item)) {
            if (this.getEvent().getCancelEvent()) {
               playerDropItemEvent.setCancelled(true);
            }

         }
      }
   }

   private final boolean containsItemWithId(PlayerInventory playerInventory, String itemID) {
      boolean flag;
      ItemStack[] contents = playerInventory.getContents();
      Object[] objs = contents;
      int value = 0;
      int value2 = objs.length;

      while(true) {
         if (value >= value2) {
            flag = false;
            break;
         }

         Object element = objs[value];
         ItemStack it = (ItemStack)element;
         int value3 = 0;
         if (it != null && Intrinsics.areEqual(NexoItems.idFromItem(it), itemID)) {
            flag = true;
            break;
         }

         ++value;
      }

      return flag;
   }

   /** Kotlin default-constructor bridge, kept for binary compatibility with classes that were not recompiled. */
   public DropAllListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction, kotlin.jvm.internal.DefaultConstructorMarker marker) {
      this(itemID, cooldown, event, clickAction);
   }
}
