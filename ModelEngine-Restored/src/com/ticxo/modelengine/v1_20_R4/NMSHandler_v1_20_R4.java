package com.ticxo.modelengine.v1_20_R4;

import com.mojang.math.MatrixUtil;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import com.ticxo.modelengine.v1_20_R4.entity.EntityContainer;
import com.ticxo.modelengine.v1_20_R4.entity.EntityHandlerImpl;
import com.ticxo.modelengine.v1_20_R4.network.NetworkHandlerImpl;
import com.ticxo.modelengine.v1_20_R4.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R4.parser.behavior.LeashParser;
import com.ticxo.modelengine.v1_20_R4.parser.behavior.MountParser;
import com.ticxo.modelengine.v1_20_R4.parser.behavior.NameTagParser;
import com.ticxo.modelengine.v1_20_R4.parser.behavior.SegmentParser;
import com.ticxo.modelengine.v1_20_R4.parser.behavior.SubHitboxParser;
import com.ticxo.modelengine.v1_20_R4.parser.model.DisplayParser;
import com.ticxo.modelengine.v1_20_R4.parser.vfx.VFXDisplayParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class NMSHandler_v1_20_R4 implements NMSHandler {
   @Getter
   private final EntityHandler entityHandler = new EntityHandlerImpl();
   @Getter
   private final NetworkHandler networkHandler = new NetworkHandlerImpl();
   @Getter
   private final RenderParsers globalParsers = this.createParsers();
   private final VFXDisplayParser vfxDisplayParser = new VFXDisplayParser();

   @Override
   public RenderParsers createParsers() {
      RenderParsers parsers = new RenderParsers();
      parsers.registerModelParser(renderer -> renderer instanceof DisplayRenderer, DisplayParser::new);
      parsers.registerBehaviorParser(renderer -> renderer instanceof MountRenderer, MountParser::new);
      parsers.registerBehaviorParser(renderer -> renderer instanceof LeashRenderer, LeashParser::new);
      parsers.registerBehaviorParser(renderer -> renderer instanceof NameTagRenderer, NameTagParser::new);
      parsers.registerBehaviorParser(renderer -> renderer instanceof SubHitboxRenderer, SubHitboxParser::new);
      parsers.registerBehaviorParser(renderer -> renderer instanceof SegmentRenderer, SegmentParser::new);
      return parsers;
   }

   @Override
   public void sendPivotOverrides() {
      PivotOverrideRegistry registry = ModelEngineAPI.getPivotOverrideRegistry();

      for (PivotOverride override : registry.getAll()) {
         ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(override.getUuid());
         BukkitEntityData data = modeledEntity != null && modeledEntity.getBase().getData() instanceof BukkitEntityData d ? d : null;
         boolean passengersDirty = override.getPassengers().isDirty();
         Set<UUID> lateTrackers = data != null ? data.getStartTracking() : Set.of();
         if (passengersDirty || !lateTrackers.isEmpty()) {
            FriendlyByteBuf byteBuf = NetworkUtils.createByteBuf(buf -> {
               buf.writeVarInt(override.getId());
               Collection<Integer> list = override.getPassengers().get();
               ArrayList<Integer> allPassengers = new ArrayList<>(list);
               if (modeledEntity != null) {
                  List<Entity> basePassengers = modeledEntity.getBase().getPassengers();
                  int[] snapshot = new int[basePassengers.size()];
                  int i = 0;

                  for (Entity passenger : basePassengers) {
                     int pid = passenger.getEntityId();
                     snapshot[i++] = pid;
                     if (!allPassengers.contains(pid)) {
                        allPassengers.add(pid);
                     }
                  }

                  override.setBasePassengerSnapshot(snapshot);
               } else {
                  for (int pid : override.getBasePassengerSnapshot()) {
                     if (!allPassengers.contains(pid)) {
                        allPassengers.add(pid);
                     }
                  }
               }

               buf.writeVarInt(allPassengers.size());
               allPassengers.forEach(buf::writeVarInt);
            });
            Set<UUID> recipients;
            if (data == null) {
               recipients = ServerInfo.getOnlinePlayers();
            } else if (passengersDirty) {
               recipients = new HashSet<>(data.getMutableTracking().keySet());
               recipients.addAll(lateTrackers);
            } else {
               recipients = lateTrackers;
            }

            NetworkUtils.send(recipients, NetworkUtils.create(ClientboundSetPassengersPacket.class, byteBuf));
            override.getPassengers().clearDirty();
         }
      }
   }

   @Override
   public <T extends ModelRenderer> ModelRendererParser<T> getModelRendererParser(T renderer) {
      return this.globalParsers.getModelParser(renderer);
   }

   @Override
   public <T extends BehaviorRenderer> BehaviorRendererParser<T> getBehaviorRendererParser(T renderer) {
      return this.globalParsers.getBehaviorParser(renderer);
   }

   @Override
   public <T extends VFXRenderer> VFXRendererParser<T> getVFXRendererParser(T renderer) {
      return renderer instanceof VFXDisplayRenderer ? (VFXRendererParser<T>)this.vfxDisplayParser : null;
   }

   @Override
   public void sendMountPassengerUpdate(Set<UUID> targets, int mountId, Collection<Integer> passengerIds) {
      ClientboundSetPassengersPacket packet = new ClientboundSetPassengersPacket(EntityContainer.of(mountId, passengerIds));

      for (UUID target : targets) {
         ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlushNow(packet));
      }
   }

   @Override
   public Transformation decompose(Matrix4f transform) {
      float f = 1.0F / transform.m33();
      Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose(new Matrix3f(transform).scale(f));
      return new Transformation(
         transform.getTranslation(new Vector3f()).mul(f),
         new Quaternionf((Quaternionfc)triple.getLeft()),
         new Vector3f((Vector3fc)triple.getMiddle()),
         new Quaternionf((Quaternionfc)triple.getRight())
      );
   }
}
