package com.ticxo.modelengine.v26_1.network;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.CancelType;
import com.ticxo.modelengine.api.nms.network.ClientDesyncMonitor;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v26_1.entity.EntityContainer;
import com.ticxo.modelengine.v26_1.network.patch.ServerboundAttackPacketWrapper;
import com.ticxo.modelengine.v26_1.network.patch.ServerboundInteractPacketWrapper;
import com.ticxo.modelengine.v26_1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v26_1.network.utils.PacketInterceptor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.PosRot;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ModelEngineChannelHandler extends ChannelDuplexHandler {
   private final Player player;
   private final ServerPlayer serverPlayer;
   private final ModelUpdaters updaters;
   private final EntityHandler entityHandler;
   private final ClientDesyncMonitor desyncMonitor;
   private final PacketInterceptor<ClientGamePacketListener> writeInterceptors;
   private final PacketInterceptor<ServerGamePacketListener> readInterceptors;

   public ModelEngineChannelHandler(Player player, PipelineWrapper pipeline) {
      this.player = player;
      this.serverPlayer = ((CraftPlayer)player).getHandle();
      this.updaters = ModelEngineAPI.getAPI().getModelUpdaters();
      this.entityHandler = ModelEngineAPI.getEntityHandler();
      this.desyncMonitor = pipeline.getDesyncMonitor();
      this.writeInterceptors = new PacketInterceptor();
      this.writeInterceptors
         .register(ClientboundAddEntityPacket.class, this::handleAddEntity)
         .register(ClientboundRemoveEntitiesPacket.class, this::handleRemoveEntities)
         .register(Pos.class, this::handleEntityId)
         .register(Rot.class, this::handleEntityId)
         .register(PosRot.class, this::handleEntityId)
         .register(ClientboundRotateHeadPacket.class, this::handleEntityId)
         .register(ClientboundEntityEventPacket.class, this::handleEntityId)
         .register(ClientboundSetEntityMotionPacket.class, this::handleEntityMotion)
         .register(ClientboundTeleportEntityPacket.class, this::handleTeleportEntity)
         .register(ClientboundEntityPositionSyncPacket.class, this::handlePositionSync)
         .register(ClientboundAnimatePacket.class, this::handleAnimate)
         .register(ClientboundSetEntityDataPacket.class, this::handleEntityData)
         .register(ClientboundSetEquipmentPacket.class, this::handleSetEquipment)
         .register(ClientboundRemoveMobEffectPacket.class, this::handleRemoveMobEffect)
         .register(ClientboundUpdateMobEffectPacket.class, this::handleUpdateMobEffect)
         .register(ClientboundKeepAlivePacket.class, this::handleKeepAlive)
         .register(ClientboundSetPassengersPacket.class, this::handlePassengers)
         .registerPost(ClientboundAddEntityPacket.class, this::handleAddEntityPost);
      this.readInterceptors = new PacketInterceptor();
      this.readInterceptors
         .register(ServerboundAttackPacket.class, this::handleAttack)
         .register(ServerboundInteractPacket.class, this::handleInteract)
         .register(ServerboundPlayerInputPacket.class, this::handlePlayerInput)
         .register(ServerboundPongPacket.class, this::handlePong)
         .register(net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos.class, this::handlePlayerMove)
         .register(net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot.class, this::handlePlayerMove)
         .register(ServerboundClientTickEndPacket.class, this::handleClientTickEnd);
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (!(msg instanceof Packet<?>)) {
         super.write(ctx, msg, promise);
      } else {
         Packet<? super ClientGamePacketListener> packet = (Packet<? super ClientGamePacketListener>)msg;
         try {
            if (packet instanceof ClientboundBundlePacket bundle) {
               ArrayList<Packet<? super ClientGamePacketListener>> list = new ArrayList<>();

               for (Packet<? super ClientGamePacketListener> subPacket : bundle.subPackets()) {
                  Packet<? super ClientGamePacketListener> result = this.writeInterceptors.accept(subPacket);
                  if (result != null) {
                     list.add(result);
                     list.addAll(this.writeInterceptors.acceptPost(result));
                  }
               }

               if (!list.isEmpty()) {
                  Packet<? super ClientGamePacketListener> packet2 = new ClientboundBundlePacket(list);
                  super.write(ctx, packet2, promise);
               }
            } else {
               Packet<? super ClientGamePacketListener> packet3 = this.writeInterceptors.accept(packet);
               if (packet3 == null) {
                  return;
               }

               ArrayList<Packet<? super ClientGamePacketListener>> list = new ArrayList<>();
               list.add(packet3);
               list.addAll(this.writeInterceptors.acceptPost(packet3));
               if (list.size() == 1) {
                  super.write(ctx, packet3, promise);
               } else {
                  Packet<? super ClientGamePacketListener> packet4 = new ClientboundBundlePacket(list);
                  super.write(ctx, packet4, promise);
               }
            }
         } catch (Throwable t) {
            t.printStackTrace();
         }
      }
   }

   public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
      if (!(msg instanceof Packet)) {
         super.channelRead(ctx, msg);
      } else {
         Packet<? super ServerGamePacketListener> packet = this.readInterceptors.accept((Packet)msg);
         if (packet != null) {
            super.channelRead(ctx, packet);
            this.readInterceptors.acceptPost(packet);
         }
      }
   }

   private ClientboundAddEntityPacket handleAddEntity(ClientboundAddEntityPacket packet) {
      int id = packet.getId();
      this.markViewersDirty(id);
      return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
   }

   private void markViewersDirty(int id) {
      ModeledEntity model = this.updaters.getModeledEntity(id);
      if (model != null) {
         model.getBase().getData().markViewersDirty();
      }
   }

   private List<Packet<? super ClientGamePacketListener>> handleAddEntityPost(ClientboundAddEntityPacket packet) {
      return this.handleMount(packet.getUUID());
   }

   private List<Packet<? super ClientGamePacketListener>> handleMount(UUID uuid) {
      Pair<ActiveModel, MountController> pair = ModelEngineAPI.getMountPairManager().get(uuid);
      if (pair == null) {
         return null;
      }

      ActiveModel model = pair.left();
      List<Packet<? super ClientGamePacketListener>> list = null;
      Optional<BehaviorRenderer> maybeMount = model.getBehaviorRenderer(BoneBehaviorTypes.MOUNT);
      if (maybeMount.isPresent() && maybeMount.get() instanceof MountRenderer renderer) {
         MountController controller = pair.right();
         if (controller.getMount() instanceof BoneBehavior behavior) {
            MountRenderer.Mount mount = renderer.getRendered().get(behavior.getBone().getBoneId());
            if (mount == null) {
               return null;
            }

            CollectionDataTracker<Integer> ids = mount.getPassengers();
            list = new ArrayList<>();
            list.add(new ClientboundSetPassengersPacket(EntityContainer.of(mount.getMountId(), ids)));
         }
      }

      return list;
   }

   private ClientboundRemoveEntitiesPacket handleRemoveEntities(ClientboundRemoveEntitiesPacket packet) {
      IntList ids = packet.getEntityIds();

      for (int i = 0; i < ids.size(); i++) {
         this.markViewersDirty(ids.getInt(i));
      }

      int[] set = packet.getEntityIds().intStream().filter(id -> ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE).toArray();
      return set.length == packet.getEntityIds().size() ? packet : new ClientboundRemoveEntitiesPacket(IntArrayList.wrap(set));
   }

   private <T extends Packet<? super ClientGamePacketListener>> T handleEntityId(T packet) {
      FriendlyByteBuf buf = NetworkUtils.readClientbound(packet);

      try {
         int id = buf.readVarInt();
         return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
      } finally {
         buf.release();
      }
   }

   private ClientboundSetEntityMotionPacket handleEntityMotion(ClientboundSetEntityMotionPacket packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.id()) != CancelType.HIDE ? packet : null;
   }

   private ClientboundTeleportEntityPacket handleTeleportEntity(ClientboundTeleportEntityPacket packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.id()) != CancelType.HIDE ? packet : null;
   }

   private ClientboundEntityPositionSyncPacket handlePositionSync(ClientboundEntityPositionSyncPacket packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.id()) != CancelType.HIDE ? packet : null;
   }

   private ClientboundAnimatePacket handleAnimate(ClientboundAnimatePacket packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.getId()) != CancelType.HIDE ? packet : null;
   }

   private ClientboundSetEntityDataPacket handleEntityData(ClientboundSetEntityDataPacket packet) {
      CancelType type = ModelEngineAPI.shouldShow(this.player, packet.id());
      if (type == CancelType.HIDE) {
         return null;
      }

      if (packet.id() != this.player.getEntityId() && type == CancelType.SHOW) {
         return packet;
      }

      ModeledEntity modeledEntity = this.updaters.getModeledEntity(packet.id());
      if (modeledEntity == null) {
         return packet;
      }

      if (this.entityHandler.isForcedInvisible(modeledEntity.getBase().getUUID())) {
         ArrayList<DataValue<?>> entityData = new ArrayList<>();

         for (DataValue<?> item : packet.packedItems()) {
            if (item.id() == 0) {
               byte data = (Byte)item.value();
               data = TMath.setBit(data, 5, true);
               entityData.add(new DataValue(0, EntityDataSerializers.BYTE, data));
            } else {
               entityData.add(item);
            }
         }

         packet = new ClientboundSetEntityDataPacket(packet.id(), entityData);
      }

      return packet;
   }

   private ClientboundSetEquipmentPacket handleSetEquipment(ClientboundSetEquipmentPacket packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.getEntity()) == CancelType.SHOW ? packet : null;
   }

   private ClientboundRemoveMobEffectPacket handleRemoveMobEffect(ClientboundRemoveMobEffectPacket packet) {
      int id = packet.entityId();
      return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
   }

   private ClientboundUpdateMobEffectPacket handleUpdateMobEffect(ClientboundUpdateMobEffectPacket packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.getEntityId()) != CancelType.HIDE ? packet : null;
   }

   private ClientboundKeepAlivePacket handleKeepAlive(ClientboundKeepAlivePacket packet) {
      if (this.desyncMonitor.clientTickShifted() || this.desyncMonitor.shouldRetest()) {
         this.desyncMonitor.startTest();
      }

      return packet;
   }

   private ClientboundSetPassengersPacket handlePassengers(ClientboundSetPassengersPacket packet) {
      PivotOverrideRegistry registry = ModelEngineAPI.getPivotOverrideRegistry();
      Optional<PivotOverride> maybeOverride = registry.get(packet.getVehicle());
      if (maybeOverride.isEmpty()) {
         return packet;
      }

      PivotOverride override = maybeOverride.get();
      Collection<Integer> extra = override.getPassengers().get();
      if (extra.isEmpty()) {
         return packet;
      }

      int[] existing = packet.getPassengers();
      IntLinkedOpenHashSet merged = new IntLinkedOpenHashSet(existing.length + extra.size());

      for (int id : existing) {
         merged.add(id);
      }

      boolean changed = false;

      for (int id : extra) {
         if (merged.add(id)) {
            changed = true;
         }
      }

      return !changed ? packet : new ClientboundSetPassengersPacket(EntityContainer.of(packet.getVehicle(), new IntArrayList(merged)));
   }

   private Packet<? super ServerGamePacketListener> handleAttack(ServerboundAttackPacket packet) {
      int entityId = packet.entityId();
      if (entityId == DynamicHitbox.getHitboxId()) {
         DynamicHitbox hitbox = ModelEngineAPI.getInteractionTracker().getDynamicHitbox(this.player.getUniqueId());
         if (hitbox != null) {
            return new ServerboundAttackPacketWrapper(hitbox.getTarget(), packet);
         }
      }

      ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(entityId);
      if (activeModel != null) {
         ModeledEntity modeledEntity = activeModel.getModeledEntity();
         if (modeledEntity == null) {
            return packet;
         }

         int id = modeledEntity.getBase().getEntityId();
         return new ServerboundAttackPacketWrapper(id, packet);
      } else {
         Integer relayed = ModelEngineAPI.getInteractionTracker().getEntityRelay(entityId);
         return (Packet<? super ServerGamePacketListener>)(relayed != null ? new ServerboundAttackPacketWrapper(relayed, packet) : packet);
      }
   }

   private Packet<? super ServerGamePacketListener> handleInteract(ServerboundInteractPacket packet) {
      int entityId = packet.entityId();
      if (entityId == DynamicHitbox.getHitboxId()) {
         DynamicHitbox hitbox = ModelEngineAPI.getInteractionTracker().getDynamicHitbox(this.player.getUniqueId());
         if (hitbox != null) {
            return new ServerboundInteractPacketWrapper(hitbox.getTarget(), packet);
         }
      }

      ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(entityId);
      if (activeModel != null) {
         ModeledEntity modeledEntity = activeModel.getModeledEntity();
         if (modeledEntity == null) {
            return packet;
         }

         int id = modeledEntity.getBase().getEntityId();
         return new ServerboundInteractPacketWrapper(id, packet);
      } else {
         Integer relayed = ModelEngineAPI.getInteractionTracker().getEntityRelay(entityId);
         return (Packet<? super ServerGamePacketListener>)(relayed != null ? new ServerboundInteractPacketWrapper(relayed, packet) : packet);
      }
   }

   private ServerboundPlayerInputPacket handlePlayerInput(ServerboundPlayerInputPacket inputPacket) {
      MountController controller = ModelEngineAPI.getMountPairManager().getController(this.player.getUniqueId());
      if (controller != null) {
         MountController.MountInput input = controller.getInput();
         if (input == null) {
            controller.setInput(
               new MountController.MountInput(
                  inputPacket.input().forward(),
                  inputPacket.input().backward(),
                  inputPacket.input().left(),
                  inputPacket.input().right(),
                  inputPacket.input().jump(),
                  inputPacket.input().shift(),
                  inputPacket.input().sprint()
               )
            );
         } else {
            input.setForward(inputPacket.input().forward());
            input.setBackward(inputPacket.input().backward());
            input.setLeft(inputPacket.input().left());
            input.setRight(inputPacket.input().right());
            input.setJump(inputPacket.input().jump());
            input.setSneak(inputPacket.input().shift());
            input.setSprint(inputPacket.input().sprint());
         }
      }

      return inputPacket;
   }

   private ServerboundPongPacket handlePong(ServerboundPongPacket packet) {
      this.desyncMonitor.recordPongTime(System.currentTimeMillis());
      return null;
   }

   private ServerboundMovePlayerPacket handlePlayerMove(ServerboundMovePlayerPacket packet) {
      if (!ConfigProperty.SYNC_CLIENT_TICK_END.getBoolean()) {
         this.desyncMonitor.recordClientSyncTime(System.currentTimeMillis());
      }

      return packet;
   }

   private ServerboundClientTickEndPacket handleClientTickEnd(ServerboundClientTickEndPacket packet) {
      if (ConfigProperty.SYNC_CLIENT_TICK_END.getBoolean()) {
         this.desyncMonitor.recordClientSyncTime(System.currentTimeMillis());
      }

      return packet;
   }
}
