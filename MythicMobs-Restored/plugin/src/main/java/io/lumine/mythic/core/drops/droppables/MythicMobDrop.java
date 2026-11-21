package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ILocationDrop;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class MythicMobDrop extends Drop implements ILocationDrop {
   protected MythicMob mm;
   protected BukkitEntityType me;
   protected String strType;
   protected PlaceholderInt amount;
   protected PlaceholderDouble level;
   protected int noise;
   protected int yNoise;
   protected double force;
   protected double yForce;
   protected boolean yUpOnly;
   protected boolean onSurface;

   public MythicMobDrop(String line, MythicLineConfig mlc) {
      super(line, mlc);
      this.strType = mlc.getString(new String[]{"type", "t", "mob", "m"}, "SKELETON");
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
      this.level = mlc.getPlaceholderDouble(new String[]{"level", "lvl", "l"}, -1.0);
      this.noise = mlc.getInteger(new String[]{"noise", "n", "radius", "r"}, 0);
      this.yNoise = mlc.getInteger(new String[]{"ynoise", "yn", "yradius", "yr"}, this.noise);
      this.yUpOnly = mlc.getBoolean(new String[]{"yradiusuponly", "yradiusonlyup", "yruo", "yu"}, false);
      this.force = mlc.getDouble(new String[]{"force", "f", "velocity", "v"}, 0.0);
      this.yForce = mlc.getDouble(new String[]{"yforce", "yf", "yvelocity", "yv"}, this.force);
      this.onSurface = mlc.getBoolean(new String[]{"onsurface", "os", "s"}, false);
      MythicBukkit.inst().getSkillManager().queueSecondPass(() -> {
         this.mm = MythicBukkit.inst().getMobManager().getMythicMob(this.strType).orElseGet(() -> null);
         if (this.mm == null) {
            this.me = BukkitEntityType.getMythicEntity(this.strType);
            if (this.me == null) {
               MythicLogger.errorDropConfig(this, mlc, "The 'type' attribute must be a valid MythicMob or MythicEntity type.");
            }
         }
      });
   }

   public MythicMobDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "In the constructor with line: " + line + " config: " + config.toString() + " amount: " + amount);
   }

   @Override
   public void drop(AbstractLocation target, DropMetadata metadata, double configAmount) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Attempting to drop a MythicMob {0} via the MythicMob drop", this.strType);
      int amount = Numbers.floor(this.amount.get() * configAmount);
      double level = this.level.get();
      if (this.mm != null) {
         if (this.noise > 0) {
            for (int i = 1; i <= amount; i++) {
               int height = this.mm.getMythicEntity() == null ? 2 : this.mm.getMythicEntity().getHeight();
               MythicBukkit.inst().getMobManager();
               AbstractLocation l = MobExecutor.findSafeSpawnLocation(target, this.noise, this.yNoise, height, this.yUpOnly, this.onSurface);
               ActiveMob ams = this.mm.spawn(l, level, SpawnReason.SUMMON);
               ams.getEntity()
                  .setVelocity(
                     new AbstractVector(
                        (Numbers.randomDouble() - 0.5) * this.force, (Numbers.randomDouble() - 0.5) * this.yForce, (Numbers.randomDouble() - 0.5) * this.force
                     )
                  );
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1}", this.strType, l.toString());
            }
         } else {
            for (int i = 1; i <= amount; i++) {
               ActiveMob ams = this.mm.spawn(target, level, SpawnReason.SUMMON);
               ams.getEntity()
                  .setVelocity(
                     new AbstractVector(
                        (Numbers.randomDouble() - 0.5) * this.force, (Numbers.randomDouble() - 0.5) * this.yForce, (Numbers.randomDouble() - 0.5) * this.force
                     )
                  );
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1}", this.strType, target.toString());
            }
         }
      } else if (this.me != null) {
         if (this.noise > 0) {
            for (int i = 1; i <= amount; i++) {
               MythicBukkit.inst().getMobManager();
               AbstractLocation l = MobExecutor.findSafeSpawnLocation(target, this.noise, this.yNoise, this.me.getHeight(), this.yUpOnly, this.onSurface);
               Entity entity = this.me.spawn(BukkitAdapter.adapt(l), SpawnReason.SUMMON);
               entity.setVelocity(
                  new Vector(
                     (Numbers.randomDouble() - 0.5) * this.force, (Numbers.randomDouble() - 0.5) * this.yForce, (Numbers.randomDouble() - 0.5) * this.force
                  )
               );
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1} with noise", this.strType, l.toString());
            }
         } else {
            for (int i = 1; i <= amount; i++) {
               Entity entity = this.me.spawn(BukkitAdapter.adapt(target), SpawnReason.SUMMON);
               entity.setVelocity(
                  new Vector(
                     (Numbers.randomDouble() - 0.5) * this.force, (Numbers.randomDouble() - 0.5) * this.yForce, (Numbers.randomDouble() - 0.5) * this.force
                  )
               );
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Summoning {0} at {1}", this.strType, target.toString());
            }
         }
      } else {
         MythicLogger.error("MythicMobDrop: Mob Type {0} not found", this.strType);
      }
   }
}
