package com.ticxo.modelengine.v1_21_R7.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.v1_21_R7.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R7.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
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
            set.add(new ClientboundRemoveEntitiesPacket(IntArrayList.toList(destroy.values().stream().mapMultiToInt((leash2, intConsumer) -> {
               intConsumer.accept(leash2.getLeashId());
               intConsumer.accept(leash2.getPivotId());
            }))));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, LeashRenderer renderer) {
      if (!targets.isEmpty()) {
         Map<String, LeashRenderer.Leash> leashes = renderer.getRendered();
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(IntArrayList.toList(leashes.values().stream().mapMultiToInt((leash, intConsumer) -> {
            intConsumer.accept(leash.getLeashId());
            intConsumer.accept(leash.getPivotId());
         }))));
      }
   }

   private Packets.PacketSupplier pivotSpawn(LeashRenderer.Leash renderer) {
      Vector3f pos = renderer.getPosition().get();
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUUID(), pos.add(0.0F, -0.45F, -0.2F, new Vector3f()));
   }

   private ClientboundAddEntityPacket leashSpawn(LeashRenderer.Leash renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new ClientboundAddEntityPacket(
         renderer.getLeashId(), renderer.getLeastUUID(), pos.x, pos.y - 0.45, pos.z - 0.2, 0.0F, 0.0F, EntityType.BAT, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket pivotData(LeashRenderer.Leash renderer) {
      return new ClientboundSetEntityDataPacket(renderer.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
   }

   private ClientboundSetEntityDataPacket leashData(LeashRenderer.Leash renderer) {
      return new ClientboundSetEntityDataPacket(renderer.getLeashId(), EntityUtils.DEFAULT_BAT_DATA);
   }

   private ClientboundSetPassengersPacket mount(LeashRenderer.Leash renderer) {
      ArrayList<Integer> passengers = new ArrayList<>(1);
      passengers.add(renderer.getLeashId());
      return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), passengers));
   }

   private Object move(LeashRenderer.Leash renderer) {
      if (!renderer.getPosition().isDirty()) {
         return null;
      }

      Vector3f pos = renderer.getPosition().get();
      return NetworkUtils.createPivotMove(renderer.getPivotId(), renderer.getPositionCodec(), pos.add(0.0F, -0.45F, -0.2F, new Vector3f()));
   }

   private ClientboundSetEntityLinkPacket link(LeashRenderer.Leash renderer, boolean spawn) {
      if (!spawn && !renderer.getConnected().isDirty()) {
         return null;
      }

      renderer.getConnected().clearDirty();
      return new ClientboundSetEntityLinkPacket(EntityContainer.of(renderer.getLeashId()), EntityContainer.of(renderer.getConnected().get()));
   }
}
