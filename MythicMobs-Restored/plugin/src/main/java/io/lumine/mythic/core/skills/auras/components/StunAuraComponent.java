package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class StunAuraComponent implements AuraComponent {
   private final AuraMechanic mechanic;
   private final Aura.AuraTracker tracker;
   private final boolean stopAI;
   private final boolean stopGravity;
   private final boolean freezeFacing;
   private final boolean knockbackResist;
   private double x;
   private double y;
   private double z;
   private float pitch;
   private float yaw;
   private boolean hadAI;
   private boolean hadGravity;
   private double kbRes;
   private Task task;

   public StunAuraComponent(AuraMechanic mechanic, Aura.AuraTracker tracker, boolean stopAI, boolean stopGravity, boolean freezeFacing, boolean knockbackResist) {
      this.mechanic = mechanic;
      this.tracker = tracker;
      this.stopAI = stopAI;
      this.stopGravity = stopGravity;
      this.freezeFacing = freezeFacing;
      this.knockbackResist = knockbackResist;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         AbstractEntity entity = maybeEntity.get();
         AbstractLocation loc = entity.getLocation();
         this.x = loc.getX();
         this.y = loc.getY();
         this.z = loc.getZ();
         this.pitch = loc.getPitch();
         this.yaw = loc.getYaw();
         if (this.stopAI && entity.hasAI()) {
            this.hadAI = true;
            entity.setAI(false);
         }

         if (this.stopGravity && entity.hasGravity()) {
            this.hadGravity = true;
            entity.setGravity(false);
         }

         if (this.knockbackResist && entity.isLiving()) {
            AttributeInstance attr = ((LivingEntity)entity.getBukkitEntity()).getAttribute(BukkitAttribute.getAttribute("KNOCKBACK_RESISTANCE"));
            if (attr != null) {
               this.kbRes = attr.getBaseValue();
               attr.setBaseValue(10.0);
            }
         }

         this.task = Schedulers.of(entity.getBukkitEntity()).runRepeating(this::tickStun, 0L, 1L);
         this.tracker.registerAuraComponent(this.task);
      }
   }

   @Override
   public void auraTick() {
   }

   private void tickStun() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         AbstractEntity entity = maybeEntity.get();
         if (this.freezeFacing) {
            AbstractLocation loc = entity.getLocation();
            this.pitch = loc.getPitch();
            this.yaw = loc.getYaw();
         }

         this.mechanic
            .getPlugin()
            .getVolatileCodeHandler()
            .getEntityHandler()
            .setLocation(entity, this.x, this.y, this.z, this.yaw, this.pitch, !this.freezeFacing, this.stopGravity);
      }
   }

   @Override
   public void auraStop() {
      if (this.task != null) {
         this.task.terminate();
         this.task = null;
      }

      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         AbstractEntity entity = maybeEntity.get();
         if (this.stopAI && this.hadAI) {
            entity.setAI(true);
         }

         if (this.stopGravity && this.hadGravity) {
            entity.setGravity(true);
         }

         if (this.knockbackResist && entity.isLiving()) {
            AttributeInstance attr = ((LivingEntity)entity.getBukkitEntity()).getAttribute(BukkitAttribute.getAttribute("KNOCKBACK_RESISTANCE"));
            if (attr != null) {
               attr.setBaseValue(this.kbRes);
            }
         }
      }
   }

   @MythicAuraComponent(
      name = "stun",
      aliases = {"stunned"},
      description = "Stuns the target while the aura is active"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final boolean stopAI;
      private final boolean stopGravity;
      private final boolean freezeFacing;
      private final boolean knockbackResist;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.mechanic.setThreadSafetyLevel(ThreadSafetyLevel.SYNC_ONLY);
         this.mechanic.addAuraTag("stun");
         this.stopAI = mlc.getBoolean(new String[]{"stopai", "ai"}, false);
         this.stopGravity = mlc.getBoolean(new String[]{"gravity", "g"}, false);
         this.freezeFacing = mlc.getBoolean(new String[]{"facing", "face", "f"}, false);
         this.knockbackResist = mlc.getBoolean(new String[]{"noknockback", "nokb", "kb"}, false);
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new StunAuraComponent(this.mechanic, tracker, this.stopAI, this.stopGravity, this.freezeFacing, this.knockbackResist);
      }
   }
}
