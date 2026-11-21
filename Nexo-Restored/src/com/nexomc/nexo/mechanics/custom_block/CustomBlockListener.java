package com.nexomc.nexo.mechanics.custom_block;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.custom_block.NexoBlockInteractEvent;
import com.nexomc.nexo.api.events.custom_block.chorusblock.NexoChorusBlockInteractEvent;
import com.nexomc.nexo.api.events.custom_block.noteblock.NexoNoteBlockInteractEvent;
import com.nexomc.nexo.api.events.custom_block.stringblock.NexoStringBlockInteractEvent;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.stringblock.stackable.StackableChild;
import com.nexomc.nexo.mechanics.limitedplacing.LimitedPlacing;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.ItemUtils;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.nexo.utils.wrappers.PotionEffectTypeWrapper;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.bukkit.ExplosionResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\f\u001a\u00020\u0005*\u00020\rH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u000e\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u000f\u001a\u00020\u0005*\u00020\rH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0011\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0012\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0013\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u000e\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\u0016\u001a\u00020\u0005*\u00020\u0017H\u0007b\u0002\b\u0007J\u0010\u0010\u0018\u001a\u00020\u0005*\u00020\u0019H\u0007b\u0002\b\u0007J&\u0010\u001a\u001a\u00020\u0005*\u00020\u001bH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001a\u001a\u00020\u0005*\u00020\u001cH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u001d\u001a\u00020\u0005*\u00020\u001eH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u000e\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\u001f\u001a\u00020\u0005*\u00020 H\u0007b\u0002\b\u0007¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "callInteract", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "ignoreCancelled", "onInteractedNexoBlock", "Lcom/nexomc/nexo/api/events/custom_block/NexoBlockInteractEvent;", "HIGH", "onInteractStorage", "HIGHEST", "onLimitedPlacing", "NORMAL", "onPrePlacingCustomBlock", "onBreakingCustomBlock", "Lorg/bukkit/event/block/BlockBreakEvent;", "onEntityExplosion", "Lorg/bukkit/event/entity/EntityExplodeEvent;", "onBlockExplosion", "Lorg/bukkit/event/block/BlockExplodeEvent;", "onPiston", "Lorg/bukkit/event/block/BlockPistonRetractEvent;", "Lorg/bukkit/event/block/BlockPistonExtendEvent;", "onPlacingVanilla", "Lorg/bukkit/event/block/BlockPlaceEvent;", "onMiddleClick", "Lio/papermc/paper/event/player/PlayerPickItemEvent;", "com.nexomc:core"}
)
public final class CustomBlockListener implements Listener {
   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void callInteract(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block block2 = clickedBlock;
         int value = 0;
         clickedBlock = playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK ? block2 : null;
         if (clickedBlock != null) {
            Block block = clickedBlock;
            CustomBlockMechanic mechanic = NexoBlocks.customBlockMechanic(block);
            NexoBlockInteractEvent nexoBlockInteractEvent;
            if (mechanic instanceof NoteBlockMechanic) {
               NoteBlockMechanic noteBlockMechanic = (NoteBlockMechanic)mechanic;
               Player player = playerInteractEvent.getPlayer();
               ItemStack item = playerInteractEvent.getItem();
               EquipmentSlot hand = playerInteractEvent.getHand();
               BlockFace blockFace = playerInteractEvent.getBlockFace();
               Action action = playerInteractEvent.getAction();
               nexoBlockInteractEvent = new NexoNoteBlockInteractEvent(noteBlockMechanic, player, item, hand, block, blockFace, action);
            } else if (mechanic instanceof StringBlockMechanic) {
               nexoBlockInteractEvent = null;
               value = 0;
               StackableChild stackableChild = mechanic instanceof StackableChild ? (StackableChild)mechanic : null;
               StringBlockMechanic stringBlockMechanic;
               block: {
                  stringBlockMechanic = stackableChild != null ? stackableChild.getParentBlock() : null;
                  if (stringBlockMechanic != null) {
                     break block;
                  }

                  stringBlockMechanic = (StringBlockMechanic)mechanic;
               }

               Player player2 = playerInteractEvent.getPlayer();
               ItemStack itemStack = playerInteractEvent.getItem();
               EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
               BlockFace blockFace2 = playerInteractEvent.getBlockFace();
               Action action2 = playerInteractEvent.getAction();
               nexoBlockInteractEvent = new NexoStringBlockInteractEvent(stringBlockMechanic, player2, itemStack, equipmentSlot, block, blockFace2, action2);
               nexoBlockInteractEvent = nexoBlockInteractEvent;
            } else {
               if (!(mechanic instanceof ChorusBlockMechanic)) {
                  return;
               }

               ChorusBlockMechanic chorusBlockMechanic = (ChorusBlockMechanic)mechanic;
               Player player3 = playerInteractEvent.getPlayer();
               ItemStack itemStack2 = playerInteractEvent.getItem();
               EquipmentSlot equipmentSlot2 = playerInteractEvent.getHand();
               BlockFace blockFace3 = playerInteractEvent.getBlockFace();
               Action action3 = playerInteractEvent.getAction();
               nexoBlockInteractEvent = new NexoChorusBlockInteractEvent(chorusBlockMechanic, player3, itemStack2, equipmentSlot2, block, blockFace3, action3);
            }

            NexoBlockInteractEvent customBlockEvent = nexoBlockInteractEvent;
            if (!EventUtils.INSTANCE.call(customBlockEvent)) {
               playerInteractEvent.setUseInteractedBlock(Result.DENY);
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onInteractedNexoBlock(@NotNull NexoBlockInteractEvent nexoBlockInteractEvent) {
      Player player = nexoBlockInteractEvent.getPlayer();
      Location location = nexoBlockInteractEvent.getBlock().getLocation();
      if (!ProtectionLib.canInteract(player, location)) {
         nexoBlockInteractEvent.setCancelled(true);
      } else if (!nexoBlockInteractEvent.getPlayer().isSneaking() && nexoBlockInteractEvent.getMechanic().hasClickActions()) {
         nexoBlockInteractEvent.getMechanic().runClickActions(nexoBlockInteractEvent.getPlayer());
         nexoBlockInteractEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onInteractStorage(@NotNull NexoBlockInteractEvent nexoBlockInteractEvent) {
      if (nexoBlockInteractEvent.getPlayer().isSneaking()) {
         ItemStack it = nexoBlockInteractEvent.getPlayer().getInventory().getItemInMainHand();
         int value = 0;
         if (it.getType().isBlock() || NexoBlocks.isCustomBlock(it) || NexoFurniture.isFurniture(it)) {
            return;
         }
      }

      StorageMechanic storage = nexoBlockInteractEvent.getMechanic().storage();
      if (storage != null) {
         StorageMechanic storageMechanic = storage;
         switch (CustomBlockListener.WhenMappings.$EnumSwitchMapping$0[storageMechanic.getStorageType().ordinal()]) {
            case 1:
            case 2:
               storageMechanic.openStorage(nexoBlockInteractEvent.getBlock(), nexoBlockInteractEvent.getPlayer());
               break;
            case 3:
               Player player = nexoBlockInteractEvent.getPlayer();
               Location location = nexoBlockInteractEvent.getBlock().getLocation();
               storageMechanic.openPersonalStorage(player, location, (ItemDisplay)null);
               break;
            case 4:
               Player player2 = nexoBlockInteractEvent.getPlayer();
               Location location2 = nexoBlockInteractEvent.getBlock().getLocation();
               storageMechanic.openDisposal(player2, location2, (ItemDisplay)null);
               break;
            case 5:
               nexoBlockInteractEvent.getPlayer().openInventory(nexoBlockInteractEvent.getPlayer().getEnderChest());
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         nexoBlockInteractEvent.setCancelled(true);
      }
   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public final void onLimitedPlacing(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         ItemStack itemStack = playerInteractEvent.getItem();
         if (itemStack != null) {
            Pair pair = TuplesKt.to(clickedBlock, itemStack);
            Block block = (Block)pair.component1();
            ItemStack item = (ItemStack)pair.component2();
            if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && (playerInteractEvent.getPlayer().isSneaking() || !BlockHelpers.isInteractable(block, (Player)null))) {
               String str = NexoItems.idFromItem(item);
               if (str != null) {
                  CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(str);
                  if (customBlockMechanic != null) {
                     CustomBlockMechanic mechanic = customBlockMechanic;
                     LimitedPlacing limitedPlacing2 = mechanic.getLimitedPlacing();
                     if (limitedPlacing2 != null) {
                        LimitedPlacing limitedPlacing = limitedPlacing2;
                        Block relative = block.getRelative(playerInteractEvent.getBlockFace()).getRelative(BlockFace.DOWN);
                        Block belowPlaced = relative;
                        BlockFace blockFace = playerInteractEvent.getBlockFace();
                        if (limitedPlacing.isNotPlacableOn(block, blockFace)) {
                           playerInteractEvent.setCancelled(true);
                        } else if (limitedPlacing.isRadiusLimited()) {
                           LimitedPlacing.RadiusLimitation radiusLimitation = limitedPlacing.getRadiusLimitation();
                           LimitedPlacing.RadiusLimitation it = radiusLimitation;
                           int value = 0;
                           Pair pair2 = TuplesKt.to(it.getRadius(), it.getAmount());
                           int rad = ((Number)pair2.component1()).intValue();
                           int amount = ((Number)pair2.component2()).intValue();
                           value = 0;
                           int x = -rad;
                           if (x <= rad) {
                              while(true) {
                                 int y = -rad;
                                 if (y <= rad) {
                                    while(true) {
                                       int z = -rad;
                                       if (z <= rad) {
                                          while(true) {
                                             Block block2 = block.getRelative(x, y, z);
                                             StringBlockMechanic relativeMechanic = NexoBlocks.stringMechanic(block2);
                                             if (relativeMechanic != null && Intrinsics.areEqual(relativeMechanic.getItemID(), mechanic.getItemID())) {
                                                ++value;
                                             }

                                             if (z == rad) {
                                                break;
                                             }

                                             ++z;
                                          }
                                       }

                                       if (y == rad) {
                                          break;
                                       }

                                       ++y;
                                    }
                                 }

                                 if (x == rad) {
                                    break;
                                 }

                                 ++x;
                              }
                           }

                           if (value >= amount) {
                              playerInteractEvent.setCancelled(true);
                           }
                        } else if (limitedPlacing.getType() == LimitedPlacing.LimitedPlacingType.ALLOW) {
                           if (!limitedPlacing.checkLimited(belowPlaced)) {
                              playerInteractEvent.setCancelled(true);
                           }
                        } else if (limitedPlacing.getType() == LimitedPlacing.LimitedPlacingType.DENY && limitedPlacing.checkLimited(belowPlaced)) {
                           playerInteractEvent.setCancelled(true);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onPrePlacingCustomBlock(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanic stringBlockMechanic;
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
         if (equipmentSlot != null) {
            Pair pair = TuplesKt.to(clickedBlock, equipmentSlot);
            Block placedAgainst = (Block)pair.component1();
            EquipmentSlot hand = (EquipmentSlot)pair.component2();
            ItemStack itemStack = playerInteractEvent.getItem();
            if (itemStack != null) {
               ItemStack p0 = itemStack;
               Player player = playerInteractEvent.getPlayer();
               Player player2 = player;
               int p0_3 = 0;
               ItemStack itemStack2 = !player2.hasCooldown(p0) ? p0 : null;
               if (itemStack2 != null) {
                  ItemStack item = itemStack2;
                  String str = NexoItems.idFromItem(item);
                  if (str == null) {
                     return;
                  }

                  String itemId = str;
                  if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.useItemInHand() != Result.DENY) {
                     if (!playerInteractEvent.getPlayer().isSneaking() && BlockHelpers.isInteractable(placedAgainst, (Player)null)) {
                        return;
                     }

                     CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(itemId);
                     if (customBlockMechanic == null) {
                        return;
                     }

                     CustomBlockMechanic mechanic = customBlockMechanic;
                     if (mechanic instanceof NoteBlockMechanic) {
                        block: {
                           DirectionalBlock directional = ((NoteBlockMechanic)mechanic).getDirectional();
                           if (directional != null) {
                              BlockFace blockFace = playerInteractEvent.getBlockFace();
                              customBlockMechanic = directional.directionMechanic(blockFace, playerInteractEvent.getPlayer());
                              if (customBlockMechanic != null) {
                                 break block;
                              }
                           }

                           directional = ((NoteBlockMechanic)mechanic).getDirectional();
                           customBlockMechanic = directional != null ? directional.getParentMechanic() : null;
                           if (customBlockMechanic == null) {
                              customBlockMechanic = (NoteBlockMechanic)mechanic;
                           }
                        }

                        customBlockMechanic = customBlockMechanic;
                     } else if (mechanic instanceof StringBlockMechanic) {
                        if (placedAgainst.getRelative(playerInteractEvent.getBlockFace()).isLiquid()) {
                           return;
                        }

                        block: {
                           String str2 = (String)CollectionsKt.randomOrNull((Collection)((StringBlockMechanic)mechanic).randomPlace(), (Random)Random.Default);
                           if (str2 != null) {
                              String p0_2 = str2;
                              NexoBlocks nexoBlocks = NexoBlocks.INSTANCE;
                              int value = 0;
                              stringBlockMechanic = NexoBlocks.stringMechanic(p0_2);
                              if (stringBlockMechanic != null) {
                                 break block;
                              }
                           }

                           stringBlockMechanic = (StringBlockMechanic)mechanic;
                        }

                        customBlockMechanic = stringBlockMechanic;
                     } else {
                        customBlockMechanic = mechanic;
                     }

                     CustomBlockMechanic mechanic_1 = customBlockMechanic;
                     CustomBlockType byClass = CustomBlockRegistry.INSTANCE.getByClass(mechanic_1.getClass());
                     if (byClass != null) {
                        Player player3 = playerInteractEvent.getPlayer();
                        BlockFace blockFace2 = playerInteractEvent.getBlockFace();
                        byClass.placeCustomBlock(player3, hand, item, mechanic_1, placedAgainst, blockFace2);
                     }

                     ItemUtils itemUtils = ItemUtils.INSTANCE;
                     Player player4 = playerInteractEvent.getPlayer();
                     itemUtils.triggerCooldown(player4, item);
                     return;
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
   public final void onBreakingCustomBlock(@NotNull BlockBreakEvent blockBreakEvent) {
      if (NexoBlocks.isCustomBlock(blockBreakEvent.getBlock())) {
         blockBreakEvent.setDropItems(false);
         Location location = blockBreakEvent.getBlock().getLocation();
         if (!NexoBlocks.remove(location, blockBreakEvent.getPlayer(), (Drop)null)) {
            blockBreakEvent.setCancelled(true);
         }

         Block relative = blockBreakEvent.getBlock().getRelative(BlockFace.UP);
         Block blockAbove = relative;
         if (!blockBreakEvent.isCancelled() && NexoBlocks.isNexoStringBlock(blockAbove)) {
            Location location2 = blockAbove.getLocation();
            NexoBlocks.remove(location2, blockBreakEvent.getPlayer(), (Drop)null);
         }
      }
   }

   @EventHandler
   public final void onEntityExplosion(@NotNull EntityExplodeEvent entityExplodeEvent) {
      boolean flag;
      List blockList = entityExplodeEvent.blockList();
      Iterable iterable = (Iterable)blockList;
      NexoBlocks nexoBlocks = NexoBlocks.INSTANCE;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         Block p0 = (Block)element;
         int value = 0;
         CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(p0);
         destination.put(element, customBlockMechanic);
      }

      Map customBlocks;
      block: {
         customBlocks = CollectionUtilsKt.filterValuesNotNull(destination);
         if (!StringsKt.contains((CharSequence)entityExplodeEvent.getEntity().getType().name(), (CharSequence)"WIND_CHARGE", false)) {
            PotionEffectType potionEffectType = PotionEffectTypeWrapper.WIND_CHARGED;
            boolean flag2;
            if (potionEffectType != null) {
               PotionEffectType it = potionEffectType;
               Entity entity = entityExplodeEvent.getEntity();
               flag2 = Intrinsics.areEqual((entity instanceof LivingEntity ? (LivingEntity)entity : null) != null ? (entity instanceof LivingEntity ? (LivingEntity)entity : null).hasPotionEffect(it) : null, true);
            } else {
               flag2 = false;
            }

            if (!flag2) {
               flag = false;
               break block;
            }
         }

         flag = true;
      }

      boolean windCharged = flag;

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)customBlocks.entrySet()) {
         int value2 = 0;
         Block block = (Block)entry.getKey();
         CustomBlockMechanic mechanic = (CustomBlockMechanic)entry.getValue();
         if (!mechanic.isBlastResistant() && !windCharged) {
            block.setType(Material.AIR);
         }

         Drop explosionDrops = mechanic.getBreakable().getDrop().getExplosionDrops();
         Location location = block.getLocation();
         ItemStack itemStack = ItemStack.of(Material.AIR);
         explosionDrops.spawns(location, itemStack);
      }

      entityExplodeEvent.blockList().removeAll((Collection)customBlocks.keySet());
   }

   @EventHandler
   public final void onBlockExplosion(@NotNull BlockExplodeEvent blockExplodeEvent) {
      List blockList = blockExplodeEvent.blockList();
      Iterable iterable = (Iterable)blockList;
      NexoBlocks nexoBlocks = NexoBlocks.INSTANCE;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         Block p0 = (Block)element;
         int value = 0;
         CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(p0);
         destination.put(element, customBlockMechanic);
      }

      Map customBlocks = CollectionUtilsKt.filterValuesNotNull(destination);
      boolean windCharged = blockExplodeEvent.getExplosionResult() == ExplosionResult.TRIGGER_BLOCK;

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)customBlocks.entrySet()) {
         int value2 = 0;
         Block block = (Block)entry.getKey();
         CustomBlockMechanic mechanic = (CustomBlockMechanic)entry.getValue();
         if (!mechanic.isBlastResistant() && !windCharged) {
            block.setType(Material.AIR);
         }

         Drop explosionDrops = mechanic.getBreakable().getDrop().getExplosionDrops();
         Location location = block.getLocation();
         ItemStack itemStack = ItemStack.of(Material.AIR);
         explosionDrops.spawns(location, itemStack);
      }

      blockExplodeEvent.blockList().removeAll((Collection)customBlocks.keySet());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPiston(@NotNull BlockPistonRetractEvent blockPistonRetractEvent) {
      Iterable blocks = (Iterable)blockPistonRetractEvent.getBlocks();
      boolean flag;
      if (blocks instanceof Collection && ((Collection)blocks).isEmpty()) {
         flag = false;
      } else {
         block: {
            for(Object element : blocks) {
               Block it = (Block)element;
               int value = 0;
               CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(it);
               if (customBlockMechanic != null ? customBlockMechanic.getImmovable() : false) {
                  flag = true;
                  break block;
               }
            }

            flag = false;
         }
      }

      if (flag) {
         blockPistonRetractEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onPiston(@NotNull BlockPistonExtendEvent blockPistonExtendEvent) {
      Iterable blocks = (Iterable)blockPistonExtendEvent.getBlocks();
      boolean flag;
      if (blocks instanceof Collection && ((Collection)blocks).isEmpty()) {
         flag = false;
      } else {
         block: {
            for(Object element : blocks) {
               Block it = (Block)element;
               int value = 0;
               CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(it);
               if (customBlockMechanic != null ? customBlockMechanic.getImmovable() : false) {
                  flag = true;
                  break block;
               }
            }

            flag = false;
         }
      }

      if (flag) {
         blockPistonExtendEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public final void onPlacingVanilla(@NotNull BlockPlaceEvent blockPlaceEvent) {
      if (blockPlaceEvent.getBlockPlaced().getType() == Material.TRIPWIRE || blockPlaceEvent.getBlockPlaced().getType() == Material.NOTE_BLOCK) {
         if (blockPlaceEvent.getBlockPlaced().getType() == Material.TRIPWIRE) {
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

            MechanicFactory mechanicFactory;
            block: {
               for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
                  int value = 0;
                  mechanicFactory = it;
                  if (!(it instanceof StringBlockMechanicFactory)) {
                     mechanicFactory = null;
                  }

                  it = (StringBlockMechanicFactory)mechanicFactory;
                  if (it != null) {
                     mechanicFactory = it;
                     break block;
                  }
               }

               mechanicFactory = null;
            }

            if (mechanicFactory == null) {
               return;
            }
         }

         if (blockPlaceEvent.getBlockPlaced().getType() == Material.NOTE_BLOCK) {
            MechanicsManager mechanicsManager2 = MechanicsManager.INSTANCE;

            MechanicFactory mechanicFactory2;
            block: {
               for(MechanicFactory mechanicFactory3 : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager2.getFACTORIES_BY_MECHANIC_ID().values()) {
                  int value2 = 0;
                  mechanicFactory2 = mechanicFactory3;
                  if (!(mechanicFactory3 instanceof NoteBlockMechanicFactory)) {
                     mechanicFactory2 = null;
                  }

                  mechanicFactory3 = (NoteBlockMechanicFactory)mechanicFactory2;
                  if (mechanicFactory3 != null) {
                     mechanicFactory2 = mechanicFactory3;
                     break block;
                  }
               }

               mechanicFactory2 = null;
            }

            if (mechanicFactory2 == null) {
               return;
            }
         }

         if (!NexoBlocks.isCustomBlock(blockPlaceEvent.getItemInHand())) {
            blockPlaceEvent.getBlockPlaced().setBlockData(blockPlaceEvent.getBlockPlaced().getType().createBlockData(), false);
         }
      }
   }

   @EventHandler
   public final void onMiddleClick(@NotNull PlayerPickItemEvent playerPickItemEvent) {
      org.bukkit.inventory.ItemStack itemStack;
      double value;
      block: {
         Attribute attribute = Attribute.BLOCK_INTERACTION_RANGE;
         if (attribute != null) {
            Attribute p0 = attribute;
            Player player = playerPickItemEvent.getPlayer();
            Player player2 = player;
            int it = 0;
            AttributeInstance attributeInstance = player2.getAttribute(p0);
            if (attributeInstance != null) {
               value = attributeInstance.getValue();
               break block;
            }
         }

         value = (double)6.0F;
      }

      double distance = value;
      RayTraceResult rayTraceResult = playerPickItemEvent.getPlayer().rayTraceBlocks(distance);
      if (rayTraceResult != null) {
         Block hitBlock = rayTraceResult.getHitBlock();
         if (hitBlock != null) {
            Block block = hitBlock;
            CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(block);
            if (customBlockMechanic == null) {
               Block relative = block.getRelative(BlockFace.DOWN);
               StringBlockMechanic stringBlockMechanic = NexoBlocks.stringMechanic(relative);
               if (stringBlockMechanic != null) {
                  StringBlockMechanic invItem = stringBlockMechanic;
                  int p0 = 0;
                  stringBlockMechanic = invItem.getTall() ? invItem : null;
               } else {
                  stringBlockMechanic = null;
               }

               if (stringBlockMechanic == null) {
                  return;
               }

               customBlockMechanic = stringBlockMechanic;
            }

            block: {
               CustomBlockMechanic mechanic = customBlockMechanic;
               NoteBlockMechanic noteMechanic = mechanic instanceof NoteBlockMechanic ? (NoteBlockMechanic)mechanic : null;
               if (noteMechanic != null) {
                  DirectionalBlock directional = noteMechanic.getDirectional();
                  if (directional != null) {
                     String parentBlock = directional.getParentBlock();
                     if (parentBlock != null) {
                        String p0 = parentBlock;
                        NexoItems nexoItems = NexoItems.INSTANCE;
                        int value2 = 0;
                        ItemBuilder itemBuilder = NexoItems.itemFromId(p0);
                        if (itemBuilder != null) {
                           itemStack = itemBuilder.build();
                           if (itemStack != null) {
                              break block;
                           }
                        }
                     }
                  }
               }

               ItemBuilder itemBuilder2 = NexoItems.itemFromId(mechanic.getItemID());
               itemStack = itemBuilder2.build();
            }

            ItemStack item = itemStack;
            String itemId = NexoItems.idFromItem(item);

            for(int i = 0; i < 9; ++i) {
               ItemStack itemStack2 = playerPickItemEvent.getPlayer().getInventory().getItem(i);
               if (itemStack2 != null) {
                  ItemStack invItem = itemStack2;
                  if (Intrinsics.areEqual(NexoItems.idFromItem(invItem), itemId)) {
                     playerPickItemEvent.getPlayer().getInventory().setHeldItemSlot(i);
                     playerPickItemEvent.setCancelled(true);
                     return;
                  }
               }
            }

            playerPickItemEvent.setCancelled(true);
            if (playerPickItemEvent.getPlayer().getGameMode() == GameMode.CREATIVE) {
               playerPickItemEvent.getPlayer().getInventory().setItemInMainHand(item);
            }

            return;
         }
      }

   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.mechanics.storage.StorageType.values().length];
         try { a_0[com.nexomc.nexo.mechanics.storage.StorageType.STORAGE.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.mechanics.storage.StorageType.SHULKER.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.mechanics.storage.StorageType.PERSONAL.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.mechanics.storage.StorageType.DISPOSAL.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.mechanics.storage.StorageType.ENDERCHEST.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
