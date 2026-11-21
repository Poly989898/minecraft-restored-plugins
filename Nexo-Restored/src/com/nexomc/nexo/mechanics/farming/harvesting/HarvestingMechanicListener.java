package com.nexomc.nexo.mechanics.farming.harvesting;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.clock.Clock;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J \u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J&\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017H\u0002¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanicListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onPlayerInteract", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "NORMAL", "ignoreCancelled", "giveItem", "player", "Lorg/bukkit/entity/Player;", "item", "Lorg/bukkit/inventory/ItemStack;", "location", "Lorg/bukkit/Location;", "nearbyBlocks", "", "Lorg/bukkit/block/Block;", "radius", "", "height", "com.nexomc:core"}
)
public final class HarvestingMechanicListener implements Listener {
   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public final void onPlayerInteract(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Location location2 = clickedBlock.getLocation();
         if (location2 != null) {
            Location location = location2;
            ItemStack it = playerInteractEvent.getPlayer().getInventory().getItemInMainHand();
            int value = 0;
            Pair pair = TuplesKt.to(it, NexoItems.idFromItem(it));
            Object component1 = pair.component1();
            ItemStack item = (ItemStack)component1;
            String itemID = (String)pair.component2();
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            while(true) {
               if (iterator.hasNext()) {
                  MechanicFactory mechanicFactory3 = (MechanicFactory)iterator.next();
                  int value2 = 0;
                  MechanicFactory mechanicFactory2 = mechanicFactory3;
                  if (!(mechanicFactory3 instanceof HarvestingMechanicFactory)) {
                     mechanicFactory2 = null;
                  }

                  mechanicFactory3 = (HarvestingMechanicFactory)mechanicFactory2;
                  if (mechanicFactory3 == null) {
                     continue;
                  }

                  mechanicFactory = mechanicFactory3;
                  break;
               }

               mechanicFactory = null;
               break;
            }

            HarvestingMechanicFactory harvestingMechanicFactory = (HarvestingMechanicFactory)mechanicFactory;
            if ((HarvestingMechanicFactory)mechanicFactory != null) {
               HarvestingMechanic harvestingMechanic = harvestingMechanicFactory.getMechanic(itemID);
               if (harvestingMechanic != null) {
                  HarvestingMechanic mechanic = harvestingMechanic;
                  if (playerInteractEvent.getHand() == EquipmentSlot.HAND && playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
                     Player player = playerInteractEvent.getPlayer();
                     Clock it_4 = mechanic.clock(player);
                     int value3 = 0;
                     Clock clock = it_4.isFinished() ? it_4 : null;
                     if (clock == null) {
                        it_4.sendToPlayer(playerInteractEvent.getPlayer());
                        return;
                     }

                     clock.reset();
                     int durabilityDamage = 0;
                     Iterable iterable = (Iterable)this.nearbyBlocks(location, mechanic.getRadius(), mechanic.getHeight());

                     for(Object element : iterable) {
                        Block block = (Block)element;
                        int value4 = 0;
                        BlockData blockData = block.getBlockData();
                        Ageable ageable2 = blockData instanceof Ageable ? (Ageable)blockData : null;
                        if ((blockData instanceof Ageable ? (Ageable)blockData : null) != null) {
                           Ageable ageable = ageable2;
                           if (ageable.getAge() == ageable.getMaximumAge()) {
                              Player player2 = playerInteractEvent.getPlayer();
                              Location location3 = block.getLocation();
                              if (ProtectionLib.canBreak(player2, location3)) {
                                 player2 = playerInteractEvent.getPlayer();
                                 location3 = block.getLocation();
                                 if (ProtectionLib.canBuild(player2, location3)) {
                                    ageable.setAge(0);
                                    block.setBlockData((BlockData)ageable);
                                    SoundGroup it_3 = block.getBlockSoundGroup();
                                    int value5 = 0;
                                    Triple triple = com.nexomc.nexo.utils.TuplesKt.to(TuplesKt.to(it_3.getBreakSound(), it_3.getVolume()), it_3.getPitch());
                                    Object obj = triple.component1();
                                    Sound sound = (Sound)obj;
                                    float volume = ((Number)triple.component2()).floatValue();
                                    float pitch = ((Number)triple.component3()).floatValue();
                                    block.getWorld().playSound(block.getLocation(), sound, volume, pitch);
                                    Collection collection;
                                    switch (HarvestingMechanicListener.WhenMappings.$EnumSwitchMapping$0[block.getType().ordinal()]) {
                                       case 1:
                                          ItemStack[] itemStacks = new ItemStack[]{ItemStack.of(Material.WHEAT), ItemStack.of(Material.WHEAT_SEEDS)};
                                          collection = (Collection)CollectionsKt.listOf(itemStacks);
                                          break;
                                       case 2:
                                          ItemStack[] itemStacks2 = new ItemStack[]{ItemStack.of(Material.BEETROOT), ItemStack.of(Material.BEETROOT_SEEDS)};
                                          collection = (Collection)CollectionsKt.listOf(itemStacks2);
                                          break;
                                       default:
                                          collection = block.getDrops();
                                    }

                                    Iterable iterable2 = (Iterable)collection;

                                    for(Object obj2 : iterable2) {
                                       ItemStack itemStack = (ItemStack)obj2;
                                       int value6 = 0;
                                       Player player3 = playerInteractEvent.getPlayer();
                                       this.giveItem(player3, itemStack, location);
                                    }

                                    ++durabilityDamage;
                                 }
                              }
                           }
                        }
                     }

                     if (mechanic.getLowerItemDurability() && item.hasData((DataComponentType)DataComponentTypes.MAX_DAMAGE) && durabilityDamage > 0) {
                        EventUtils.INSTANCE.call((Event)(new PlayerItemDamageEvent(playerInteractEvent.getPlayer(), item, durabilityDamage, durabilityDamage)));
                     }

                     return;
                  }

                  return;
               }
            }

            return;
         }
      }

   }

   private final void giveItem(Player player, ItemStack item, Location location) {
      if (player.getInventory().firstEmpty() != -1) {
         PlayerInventory inventory = player.getInventory();
         ItemStack[] itemStacks = new ItemStack[]{item};
         Set entrySet = inventory.addItem(itemStacks).entrySet();
         Iterable iterable = (Iterable)entrySet;

         for(Object element : iterable) {
            Map.Entry itemStack = (Map.Entry)element;
            int value = 0;
            player.getWorld().dropItem(player.getLocation(), (ItemStack)itemStack.getValue());
         }

         Unit unit = Unit.INSTANCE;
      } else {
         Intrinsics.checkNotNullExpressionValue(player.getWorld().dropItemNaturally(location.toCenterLocation(), item), "dropItemNaturally(...)");
      }

   }

   private final List<Block> nearbyBlocks(Location location, int radius, int height) {
      List blocks = (List)(new ArrayList());
      int blockX = location.getBlockX();
      int z = 2;
      int value = radius / z;
      if ((radius ^ z) < 0 && value * z != radius) {
         --value;
      }

      int x = blockX - value;

      while(true) {
         int value2 = location.getBlockX();
         z = 2;
         value = radius / z;
         if ((radius ^ z) < 0 && value * z != radius) {
            --value;
         }

         if (x > value2 + value) {
            return blocks;
         }

         blockX = location.getBlockY();
         value = 2;
         int value3 = height / value;
         if ((height ^ value) < 0 && value3 * value != height) {
            --value3;
         }

         int y = blockX - value3;

         while(true) {
            value2 = location.getBlockY();
            value = 2;
            value3 = height / value;
            if ((height ^ value) < 0 && value3 * value != height) {
               --value3;
            }

            if (y > value2 + value3) {
               ++x;
               break;
            }

            blockX = location.getBlockZ();
            value3 = 2;
            int value4 = radius / value3;
            if ((radius ^ value3) < 0 && value4 * value3 != radius) {
               --value4;
            }

            z = blockX - value4;

            while(true) {
               value2 = location.getBlockZ();
               value3 = 2;
               value4 = radius / value3;
               if ((radius ^ value3) < 0 && value4 * value3 != radius) {
                  --value4;
               }

               if (z > value2 + value4) {
                  ++y;
                  break;
               }

               ((Collection)blocks).add(location.getWorld().getBlockAt(x, y, z));
               ++z;
            }
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.Material.values().length];
         try { a_0[org.bukkit.Material.WHEAT.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.Material.BEETROOTS.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
