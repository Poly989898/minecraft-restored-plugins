package io.lumine.mythic.core.skills.auras.attachment;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.auras.AuraAttachment;
import io.lumine.mythic.core.skills.auras.AuraAttachmentType;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;
import java.util.UUID;

public class MEGAttachment extends AuraAttachment {
   @MythicField(
      name = "attachmentModel",
      description = "The MEG model to use for the attachment."
   )
   protected PlaceholderString attachmentModel = null;
   @MythicField(
      name = "attachmentOffset",
      description = "Relative location specifying the model offset."
   )
   protected Orient offset;
   @MythicField(
      name = "attachmentState",
      description = "The MEG model's state to use for the attachment."
   )
   protected PlaceholderString attachmentState = null;
   @MythicField(
      name = "attachmentColor",
      description = "The color of the attachment type"
   )
   protected PlaceholderString attachmentColor = null;
   protected PlaceholderDouble attachmentScale;
   @MythicField(
      name = "attachmentEnchanted",
      aliases = {"enchanted"},
      description = "Whether the item should be enchanted or not",
      defValue = "false"
   )
   protected boolean attachmentEnchanted;
   @MythicField(
      name = "attachmentGlowing",
      aliases = {"glowing"},
      description = "Whether the item should be glowing or not",
      defValue = "false"
   )
   protected boolean attachmentGlowing;
   @MythicField(
      name = "bulletBrightness",
      description = "The brightness of the bullet"
   )
   protected PlaceholderInt bulletBrightness = null;
   @MythicField(
      name = "attachmentCulling",
      aliases = {"culling"},
      description = "",
      defValue = "true"
   )
   protected boolean attachmentCulling;
   @MythicField(
      name = "attachmentViewRadius",
      aliases = {"viewradius"},
      description = "",
      defValue = ""
   )
   protected int attachmentViewRadius;
   @MythicField(
      name = "attachmentGlowColor",
      description = "The color of the attachment glow"
   )
   protected PlaceholderString attachmentGlowColor = null;

   public MEGAttachment(AuraAttachmentType type, SkillMechanic projectile, MythicLineConfig config) {
      super(type, projectile, config);
      this.attachmentModel = config.getPlaceholderString(new String[]{"attachmentmodel", "attachmodel", "model"}, null);
      this.attachmentState = config.getPlaceholderString(new String[]{"attachmentstate", "attachstate", "state"}, null);
      this.attachmentColor = config.getPlaceholderString(new String[]{"attachmentcolor", "attachcolor"}, null);
      this.attachmentScale = config.getPlaceholderDouble(new String[]{"attachmentscale", "attachscale"}, 1.0);
      this.attachmentViewRadius = config.getInteger(new String[]{"attachmentViewRadius", "attackviewradius"}, -1);
      this.attachmentEnchanted = config.getBoolean(new String[]{"attachmentEnchanted", "attachEnchanted", "enchanted"}, false);
      this.attachmentGlowing = config.getBoolean(new String[]{"attachmentGlowing", "attachGlowing", "glowing"}, false);
      this.attachmentCulling = config.getBoolean(new String[]{"attachmentCulling", "attachCulling", "culling"}, true);
      this.attachmentGlowColor = config.getPlaceholderString(new String[]{"attachmentglowcolor", "attachglowcolor"}, null);
      this.bulletBrightness = config.getPlaceholderInteger(new String[]{"bulletbrightness"}, (String)null);
      String offset = config.getString(new String[]{"attachmentoffset", "attachoffset"}, null);
      if (offset == null) {
         this.offset = Orient.of(0.0, 0.0, 0.0, 0.0F, 0.0F);
      } else {
         try {
            String[] split = offset.split(",");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            float yaw = split.length > 3 ? Float.parseFloat(split[3]) : 0.0F;
            float pitch = split.length > 4 ? Float.parseFloat(split[4]) : 0.0F;
            this.offset = Orient.of(x, y, z, yaw, pitch);
         } catch (Throwable e) {
            this.offset = Orient.of(0.0, 0.0, 0.0, 0.0F, 0.0F);
         }
      }

      this.getPlugin().getSkillManager().queueAfterLoad(() -> {
         Optional<AbstractModelEngineSupport> support = this.getPlugin().getCompatibility().getModelEngine();
         if (!support.isPresent()) {
            MythicLogger.errorMechanicConfig(projectile, config, "MEG Aura Attachments are not usable without ModelEngine");
         }
      });
   }

