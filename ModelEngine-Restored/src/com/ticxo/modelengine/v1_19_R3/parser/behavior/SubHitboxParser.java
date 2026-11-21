package com.ticxo.modelengine.v1_19_R3.parser.behavior;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.v1_19_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_19_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_19_R3.network.utils.Packets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class SubHitboxParser implements BehaviorRendererParser<SubHitboxRenderer> {
   public void sendToClients(SubHitboxRenderer renderer) {
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
   }

   public void destroy(SubHitboxRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, SubHitboxRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add(this.pivotData(subHitbox));
            set.add(this.hitboxSpawn(subHitbox));
            set.add(this.hitboxData(subHitbox, true));
            set.add(this.pivotMount(subHitbox));
         }

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add(this.pivotData(subHitbox));
            set.add(this.hitboxSpawn(subHitbox));
            set.add(this.hitboxData(subHitbox, true));
            set.add(this.pivotMount(subHitbox));
         }

         BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
         NetworkUtils.sendBundled(targets, set, player -> player != base.getOriginal());
      }
   }

   private void update(Set<UUID> targets, SubHitboxRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getRendered().values()) {
            set.add(this.hitboxData(subHitbox, false));
            if (subHitbox.getPosition().isDirty()) {
               set.add(this.pivotMove(subHitbox));
               subHitbox.getPosition().clearDirty();
            }
         }

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add(this.pivotData(subHitbox));
            set.add(this.hitboxSpawn(subHitbox));
            set.add(this.hitboxData(subHitbox, true));
            set.add(this.pivotMount(subHitbox));
         }

         Map<String, SubHitboxRenderer.SubHitbox> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.d(destroy.size() * 2);
            destroy.forEach((s, subHitbox) -> {
               buf.d(subHitbox.getPivotId());
               buf.d(subHitbox.getHitboxId());
            });
            set.add(new PacketPlayOutEntityDestroy(buf));
         }

         BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
         NetworkUtils.sendBundled(targets, set, player -> player != base.getOriginal());
      }
   }

   private void remove(Set<UUID> targets, SubHitboxRenderer renderer) {
      if (!targets.isEmpty()) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         Collection<SubHitboxRenderer.SubHitbox> subHitboxes = renderer.getRendered().values();
         buf.d(subHitboxes.size() * 2);
         subHitboxes.forEach(subHitbox -> {
            buf.d(subHitbox.getPivotId());
            buf.d(subHitbox.getHitboxId());
         });
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(buf));
      }
   }

   private Packets.PacketSupplier pivotSpawn(SubHitboxRenderer.SubHitbox renderer) {
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
   }

   private PacketPlayOutEntityMetadata pivotData(SubHitboxRenderer.SubHitbox renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(renderer.getPivotId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.writeByte(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private Object pivotMove(SubHitboxRenderer.SubHitbox renderer) {
      return NetworkUtils.createPivotMove(renderer.getPivotId(), renderer.getPositionCodec(), renderer.getPosition().get());
   }

   private PacketPlayOutMount pivotMount(SubHitboxRenderer.SubHitbox renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(renderer.getPivotId());
      buf.d(1);
      buf.d(renderer.getHitboxId());
      return new PacketPlayOutMount(buf);
   }

   private PacketPlayOutSpawnEntity hitboxSpawn(SubHitboxRenderer.SubHitbox renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new PacketPlayOutSpawnEntity(renderer.getHitboxId(), renderer.getHitboxUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ab, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata hitboxData(SubHitboxRenderer.SubHitbox renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(renderer.getHitboxId());
      renderer.getWidth().ifDirty(width -> EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, width), spawn);
      renderer.getHeight().ifDirty(height -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.d, height), spawn);
      buf.writeByte(255);
      renderer.clearDirty();
      return new PacketPlayOutEntityMetadata(buf);
   }
}
