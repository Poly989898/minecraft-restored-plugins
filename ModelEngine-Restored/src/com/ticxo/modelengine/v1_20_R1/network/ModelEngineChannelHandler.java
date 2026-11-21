package com.ticxo.modelengine.v1_20_R1.network;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.CancelType;
import com.ticxo.modelengine.api.nms.network.ClientDesyncMonitor;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_20_R1.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R1.network.patch.ServerboundInteractPacketWrapper;
import com.ticxo.modelengine.v1_20_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.PacketInterceptor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutKeepAlive;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.network.protocol.game.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.b;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ModelEngineChannelHandler extends ChannelDuplexHandler {
   private final Player player;
   private final EntityPlayer serverPlayer;
   private final ModelUpdaters updaters;
   private final EntityHandler entityHandler;
   private final ClientDesyncMonitor desyncMonitor;
   private final PacketInterceptor writeInterceptors;
   private final PacketInterceptor readInterceptors;

   public ModelEngineChannelHandler(Player player, PipelineWrapper pipeline) {
      this.player = player;
      this.serverPlayer = ((CraftPlayer)player).getHandle();
      this.updaters = ModelEngineAPI.getAPI().getModelUpdaters();
      this.entityHandler = ModelEngineAPI.getEntityHandler();
      this.desyncMonitor = pipeline.getDesyncMonitor();
      this.writeInterceptors = new PacketInterceptor();
      this.writeInterceptors
         .register(PacketPlayOutSpawnEntity.class, this::handleAddEntity)
         .register(PacketPlayOutNamedEntitySpawn.class, this::handleAddPlayer)
         .register(PacketPlayOutEntityDestroy.class, this::handleRemoveEntities)
         .register(PacketPlayOutRelEntityMove.class, this::handleEntityId)
         .register(PacketPlayOutEntityLook.class, this::handleEntityId)
         .register(PacketPlayOutRelEntityMoveLook.class, this::handleEntityId)
         .register(PacketPlayOutEntityHeadRotation.class, this::handleEntityId)
         .register(PacketPlayOutEntityStatus.class, this::handleEntityId)
         .register(PacketPlayOutEntityVelocity.class, this::handleEntityMotion)
         .register(PacketPlayOutEntityTeleport.class, this::handleTeleportEntity)
         .register(PacketPlayOutAnimation.class, this::handleAnimate)
         .register(PacketPlayOutEntityMetadata.class, this::handleEntityData)
         .register(PacketPlayOutEntityEquipment.class, this::handleSetEquipment)
         .register(PacketPlayOutRemoveEntityEffect.class, this::handleRemoveMobEffect)
         .register(PacketPlayOutEntityEffect.class, this::handleUpdateMobEffect)
         .register(PacketPlayOutKeepAlive.class, this::handleKeepAlive)
         .register(PacketPlayOutMount.class, this::handlePassengers)
         .registerPost(PacketPlayOutSpawnEntity.class, this::handleAddEntityPost)
         .registerPost(PacketPlayOutNamedEntitySpawn.class, this::handleAddPlayerPost);
      this.readInterceptors = new PacketInterceptor();
      this.readInterceptors
         .register(PacketPlayInUseEntity.class, this::handleInteract)
         .register(PacketPlayInSteerVehicle.class, this::handlePlayerInput)
         .register(ServerboundPongPacket.class, this::handlePong)
         .register(PacketPlayInPosition.class, this::handlePlayerMove)
         .register(PacketPlayInPositionLook.class, this::handlePlayerMove);
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (!(msg instanceof Packet packet)) {
         super.write(ctx, msg, promise);
      } else {
         try {
            if (packet instanceof ClientboundBundlePacket bundle) {
               ArrayList<Packet<PacketListenerPlayOut>> list = new ArrayList<>();

               for (Packet<PacketListenerPlayOut> subPacket : bundle.a()) {
                  Packet result = this.writeInterceptors.accept(subPacket);
                  if (result != null) {
                     list.add(result);

                     for (Packet p : this.writeInterceptors.acceptPost(result)) {
                        list.add(p);
                     }
                  }
               }

               if (!list.isEmpty()) {
                  Packet packet2 = new ClientboundBundlePacket(list);
                  super.write(ctx, packet2, promise);
               }
            } else {
               Packet packet3 = this.writeInterceptors.accept(packet);
               if (packet3 == null) {
                  return;
               }

               ArrayList<Packet<PacketListenerPlayOut>> list = new ArrayList<>();
               list.add(packet3);

               for (Packet p : this.writeInterceptors.acceptPost(packet3)) {
                  list.add(p);
               }

               if (list.size() == 1) {
                  super.write(ctx, packet3, promise);
               } else {
                  Packet packet4 = new ClientboundBundlePacket(list);
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
         Packet packet = this.readInterceptors.accept((Packet)msg);
         if (packet != null) {
            super.channelRead(ctx, packet);
            this.readInterceptors.acceptPost(packet);
         }
      }
   }

   private PacketPlayOutSpawnEntity handleAddEntity(PacketPlayOutSpawnEntity packet) {
      int id = packet.a();
      this.markViewersDirty(id);
      return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
   }

   private void markViewersDirty(int id) {
      ModeledEntity model = this.updaters.getModeledEntity(id);
      if (model != null) {
         model.getBase().getData().markViewersDirty();
      }
   }

   private PacketPlayOutNamedEntitySpawn handleAddPlayer(PacketPlayOutNamedEntitySpawn packet) {
      int id = packet.a();
      this.markViewersDirty(id);
      return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
   }

   private List<Packet> handleAddEntityPost(PacketPlayOutSpawnEntity packet) {
      return this.handleMount(packet.c());
   }

   private List<Packet> handleAddPlayerPost(PacketPlayOutNamedEntitySpawn packet) {
      return this.handleMount(packet.c());
   }

   private List<Packet> handleMount(UUID uuid) {
      Pair<ActiveModel, MountController> pair = ModelEngineAPI.getMountPairManager().get(uuid);
      if (pair == null) {
         return null;
      }

      ActiveModel model = pair.left();
      ArrayList<Packet> list = new ArrayList<>();
      model.getBehaviorRenderer(BoneBehaviorTypes.MOUNT).ifPresent(behaviorRenderer -> {
         if (behaviorRenderer instanceof MountRenderer renderer) {
            MountController controller = pair.right();
            if (controller.getMount() instanceof BoneBehavior behavior) {
               MountRenderer.Mount mount = renderer.getRendered().get(behavior.getBone().getBoneId());
               CollectionDataTracker<Integer> ids = mount.getPassengers();
               PacketDataSerializer buf = NetworkUtils.createByteBuf();
               buf.d(mount.getMountId());
               buf.d(ids.size());
               ids.forEach(buf::d);
               list.add(new PacketPlayOutMount(buf));
            }
         }
      });
      return list;
   }

   private PacketPlayOutEntityDestroy handleRemoveEntities(PacketPlayOutEntityDestroy packet) {
      PacketDataSerializer buf = NetworkUtils.readData(packet);
      int size = buf.m();
      HashSet<Integer> set = new HashSet<>(size);

      for (int i = 0; i < size; i++) {
         int id = buf.m();
         this.markViewersDirty(id);
         if (ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE) {
            set.add(id);
         }
      }

      if (set.size() == size) {
         return packet;
      }

      buf = NetworkUtils.createByteBuf();
      buf.d(set.size());

      for (int id : set) {
         buf.d(id);
      }

      return new PacketPlayOutEntityDestroy(buf);
   }

   private <U extends PacketListener, T extends Packet<U>> Packet<U> handleEntityId(T packet) {
      PacketDataSerializer buf = NetworkUtils.readData(packet);
      int id = buf.m();
      return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
   }

   private PacketPlayOutEntityVelocity handleEntityMotion(PacketPlayOutEntityVelocity packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
   }

   private PacketPlayOutEntityTeleport handleTeleportEntity(PacketPlayOutEntityTeleport packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
   }

   private PacketPlayOutAnimation handleAnimate(PacketPlayOutAnimation packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
   }

   private PacketPlayOutEntityMetadata handleEntityData(PacketPlayOutEntityMetadata packet) {
      CancelType type = ModelEngineAPI.shouldShow(this.player, packet.a());
      if (type == CancelType.HIDE) {
         return null;
      }

      if (packet.a() != this.player.getEntityId() && type == CancelType.SHOW) {
         return packet;
      }

      ModeledEntity modeledEntity = this.updaters.getModeledEntity(packet.a());
      if (modeledEntity == null) {
         return packet;
      }

      if (this.entityHandler.isForcedInvisible(modeledEntity.getBase().getUUID())) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         buf.d(packet.a());

         for (b<?> item : packet.c()) {
            if (item.a() == 0) {
               byte data = (Byte)item.c();
               data = TMath.setBit(data, 5, true);
               EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, data);
            } else {
               EntityUtils.writeDataUnsafe(buf, item.a(), item.b(), item.c());
            }
         }

         buf.writeByte(255);
         packet = new PacketPlayOutEntityMetadata(buf);
      }

      return packet;
   }

   private PacketPlayOutEntityEquipment handleSetEquipment(PacketPlayOutEntityEquipment packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.a()) == CancelType.SHOW ? packet : null;
   }

   private PacketPlayOutRemoveEntityEffect handleRemoveMobEffect(PacketPlayOutRemoveEntityEffect packet) {
      PacketDataSerializer buf = NetworkUtils.readData(packet);
      int id = buf.m();
      return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
   }

   private PacketPlayOutEntityEffect handleUpdateMobEffect(PacketPlayOutEntityEffect packet) {
      return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
   }

   private PacketPlayOutKeepAlive handleKeepAlive(PacketPlayOutKeepAlive packet) {
      if (this.desyncMonitor.clientTickShifted() || this.desyncMonitor.shouldRetest()) {
         this.desyncMonitor.startTest();
      }

      return packet;
   }

   private PacketPlayOutMount handlePassengers(PacketPlayOutMount packet) {
      PivotOverrideRegistry registry = ModelEngineAPI.getPivotOverrideRegistry();
      Optional<PivotOverride> maybeOverride = registry.get(packet.c());
      if (maybeOverride.isEmpty()) {
         return packet;
      }

      PivotOverride override = maybeOverride.get();
      Collection<Integer> extra = override.getPassengers().get();
      if (extra.isEmpty()) {
         return packet;
      }

      int[] existing = packet.a();
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

      if (!changed) {
         return packet;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(packet.c());
      buf.d(merged.size());
      IntListIterator intListIterator = merged.iterator();

      while (intListIterator.hasNext()) {
         int id = intListIterator.next();
         buf.d(id);
      }

      return new PacketPlayOutMount(buf);
   }

   private PacketPlayInUseEntity handleInteract(PacketPlayInUseEntity packet) {
      PacketDataSerializer buf = NetworkUtils.readData(packet);
      int entityId = buf.m();
      if (entityId == DynamicHitbox.getHitboxId()) {
         DynamicHitbox hitbox = ModelEngineAPI.getInteractionTracker().getDynamicHitbox(this.player.getUniqueId());
         if (hitbox != null) {
            return new ServerboundInteractPacketWrapper(entityId, hitbox.getTarget(), packet);
         }
      }

      ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(entityId);
      if (activeModel != null) {
         ModeledEntity modeledEntity = activeModel.getModeledEntity();
         if (modeledEntity == null) {
            return packet;
         }

         int id = modeledEntity.getBase().getEntityId();
         return new ServerboundInteractPacketWrapper(entityId, id, packet);
      } else {
         Integer relayed = ModelEngineAPI.getInteractionTracker().getEntityRelay(entityId);
         return relayed != null ? new ServerboundInteractPacketWrapper(entityId, relayed, packet) : packet;
      }
   }

   private PacketPlayInSteerVehicle handlePlayerInput(PacketPlayInSteerVehicle inputPacket) {
      MountController controller = ModelEngineAPI.getMountPairManager().getController(this.player.getUniqueId());
      if (controller != null) {
         MountController.MountInput input = controller.getInput();
         if (input == null) {
            controller.setInput(new MountController.MountInput(inputPacket.a(), inputPacket.c(), inputPacket.d(), inputPacket.e()));
         } else {
            input.setSide(inputPacket.a());
            input.setFront(inputPacket.c());
            input.setJump(inputPacket.d());
            input.setSneak(inputPacket.e());
         }
      }

      return inputPacket;
   }

   private ServerboundPongPacket handlePong(ServerboundPongPacket packet) {
      this.desyncMonitor.recordPongTime(System.currentTimeMillis());
      return null;
   }

   private PacketPlayInFlying handlePlayerMove(PacketPlayInFlying packet) {
      this.desyncMonitor.recordClientSyncTime(System.currentTimeMillis());
      return packet;
   }
}
