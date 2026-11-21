package com.nexomc.nexo.mechanics.misc.soulbound;

import com.nexomc.nexo.api.NexoItems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007b\u0002\b\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanicListener;", "Lorg/bukkit/event/Listener;", "factory", "Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanicFactory;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/soulbound/SoulBoundMechanicFactory;)V", "onPlayerDeath", "", "Lorg/bukkit/event/entity/PlayerDeathEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class SoulBoundMechanicListener implements Listener {
   @NotNull
   private final SoulBoundMechanicFactory factory;

   public SoulBoundMechanicListener(@NotNull SoulBoundMechanicFactory factory) {
      super();
      this.factory = factory;
   }

   @EventHandler
   public final void onPlayerDeath(@NotNull PlayerDeathEvent playerDeathEvent) {
      if (!playerDeathEvent.getKeepInventory()) {
         ThreadLocalRandom random = ThreadLocalRandom.current();
         List drops = playerDeathEvent.getDrops();
         Iterable iterable = (Iterable)drops;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value = 0;
            ItemStack drop = (ItemStack)element;
            int value2 = 0;
            String str = NexoItems.idFromItem(drop);
            ItemStack itemStack;
            if (str == null) {
               itemStack = null;
            } else {
               String itemID = str;
               SoulBoundMechanic soulBoundMechanic = this.factory.getMechanic(itemID);
               if (soulBoundMechanic == null) {
                  itemStack = null;
               } else {
                  SoulBoundMechanic mechanic = soulBoundMechanic;
                  itemStack = (double)random.nextInt(100) >= mechanic.getLoseChance() * (double)100 ? drop : null;
               }
            }

            if (itemStack != null) {
               Object it = itemStack;
               int value3 = 0;
               destination.add(it);
            }
         }

         Set items = CollectionsKt.toSet((Iterable)((List)destination));
         drops = playerDeathEvent.getItemsToKeep();
         CollectionsKt.addAll((Collection)drops, (Iterable)items);
         drops = playerDeathEvent.getDrops();
         CollectionsKt.removeAll((Collection)drops, (Iterable)items);
      }
   }
}
