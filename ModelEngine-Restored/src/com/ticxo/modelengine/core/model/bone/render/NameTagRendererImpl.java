package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.entity.Display.Billboard;
import org.joml.Vector3f;

public class NameTagRendererImpl extends AbstractBehaviorRenderer implements NameTagRenderer {
   @Getter
   private final Map<String, NameTagRenderer.NameTag> spawnQueue = new HashMap<>();
   @Getter
   private final Map<String, NameTagRenderer.NameTag> rendered = new HashMap<>();
   @Getter
   private final Map<String, NameTagRenderer.NameTag> destroyQueue = new HashMap<>();
   private boolean initialized;
   private BehaviorRendererParser<NameTagRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public NameTagRendererImpl(ActiveModel activeModel) {
      super(activeModel);
   }

   @Override
   public void initialize() {
      for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
         String boneId = boneEntry.getKey();
         ModelBone modelBone = boneEntry.getValue();
         this.create(boneId, modelBone);
      }

      this.initialized = true;
   }

   private void create(String boneId, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG);
      if (!maybeData.isEmpty()) {
         BoneBehavior nameTagData = (BoneBehavior)maybeData.get();
         NameTagRendererImpl.NameTagImpl nameTag = new NameTagRendererImpl.NameTagImpl(
            this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID(), this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID()
         );
         nameTag.position.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getLocation());
         nameTag.jsonString.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getJsonString());
         nameTag.visibility.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).isVisible());
         nameTag.backgroundColor
            .set(
               ((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).isUseDefaultBackgroundColor()
                  ? 1073741824
                  : ((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getBackgroundColor()
            );
         nameTag.billboard.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getBillboard());
         nameTag.textOpacity.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getTextOpacity());
         nameTag.lineWidth.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getLineWidth());
         nameTag.translate.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getPosition());
         nameTag.scale.set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getScale());
         nameTag.getStyle().set(this.getStyle((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData));
         nameTag.getBrightness().set(modelBone.getBrightness());
         this.spawnQueue.put(boneId, nameTag);
         this.destroyQueue.remove(boneId);
      }
   }

   @Override
   public void readBoneData() {
      if (this.initialized) {
         this.destroyQueue.putAll(this.rendered);

         for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            NameTagRenderer.NameTag renderer = this.getQueued(boneId);
            if (renderer != null) {
               this.read(boneId, renderer, modelBone);
            } else {
               this.create(boneId, modelBone);
            }
         }
      }
   }

   private void read(String boneId, NameTagRenderer.NameTag nameTag, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG);
      maybeData.ifPresent(
         nameTagData -> {
            nameTag.getPosition().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getLocation());
            nameTag.getJsonString().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getJsonString());
            nameTag.getVisibility().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).isVisible());
            nameTag.getBillboard().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getBillboard());
            nameTag.getBackgroundColor()
               .set(
                  ((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).isUseDefaultBackgroundColor()
                     ? 1073741824
                     : ((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getBackgroundColor()
               );
            nameTag.getTextOpacity().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getTextOpacity());
            nameTag.getLineWidth().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getLineWidth());
            nameTag.getTranslate().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getPosition());
            nameTag.getScale().set(((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData).getScale());
            nameTag.getStyle().set(this.getStyle((com.ticxo.modelengine.api.model.bone.type.NameTag)nameTagData));
            nameTag.getBrightness().set(modelBone.getBrightness());
            this.destroyQueue.remove(boneId);
         }
      );
   }

   private byte getStyle(com.ticxo.modelengine.api.model.bone.type.NameTag nameTagData) {
      byte style = 0;
      style = TMath.setBit(style, 0, nameTagData.isShadow());
      style = TMath.setBit(style, 1, nameTagData.isSeeThrough());
      style = TMath.setBit(style, 2, nameTagData.isUseDefaultBackgroundColor());

      return switch (nameTagData.getAlignment()) {
         case CENTER -> style;
         case LEFT -> (byte)(style | 8);
         case RIGHT -> (byte)(style | 16);
         default -> throw new IncompatibleClassChangeError();
      };
   }

   @Override
   public void sendToClient(RenderParsers parsers) {
      if (this.initialized) {
         this.destroyQueue.keySet().forEach(this.rendered::remove);
         this.parser(parsers).sendToClients(this);
         this.rendered.putAll(this.spawnQueue);
         this.spawnQueue.clear();
         this.destroyQueue.clear();
      }
   }

   @Override
   public void destroy(RenderParsers parsers) {
      if (this.initialized) {
         parsers.getBehaviorParser(this).destroy(this);
      }
   }

   private BehaviorRendererParser<NameTagRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getBehaviorParser(this);
      }

      return this.cachedParser;
   }

   @Getter
   public static class NameTagImpl implements NameTagRenderer.NameTag {
      private final int pivotId;
      private final UUID pivotUuid;
      private final int tagId;
      private final UUID tagUuid;
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<String> jsonString = new DataTracker<>();
      private final DataTracker<Boolean> visibility = new DataTracker<>(true);
      private final DataTracker<Integer> backgroundColor = new DataTracker<>(1073741824);
      private final DataTracker<Billboard> billboard = new DataTracker<>(Billboard.CENTER);
      private final DataTracker<Byte> textOpacity = new DataTracker<>((byte)0);
      private final DataTracker<Integer> lineWidth = new DataTracker<>(0);
      private final DataTracker<Vector3f> translate = new DataTracker<>(new Vector3f(0.0F));
      private final DataTracker<Vector3f> scale = new DataTracker<>(new Vector3f(1.0F));
      private final DataTracker<Byte> style = new DataTracker<>((byte)0);
      private final DataTracker<Integer> brightness = new DataTracker<>(0);
      private final PositionCodec positionCodec = new PositionCodec();

      @Generated
      public NameTagImpl(int pivotId, UUID pivotUuid, int tagId, UUID tagUuid) {
         this.pivotId = pivotId;
         this.pivotUuid = pivotUuid;
         this.tagId = tagId;
         this.tagUuid = tagUuid;
      }
   }
}