   @Override
   public AuraAttachment.AttachmentTracker create(Aura.AuraTracker aura, AbstractEntity target) {
      return new MEGAttachment.MEGAttachmentTracker(aura, target);
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   public class MEGAttachmentTracker extends AuraAttachment.AttachmentTracker {
      protected AbstractModelEngineSupport.MEGAttachment attachment;

      public MEGAttachmentTracker(Aura.AuraTracker aura, AbstractEntity target) {
         super(aura, target);
      }

      public UUID getEntityId() {
         return this.attachment.getEntityId();
      }

      @Override
      public void spawn(AbstractEntity target) {
         Optional<AbstractModelEngineSupport> support = MEGAttachment.this.getPlugin().getCompatibility().getModelEngine();
         if (!support.isPresent()) {
            MythicLogger.errorMechanicConfig(
               MEGAttachment.this.getProjectileMechanic(), MEGAttachment.this.getConfig(), "MEG Bullets are not usable without ModelEngine"
            );
         } else {
            String model = MEGAttachment.this.attachmentModel.get(this.getAura().getSkillMetadata());
            String state = MEGAttachment.this.attachmentState == null ? null : MEGAttachment.this.attachmentState.get(this.getAura().getSkillMetadata());
            double scale = MEGAttachment.this.attachmentScale.get(this.getAura().getSkillMetadata());
            String color = MEGAttachment.this.attachmentColor == null ? null : MEGAttachment.this.attachmentColor.get(this.getAura().getSkillMetadata());
            String glowColor = MEGAttachment.this.attachmentGlowColor == null
               ? null
               : MEGAttachment.this.attachmentGlowColor.get(this.getAura().getSkillMetadata());
            Integer brightness = MEGAttachment.this.bulletBrightness == null
               ? null
               : MEGAttachment.this.bulletBrightness.get(this.getAura().getSkillMetadata());
            AbstractModelEngineSupport.MEGAttachmentData data = new AbstractModelEngineSupport.MEGAttachmentData(
               model,
               MEGAttachment.this.offset,
               scale,
               color,
               MEGAttachment.this.attachmentEnchanted,
               MEGAttachment.this.attachmentGlowing,
               glowColor,
               state,
               AbstractModelEngineSupport.MEGAttachmentAnchor.BODY,
               brightness,
               true
            );
            this.attachment = support.get().createMEGAttachment(data, target);
            if (this.attachment == null) {
               MythicLogger.errorMechanicConfig(
                  MEGAttachment.this.getProjectileMechanic(), MEGAttachment.this.getConfig(), "MEG Bullet is an invalid model, or MEG is out of date"
               );
            } else {
               if (!MEGAttachment.this.attachmentCulling) {
                  this.attachment.disableCulling();
               }

               if (MEGAttachment.this.attachmentViewRadius > 0) {
                  this.attachment.setRenderRadius(MEGAttachment.this.attachmentViewRadius);
               }

               if (this.getAura().hasTerminated()) {
                  this.attachment.terminate();
               }
            }
         }
      }

      @Override
      public void tick() {
         if (this.attachment != null) {
            if (this.getAura().hasTerminated()) {
               this.despawn();
            }
         }
      }

      @Override
      public void despawn() {
         if (this.attachment != null) {
            this.attachment.terminate();
         }
      }
   }
}
