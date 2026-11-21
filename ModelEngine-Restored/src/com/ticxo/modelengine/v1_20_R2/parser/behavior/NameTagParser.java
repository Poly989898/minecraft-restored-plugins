package com.ticxo.modelengine.v1_20_R2.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.v1_20_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R2.network.utils.Packets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class NameTagParser implements BehaviorRendererParser<NameTagRenderer> {
   private float viewRange = 4096.0F;

   public void sendToClients(NameTagRenderer renderer) {
      this.viewRange = renderer.getActiveModel().getViewRange();
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
   }

   public void destroy(NameTagRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, NameTagRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add(this.pivotData(nameTag, true));
            set.add(this.tagSpawn(nameTag));
            set.add(this.tagData(nameTag, true));
            set.add(this.pivotMount(nameTag));
         }

         for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add(this.pivotData(nameTag, true));
            set.add(this.tagSpawn(nameTag));
            set.add(this.tagData(nameTag, true));
            set.add(this.pivotMount(nameTag));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void update(Set<UUID> targets, NameTagRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add(this.tagData(nameTag, false));
            if (nameTag.getPosition().isDirty()) {
               set.add(this.pivotMove(nameTag));
               nameTag.getPosition().clearDirty();
            }
         }

         for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add(this.pivotData(nameTag, true));
            set.add(this.tagSpawn(nameTag));
            set.add(this.tagData(nameTag, true));
            set.add(this.pivotMount(nameTag));
         }

         Map<String, NameTagRenderer.NameTag> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(destroy.size() * 2);
            destroy.forEach((s, mount) -> {
               buf.c(mount.getPivotId());
               buf.c(mount.getTagId());
            });
            set.add(new PacketPlayOutEntityDestroy(buf));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, NameTagRenderer renderer) {
      if (!targets.isEmpty()) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         Collection<NameTagRenderer.NameTag> nameTags = renderer.getRendered().values();
         buf.c(nameTags.size() * 2);
         nameTags.forEach(nameTag -> {
            buf.c(nameTag.getPivotId());
            buf.c(nameTag.getTagId());
         });
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(buf));
      }
   }

   private Packets.PacketSupplier pivotSpawn(NameTagRenderer.NameTag renderer) {
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
   }

   private PacketPlayOutEntityMetadata pivotData(NameTagRenderer.NameTag renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getPivotId());
      if (spawn) {
         EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
         EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      }

      buf.k(255);
      renderer.clearDirty();
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutSpawnEntity tagSpawn(NameTagRenderer.NameTag renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new PacketPlayOutSpawnEntity(renderer.getTagId(), renderer.getTagUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.aX, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata tagData(NameTagRenderer.NameTag renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getTagId());
      renderer.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, flag ? this.viewRange : 0.0F), spawn);
      renderer.getTranslate().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), spawn);
      renderer.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.A, vector3f), spawn);
      renderer.getTextOpacity().ifDirty(flag -> EntityUtils.writeData(buf, 26, DataWatcherRegistry.a, flag), spawn);
      renderer.getBackgroundColor().ifDirty(flag -> EntityUtils.writeData(buf, 25, DataWatcherRegistry.b, flag), spawn);
      renderer.getLineWidth().ifDirty(flag -> EntityUtils.writeData(buf, 24, DataWatcherRegistry.b, flag), spawn);
      renderer.getStyle().ifDirty(flags -> EntityUtils.writeData(buf, 27, DataWatcherRegistry.a, flags), spawn);
      renderer.getBillboard().ifDirty(flag -> {
         byte value = switch (flag) {
            case FIXED -> 0;
            case VERTICAL -> 1;
            case HORIZONTAL -> 2;
            case CENTER -> 3;
            default -> throw new IncompatibleClassChangeError();
         };
         EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, value);
      }, spawn);
      renderer.getJsonString().ifDirty(jsonString -> {
         if (jsonString == null) {
            EntityUtils.writeData(buf, 23, DataWatcherRegistry.f, IChatBaseComponent.h());
         } else {
            EntityUtils.writeData(buf, 23, DataWatcherRegistry.f, ChatSerializer.a(jsonString));
         }
      }, spawn);
      renderer.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, val), spawn);
      buf.k(255);
      renderer.clearDirty();
      return new PacketPlayOutEntityMetadata(buf);
   }

   private Object pivotMove(NameTagRenderer.NameTag renderer) {
      return NetworkUtils.createPivotMove(renderer.getPivotId(), renderer.getPositionCodec(), renderer.getPosition().get());
   }

   private PacketPlayOutMount pivotMount(NameTagRenderer.NameTag renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getPivotId());
      buf.c(1);
      buf.c(renderer.getTagId());
      return new PacketPlayOutMount(buf);
   }
}
