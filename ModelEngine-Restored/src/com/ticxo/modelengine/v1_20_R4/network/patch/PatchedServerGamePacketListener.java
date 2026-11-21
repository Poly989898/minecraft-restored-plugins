package com.ticxo.modelengine.v1_20_R4.network.patch;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v1_20_R4.NMSMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket.Handler;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PatchedServerGamePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void handleInteract(final ServerboundInteractPacketWrapper interactPacket, ServerGamePacketListener listener) {
      if (listener instanceof ServerGamePacketListenerImpl connection) {
         PacketUtils.ensureRunningOnSameThread(interactPacket, connection, connection.getPlayer().serverLevel());
         final ServerPlayer player = connection.getPlayer();
         if (!player.isImmobile()) {
            final CraftPlayer craftPlayer = player.getBukkitEntity();
            final ServerLevel serverLevel = player.serverLevel();
            final CraftServer craftServer = serverLevel.getServer().server;
            final Entity entity = interactPacket.getTarget(serverLevel);
            interactPacket.dispatch(new Handler() {
               public void onInteraction(InteractionHand hand) {
                  if (interactPacket.isFakeInteraction()) {
                     EntityHandler entityHandler = ModelEngineAPI.getNMSHandler().getEntityHandler();
                     entityHandler.forceUseItem(craftPlayer, org.bukkit.inventory.EquipmentSlot.HAND);
                     entityHandler.forceUseItem(craftPlayer, org.bukkit.inventory.EquipmentSlot.OFF_HAND);
                  }
               }

               public void onInteraction(InteractionHand hand, Vec3 pos) {
               }

               public void onAttack() {
               }
            });
            if (entity != player || player.isSpectator()) {
               player.resetLastActionTime();
               player.setShiftKeyDown(interactPacket.isUsingSecondaryAction());
               final ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(interactPacket.getOriginalId());
               if (activeModel != null) {
                  final BaseEntity<?> base = activeModel.getModeledEntity().getBase();
                  interactPacket.dispatch(
                     new Handler() {
                        public void onInteraction(InteractionHand hand) {
                           craftServer.getPluginManager()
                              .callEvent(
                                 new BaseEntityInteractEvent(
                                    craftPlayer,
                                    base,
                                    activeModel,
                                    BaseEntityInteractEvent.Action.INTERACT,
                                    org.bukkit.inventory.EquipmentSlot.HAND,
                                    interactPacket.isUsingSecondaryAction(),
                                    craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HAND),
                                    null
                                 )
                              );
                           craftServer.getPluginManager()
                              .callEvent(
                                 new BaseEntityInteractEvent(
                                    craftPlayer,
                                    base,
                                    activeModel,
                                    BaseEntityInteractEvent.Action.INTERACT,
                                    org.bukkit.inventory.EquipmentSlot.OFF_HAND,
                                    interactPacket.isUsingSecondaryAction(),
                                    craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND),
                                    null
                                 )
                              );
                        }

                        public void onInteraction(InteractionHand hand, Vec3 pos) {
                           Vector position = new Vector(pos.x, pos.y, pos.z);
                           craftServer.getPluginManager()
                              .callEvent(
                                 new BaseEntityInteractEvent(
                                    craftPlayer,
                                    base,
                                    activeModel,
                                    BaseEntityInteractEvent.Action.INTERACT_ON,
                                    org.bukkit.inventory.EquipmentSlot.HAND,
                                    interactPacket.isUsingSecondaryAction(),
                                    craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HAND),
                                    position
                                 )
                              );
                           craftServer.getPluginManager()
                              .callEvent(
                                 new BaseEntityInteractEvent(
                                    craftPlayer,
                                    base,
                                    activeModel,
                                    BaseEntityInteractEvent.Action.INTERACT_ON,
                                    org.bukkit.inventory.EquipmentSlot.OFF_HAND,
                                    interactPacket.isUsingSecondaryAction(),
                                    craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND),
                                    position
                                 )
                              );
                        }

                        public void onAttack() {
                           craftServer.getPluginManager()
                              .callEvent(
                                 new BaseEntityInteractEvent(
                                    craftPlayer,
                                    base,
                                    activeModel,
                                    BaseEntityInteractEvent.Action.ATTACK,
                                    org.bukkit.inventory.EquipmentSlot.HAND,
                                    interactPacket.isUsingSecondaryAction(),
                                    craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HAND),
                                    null
                                 )
                              );
                        }
                     }
                  );
               }

               if (entity == null) {
                  if (ServerInfo.IS_PAPER) {
                     interactPacket.dispatch(
                        new Handler() {
                           public void onInteraction(InteractionHand hand) {
                              PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, hand, null);
                           }

                           public void onInteraction(InteractionHand hand, Vec3 pos) {
                              PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, hand, pos);
                           }

                           public void onAttack() {
                              PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(
                                 craftServer, craftPlayer, interactPacket, InteractionHand.MAIN_HAND, null
                              );
                           }
                        }
                     );
                  }
               } else if (serverLevel.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                  AABB boundingBox = entity.getBoundingBox();
                  if (!(boundingBox.distanceToSqr(player.getEyePosition()) >= player.entityInteractionRange() * player.entityInteractionRange())) {
                     interactPacket.dispatch(
                        new Handler() {
                           private void performInteraction(
                              InteractionHand enumhand, PatchedServerGamePacketListener.EntityInteraction entityInteraction, PlayerInteractEntityEvent event
                           ) {
                              ItemStack itemstack = player.getItemInHand(enumhand);
                              if (itemstack.isItemEnabled(serverLevel.enabledFeatures())) {
                                 ItemStack itemstack1 = itemstack.copy();
                                 ItemStack itemInHand = player.getItemInHand(enumhand);
                                 boolean triggerLeashUpdate = itemInHand.getItem() == Items.LEAD && entity instanceof Mob;
                                 Item origItem = player.getInventory().getSelected().getItem();
                                 craftServer.getPluginManager().callEvent(event);
                                 if (entity instanceof Bucketable
                                    && entity instanceof LivingEntity
                                    && origItem.asItem() == Items.WATER_BUCKET
                                    && (event.isCancelled() || player.getInventory().getSelected().getItem() != origItem)) {
                                    if (entity.tracker != null && player.getBukkitEntity().canSee(entity.getBukkitEntity())) {
                                       ServerEntity serverEntity = entity.tracker.serverEntity;
                                       List<Packet<? super ClientGamePacketListener>> list = new ArrayList<>();
                                       serverEntity.sendPairingData(player, list::add);
                                       player.connection.send(new ClientboundBundlePacket(list));
                                    }

                                    player.containerMenu.sendAllDataToRemote();
                                 }

                                 if (triggerLeashUpdate && (event.isCancelled() || player.getInventory().getSelected().getItem() != origItem)) {
                                    connection.send(new ClientboundSetEntityLinkPacket(entity, ((Mob)entity).getLeashHolder()));
                                 }

                                 if (event.isCancelled() || player.getInventory().getSelected().getItem() != origItem) {
                                    List<DataValue<?>> data = entity.getEntityData().packAll();
                                    if (data != null && player.getBukkitEntity().canSee(entity.getBukkitEntity())) {
                                       player.connection.send(new ClientboundSetEntityDataPacket(entity.getId(), data));
                                    }

                                    if (entity instanceof Allay allay) {
                                       List<Pair<EquipmentSlot, ItemStack>> equipment = Arrays.stream(EquipmentSlot.values())
                                          .map(slot -> Pair.of(slot, allay.getItemBySlot(slot)))
                                          .collect(Collectors.toList());
                                       connection.send(new ClientboundSetEquipmentPacket(entity.getId(), equipment));
                                       player.containerMenu.sendAllDataToRemote();
                                    }
                                 }

                                 if (event.isCancelled()) {
                                    player.containerMenu.sendAllDataToRemote();
                                    return;
                                 }

                                 InteractionResult enuminteractionresult = entityInteraction.run(player, entity, enumhand);
                                 if (!itemInHand.isEmpty() && itemInHand.getCount() <= -1) {
                                    player.containerMenu.sendAllDataToRemote();
                                 }

                                 if (enuminteractionresult.consumesAction()) {
                                    CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, itemstack1, entity);
                                    if (enuminteractionresult.shouldSwing()) {
                                       player.swing(enumhand, true);
                                    }
                                 }
                              }
                           }

                           public void onInteraction(@NotNull InteractionHand hand) {
                              this.performInteraction(
                                 hand,
                                 Player::interactOn,
                                 new PlayerInteractEntityEvent(
                                    connection.getCraftPlayer(),
                                    entity.getBukkitEntity(),
                                    hand == InteractionHand.OFF_HAND ? org.bukkit.inventory.EquipmentSlot.OFF_HAND : org.bukkit.inventory.EquipmentSlot.HAND
                                 )
                              );
                           }

                           public void onInteraction(@NotNull InteractionHand hand, @NotNull Vec3 pos) {
                              this.performInteraction(
                                 hand,
                                 (entityplayer, entity1, enumhand1) -> entity1.interactAt(entityplayer, pos, enumhand1),
                                 new PlayerInteractAtEntityEvent(
                                    connection.getCraftPlayer(),
                                    entity.getBukkitEntity(),
                                    new Vector(pos.x, pos.y, pos.z),
                                    hand == InteractionHand.OFF_HAND ? org.bukkit.inventory.EquipmentSlot.OFF_HAND : org.bukkit.inventory.EquipmentSlot.HAND
                                 )
                              );
                           }

                           public void onAttack() {
                              if (!(entity instanceof ItemEntity)
                                 && !(entity instanceof ExperienceOrb)
                                 && !(entity instanceof AbstractArrow)
                                 && (entity != player || player.isSpectator())) {
                                 ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
                                 if (itemstack.isItemEnabled(serverLevel.enabledFeatures())) {
                                    player.attack(entity);
                                    if (!itemstack.isEmpty() && itemstack.getCount() <= -1) {
                                       player.containerMenu.sendAllDataToRemote();
                                    }
                                 }
                              }
                           }
                        }
                     );
                  }
               }
            }
         }
      }
   }

   public static void handleUseItem(ServerboundUseItemPacket packet, ServerGamePacketListener listener, Consumer<InteractionResult> afterUse) {
      if (listener instanceof ServerGamePacketListenerImpl connection) {
         ServerPlayer player = connection.getPlayer();
         PacketUtils.ensureRunningOnSameThread(packet, connection, player.serverLevel());
         if (!player.isImmobile()) {
            boolean shouldLimit = Boolean.TRUE
               .equals(ReflectionUtils.call(connection, NMSMethods.SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit, packet.timestamp));
            if (shouldLimit) {
               connection.ackBlockChangesUpTo(packet.getSequence());
               ServerLevel worldserver = player.serverLevel();
               InteractionHand enumhand = packet.getHand();
               ItemStack itemstack = player.getItemInHand(enumhand);
               player.resetLastActionTime();
               if (!itemstack.isEmpty() && itemstack.isItemEnabled(worldserver.enabledFeatures())) {
                  float f1 = player.getXRot();
                  float f2 = player.getYRot();
                  double d0 = player.getX();
                  double d1 = player.getY() + player.getEyeHeight();
                  double d2 = player.getZ();
                  Vec3 vec3d = new Vec3(d0, d1, d2);
                  float f3 = Mth.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
                  float f4 = Mth.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
                  float f5 = -Mth.cos(-f1 * (float) (Math.PI / 180.0));
                  float f6 = Mth.sin(-f1 * (float) (Math.PI / 180.0));
                  float f7 = f4 * f5;
                  float f8 = f3 * f5;
                  double d3 = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE ? 5.0 : 4.5;
                  Vec3 vec3d1 = vec3d.add(f7 * d3, f6 * d3, f8 * d3);
                  HitResult movingobjectposition = player.level().clip(new ClipContext(vec3d, vec3d1, Block.OUTLINE, Fluid.NONE, player));
                  boolean cancelled;
                  if (movingobjectposition != null && movingobjectposition.getType() == Type.BLOCK) {
                     BlockHitResult movingobjectpositionblock = (BlockHitResult)movingobjectposition;
                     if (player.gameMode.firedInteract
                        && player.gameMode.interactPosition.equals(movingobjectpositionblock.getBlockPos())
                        && player.gameMode.interactHand == enumhand
                        && ItemStack.isSameItemSameComponents(player.gameMode.interactItemStack, itemstack)) {
                        cancelled = player.gameMode.interactResult;
                     } else {
                        PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(
                           player,
                           Action.RIGHT_CLICK_BLOCK,
                           movingobjectpositionblock.getBlockPos(),
                           movingobjectpositionblock.getDirection(),
                           itemstack,
                           true,
                           enumhand,
                           movingobjectpositionblock.getLocation()
                        );
                        cancelled = event.useItemInHand() == Result.DENY;
                     }

                     player.gameMode.firedInteract = false;
                  } else {
                     PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, itemstack, enumhand);
                     cancelled = event.useItemInHand() == Result.DENY;
                  }

                  if (cancelled) {
                     player.resyncUsingItem(player);
                     player.getBukkitEntity().updateInventory();
                     return;
                  }

                  itemstack = player.getItemInHand(enumhand);
                  if (itemstack.isEmpty()) {
                     return;
                  }

                  InteractionResult enuminteractionresult = player.gameMode.useItem(player, worldserver, itemstack, enumhand);
                  afterUse.accept(enuminteractionresult);
                  if (enuminteractionresult.shouldSwing()) {
                     player.swing(enumhand, true);
                  }
               }
            }
         }
      }
   }

   private static void callPlayerUseUnknownEntityEvent(
      CraftServer cserver, CraftPlayer craftPlayer, ServerboundInteractPacketWrapper packet, InteractionHand hand, @Nullable Vec3 vector
   ) {
      cserver.getPluginManager()
         .callEvent(
            new PlayerUseUnknownEntityEvent(
               craftPlayer,
               packet.getEntityId(),
               packet.isAttack(),
               hand == InteractionHand.MAIN_HAND ? org.bukkit.inventory.EquipmentSlot.HAND : org.bukkit.inventory.EquipmentSlot.OFF_HAND,
               vector != null ? new Vector(vector.x, vector.y, vector.z) : null
            )
         );
   }

   @FunctionalInterface
   private interface EntityInteraction {
      InteractionResult run(ServerPlayer serverPlayer, Entity entity, InteractionHand interactionHand);
   }
}
