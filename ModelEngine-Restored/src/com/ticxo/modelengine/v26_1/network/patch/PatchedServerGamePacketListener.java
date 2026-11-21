package com.ticxo.modelengine.v26_1.network.patch;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v26_1.NMSMethods;
import io.papermc.paper.configuration.GlobalConfiguration;
import io.papermc.paper.util.MCUtil;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.SwingSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.slf4j.Logger;

public class PatchedServerGamePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void handleAttack(ServerboundAttackPacketWrapper attackPacket, ServerGamePacketListener listener) {
      if (listener instanceof ServerGamePacketListenerImpl connection) {
         PacketUtils.ensureRunningOnSameThread(attackPacket, connection, connection.getPlayer().level());
         ServerPlayer player = connection.getPlayer();
         if (!player.isImmobile()) {
            if (connection.hasClientLoaded()) {
               CraftPlayer craftPlayer = player.getBukkitEntity();
               ServerLevel level = player.level();
               CraftServer craftServer = level.getServer().server;
               Entity target = level.getEntityOrPart(attackPacket.getRelayedId());
               if (target != player || player.isSpectator()) {
                  player.resetLastActionTime();
                  callBaseEntityEvents(
                     attackPacket,
                     (base, model, hitBone) -> craftServer.getPluginManager()
                        .callEvent(
                           new BaseEntityInteractEvent(
                              craftPlayer,
                              base,
                              model,
                              BaseEntityInteractEvent.Action.ATTACK,
                              EquipmentSlot.HAND,
                              player.isShiftKeyDown(),
                              craftPlayer.getInventory().getItem(EquipmentSlot.HAND),
                              null,
                              hitBone
                           )
                        )
                  );
                  if (target != null && level.getWorldBorder().isWithinBounds(target.blockPosition())) {
                     AABB targetBounds = target.getBoundingBox();
                     ItemStack mainHandItem = player.getMainHandItem();
                     if (player.isWithinAttackRange(mainHandItem, targetBounds, GlobalConfiguration.get().misc.clientInteractionLeniencyDistance.or(3.0))
                        && !mainHandItem.has(DataComponents.PIERCING_WEAPON)) {
                        if (target instanceof ItemEntity
                           || target instanceof ExperienceOrb
                           || target == player
                           || target instanceof AbstractArrow abstractArrow && !abstractArrow.isAttackable()) {
                           connection.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"), Cause.INVALID_ENTITY_ATTACKED);
                           LOGGER.warn("Player {} tried to attack an invalid entity", player.getPlainTextName());
                        } else if (mainHandItem.isItemEnabled(level.enabledFeatures()) && !player.cannotAttackWithItem(mainHandItem, 5)) {
                           player.attack(target);
                        }
                     }
                  } else if (target == null) {
                     CraftEventFactory.callPlayerUseUnknownEntityEvent(player, attackPacket.getOriginalId(), true, InteractionHand.MAIN_HAND, null);
                  }

                  if (GlobalConfiguration.get().unsupportedSettings.updateEquipmentOnPlayerActions) {
                     player.detectEquipmentUpdates();
                  }
               }
            }
         }
      }
   }

   public static void handleInteract(ServerboundInteractPacketWrapper interactPacket, ServerGamePacketListener listener) {
      if (listener instanceof ServerGamePacketListenerImpl connection) {
         PacketUtils.ensureRunningOnSameThread(interactPacket, connection, connection.getPlayer().level());
         ServerPlayer player = connection.getPlayer();
         if (!player.isImmobile()) {
            if (connection.hasClientLoaded()) {
               CraftPlayer craftPlayer = player.getBukkitEntity();
               ServerLevel level = player.level();
               CraftServer craftServer = level.getServer().server;
               Entity target = level.getEntityOrPart(interactPacket.getRelayedId());
               if (interactPacket.isFakeInteraction()) {
                  EntityHandler entityHandler = ModelEngineAPI.getNMSHandler().getEntityHandler();
                  entityHandler.forceUseItem(craftPlayer, EquipmentSlot.HAND);
                  entityHandler.forceUseItem(craftPlayer, EquipmentSlot.OFF_HAND);
               }

               if (target != player || player.isSpectator()) {
                  player.resetLastActionTime();
                  player.setShiftKeyDown(interactPacket.isUsingSecondaryAction());
                  callBaseEntityEvents(
                     interactPacket,
                     (base, model, hitBone) -> {
                        craftServer.getPluginManager()
                           .callEvent(
                              new BaseEntityInteractEvent(
                                 craftPlayer,
                                 base,
                                 model,
                                 BaseEntityInteractEvent.Action.INTERACT,
                                 EquipmentSlot.HAND,
                                 interactPacket.isUsingSecondaryAction(),
                                 craftPlayer.getInventory().getItem(EquipmentSlot.HAND),
                                 null,
                                 hitBone
                              )
                           );
                        craftServer.getPluginManager()
                           .callEvent(
                              new BaseEntityInteractEvent(
                                 craftPlayer,
                                 base,
                                 model,
                                 BaseEntityInteractEvent.Action.INTERACT,
                                 EquipmentSlot.OFF_HAND,
                                 interactPacket.isUsingSecondaryAction(),
                                 craftPlayer.getInventory().getItem(EquipmentSlot.OFF_HAND),
                                 null,
                                 hitBone
                              )
                           );
                        Vec3 pos = interactPacket.getLocation();
                        Vector position = new Vector(pos.x, pos.y, pos.z);
                        craftServer.getPluginManager()
                           .callEvent(
                              new BaseEntityInteractEvent(
                                 craftPlayer,
                                 base,
                                 model,
                                 BaseEntityInteractEvent.Action.INTERACT_ON,
                                 EquipmentSlot.HAND,
                                 interactPacket.isUsingSecondaryAction(),
                                 craftPlayer.getInventory().getItem(EquipmentSlot.HAND),
                                 position,
                                 hitBone
                              )
                           );
                        craftServer.getPluginManager()
                           .callEvent(
                              new BaseEntityInteractEvent(
                                 craftPlayer,
                                 base,
                                 model,
                                 BaseEntityInteractEvent.Action.INTERACT_ON,
                                 EquipmentSlot.OFF_HAND,
                                 interactPacket.isUsingSecondaryAction(),
                                 craftPlayer.getInventory().getItem(EquipmentSlot.OFF_HAND),
                                 position,
                                 hitBone
                              )
                           );
                     }
                  );
                  if (target != null && level.getWorldBorder().isWithinBounds(target.blockPosition())) {
                     AABB targetBounds = target.getBoundingBox();
                     if (player.isWithinEntityInteractionRange(targetBounds, GlobalConfiguration.get().misc.clientInteractionLeniencyDistance.or(3.0))) {
                        InteractionHand hand = interactPacket.getHand();
                        Vec3 location = interactPacket.getLocation();
                        ItemStack tool = player.getItemInHand(hand);
                        if (tool.isItemEnabled(level.enabledFeatures())) {
                           ItemStack usedItemStack = tool.copy();
                           Item itemType = tool.getItem();
                           boolean triggerLeashUpdate = usedItemStack.is(Items.LEAD) && target instanceof Leashable;
                           PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(
                              connection.getCraftPlayer(), target.getBukkitEntity(), CraftVector.toBukkit(location), CraftEquipmentSlot.getHand(hand)
                           );
                           connection.cserver.getPluginManager().callEvent(event);
                           boolean resendData = event.isCancelled() || !player.getItemInHand(hand).is(itemType);
                           if (itemType == Items.WATER_BUCKET && target instanceof Bucketable && target instanceof LivingEntity && resendData) {
                              target.resendPossiblyDesyncedEntityData(player);
                           }

                           if (resendData) {
                              if (MCUtil.clientPredictsInteraction(player, target, tool)) {
                                 player.containerMenu.sendAllDataToRemote();
                              } else {
                                 player.containerMenu.forceHeldSlot(hand);
                              }
                           }

                           if (triggerLeashUpdate && resendData) {
                              connection.send(new ClientboundSetEntityLinkPacket(target, ((Leashable)target).getLeashHolder()));
                           }

                           if (resendData) {
                              target.refreshEntityData(player);
                              if (!(target instanceof Allay) && !(target instanceof AbstractHorse)) {
                                 player.containerMenu.forceHeldSlot(hand);
                              } else {
                                 connection.send(
                                    new ClientboundSetEquipmentPacket(
                                       target.getId(),
                                       Arrays.stream(net.minecraft.world.entity.EquipmentSlot.values())
                                          .map(slot -> Pair.of(slot, ((LivingEntity)target).getItemBySlot(slot).copy()))
                                          .collect(Collectors.toList()),
                                       true
                                    )
                                 );
                                 player.containerMenu.sendAllDataToRemote();
                              }
                           }

                           if (event.isCancelled()) {
                              return;
                           }

                           if (player.interactOn(target, hand, location) instanceof Success success) {
                              ItemStack awardedForStack = success.wasItemInteraction() ? usedItemStack : ItemStack.EMPTY;
                              CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, awardedForStack, target);
                              if (success.swingSource() == SwingSource.SERVER) {
                                 player.swing(hand, true);
                              }
                           }
                        }
                     }
                  } else if (target == null) {
                     CraftEventFactory.callPlayerUseUnknownEntityEvent(
                        player, interactPacket.getOriginalId(), false, interactPacket.getHand(), interactPacket.getLocation()
                     );
                  }

                  if (GlobalConfiguration.get().unsupportedSettings.updateEquipmentOnPlayerActions) {
                     player.detectEquipmentUpdates();
                  }
               }
            }
         }
      }
   }

   public static void handleUseItem(ServerboundUseItemPacket packet, ServerGamePacketListener listener, Consumer<InteractionResult> afterUse) {
      if (listener instanceof ServerGamePacketListenerImpl connection) {
         ServerPlayer player = connection.getPlayer();
         PacketUtils.ensureRunningOnSameThread(packet, connection, player.level());
         if (!player.isImmobile()) {
            boolean shouldLimit = Boolean.TRUE
               .equals(ReflectionUtils.call(connection, NMSMethods.SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit, packet.timestamp));
            if (shouldLimit) {
               if (connection.hasClientLoaded()) {
                  connection.ackBlockChangesUpTo(packet.getSequence());
                  ServerLevel level = player.level();
                  InteractionHand hand = packet.getHand();
                  ItemStack itemStack = player.getItemInHand(hand);
                  player.resetLastActionTime();
                  if (!itemStack.isEmpty() && itemStack.isItemEnabled(level.enabledFeatures())) {
                     float targetYRot = Mth.wrapDegrees(packet.getYRot());
                     float targetXRot = Mth.wrapDegrees(packet.getXRot());
                     if (targetXRot != player.getXRot() || targetYRot != player.getYRot()) {
                        player.absSnapRotationTo(targetYRot, targetXRot);
                     }

                     Vec3 from = player.getEyePosition();
                     Vec3 delta = player.getViewVector(1.0F).scale(player.blockInteractionRange());
                     Vec3 to = from.add(delta);
                     BlockHitResult hitResult = player.level().clip(new ClipContext(from, to, Block.OUTLINE, Fluid.NONE, player));
                     boolean cancelled;
                     if (hitResult.getType() != Type.BLOCK) {
                        PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, itemStack, hand);
                        cancelled = event.useItemInHand() == Result.DENY;
                     } else {
                        if (player.gameMode.firedInteract
                           && player.gameMode.interactPosition.equals(hitResult.getBlockPos())
                           && player.gameMode.interactHand == hand
                           && ItemStack.isSameItemSameComponents(player.gameMode.interactItemStack, itemStack)) {
                           cancelled = player.gameMode.interactResult;
                        } else {
                           PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(
                              player,
                              Action.RIGHT_CLICK_BLOCK,
                              hitResult.getBlockPos(),
                              hitResult.getDirection(),
                              itemStack,
                              true,
                              hand,
                              hitResult.getLocation()
                           );
                           cancelled = event.useItemInHand() == Result.DENY;
                        }

                        player.gameMode.firedInteract = false;
                     }

                     if (cancelled) {
                        player.resyncUsingItem(player);
                        if (MCUtil.clientPredictsInteraction(player, Blocks.AIR.defaultBlockState(), itemStack)) {
                           player.containerMenu.sendAllDataToRemote();
                        } else {
                           player.containerMenu.forceHeldSlotAndArmor(hand);
                        }

                        return;
                     }

                     itemStack = player.getItemInHand(hand);
                     if (itemStack.isEmpty()) {
                        return;
                     }

                     InteractionResult result = player.gameMode.useItem(player, level, itemStack, hand);
                     afterUse.accept(result);
                     if (result instanceof Success success && success.swingSource() == SwingSource.SERVER) {
                        player.swing(hand, true);
                     }
                  }
               }
            }
         }
      }
   }

   private static void callBaseEntityEvents(RelayedPacketWrapper<?> relayedPacket, PatchedServerGamePacketListener.BaseEntityEventCallback callback) {
      ModelBone hitBone = null;
      ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(relayedPacket.getOriginalId());
      if (activeModel == null) {
         HitboxEntity hitbox = ModelEngineAPI.getInteractionTracker().getHitbox(relayedPacket.getRelayedId());
         if (hitbox != null) {
            hitBone = hitbox.getBone();
            activeModel = hitBone.getActiveModel();
         }
      }

      if (activeModel != null) {
         BaseEntity<?> base = activeModel.getModeledEntity().getBase();
         callback.call(base, activeModel, hitBone);
      }
   }

   @FunctionalInterface
   private interface BaseEntityEventCallback {
      void call(BaseEntity<?> baseEntity, ActiveModel activeModel, ModelBone modelBone);
   }
}
