package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.Collection;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TriggeredSkill implements IParentSkill {
   private SkillMetadata data;
   private boolean sync = false;
   private boolean cancel = false;

   public TriggeredSkill(SkillMetadata data) {
      this.data = data;
      this.sync = !data.getIsAsync();
      this.data.setCallingEvent(this);
   }

   @Deprecated
   public TriggeredSkill(SkillTrigger cause, ActiveMob am, AbstractEntity trigger) {
      this(cause, am, trigger, false, null);
   }

   @Deprecated
   public TriggeredSkill(SkillTrigger cause, ActiveMob am, AbstractEntity trigger, boolean sync) {
      this(cause, am, trigger, sync, null);
   }

   @Deprecated
   public TriggeredSkill(SkillTrigger cause, ActiveMob am, AbstractEntity trigger, boolean sync, Consumer<SkillMetadata> transformer) {
      this(cause, am, null, trigger, sync, transformer);
   }

   public TriggeredSkill(SkillTrigger cause, ActiveMob am, AbstractLocation origin, AbstractEntity trigger, boolean sync, Consumer<SkillMetadata> transformer) {
      this.data = new SkillMetadataImpl(cause, am, trigger);
      this.data.setCallingEvent(this);
      if (sync) {
         this.data.setIsAsync(false);
      } else {
         this.data.setIsAsync(true);
      }

      if (origin != null) {
         this.data.setOrigin(origin);
      }

      if (this.data.getTrigger() != null) {
         am.setLastAggroCause(this.data.getTrigger());
         this.data.setEntityTarget(trigger);
      }

      this.data.setPower(am.getPower());
      if (transformer != null) {
         transformer.accept(this.data);
      }

      MythicLogger.debug(MythicLogger.DebugLevel.TRIGGER, "Skill Trigger {0} fired for ActiveMob {1} (uuid: {2})", cause, am.getDisplayName(), am.getUniqueId());
      am.getType().executeSkills(this.data.getCause(), this.data);
   }

   @Deprecated
   public TriggeredSkill(SkillTrigger cause, SkillCaster am, AbstractLocation origin, AbstractEntity trigger, Collection<SkillMechanic> mechanics, boolean sync) {
      this(cause, am, origin, trigger, mechanics, sync, null);
   }

   @Deprecated
   public TriggeredSkill(
      SkillTrigger cause,
      SkillCaster caster,
      AbstractLocation origin,
      AbstractEntity trigger,
      Collection<SkillMechanic> mechanics,
      boolean sync,
      Consumer<SkillMetadata> transformer
   ) {
      if (caster == null) {
         throw new IllegalArgumentException("Skill caster cannot be null");
      } else {
         this.data = new SkillMetadataImpl(cause, caster, trigger);
         this.data.setCallingEvent(this);
         if (sync) {
            this.data.setIsAsync(false);
         } else {
            this.data.setIsAsync(true);
         }

         if (origin != null) {
            this.data.setOrigin(origin);
         }

         if (this.data.getTrigger() != null) {
            this.data.setEntityTarget(trigger);
         }

         this.data.setPower(caster.getPower());
         if (transformer != null) {
            transformer.accept(this.data);
         }

         if (this.data.getIsAsync()) {
            for (SkillMechanic ms : mechanics) {
               if (!ms.getRunAsync() && ms.isUsableFromCaster(this.data)) {
                  ms.execute(this.data.deepClone().setIsAsync(false));
               }
            }

            Schedulers.async().run(() -> {
               for (SkillMechanic msx : mechanics) {
                  if (msx.getRunAsync() && msx.isUsableFromCaster(this.data)) {
                     msx.execute(this.data);
                  }
               }
            });
         } else {
            for (SkillMechanic msx : mechanics) {
               if (msx.isUsableFromCaster(this.data)) {
                  msx.execute(this.data);
               }
            }
         }
      }
   }

   @Override
   public void setCancelled() {
      this.cancel = true;
   }

   @Override
   public boolean getCancelled() {
      return this.cancel;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TriggeredSkill other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isSync() != other.isSync()) {
         return false;
      } else if (this.isCancel() != other.isCancel()) {
         return false;
      } else {
         Object this$data = this.getData();
         Object other$data = other.getData();
         return this$data == null ? other$data == null : this$data.equals(other$data);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof TriggeredSkill;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isSync() ? 79 : 97);
      result = result * 59 + (this.isCancel() ? 79 : 97);
      Object $data = this.getData();
      return result * 59 + ($data == null ? 43 : $data.hashCode());
   }
}
