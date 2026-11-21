package com.nexomc.nexo.mechanics.trident;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.SchedulerUtils;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0002\b\u0007J\u0010\u0010\n\u001a\u00020\u0005*\u00020\u000bH\u0007b\u0002\b\u0007J\u0010\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0002\b\u0007¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/trident/CustomTridentListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onAdd", "", "Lcom/destroystokyo/paper/event/entity/EntityAddToWorldEvent;", "Lorg/bukkit/event/EventHandler;", "onTridentHit", "Lorg/bukkit/event/entity/ProjectileHitEvent;", "onTridentReturn", "Lorg/bukkit/event/player/PlayerPickupArrowEvent;", "onTrack", "Lio/papermc/paper/event/player/PlayerTrackEntityEvent;", "com.nexomc:core"}
)
public final class CustomTridentListener implements Listener {
   @EventHandler
   public final void onAdd(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      Entity entity = entityAddToWorldEvent.getEntity();
      Trident trident2 = entity instanceof Trident ? (Trident)entity : null;
      if ((entity instanceof Trident ? (Trident)entity : null) != null) {
         Trident trident = trident2;
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof TridentFactory)) {
                  mechanicFactory2 = null;
               }

               it = (TridentFactory)mechanicFactory2;
               if (it == null) {
                  continue;
               }

               mechanicFactory = it;
               break;
            }

            mechanicFactory = null;
            break;
         }

         TridentFactory tridentFactory = (TridentFactory)mechanicFactory;
         if (tridentFactory != null) {
            TridentMechanic tridentMechanic = tridentFactory.getMechanic(trident.getItemStack());
            if (tridentMechanic != null) {
               TridentMechanic mechanic = tridentMechanic;
               trident.setDamage(mechanic.getDamage());
               ICustomTridentEntity iCustomTridentEntity = NMSHandlers.handler().customEntityHandler().createTridentEntity(trident, mechanic);
               if (iCustomTridentEntity == null) {
                  return;
               }

               ICustomTridentEntity tridentEntity = iCustomTridentEntity;
               SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
               Location location = trident.getLocation();
               schedulerUtils.runAtLocationLater(location, 1L, (a0_ -> CustomTridentListener.onAdd$lambda$0(mechanic, trident, tridentEntity, (Location)a0_)));
               return;
            }
         }

      }
   }

   @EventHandler
   public final void onTridentHit(@NotNull ProjectileHitEvent projectileHitEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      Projectile entity = projectileHitEvent.getEntity();
      Trident trident2 = entity instanceof Trident ? (Trident)entity : null;
      if ((entity instanceof Trident ? (Trident)entity : null) != null) {
         Trident trident = trident2;
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof TridentFactory)) {
                  mechanicFactory2 = null;
               }

               it = (TridentFactory)mechanicFactory2;
               if (it == null) {
                  continue;
               }

               mechanicFactory = it;
               break;
            }

            mechanicFactory = null;
            break;
         }

         TridentFactory tridentFactory = (TridentFactory)mechanicFactory;
         if (tridentFactory != null) {
            TridentMechanic tridentMechanic = tridentFactory.getMechanic(trident.getItemStack());
            if (tridentMechanic != null) {
               TridentMechanic mechanic = tridentMechanic;
               Entity hitEntity = projectileHitEvent.getHitEntity();
               if (hitEntity != null) {
                  Location location = hitEntity.getLocation();
                  if (location != null) {
                     Location p0 = location;
                     TridentMechanic.TridentSound hit = mechanic.getSounds().getHit();
                     int value2 = 0;
                     hit.playSound(p0);
                  }
               }

               Block hitBlock = projectileHitEvent.getHitBlock();
               if (hitBlock != null) {
                  Location location2 = hitBlock.getLocation();
                  if (location2 != null) {
                     Location p0 = location2;
                     TridentMechanic.TridentSound hitGround = mechanic.getSounds().getHitGround();
                     int value3 = 0;
                     hitGround.playSound(p0);
                  }
               }

               return;
            }
         }

      }
   }

   @EventHandler
   public final void onTridentReturn(@NotNull PlayerPickupArrowEvent playerPickupArrowEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      AbstractArrow arrow = playerPickupArrowEvent.getArrow();
      Trident trident2 = arrow instanceof Trident ? (Trident)arrow : null;
      if ((arrow instanceof Trident ? (Trident)arrow : null) != null) {
         Trident trident = trident2;
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof TridentFactory)) {
                  mechanicFactory2 = null;
               }

               it = (TridentFactory)mechanicFactory2;
               if (it == null) {
                  continue;
               }

               mechanicFactory = it;
               break;
            }

            mechanicFactory = null;
            break;
         }

         TridentFactory tridentFactory = (TridentFactory)mechanicFactory;
         if (tridentFactory != null) {
            TridentMechanic tridentMechanic = tridentFactory.getMechanic(trident.getItemStack());
            if (tridentMechanic != null) {
               TridentMechanic mechanic = tridentMechanic;
               TridentMechanic.TridentSound get_return = mechanic.getSounds().get_return();
               Location location = playerPickupArrowEvent.getPlayer().getLocation();
               get_return.playSound(location);
               return;
            }
         }

      }
   }

   @EventHandler
   public final void onTrack(@NotNull PlayerTrackEntityEvent playerTrackEntityEvent) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof TridentFactory)) {
               mechanicFactory = null;
            }

            it = (TridentFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      TridentMechanic tridentMechanic;
      if (mechanicFactory != null) {
         Entity entity = playerTrackEntityEvent.getEntity();
         tridentMechanic = ((TridentFactory)mechanicFactory).getMechanic((entity instanceof Trident ? (Trident)entity : null) != null ? (entity instanceof Trident ? (Trident)entity : null).getItemStack() : null);
      } else {
         tridentMechanic = null;
      }

      if (tridentMechanic != null) {
         playerTrackEntityEvent.setCancelled(true);
      }

   }

   private static final Unit onAdd$lambda$0(TridentMechanic mechanic, Trident trident, ICustomTridentEntity tridentEntity, Location it) {
      TridentMechanic.TridentSound get_throw = mechanic.getSounds().get_throw();
      Location location = trident.getLocation();
      get_throw.playSound(location);
      location = trident.getLocation();
      tridentEntity.spawn(location);
      return Unit.INSTANCE;
   }
}
