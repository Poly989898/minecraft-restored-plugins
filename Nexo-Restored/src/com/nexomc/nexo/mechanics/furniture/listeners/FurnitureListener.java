package com.nexomc.nexo.mechanics.furniture.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.placements.FurniturePlacement;
import com.nexomc.nexo.mechanics.furniture.placements.PlacementType;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.mechanics.furniture.states.FurnitureStates;
import com.nexomc.nexo.mechanics.limitedplacing.LimitedPlacing;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.mechanics.storage.StorageType;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.FurnitureHelpers;
import com.nexomc.nexo.utils.ItemUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\nJ&\u0010\u000b\u001a\u00020\u0005*\u00020\fH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\r\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u000f\u001a\u00020\u0005*\u00020\fH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010\u0012\b\b\u000e\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u0005*\u00020\fH\u0007b\u0002\b\u0007J\u001c\u0010\u0012\u001a\u00020\u0005*\u00020\u0013H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\u0010J\u0010\u0010\u0014\u001a\u00020\u0005*\u00020\u0015H\u0007b\u0002\b\u0007J\u001c\u0010\u0016\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\rJ\u001c\u0010\u0016\u001a\u00020\u0005*\u00020\u0017H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\r¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurnitureListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onAdd", "", "Lcom/destroystokyo/paper/event/entity/EntityAddToWorldEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "onLimitedPlacing", "Lorg/bukkit/event/player/PlayerInteractEvent;", "LOW", "ignoreCancelled", "onFurniturePlace", "HIGHEST", "onPlaceAgainstFurniture", "onFurnitureInteract", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "onPlayerQuitEvent", "Lorg/bukkit/event/player/PlayerQuitEvent;", "onInvalidFurniture", "Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "Companion", "com.nexomc:core"}
)
public final class FurnitureListener implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void onAdd(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      Entity entity = entityAddToWorldEvent.getEntity();
      ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
      if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
         NexoFurniture.convertFurniture(itemDisplay);
      }
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public final void onLimitedPlacing(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block block2 = clickedBlock;
         int value = 0;
         clickedBlock = playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK ? block2 : null;
         if (clickedBlock != null) {
            ItemStack item = playerInteractEvent.getItem();
            if (item != null) {
               ItemStack itemStack = item;
               NexoItems nexoItems = NexoItems.INSTANCE;
               Block block3 = clickedBlock;
               int value2 = 0;
               String str = NexoItems.idFromItem(itemStack);
               if (str != null) {
                  Pair pair = TuplesKt.to(block3, str);
                  Block block = (Block)pair.component1();
                  String itemId = (String)pair.component2();
                  FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(itemId);
                  if (furnitureMechanic != null) {
                     LimitedPlacing limitedPlacing2 = furnitureMechanic.getLimitedPlacing();
                     if (limitedPlacing2 != null) {
                        LimitedPlacing limitedPlacing = limitedPlacing2;
                        Block relative = block.getRelative(playerInteractEvent.getBlockFace()).getRelative(BlockFace.DOWN);
                        Block belowPlaced = relative;
                        if (!playerInteractEvent.getPlayer().isSneaking() && BlockHelpers.isInteractable(playerInteractEvent.getClickedBlock(), playerInteractEvent.getPlayer())) {
                           return;
                        }

                        if (block.isEmpty() && !IFurniturePacketManager.Companion.blockIsHitbox(block, (UUID)null, true)) {
                           playerInteractEvent.setCancelled(true);
                        } else {
                           if (!block.isEmpty()) {
                              BlockFace blockFace = playerInteractEvent.getBlockFace();
                              if (limitedPlacing.isNotPlacableOn(block, blockFace)) {
                                 playerInteractEvent.setCancelled(true);
                                 return;
                              }
                           }

                           if (limitedPlacing.getType() == LimitedPlacing.LimitedPlacingType.ALLOW) {
                              if (!limitedPlacing.checkLimited(belowPlaced)) {
                                 playerInteractEvent.setCancelled(true);
                              } else if (!((Collection)limitedPlacing.getWorlds()).isEmpty() && !limitedPlacing.getWorlds().contains(playerInteractEvent.getPlayer().getWorld())) {
                                 playerInteractEvent.setCancelled(true);
                              }
                           } else if (limitedPlacing.getType() == LimitedPlacing.LimitedPlacingType.DENY) {
                              if (limitedPlacing.checkLimited(belowPlaced)) {
                                 playerInteractEvent.setCancelled(true);
                              } else if (!((Collection)limitedPlacing.getWorlds()).isEmpty() && limitedPlacing.getWorlds().contains(playerInteractEvent.getPlayer().getWorld())) {
                                 playerInteractEvent.setCancelled(true);
                              }
                           } else {
                              LimitedPlacing.RadiusLimitation radiusLimitation = limitedPlacing.getRadiusLimitation();
                              if (radiusLimitation == null) {
                                 return;
                              }

                              LimitedPlacing.RadiusLimitation it = radiusLimitation;
                              int value3 = 0;
                              Pair pair2 = TuplesKt.to((double)it.getRadius(), (double)it.getAmount());
                              if (pair2 == null) {
                                 return;
                              }

                              Pair p0 = pair2;
                              double radius = ((Number)p0.component1()).doubleValue();
                              double amount = ((Number)p0.component2()).doubleValue();
                              Collection nearbyEntitiesByType = block.getWorld().getNearbyEntitiesByType(ItemDisplay.class, block.getLocation(), radius);
                              Iterable iterable = (Iterable)nearbyEntitiesByType;
                              Collection destination = (Collection)(new ArrayList());

                              for(Object element : iterable) {
                                 ItemDisplay it_11 = (ItemDisplay)element;
                                 int value4 = 0;
                                 FurnitureMechanic furnitureMechanic2 = NexoFurniture.furnitureMechanic((Entity)it_11);
                                 if (Intrinsics.areEqual(furnitureMechanic2 != null ? furnitureMechanic2.getItemID() : null, itemId)) {
                                    destination.add(element);
                                 }
                              }

                              iterable = (Iterable)((List)destination);
                              int value5;
                              if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                                 value5 = 0;
                              } else {
                                 int count = 0;

                                 for(Object obj : iterable) {
                                    ItemDisplay it_10 = (ItemDisplay)obj;
                                    int value6 = 0;
                                    if (it_10.getLocation().distanceSquared(block.getLocation()) <= radius * radius) {
                                       ++count;
                                       if (count < 0) {
                                          CollectionsKt.throwCountOverflow();
                                       }
                                    }
                                 }

                                 value5 = count;
                              }

                              if ((double)value5 >= amount) {
                                 playerInteractEvent.setCancelled(true);
                              }
                           }
                        }

                        return;
                     }
                  }

                  return;
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
   public final void onFurniturePlace(@NotNull PlayerInteractEvent playerInteractEvent) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      com.nexomc.nexo.mechanics.furniture.FurnitureMechanic furnitureMechanic;
      Block clickedBlock = playerInteractEvent.getClickedBlock();
      if (clickedBlock != null) {
         Block it = clickedBlock;
         int value = 0;
         if (BlockHelpers.isReplaceable(it, (UUID)null)) {
            clickedBlock = it;
         } else {
            clickedBlock = it.getRelative(playerInteractEvent.getBlockFace());
         }

         if (clickedBlock != null) {
            Block block = clickedBlock;
            ItemStack itemStack = playerInteractEvent.getItem();
            if (itemStack == null) {
               return;
            }

            EquipmentSlot equipmentSlot = playerInteractEvent.getHand();
            if (equipmentSlot != null) {
               EquipmentSlot mechanic = equipmentSlot;
               ItemStack itemStack2 = itemStack;
               int value2 = 0;
               boolean flag = mechanic == EquipmentSlot.HAND;
               equipmentSlot = flag ? mechanic : null;
               if ((flag ? mechanic : null) != null) {
                  Pair pair = TuplesKt.to(itemStack2, equipmentSlot);
                  ItemStack item = (ItemStack)pair.component1();
                  EquipmentSlot hand = (EquipmentSlot)pair.component2();
                  MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
                  Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

                  while(true) {
                     if (iterator.hasNext()) {
                        MechanicFactory mechanicFactory3 = (MechanicFactory)iterator.next();
                        int value3 = 0;
                        MechanicFactory mechanicFactory2 = mechanicFactory3;
                        if (!(mechanicFactory3 instanceof FurnitureFactory)) {
                           mechanicFactory2 = null;
                        }

                        mechanicFactory3 = (FurnitureFactory)mechanicFactory2;
                        if (mechanicFactory3 == null) {
                           continue;
                        }

                        mechanicFactory = mechanicFactory3;
                        break;
                     }

                     mechanicFactory = null;
                     break;
                  }

                  FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
                  if (furnitureFactory != null) {
                     FurnitureMechanic furnitureMechanic2 = furnitureFactory.getMechanic(item);
                     if (furnitureMechanic2 != null) {
                        FurnitureMechanic mechanic_9 = furnitureMechanic2;
                        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && (playerInteractEvent.useInteractedBlock() != Result.DENY || playerInteractEvent.getPlayer().isSneaking()) && playerInteractEvent.useItemInHand() != Result.DENY) {
                           if (NexoFurniture.isFurniture(item) && !BlockHelpers.isStandingInside(playerInteractEvent.getPlayer(), block)) {
                              Player player = playerInteractEvent.getPlayer();
                              Location location = block.getLocation();
                              if (!ProtectionLib.canBuild(player, location)) {
                                 return;
                              }

                              if (mechanic_9.getFarmlandRequired() && block.getRelative(BlockFace.DOWN).getType() != Material.FARMLAND) {
                                 return;
                              }

                              if (!playerInteractEvent.getPlayer().isSneaking() && BlockHelpers.isInteractable(playerInteractEvent.getClickedBlock(), playerInteractEvent.getPlayer())) {
                                 return;
                              }

                              BlockPlaceEvent blockPlaceEvent;
                              float yaw;
                              block: {
                                 BlockState state = block.getState();
                                 Block block2 = playerInteractEvent.getClickedBlock();
                                 blockPlaceEvent = new BlockPlaceEvent(block, state, block2, item, playerInteractEvent.getPlayer(), true, hand);
                                 Rotation rotation = Companion.getRotation((double)playerInteractEvent.getPlayer().getEyeLocation().getYaw(), mechanic_9);
                                 yaw = FurnitureHelpers.INSTANCE.correctedYaw(mechanic_9, FurnitureHelpers.rotationToYaw(rotation));
                                 FurniturePlacement placements = mechanic_9.getPlacements();
                                 if (placements != null) {
                                    PlacementType.Companion companion = PlacementType.Companion;
                                    BlockFace blockFace = playerInteractEvent.getBlockFace();
                                    furnitureMechanic = placements.mechanicFor(companion.fromBlockFace(blockFace));
                                    if (furnitureMechanic != null) {
                                       break block;
                                    }
                                 }

                                 furnitureMechanic = mechanic_9;
                              }

                              FurnitureMechanic resolvedMechanic = furnitureMechanic;
                              if (playerInteractEvent.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                                 blockPlaceEvent.setCancelled(true);
                              }

                              location = block.getLocation();
                              if (resolvedMechanic.notEnoughSpace(location, yaw)) {
                                 blockPlaceEvent.setCancelled(true);
                                 Message.NOT_ENOUGH_SPACE.send((CommandSender)playerInteractEvent.getPlayer());
                              }

                              if (blockPlaceEvent.canBuild() && EventUtils.INSTANCE.call((Event)blockPlaceEvent)) {
                                 location = block.getLocation();
                                 BlockFace blockFace2 = playerInteractEvent.getBlockFace();
                                 ItemDisplay itemDisplay = mechanic_9.place(location, yaw, blockFace2, false);
                                 if (itemDisplay == null) {
                                    return;
                                 }

                                 ItemDisplay baseEntity = itemDisplay;
                                 PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
                                 PersistentDataContainer pdc = persistentDataContainer;
                                 Colorable colorable = ColorableKt.asColorable(item);
                                 if (colorable != null) {
                                    Color color = colorable.getColor();
                                    if (color != null) {
                                       Integer asRGB = color.asRGB();
                                       int it_8 = ((Number)asRGB).intValue();
                                       int value4 = 0;
                                       pdc.set(FurnitureMechanic.Companion.getFURNITURE_DYE_KEY(), PersistentDataType.INTEGER, it_8);
                                    }
                                 }

                                 Component data = (Component)item.getData(DataComponentTypes.CUSTOM_NAME);
                                 if (data != null) {
                                    String str = AdventureUtilsKt.serialize(data);
                                    if (str != null) {
                                       String it_7 = str;
                                       int value5 = 0;
                                       pdc.set(FurnitureMechanic.Companion.getDISPLAY_NAME_KEY(), PersistentDataType.STRING, it_7);
                                    }
                                 }

                                 StorageMechanic storage = mechanic_9.getStorage();
                                 StorageType storageType = storage != null ? storage.getStorageType() : null;
                                 switch (storageType == null ? -1 : FurnitureListener.WhenMappings.$EnumSwitchMapping$0[storageType.ordinal()]) {
                                    case 1:
                                    case 2:
                                       ItemStack[] items = (ItemStack[])item.getPersistentDataContainer().getOrDefault(StorageMechanic.Companion.getSTORAGE_KEY(), DataType.ITEM_STACK_ARRAY, new ItemStack[0]);
                                       pdc.set(StorageMechanic.Companion.getSTORAGE_KEY(), DataType.ITEM_STACK_ARRAY, items);
                                    default:
                                       EventUtils eventUtils = EventUtils.INSTANCE;
                                       Player player2 = playerInteractEvent.getPlayer();
                                       if (!eventUtils.call(new NexoFurniturePlaceEvent(mechanic_9, block, baseEntity, player2, item, hand))) {
                                          NexoFurniture.remove((Entity)((Entity)baseEntity), (Player)null, (Drop)null);
                                          return;
                                       }

                                       playerInteractEvent.getPlayer().swingHand(hand);
                                       if (!mechanic_9.getWaterloggable() || block.getType() != Material.WATER) {
                                          block.setType(Material.AIR);
                                       }

                                       if (playerInteractEvent.getPlayer().getGameMode() != GameMode.CREATIVE) {
                                          item.subtract();
                                       }

                                       playerInteractEvent.setUseInteractedBlock(Result.DENY);
                                       playerInteractEvent.setUseItemInHand(Result.DENY);
                                       baseEntity.getWorld().sendGameEvent((Entity)playerInteractEvent.getPlayer(), GameEvent.BLOCK_PLACE, baseEntity.getLocation().toVector());
                                       ItemUtils itemUtils = ItemUtils.INSTANCE;
                                       Player player3 = playerInteractEvent.getPlayer();
                                       itemUtils.triggerCooldown(player3, item);
                                       return;
                                 }
                              }

                              return;
                           }

                           return;
                        }

                        return;
                     }
                  }

                  return;
               }
            }

            return;
         }
      }

   }

   @EventHandler
   public final void onPlaceAgainstFurniture(@NotNull PlayerInteractEvent playerInteractEvent) {
      Block block2;
      block: {
         block2 = playerInteractEvent.getClickedBlock();
         if (block2 != null) {
            Block it = block2;
            int value = 0;
            block2 = it.getType().isAir() && playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK ? it : null;
            if (block2 != null) {
               break block;
            }
         }

         block2: {
            Location interactionPoint = playerInteractEvent.getInteractionPoint();
            if (interactionPoint != null) {
               Block block3 = interactionPoint.getBlock();
               if (block3 != null) {
                  block2 = block3.getRelative(playerInteractEvent.getBlockFace().getOppositeFace());
                  break block2;
               }
            }

            block2 = null;
         }

         if (block2 == null) {
            return;
         }
      }

      Block block = block2;
      ItemStack item = playerInteractEvent.getItem();
      if (item != null) {
         ItemStack it = item;
         Player player = playerInteractEvent.getPlayer();
         Player player2 = player;
         int value2 = 0;
         ItemStack itemStack2 = !player2.hasCooldown(it) ? it : null;
         if (itemStack2 != null) {
            ItemStack itemStack = itemStack2;
            Material type = itemStack.getType();
            boolean isPainting;
            Triple triple = com.nexomc.nexo.utils.TuplesKt.to(TuplesKt.to(StringsKt.contains((CharSequence)type.name(), (CharSequence)"ITEM_FRAME", false), StringsKt.contains((CharSequence)type.name(), (CharSequence)"ARMOR_STAND", false)), StringsKt.contains((CharSequence)type.name(), (CharSequence)"PAINTING", false));
            boolean isItemFrame = (Boolean)triple.component1();
            boolean isArmorStand = (Boolean)triple.component2();
            isPainting = (Boolean)triple.component3();
            if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.useItemInHand() != Result.DENY) {
               if (!block.isEmpty() && block.getType() != Material.BARRIER) {
                  return;
               }

               if (!itemStack.getType().isBlock() && !isItemFrame && !isArmorStand && !isPainting) {
                  return;
               }

               IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
               Location location = block.getLocation();
               ItemDisplay itemDisplay = companion.baseEntityFromHitbox(location);
               if (itemDisplay == null) {
                  return;
               }

               ItemDisplay baseEntity = itemDisplay;
               if (NexoFurniture.furnitureMechanic((Entity)baseEntity) == null) {
                  return;
               }

               block.setType(Material.BARRIER, false);
               SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
               location = block.getLocation();
               schedulerUtils.runAtLocationLater(location, 1L, (a0_ -> FurnitureListener.onPlaceAgainstFurniture$lambda$2(block, (Location)a0_)));
               return;
            }

            return;
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onFurnitureInteract(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      Event.Result allowUseItem = nexoFurnitureInteractEvent.getUseItemInHand();
      nexoFurnitureInteractEvent.setUseItemInHand(Result.DENY);
      if (nexoFurnitureInteractEvent.getHand() == EquipmentSlot.HAND) {
         if (nexoFurnitureInteractEvent.getCanRunAction() != Result.DENY) {
            nexoFurnitureInteractEvent.getMechanic().runClickActions(nexoFurnitureInteractEvent.getPlayer());
         }

         if (nexoFurnitureInteractEvent.getCanToggleLight() != Result.DENY) {
            NexoFurniture.toggleLight(nexoFurnitureInteractEvent.getBaseEntity(), (Boolean)null, nexoFurnitureInteractEvent.getMechanic());
         }

         if (nexoFurnitureInteractEvent.getCanRotate() != Result.DENY) {
            nexoFurnitureInteractEvent.getMechanic().rotateFurniture(nexoFurnitureInteractEvent.getBaseEntity());
         } else if (nexoFurnitureInteractEvent.getCanSit() != Result.DENY) {
            FurnitureSeat.Companion.sitOnSeat(nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getMechanic(), nexoFurnitureInteractEvent.getPlayer(), nexoFurnitureInteractEvent.getInteractionPoint());
         } else if (nexoFurnitureInteractEvent.getCanSleep() != Result.DENY) {
            FurnitureBed.Companion.sleepOnBed(nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getMechanic(), nexoFurnitureInteractEvent.getPlayer(), nexoFurnitureInteractEvent.getInteractionPoint());
         } else if (nexoFurnitureInteractEvent.getCanOpenStorage() != Result.DENY && nexoFurnitureInteractEvent.getMechanic().getStorage() != null) {
            switch (FurnitureListener.WhenMappings.$EnumSwitchMapping$0[nexoFurnitureInteractEvent.getMechanic().getStorage().getStorageType().ordinal()]) {
               case 1:
               case 2:
                  nexoFurnitureInteractEvent.getMechanic().getStorage().openStorage(nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getPlayer());
                  break;
               case 3:
                  StorageMechanic storage = nexoFurnitureInteractEvent.getMechanic().getStorage();
                  Player player = nexoFurnitureInteractEvent.getPlayer();
                  Location location = nexoFurnitureInteractEvent.getBaseEntity().getLocation();
                  storage.openPersonalStorage(player, location, nexoFurnitureInteractEvent.getBaseEntity());
                  break;
               case 4:
                  StorageMechanic storageMechanic = nexoFurnitureInteractEvent.getMechanic().getStorage();
                  Player player2 = nexoFurnitureInteractEvent.getPlayer();
                  Location location2 = nexoFurnitureInteractEvent.getBaseEntity().getLocation();
                  storageMechanic.openDisposal(player2, location2, nexoFurnitureInteractEvent.getBaseEntity());
                  break;
               case 5:
                  nexoFurnitureInteractEvent.getPlayer().openInventory(nexoFurnitureInteractEvent.getPlayer().getEnderChest());
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }

            if (nexoFurnitureInteractEvent.getCanCycleState() != Result.DENY) {
               FurnitureStates.Companion.cycleState(nexoFurnitureInteractEvent.getPlayer(), nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getMechanic());
            }
         } else if (nexoFurnitureInteractEvent.getCanCycleState() != Result.DENY) {
            FurnitureStates.Companion.cycleState(nexoFurnitureInteractEvent.getPlayer(), nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getMechanic());
         } else if (nexoFurnitureInteractEvent.getCanRunAction() == Result.DENY && nexoFurnitureInteractEvent.getCanToggleLight() == Result.DENY) {
            nexoFurnitureInteractEvent.setUseItemInHand(allowUseItem);
         }
      }

      nexoFurnitureInteractEvent.getPlayer().updateInventory();
   }

   @EventHandler
   public final void onPlayerQuitEvent(@NotNull PlayerQuitEvent playerQuitEvent) {
      if (NexoFurniture.isFurniture(playerQuitEvent.getPlayer().getVehicle())) {
         playerQuitEvent.getPlayer().leaveVehicle();
      }

   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void onInvalidFurniture(@NotNull EntityAddToWorldEvent entityAddToWorldEvent) {
      if (!VersionUtil.INSTANCE.isFoliaServer()) {
         PersistentDataContainer persistentDataContainer = entityAddToWorldEvent.getEntity().getPersistentDataContainer();
         PersistentDataContainer pdc = persistentDataContainer;
         Entity entity = entityAddToWorldEvent.getEntity();
         ItemDisplay itemDisplay = entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
         if ((entity instanceof ItemDisplay ? (ItemDisplay)entity : null) != null) {
            ItemDisplay itemDisplay2 = itemDisplay;
            int value = 0;
            itemDisplay = pdc.has(FurnitureMechanic.Companion.getFURNITURE_KEY()) ? itemDisplay2 : null;
            if (itemDisplay != null) {
               ItemDisplay baseEntity = itemDisplay;
               if (NexoFurniture.isFurniture((Entity)baseEntity)) {
                  pdc.remove(FurnitureMechanic.Companion.getINVALID_FURNITURE_KEY());
                  return;
               }

               if (Settings.REMOVE_INVALID_FURNITURE.toBool()) {
                  entityAddToWorldEvent.getEntity().remove();
               } else {
                  pdc.set(FurnitureMechanic.Companion.getINVALID_FURNITURE_KEY(), PersistentDataType.BOOLEAN, true);
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public final void onInvalidFurniture(@NotNull NexoItemsLoadedEvent nexoItemsLoadedEvent) {
      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (a0_ -> FurnitureListener.onInvalidFurniture$lambda$1((ItemDisplay)a0_));
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value2 = 0;

            for(int i = objs.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               Chunk chunk = (Chunk)obj;
               int value3 = 0;
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new FurnitureListener$onInvalidFurniture$$inlined$runAtWorldEntities$1(chunk, task));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity p0 = (Entity)obj2;
               int value4 = 0;
               task.invoke(p0);
            }
         }
      }

   }

   private static final Unit onPlaceAgainstFurniture$lambda$2(Block block, Location it) {
      block.setType(Material.AIR, false);
      return Unit.INSTANCE;
   }

   private static final Unit onInvalidFurniture$lambda$1(ItemDisplay baseEntity) {
      PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
      PersistentDataContainer pdc = persistentDataContainer;
      if (!pdc.has(FurnitureMechanic.Companion.getFURNITURE_KEY())) {
         return Unit.INSTANCE;
      } else if (NexoFurniture.isFurniture((Entity)baseEntity)) {
         pdc.remove(FurnitureMechanic.Companion.getINVALID_FURNITURE_KEY());
         return Unit.INSTANCE;
      } else {
         if (Settings.REMOVE_INVALID_FURNITURE.toBool()) {
            baseEntity.remove();
         } else {
            pdc.set(FurnitureMechanic.Companion.getINVALID_FURNITURE_KEY(), PersistentDataType.BOOLEAN, true);
         }

         return Unit.INSTANCE;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/listeners/FurnitureListener$Companion;", "", "<init>", "()V", "getRotation", "Lorg/bukkit/Rotation;", "yaw", "", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final Rotation getRotation(double yaw, FurnitureMechanic mechanic) {
         FurnitureMechanic.RestrictedRotation restrictedRotation = mechanic.getRestrictedRotation();
         int id = (int)((double)((Location.normalizeYaw((float)yaw) + (float)180) * (float)8 / (float)360) + (double)0.5F) % 8;
         int offset = restrictedRotation == FurnitureMechanic.RestrictedRotation.STRICT ? 0 : 1;
         if (restrictedRotation != FurnitureMechanic.RestrictedRotation.NONE && id % 2 != 0) {
            id -= offset;
         }

         return Rotation.values()[id];
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
