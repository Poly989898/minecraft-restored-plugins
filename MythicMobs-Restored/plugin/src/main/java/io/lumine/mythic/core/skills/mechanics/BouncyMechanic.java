package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.core.utils.physics.CollisionHelper;
import io.lumine.mythic.core.utils.physics.PhysicsCollision;
import java.io.File;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "bouncy",
   description = "Applies an aura to the target that makes it bouncy"
)
public class BouncyMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onBounceSkill",
      aliases = {"onbounce", "ob"},
      description = "The meta-skill to execute on bounce"
   )
   protected Optional<Skill> onBounceSkill = Optional.empty();
   protected String onBounceSkillName;
   @MythicField(
      name = "cancelDamage",
      aliases = {"cd"},
      defValue = "false",
      version = "5.0",
      description = "Whether or not to cancel the event that triggered the aura"
   )
   protected boolean cancelDamage;

   public BouncyMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onBounceSkillName = mlc.getString(new String[]{"onbounceskill", "onbounce", "ob"});
      this.cancelDamage = mlc.getBoolean(new String[]{"cancelevent", "ce", "canceldamage", "cd"}, false);
      this.getManager().queueSecondPass(() -> {
         if (this.onBounceSkillName != null) {
            this.onBounceSkill = this.getManager().getSkill(file, this, this.onBounceSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new BouncyMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   public static class PhysicsSession implements Runnable, Terminable {
      private static final double AIR_RESISTANCE = 0.98;
      private static final double TERMINAL_VELOCITY = -0.99;
      private static final double GRAVITATIONAL_ACCELERATION = 0.05;
      private static final double MINIMUM_SPEED = 0.0075;
      private static final Vector RAW_BB_SHIFT_CORRECTION = new Vector(-0.125, 0.0, -0.125);
      private BouncyMechanic.Tracker tracker;
      private final Task runnable;
      private int tick = 0;
      private final Entity ball;
      private final World world;
      private Block lastHitBlock;
      private boolean applyGravity = true;
      private boolean doFriction = true;
      private long noGravityTicks = 0L;

      public PhysicsSession(BouncyMechanic.Tracker tracker, Entity entity, Vector initialVelocity) {
         this.tracker = tracker;
         this.ball = entity;
         this.world = this.ball.getWorld();
         this.runnable = Schedulers.of(entity).runRepeating(this, 0L, 1L);
      }

      @Override
      public void close() {
         this.runnable.terminate();
      }

      @Override
      public void run() {
         try {
            this.lastHitBlock = null;
            this.tick++;
            if (!this.ball.isValid()) {
               this.tracker.terminate();
               return;
            }

            if (this.ball.isInWater()) {
               this.tracker.terminate();
               return;
            }

            if (this.ball.getVelocity().length() < 0.0075) {
               this.tracker.terminate();
               return;
            }

            this.applyAirResistance();
            this.doCollisions();
         } catch (Exception e) {
            MythicLogger.log("An error occurred in bouncy physics");
            e.printStackTrace();
            this.tracker.terminate();
         }
      }

      private void applyAirResistance() {
      }

      private void doCollisions() {
         BoundingBox ballEntityBoundingBox = this.ball.getBoundingBox().clone();
         Vector velocity = this.ball.getVelocity().clone();

         for (PhysicsCollision collision : CollisionHelper.getCollisions(
            BukkitAdapter.adapt(velocity), ballEntityBoundingBox, BukkitAdapter.adapt(this.ball).getLocation()
         )) {
            switch (collision.getBlockFace()) {
               case NORTH:
                  if (velocity.getZ() > 0.0) {
                     velocity.setZ(velocity.getZ() * -0.995);
                     this.lastHitBlock = collision.getBlock();
                  }

                  this.tracker.doBouncySkill();
                  break;
               case EAST:
                  if (velocity.getX() < 0.0) {
                     velocity.setX(velocity.getX() * -0.995);
                     this.lastHitBlock = collision.getBlock();
                  }

                  this.tracker.doBouncySkill();
                  break;
               case SOUTH:
                  if (velocity.getZ() < 0.0) {
                     velocity.setZ(velocity.getZ() * -0.995);
                     this.lastHitBlock = collision.getBlock();
                  }

                  this.tracker.doBouncySkill();
                  break;
               case WEST:
                  if (velocity.getX() > 0.0) {
                     velocity.setX(velocity.getX() * -0.995);
                     this.lastHitBlock = collision.getBlock();
                  }

                  this.tracker.doBouncySkill();
                  break;
               case UP:
                  if (velocity.getY() < 0.0) {
                     Material hitBlockType = collision.getBlock().getType();
                     velocity.setY(velocity.getY() * -1.8);
                     this.lastHitBlock = collision.getBlock();
                     this.tracker.doBouncySkill();
                  }
                  break;
               case DOWN:
                  if (velocity.getY() > 0.0) {
                     velocity.setY(velocity.getY() * -0.95);
                     this.lastHitBlock = collision.getBlock();
                  }

                  this.tracker.doBouncySkill();
            }
         }

         this.ball.setVelocity(velocity);
      }

      public boolean hasConcluded() {
         return this.runnable.isClosed();
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      private BouncyMechanic.PhysicsSession physicsSession;

      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(EntityDamageEvent.class, EventPriority.HIGHEST)
               .filter(event -> event.getEntity().getUniqueId().equals(this.entity.get().getUniqueId()))
               .filter(event -> event.getCause() == DamageCause.FALL)
               .handler(event -> {
                  if (BouncyMechanic.this.cancelDamage) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Bouncy aura cancelling fall damage");
                     event.setCancelled(true);
                  }
               })
         );
         this.physicsSession = new BouncyMechanic.PhysicsSession(
            this, BukkitAdapter.adapt(this.entity.get()), BukkitAdapter.adapt(this.entity.get().getVelocity())
         );
         this.registerAuraComponent(this.physicsSession);
         this.executeAuraSkill(BouncyMechanic.this.onStartSkill, this.skillMetadata);
      }

      public void doBouncySkill() {
         this.executeAuraSkill(BouncyMechanic.this.onBounceSkill, this.skillMetadata);
      }
   }
}
