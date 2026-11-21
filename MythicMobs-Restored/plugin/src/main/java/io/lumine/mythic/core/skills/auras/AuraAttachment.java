package io.lumine.mythic.core.skills.auras;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillMechanic;
import lombok.Generated;

public abstract class AuraAttachment {
   private final SkillMechanic projectileMechanic;
   private final MythicLineConfig config;

   public AuraAttachment(AuraAttachmentType type, SkillMechanic projectile, MythicLineConfig config) {
      this.projectileMechanic = projectile;
      this.config = config;
   }

   public MythicBukkit getPlugin() {
      return this.projectileMechanic == null ? MythicBukkit.inst() : this.projectileMechanic.getPlugin();
   }

   public abstract AuraAttachment.AttachmentTracker create(Aura.AuraTracker auraTracker, AbstractEntity abstractEntity);

   public abstract boolean isVirtual();

   @Generated
   public SkillMechanic getProjectileMechanic() {
      return this.projectileMechanic;
   }

   @Generated
   public MythicLineConfig getConfig() {
      return this.config;
   }

   public abstract class AttachmentTracker {
      private final Aura.AuraTracker aura;
      private final AbstractEntity target;

      public boolean isVirtual() {
         return AuraAttachment.this.isVirtual();
      }

      public abstract void spawn(AbstractEntity abstractEntity);

      public abstract void tick();

      public abstract void despawn();

      @Generated
      public AttachmentTracker(final Aura.AuraTracker aura, final AbstractEntity target) {
         this.aura = aura;
         this.target = target;
      }

      @Generated
      public Aura.AuraTracker getAura() {
         return this.aura;
      }

      @Generated
      public AbstractEntity getTarget() {
         return this.target;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AuraAttachment.AttachmentTracker other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$aura = this.getAura();
            Object other$aura = other.getAura();
            if (this$aura == null ? other$aura == null : this$aura.equals(other$aura)) {
               Object this$target = this.getTarget();
               Object other$target = other.getTarget();
               return this$target == null ? other$target == null : this$target.equals(other$target);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AuraAttachment.AttachmentTracker;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $aura = this.getAura();
         result = result * 59 + ($aura == null ? 43 : $aura.hashCode());
         Object $target = this.getTarget();
         return result * 59 + ($target == null ? 43 : $target.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AuraAttachment.AttachmentTracker(aura=" + this.getAura() + ", target=" + this.getTarget() + ")";
      }
   }
}
