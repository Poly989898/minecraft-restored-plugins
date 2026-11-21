package com.ticxo.modelengine.v1_20_R3.parser.vfx;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import java.util.HashSet;
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
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.joml.Vector3f;

public class VFXDisplayParser implements VFXRendererParser<VFXDisplayRenderer> {
   public void sendToClients(VFXDisplayRenderer renderer) {
      IEntityData data = renderer.getVFX().getBase().getData();
      if (renderer.isRespawnRequired()) {
         this.teleport(renderer.getVFXModel());
         this.spawn(data.getTracking().keySet(), renderer.getVFXModel());
         renderer.setRespawnRequired(false);
      } else {
         this.update(data.getTracking().keySet(), renderer.getVFXModel());
         this.spawn(data.getStartTracking(), renderer.getVFXModel());
         this.remove(data.getStopTracking(), renderer.getVFXModel());
      }
   }

   public void destroy(VFXDisplayRenderer renderer) {
      IEntityData data = renderer.getVFX().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer.getVFXModel());
   }

   public void spawn(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         set.add(this.pivotSpawn(vfx));
         set.add(this.pivotData(vfx));
         set.add(this.vfxSpawn(vfx));
         set.add(this.vfxData(vfx, true));
         set.add(this.mount(vfx));
         NetworkUtils.sendBundled(targets, set);
      }
   }

   public void update(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         set.add(this.teleport(vfx));
         set.add(this.vfxData(vfx, false));
         NetworkUtils.sendBundled(targets, set);
      }
   }

   public void remove(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
      if (!targets.isEmpty()) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         buf.c(2);
         buf.c(vfx.getPivotId());
         buf.c(vfx.getModelId());
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(buf));
      }
   }

   private Packets.PacketSupplier pivotSpawn(VFXDisplayRenderer.VFXModel vfx) {
      return NetworkUtils.createPivotSpawn(vfx.getPivotId(), vfx.getPivotUuid(), vfx.getOrigin().get());
   }

   private PacketPlayOutEntityMetadata pivotData(VFXDisplayRenderer.VFXModel vfx) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(vfx.getPivotId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutSpawnEntity vfxSpawn(VFXDisplayRenderer.VFXModel vfx) {
      Vector3f location = vfx.getOrigin().get();
      return new PacketPlayOutSpawnEntity(vfx.getModelId(), vfx.getModelUuid(), location.x, location.y, location.z, 0.0F, 0.0F, EntityTypes.af, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata vfxData(VFXDisplayRenderer.VFXModel vfx, boolean spawn) {
      if (!spawn && !vfx.isModelDirty()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(vfx.getModelId());
      if (spawn) {
         EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
         EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
         EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, 1);
         EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, 4096.0F);
         EntityUtils.writeData(buf, 24, DataWatcherRegistry.a, (byte)ItemDisplayTransform.HEAD.ordinal());
      } else if (vfx.isModelDirty()) {
         EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
      }

      vfx.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), spawn);
      vfx.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.A, vector3f), spawn);
      vfx.getLeftRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 13, DataWatcherRegistry.B, quaternionf), spawn);
      vfx.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.h, CraftItemStack.asNMSCopy(itemStack)), spawn);
      vfx.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, val), spawn);
      buf.k(255);
      vfx.clearModelDirty();
      return new PacketPlayOutEntityMetadata(buf);
   }

   private Object teleport(VFXDisplayRenderer.VFXModel vfx) {
      return !vfx.getOrigin().isDirty() ? null : NetworkUtils.createPivotMove(vfx.getPivotId(), vfx.getPositionCodec(), vfx.getOrigin().get());
   }

   private PacketPlayOutMount mount(VFXDisplayRenderer.VFXModel vfx) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(vfx.getPivotId());
      buf.c(1);
      buf.c(vfx.getModelId());
      return new PacketPlayOutMount(buf);
   }
}
