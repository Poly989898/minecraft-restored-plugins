package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

@MythicMechanic(
   author = "Ashijin",
   version = "4.8",
   name = "stun",
   description = "Applies an aura that stuns the target entity",
   deprecated = true
)
public class StunMechanic extends Aura implements ITargetedEntitySkill {
   private boolean stopAI;
   private boolean stopGravity;
   private boolean freezeFacing;
   private boolean knockbackResist;

   public StunMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.auraGroup = Optional.ofNullable(mlc.getPlaceholderString(new String[]{"auragroup", "auratype", "group", "type", "g"}, "#stun"));
      this.stopAI = mlc.getBoolean(new String[]{"stopai", "ai"}, false);
      this.stopGravity = mlc.getBoolean(new String[]{"gravity"}, false);
      this.freezeFacing = mlc.getBoolean(new String[]{"facing", "face", "f"}, false);
      this.knockbackResist = mlc.getBoolean(new String[]{"noknockback", "nokb", "kb"}, false);
      this.interval = PlaceholderDuration.of("1", DurationUnit.TICKS);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new StunMechanic.StunMechanicTracker(target, data);
      return SkillResult.SUCCESS;
   }

   protected class StunMechanicTracker extends Aura.AuraTracker {
      private double x;
      private double y;
      private double z;
      private float pitch;
      private float yaw;
      private boolean hadAI = false;
      private boolean hadGravity = false;
      private double kbRes;

      public StunMechanicTracker(AbstractEntity target, SkillMetadata data) {
         super(target, data);
         AbstractLocation loc = target.getLocation();
         this.x = loc.getX();
         this.y = loc.getY();
         this.z = loc.getZ();
         this.pitch = loc.getPitch();
         this.yaw = loc.getYaw();
         this.start();
      }

      @Override
      public void auraStart() {
         if (StunMechanic.this.stopAI && this.entity.get().hasAI()) {
            this.hadAI = true;
            this.entity.get().setAI(false);
         }

         if (StunMechanic.this.stopGravity && this.entity.get().hasGravity()) {
            this.hadGravity = true;
            this.entity.get().setGravity(false);
         }

         if (StunMechanic.this.knockbackResist && this.entity.get().isLiving()) {
            AttributeInstance attr = ((LivingEntity)this.entity.get().getBukkitEntity()).getAttribute(BukkitAttribute.getAttribute("KNOCKBACK_RESISTANCE"));
            if (attr != null) {
               this.kbRes = attr.getBaseValue();
               attr.setBaseValue(10.0);
            }
         }

         this.executeAuraSkill(StunMechanic.this.onStartSkill, this.skillMetadata);
      }

      @Override
      public void auraTick() {
         AbstractEntity ent = this.entity.get();
         if (StunMechanic.this.freezeFacing) {
            AbstractLocation loc = ent.getLocation();
            this.pitch = loc.getPitch();
            this.yaw = loc.getYaw();
         }

         StunMechanic.this.getPlugin()
            .getVolatileCodeHandler()
            .getEntityHandler()
            .setLocation(ent, this.x, this.y, this.z, this.yaw, this.pitch, !StunMechanic.this.freezeFacing, StunMechanic.this.stopGravity);
      }

      @Override
      public void auraStop() {
         if (StunMechanic.this.stopAI && this.hadAI) {
            this.entity.get().setAI(true);
         }

         if (StunMechanic.this.stopGravity && this.hadGravity) {
            this.entity.get().setGravity(true);
         }

         if (StunMechanic.this.knockbackResist && this.entity.get().isLiving()) {
            AttributeInstance attr = ((LivingEntity)this.entity.get().getBukkitEntity()).getAttribute(BukkitAttribute.getAttribute("KNOCKBACK_RESISTANCE"));
            if (attr != null) {
               attr.setBaseValue(this.kbRes);
            }
         }

         this.executeAuraSkill(StunMechanic.this.onEndSkill, this.skillMetadata);
      }
   }
}
