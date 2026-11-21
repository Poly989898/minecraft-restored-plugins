package com.ticxo.modelengine.core.vfx.render;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VFXDisplayRendererImpl implements VFXDisplayRenderer {
   private final VFX vfx;
   private final EntityHandler entityHandler;
   private final VFXRendererParser<VFXDisplayRenderer> parser;
   private VFXDisplayRendererImpl.VFXModelImpl vfxModel;
   @Getter @Setter
   private boolean respawnRequired;
   @Getter
   private boolean initialized;

   public VFXDisplayRendererImpl(VFX vfx) {
      this.vfx = vfx;
      this.entityHandler = ModelEngineAPI.getEntityHandler();
      this.parser = ModelEngineAPI.getNMSHandler().getVFXRendererParser(this);
   }

   @Override
   public VFX getVFX() {
      return this.vfx;
   }

   @Override
   public void initialize() {
      this.vfxModel = new VFXDisplayRendererImpl.VFXModelImpl(
         this.entityHandler.getNextEntityId(), UUID.randomUUID(), this.entityHandler.getNextEntityId(), UUID.randomUUID()
      );
      this.vfxModel.getOrigin().set(this.vfx.getOrigin().toVector3f());
      this.vfxModel.getPosition().set(this.calculatePosition());
      this.vfxModel.getLeftRotation().set(this.calculateRotation());
      this.vfxModel.getScale().set(this.vfx.getScale());
      this.vfxModel.getModel().set(this.vfx.isVisible() ? this.vfx.getModel() : null);
      this.vfxModel.getBrightness().set(this.vfx.getBrightness());
      this.initialized = true;
      this.respawnRequired = true;
   }

   @Override
   public void readVFXData() {
      if (this.initialized) {
         this.vfxModel.getOrigin().set(this.vfx.getOrigin().toVector3f());
         this.vfxModel.getPosition().set(this.calculatePosition());
         this.vfxModel.getLeftRotation().set(this.calculateRotation());
         this.vfxModel.getScale().set(this.vfx.getScale());
         this.vfxModel.getBrightness().set(this.vfx.getBrightness());
         if (!this.vfx.isVisible()) {
            this.vfxModel.getModel().set(null);
         } else {
            this.vfxModel.getModel().set(this.vfx.getModel());
            if (this.vfx.getModelTracker().isDirty()) {
               this.vfx.getModelTracker().clearDirty();
               this.vfxModel.getModel().markDirty();
            }
         }
      }
   }

   @Override
   public void sendToClient() {
      if (this.initialized) {
         this.parser.sendToClients(this);
      }
   }

   @Override
   public void destroy() {
      if (this.initialized) {
         this.parser.destroy(this);
      }
   }

   @Override
   public VFXDisplayRenderer.VFXModel getVFXModel() {
      return this.vfxModel;
   }

   private Vector3f calculatePosition() {
      float yaw = this.vfx.getYaw();
      float pitch = this.vfx.getPitch();
      Vector3f pos = this.vfx.getPosition();
      return new Vector3f(pos).rotateX(pitch * (float) (Math.PI / 180.0)).rotateY(-yaw * (float) (Math.PI / 180.0));
   }

   private Quaternionf calculateRotation() {
      float yaw = this.vfx.getYaw();
      float pitch = this.vfx.getPitch();
      Vector3f rot = this.vfx.getRotation();
      return new Quaternionf().rotateY((180.0F - yaw) * (float) (Math.PI / 180.0)).rotateX(-pitch * (float) (Math.PI / 180.0)).rotateZYX(rot.z, rot.y, rot.x);
   }

   @Getter
   @AllArgsConstructor
   public static class VFXModelImpl implements VFXDisplayRenderer.VFXModel {
      private final int pivotId;
      private final UUID pivotUuid;
      private final int modelId;
      private final UUID modelUuid;
      private final DataTracker<Vector3f> origin = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final PositionCodec positionCodec = new PositionCodec();
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<Quaternionf> leftRotation = new UpdateDataTracker<>(new Quaternionf(), Quaternionf::set);
      private final DataTracker<Vector3f> scale = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<ItemStack> model = new DataTracker<>();
      private final DataTracker<Integer> brightness = new DataTracker<>(-1);
   }
}
