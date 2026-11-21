package com.ticxo.modelengine.v1_20_R3.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class LeashParser implements BehaviorRendererParser<LeashRenderer> {
   public void sendToClients(LeashRenderer renderer) {
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      HashSet<UUID> stop = new HashSet<>(data.getStopTracking());
      stop.removeAll(data.getTracking().keySet());
      this.remove(stop, renderer);
   }

   public void destroy(LeashRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, LeashRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (LeashRenderer.Leash leash : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(leash));
            set.add(this.pivotData(leash));
            set.add(this.leashSpawn(leash));
            set.add(this.leashData(leash));
            set.add(this.mount(leash));
            set.add(this.link(leash, true));
         }

         for (LeashRenderer.Leash leash : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(leash));
            set.add(this.pivotData(leash));
            set.add(this.leashSpawn(leash));
            set.add(this.leashData(leash));
            set.add(this.mount(leash));
            set.add(this.link(leash, true));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void update(Set<UUID> targets, LeashRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (LeashRenderer.Leash leash : renderer.getRendered().values()) {
            set.add(this.link(leash, false));
            set.add(this.move(leash));
         }

         for (LeashRenderer.Leash leash : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(leash));
            set.add(this.pivotData(leash));
            set.add(this.leashSpawn(leash));
            set.add(this.leashData(leash));
            set.add(this.mount(leash));
            set.add(this.link(leash, true));
         }

         Map<String, LeashRenderer.Leash> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            set.add(new PacketPlayOutEntityDestroy(IntArrayList.toList(destroy.values().stream().mapMultiToInt((leash2, intConsumer) -> {
               intConsumer.accept(leash2.getLeashId());
               intConsumer.accept(leash2.getPivotId());
            }))));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, LeashRenderer renderer) {
      if (!targets.isEmpty()) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         Map<String, LeashRenderer.Leash> leashes = renderer.getRendered();
         buf.c(leashes.size());
         leashes.forEach((s, leash) -> buf.c(leash.getLeashId()));
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(buf));
      }
   }

   private Packets.PacketSupplier pivotSpawn(LeashRenderer.Leash renderer) {
      Vector3f pos = renderer.getPosition().get();
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUUID(), pos);
   }

   private PacketPlayOutSpawnEntity leashSpawn(LeashRenderer.Leash renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new PacketPlayOutSpawnEntity(renderer.getLeashId(), renderer.getLeastUUID(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.aM, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata pivotData(LeashRenderer.Leash renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getLeashId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutEntityMetadata leashData(LeashRenderer.Leash renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getLeashId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
      EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, 0);
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutMount mount(LeashRenderer.Leash renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getPivotId());
      buf.c(1);
      buf.c(renderer.getLeashId());
      return new PacketPlayOutMount(buf);
   }

   private Object move(LeashRenderer.Leash renderer) {
      if (!renderer.getPosition().isDirty()) {
         return null;
      }

      Vector3f pos = renderer.getPosition().get();
      return NetworkUtils.createPivotMove(renderer.getPivotId(), renderer.getPositionCodec(), pos);
   }

   private PacketPlayOutAttachEntity link(LeashRenderer.Leash renderer, boolean spawn) {
      if (!spawn && !renderer.getConnected().isDirty()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.p(renderer.getLeashId());
      buf.p(renderer.getConnected().get());
      renderer.getConnected().clearDirty();
      return new PacketPlayOutAttachEntity(buf);
   }
}
