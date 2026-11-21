package com.nexomc.nexo.mechanics.misc.misc;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent.ItemFrameChangeAction;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0002\b\u0007J&\u0010\n\u001a\u00020\u0005*\u00020\u000bH\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u000e\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0010\u001a\u00020\u0005*\u00020\u000bH\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0011\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0012\u001a\u00020\u0005*\u00020\u0013H\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0011\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0014\u001a\u00020\u0005*\u00020\u000bH\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0015\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0016\u001a\u00020\u0005*\u00020\u0017H\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0011\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0018\u001a\u00020\u0005*\u00020\u0019H\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0011\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001a\u001a\u00020\u0005*\u00020\u001bH\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0011\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001c\u001a\u00020\u0005*\u00020\u001dH\u0007b\u0018\b\u0007\u0012\n\b\f\u0012\u0006\b\n0\r8\u0011\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J\u0012\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0002J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/misc/MiscListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onDamage", "", "Lorg/bukkit/event/player/PlayerItemDamageEvent;", "Lorg/bukkit/event/EventHandler;", "onHopperCompost", "Lorg/bukkit/event/inventory/InventoryMoveItemEvent;", "onCompost", "Lorg/bukkit/event/player/PlayerInteractEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "NORMAL", "ignoreCancelled", "onStripLog", "HIGHEST", "onPiglinAggro", "Lorg/bukkit/event/entity/EntityTargetLivingEntityEvent;", "onDisableVanillaInteraction", "MONITOR", "onDisableItemConsume", "Lorg/bukkit/event/player/PlayerItemConsumeEvent;", "onDisableBowShoot", "Lorg/bukkit/event/entity/EntityShootBowEvent;", "onFrame", "Lio/papermc/paper/event/player/PlayerItemFrameChangeEvent;", "onRename", "Lorg/bukkit/event/inventory/PrepareAnvilEvent;", "getStrippedLog", "Lorg/bukkit/Material;", "log", "shouldPreventPiglinAggro", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class MiscListener implements Listener {
   @EventHandler
   public final void onDamage(@NotNull PlayerItemDamageEvent playerItemDamageEvent2) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      boolean flag;
      PlayerItemDamageEvent playerItemDamageEvent = playerItemDamageEvent2;

      try {
         int value = 0;
         Integer data = (Integer)playerItemDamageEvent.getItem().getData(DataComponentTypes.MAX_DAMAGE);
         if (data == null) {
            return;
         }

         int maxDamage = data;
         data = (Integer)playerItemDamageEvent.getItem().getData(DataComponentTypes.DAMAGE);
         if (data == null) {
            return;
         }

         int currentDamage = data;
         if (maxDamage - currentDamage > playerItemDamageEvent.getDamage()) {
            return;
         }

         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value2 = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof MiscMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               it = (MiscMechanicFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         block: {
            MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
            if (miscMechanicFactory != null) {
               MiscMechanic mechanic = miscMechanicFactory.getMechanic(playerItemDamageEvent.getItem());
               if (mechanic != null) {
                  flag = mechanic.getPreventBreaking();
                  break block;
               }
            }

            flag = false;
         }

         if (!flag) {
            return;
         }

         playerItemDamageEvent.setCancelled(true);
         Object obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   @EventHandler
   public final void onHopperCompost(@NotNull InventoryMoveItemEvent inventoryMoveItemEvent) {
      boolean flag;
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      if (inventoryMoveItemEvent.getSource().getType() == InventoryType.HOPPER && inventoryMoveItemEvent.getDestination().getType() == InventoryType.COMPOSTER) {
         Block block;
         if (inventoryMoveItemEvent.getSource().getLocation() != null) {
            Location location = inventoryMoveItemEvent.getSource().getLocation();
            block = location.getBlock();
         } else {
            block = null;
         }

         Block hopper = block;
         if (hopper != null && hopper.getType() == Material.HOPPER) {
            block = hopper.getRelative(BlockFace.DOWN);
            Block composter = block;
            if (composter.getType() == Material.COMPOSTER) {
               BlockData blockData = composter.getBlockData();
               Levelled levelled2 = blockData instanceof Levelled ? (Levelled)blockData : null;
               if ((blockData instanceof Levelled ? (Levelled)blockData : null) != null) {
                  Levelled levelled = levelled2;
                  MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
                  Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

                  while(true) {
                     if (iterator.hasNext()) {
                        MechanicFactory it = (MechanicFactory)iterator.next();
                        int value = 0;
                        MechanicFactory mechanicFactory2 = it;
                        if (!(it instanceof MiscMechanicFactory)) {
                           mechanicFactory2 = null;
                        }

                        it = (MiscMechanicFactory)mechanicFactory2;
                        if (it == null) {
                           continue;
                        }

                        mechanicFactory = it;
                        break;
                     }

                     mechanicFactory = null;
                     break;
                  }

                  block: {
                     MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
                     if (miscMechanicFactory != null) {
                        MiscMechanic mechanic = miscMechanicFactory.getMechanic(inventoryMoveItemEvent.getItem());
                        if (mechanic != null) {
                           flag = !mechanic.isCompostable();
                           break block;
                        }
                     }

                     flag = false;
                  }

                  if (flag) {
                     if (levelled.getLevel() < levelled.getMaximumLevel()) {
                        if (Random.Default.nextDouble() <= 0.65) {
                           levelled.setLevel(levelled.getLevel() + 1);
                        }

                        composter.setBlockData((BlockData)levelled);
                        composter.getWorld().playEffect(composter.getLocation(), Effect.COMPOSTER_FILL_ATTEMPT, 0, 1);
                        inventoryMoveItemEvent.getItem().subtract();
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public final void onCompost(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      boolean flag;
      ItemStack itemStack = playerInteractEvent.getItem();
      if (itemStack != null) {
         EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
         if (equipmentSlot != null) {
            Pair pair = TuplesKt.to(itemStack, equipmentSlot);
            ItemStack item = (ItemStack)pair.component1();
            EquipmentSlot hand = (EquipmentSlot)pair.component2();
            Block clickedBlock = playerInteractEvent.getClickedBlock();
            if (clickedBlock != null) {
               Block it = clickedBlock;
               int value = 0;
               clickedBlock = it.getType() == Material.COMPOSTER ? it : null;
               if (clickedBlock != null) {
                  Block block2 = playerInteractEvent.getClickedBlock();
                  BlockData blockData = block2 != null ? block2.getBlockData() : null;
                  Levelled levelled2 = blockData instanceof Levelled ? (Levelled)blockData : null;
                  if ((blockData instanceof Levelled ? (Levelled)blockData : null) == null) {
                     return;
                  }

                  Pair pair2 = TuplesKt.to(clickedBlock, levelled2);
                  Block block = (Block)pair2.component1();
                  Levelled levelled = (Levelled)pair2.component2();
                  if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
                     Player player = playerInteractEvent.getPlayer();
                     Location location = block.getLocation();
                     if (ProtectionLib.canInteract(player, location)) {
                        MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
                        value = 0;
                        Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

                        while(true) {
                           if (iterator.hasNext()) {
                              MechanicFactory mechanicFactory3 = (MechanicFactory)iterator.next();
                              int value2 = 0;
                              MechanicFactory mechanicFactory2 = mechanicFactory3;
                              if (!(mechanicFactory3 instanceof MiscMechanicFactory)) {
                                 mechanicFactory2 = null;
                              }

                              mechanicFactory3 = (MiscMechanicFactory)mechanicFactory2;
                              if (mechanicFactory3 == null) {
                                 continue;
                              }

                              mechanicFactory = mechanicFactory3;
                              break;
                           }

                           mechanicFactory = null;
                           break;
                        }

                        block: {
                           MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
                           if (miscMechanicFactory != null) {
                              MiscMechanic mechanic = miscMechanicFactory.getMechanic(item);
                              if (mechanic != null) {
                                 flag = mechanic.isCompostable();
                                 break block;
                              }
                           }

                           flag = false;
                        }

                        if (!flag) {
                           return;
                        }

                        if (playerInteractEvent.useInteractedBlock() == org.bukkit.event.Event.Result.DENY) {
                           return;
                        }

                        playerInteractEvent.setUseInteractedBlock(org.bukkit.event.Event.Result.ALLOW);
                        playerInteractEvent.setUseItemInHand(org.bukkit.event.Event.Result.ALLOW);
                        if (levelled.getLevel() >= levelled.getMaximumLevel()) {
                           return;
                        }

                        if (Math.random() <= 0.65) {
                           levelled.setLevel(levelled.getLevel() + 1);
                        }

                        block.setBlockData((BlockData)levelled);
                        block.getWorld().playEffect(block.getLocation(), Effect.COMPOSTER_FILL_ATTEMPT, 0, 1);
                        if (playerInteractEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                           item.setAmount(item.getAmount() - 1);
                        }

                        playerInteractEvent.getPlayer().swingHand(hand);
                        return;
                     }
                  }

                  return;
               }
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onStripLog(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block it = clickedBlock;
         int value = 0;
         clickedBlock = it.getType() == Material.COMPOSTER ? it : null;
         if (clickedBlock != null) {
            Block block = clickedBlock;
            ItemStack itemStack = playerInteractEvent.getItem();
            if (itemStack == null) {
               return;
            }

            EquipmentSlot hand = playerInteractEvent.getHand();
            if (hand == null) {
               return;
            }

            ItemStack item = (ItemStack)TuplesKt.to(itemStack, hand).component1();
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            value = 0;
            Iterator it_6 = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            while(true) {
               if (it_6.hasNext()) {
                  MechanicFactory mechanicFactory3 = (MechanicFactory)it_6.next();
                  int value2 = 0;
                  MechanicFactory mechanicFactory2 = mechanicFactory3;
                  if (!(mechanicFactory3 instanceof MiscMechanicFactory)) {
                     mechanicFactory2 = null;
                  }

                  mechanicFactory3 = (MiscMechanicFactory)mechanicFactory2;
                  if (mechanicFactory3 == null) {
                     continue;
                  }

                  mechanicFactory = mechanicFactory3;
                  break;
               }

               mechanicFactory = null;
               break;
            }

            MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
            if (miscMechanicFactory != null) {
               MiscMechanic mechanic = miscMechanicFactory.getMechanic(item);
               if (mechanic != null) {
                  MiscMechanic it_5 = mechanic;
                  int value3 = 0;
                  if ((it_5.getCanStripLogs() ? it_5 : null) != null) {
                     if (!Tag.LOGS.isTagged(block.getType())) {
                        return;
                     }

                     if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                     }

                     if (item.damage(1, (LivingEntity)playerInteractEvent.getPlayer()).isEmpty()) {
                        playerInteractEvent.getPlayer().playSound(playerInteractEvent.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                     }

                     Material type = block.getType();
                     Material strippedLog = this.getStrippedLog(type);
                     block.setType(strippedLog);
                     return;
                  }
               }
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPiglinAggro(@NotNull EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
      boolean flag;
      if (entityTargetLivingEntityEvent.getEntity() instanceof Piglin) {
         LivingEntity target = entityTargetLivingEntityEvent.getTarget();
         Player player = target instanceof Player ? (Player)target : null;
         if ((target instanceof Player ? (Player)target : null) != null) {
            EntityEquipment entityEquipment = player.getEquipment();
            if (entityEquipment != null) {
               EntityEquipment equipment = entityEquipment;
               ItemStack itemInMainHand = equipment.getItemInMainHand();
               if (this.shouldPreventPiglinAggro(itemInMainHand)) {
                  entityTargetLivingEntityEvent.setCancelled(true);
               }

               itemInMainHand = equipment.getItemInOffHand();
               if (this.shouldPreventPiglinAggro(itemInMainHand)) {
                  entityTargetLivingEntityEvent.setCancelled(true);
               }

               ItemStack[] armorContents = equipment.getArmorContents();
               Object[] objs = armorContents;
               int value = 0;
               int value2 = objs.length;

               while(true) {
                  if (value >= value2) {
                     flag = false;
                     break;
                  }

                  Object element = objs[value];
                  ItemStack p0 = (ItemStack)element;
                  int value3 = 0;
                  if (this.shouldPreventPiglinAggro(p0)) {
                     flag = true;
                     break;
                  }

                  ++value;
               }

               if (flag) {
                  entityTargetLivingEntityEvent.setCancelled(true);
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onDisableVanillaInteraction(@NotNull PlayerInteractEvent playerInteractEvent) {
      if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator it = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         MechanicFactory mechanicFactory;
         while(true) {
            if (it.hasNext()) {
               MechanicFactory mechanicFactory2 = (MechanicFactory)it.next();
               int value = 0;
               mechanicFactory = mechanicFactory2;
               if (!(mechanicFactory2 instanceof MiscMechanicFactory)) {
                  mechanicFactory = null;
               }

               mechanicFactory2 = (MiscMechanicFactory)mechanicFactory;
               if (mechanicFactory2 == null) {
                  continue;
               }

               mechanicFactory = mechanicFactory2;
               break;
            }

            mechanicFactory = null;
            break;
         }

         mechanicFactory = mechanicFactory;
         if (mechanicFactory != null) {
            MiscMechanic mechanic = (MiscMechanic)(mechanicFactory.getMechanic(playerInteractEvent.getItem()));
            if (mechanic != null) {
               MiscMechanic it_4 = mechanic;
               int value2 = 0;
               if ((it_4.isVanillaInteractionDisabled() ? it_4 : null) != null) {
                  playerInteractEvent.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                  return;
               }
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onDisableItemConsume(@NotNull PlayerItemConsumeEvent playerItemConsumeEvent) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator it = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (it.hasNext()) {
            MechanicFactory mechanicFactory2 = (MechanicFactory)it.next();
            int value = 0;
            mechanicFactory = mechanicFactory2;
            if (!(mechanicFactory2 instanceof MiscMechanicFactory)) {
               mechanicFactory = null;
            }

            mechanicFactory2 = (MiscMechanicFactory)mechanicFactory;
            if (mechanicFactory2 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory2;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         MiscMechanic mechanic = (MiscMechanic)(mechanicFactory.getMechanic(playerItemConsumeEvent.getItem()));
         if (mechanic != null) {
            MiscMechanic it_3 = mechanic;
            int value2 = 0;
            if ((it_3.isVanillaInteractionDisabled() ? it_3 : null) != null) {
               playerItemConsumeEvent.setCancelled(true);
               return;
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onDisableBowShoot(@NotNull EntityShootBowEvent entityShootBowEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      LivingEntity entity = entityShootBowEvent.getEntity();
      Player player2 = entity instanceof Player ? (Player)entity : null;
      if ((entity instanceof Player ? (Player)entity : null) != null) {
         Player player = player2;
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator it = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         while(true) {
            if (it.hasNext()) {
               MechanicFactory mechanicFactory3 = (MechanicFactory)it.next();
               int value = 0;
               MechanicFactory mechanicFactory2 = mechanicFactory3;
               if (!(mechanicFactory3 instanceof MiscMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               mechanicFactory3 = (MiscMechanicFactory)mechanicFactory2;
               if (mechanicFactory3 == null) {
                  continue;
               }

               mechanicFactory = mechanicFactory3;
               break;
            }

            mechanicFactory = null;
            break;
         }

         MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
         if (miscMechanicFactory != null) {
            ItemStack consumable = entityShootBowEvent.getConsumable();
            MiscMechanic mechanic = miscMechanicFactory.getMechanic(consumable);
            if (mechanic != null) {
               MiscMechanic it_2 = mechanic;
               int value2 = 0;
               if ((it_2.isVanillaInteractionDisabled() ? it_2 : null) != null) {
                  entityShootBowEvent.setCancelled(true);
                  player.updateInventory();
                  return;
               }
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onFrame(@NotNull PlayerItemFrameChangeEvent playerItemFrameChangeEvent) {
      if (playerItemFrameChangeEvent.getAction() == ItemFrameChangeAction.PLACE) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         MechanicFactory mechanicFactory;
         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               mechanicFactory = it;
               if (!(it instanceof MiscMechanicFactory)) {
                  mechanicFactory = null;
               }

               it = (MiscMechanicFactory)mechanicFactory;
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
         if (mechanicFactory != null) {
            MiscMechanic miscMechanic = (MiscMechanic)(mechanicFactory.getMechanic(playerItemFrameChangeEvent.getItemStack()));
            if (miscMechanic != null) {
               MiscMechanic mechanic = miscMechanic;
               if (mechanic.getPreventItemFrames()) {
                  playerItemFrameChangeEvent.setCancelled(true);
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onRename(@NotNull PrepareAnvilEvent prepareAnvilEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      ItemStack firstItem = prepareAnvilEvent.getInventory().getFirstItem();
      if (firstItem != null) {
         ItemStack input = firstItem;
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof MiscMechanicFactory)) {
                  mechanicFactory2 = null;
               }

               it = (MiscMechanicFactory)mechanicFactory2;
               if (it == null) {
                  continue;
               }

               mechanicFactory = it;
               break;
            }

            mechanicFactory = null;
            break;
         }

         MiscMechanicFactory miscMechanicFactory = (MiscMechanicFactory)mechanicFactory;
         if (miscMechanicFactory != null) {
            MiscMechanic miscMechanic = miscMechanicFactory.getMechanic(input);
            if (miscMechanic != null) {
               MiscMechanic mechanic = miscMechanic;
               if (!mechanic.getPreventRenaming()) {
                  return;
               }

               String renameText = prepareAnvilEvent.getView().getRenameText();
               CharSequence inputName = (CharSequence)renameText;
               if (inputName == null || inputName.length() == 0) {
                  return;
               }

               Component dataOrDefault = (Component)input.getDataOrDefault(DataComponentTypes.CUSTOM_NAME, input.getData(DataComponentTypes.ITEM_NAME));
               if (dataOrDefault == null) {
                  return;
               }

               Component inputName_1 = dataOrDefault;
               if (Intrinsics.areEqual(renameText, PlainTextComponentSerializer.plainText().serialize(inputName_1))) {
                  return;
               }

               prepareAnvilEvent.setResult((ItemStack)null);
               return;
            }
         }

      }
   }

   private final Material getStrippedLog(Material log) {
      Material material;
      switch (MiscListener.WhenMappings.$EnumSwitchMapping$0[log.ordinal()]) {
         case 1 -> material = Material.STRIPPED_OAK_LOG;
         case 2 -> material = Material.STRIPPED_SPRUCE_LOG;
         case 3 -> material = Material.STRIPPED_BIRCH_LOG;
         case 4 -> material = Material.STRIPPED_JUNGLE_LOG;
         case 5 -> material = Material.STRIPPED_ACACIA_LOG;
         case 6 -> material = Material.STRIPPED_DARK_OAK_LOG;
         case 7 -> material = Material.STRIPPED_CRIMSON_STEM;
         case 8 -> material = Material.STRIPPED_WARPED_STEM;
         case 9 -> material = Material.STRIPPED_MANGROVE_LOG;
         case 10 -> material = Material.STRIPPED_OAK_WOOD;
         case 11 -> material = Material.STRIPPED_SPRUCE_WOOD;
         case 12 -> material = Material.STRIPPED_BIRCH_WOOD;
         case 13 -> material = Material.STRIPPED_JUNGLE_WOOD;
         case 14 -> material = Material.STRIPPED_ACACIA_WOOD;
         case 15 -> material = Material.STRIPPED_DARK_OAK_WOOD;
         case 16 -> material = Material.STRIPPED_CRIMSON_HYPHAE;
         case 17 -> material = Material.STRIPPED_WARPED_HYPHAE;
         case 18 -> material = Material.STRIPPED_MANGROVE_WOOD;
         default -> material = null;
      }

      return material;
   }

   private final boolean shouldPreventPiglinAggro(ItemStack itemStack) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory mechanicFactory2 = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = mechanicFactory2;
            if (!(mechanicFactory2 instanceof MiscMechanicFactory)) {
               mechanicFactory = null;
            }

            mechanicFactory2 = (MiscMechanicFactory)mechanicFactory;
            if (mechanicFactory2 == null) {
               continue;
            }

            mechanicFactory = mechanicFactory2;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      MiscMechanic miscMechanic;
      if (mechanicFactory != null) {
         miscMechanic = ((MiscMechanicFactory)mechanicFactory).getMechanic(itemStack);
         if (miscMechanic != null) {
            MiscMechanic it = miscMechanic;
            int value2 = 0;
            miscMechanic = it.getPiglinsIgnoreWhenEquipped() ? it : null;
            return miscMechanic != null;
         }
      }

      miscMechanic = null;
      return miscMechanic != null;
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.Material.values().length];
         try { a_0[org.bukkit.Material.OAK_LOG.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.SPRUCE_LOG.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.BIRCH_LOG.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.JUNGLE_LOG.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.ACACIA_LOG.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.DARK_OAK_LOG.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.CRIMSON_STEM.ordinal()] = 7; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.WARPED_STEM.ordinal()] = 8; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.MANGROVE_LOG.ordinal()] = 9; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.OAK_WOOD.ordinal()] = 10; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.SPRUCE_WOOD.ordinal()] = 11; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.BIRCH_WOOD.ordinal()] = 12; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.JUNGLE_WOOD.ordinal()] = 13; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.ACACIA_WOOD.ordinal()] = 14; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.DARK_OAK_WOOD.ordinal()] = 15; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.CRIMSON_HYPHAE.ordinal()] = 16; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.WARPED_HYPHAE.ordinal()] = 17; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.MANGROVE_WOOD.ordinal()] = 18; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
