package io.lumine.mythic.bukkit;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.adapters.VirtualEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Facade for other plugins: look up and spawn Mythic mobs, cast skills and manage mob threat.
 * All methods go through the running {@link MythicBukkit} instance.
 */
public class BukkitAPIHelper {
   /** Returns the mob type with this name, or null if there is none. */
   public MythicMob getMythicMob(String type) {
      return MythicBukkit.inst().getMobManager().getMythicMob(type).orElseGet(() -> null);
   }

   /**
    * Spawns the given mob type at the location with the given level.
    *
    * @throws InvalidMobTypeException if {@code type} is null
    */
   public Entity spawnMythicMob(MythicMob type, Location location, int level) throws InvalidMobTypeException {
      if (type == null) {
         throw new InvalidMobTypeException("Supplied mob type cannot be null.");
      } else {
         return type.spawn(BukkitAdapter.adapt(location), level, SpawnReason.OTHER).getEntity().getBukkitEntity();
      }
   }

   /** Spawns the named mob type at level 1. See {@link #spawnMythicMob(String, Location, int)}. */
   public Entity spawnMythicMob(String type, Location location) throws InvalidMobTypeException {
      return this.spawnMythicMob(type, location, 1);
   }

   /**
    * Spawns the named mob type at the location with the given level.
    *
    * @throws InvalidMobTypeException if no mob type has this name
    */
   public Entity spawnMythicMob(String type, Location location, int level) throws InvalidMobTypeException {
      MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(type).orElseGet(() -> null);
      if (mm == null) {
         throw new InvalidMobTypeException(type + " is not a valid Mythic Mob type.");
      } else {
         return mm.spawn(BukkitAdapter.adapt(location), level, SpawnReason.OTHER).getEntity().getBukkitEntity();
      }
   }

   /** Returns true if the entity is an active Mythic mob. */
   public boolean isMythicMob(Entity l) {
      return MythicBukkit.inst().getMobManager().isActiveMob(BukkitAdapter.adapt(l));
   }

   /** Returns true if the unique id belongs to an active Mythic mob. */
   public boolean isMythicMob(UUID u) {
      return MythicBukkit.inst().getMobManager().isActiveMob(u);
   }

   public ActiveMob getMythicMobInstance(Entity e) {
      return MythicBukkit.inst().getMobManager().getMythicMobInstance(BukkitAdapter.adapt(e));
   }

   public boolean castSkill(Entity e, String skillName) {
      return this.castSkill(e, skillName, e.getLocation(), 1.0F);
   }

   public boolean castSkill(Entity e, String skillName, Consumer<SkillMetadata> transformer) {
      return this.castSkill(e, skillName, e.getLocation(), 1.0F, transformer);
   }

   public boolean castSkill(Entity e, String skillName, float power) {
      return this.castSkill(e, skillName, e.getLocation(), power);
   }

   public boolean castSkill(Entity e, String skillName, float power, Consumer<SkillMetadata> transformer) {
      return this.castSkill(e, skillName, e.getLocation(), power, transformer);
   }

   public boolean castSkill(Entity e, String skillName, Location origin) {
      return this.castSkill(e, skillName, origin, 1.0F);
   }

   public boolean castSkill(Entity e, String skillName, Location origin, Consumer<SkillMetadata> transformer) {
      return this.castSkill(e, skillName, origin, 1.0F, transformer);
   }

   public boolean castSkill(Entity e, String skillName, Location origin, float power) {
      return this.castSkill(e, skillName, null, origin, null, null, power);
   }

   public boolean castSkill(Entity e, String skillName, Location origin, float power, Consumer<SkillMetadata> transformer) {
      return this.castSkill(e, skillName, null, origin, null, null, power, transformer);
   }

   public boolean castSkill(Entity e, String skillName, Location origin, Collection<Entity> eTargets, Collection<Location> lTargets, float power) {
      return this.castSkill(e, skillName, null, origin, eTargets, lTargets, power);
   }

   public boolean castSkill(
      Entity e, String skillName, Entity trigger, Location origin, Collection<Entity> eTargets, Collection<Location> lTargets, float power
   ) {
      return this.castSkill(e, skillName, trigger, origin, eTargets, lTargets, power, null);
   }

   /**
    * Casts a skill with full control over caster, trigger, origin and targets.
    *
    * The caster is chosen in this order: a virtual entity at {@code origin} (or at the trigger's location) when
    * {@code e} is null; the active Mythic mob instance when {@code e} is one; the player's profile when it exists;
    * otherwise a generic caster around the entity. The {@code transformer}, if given, may change the skill metadata
    * before execution. The skill only executes if it is usable for the API trigger (its conditions pass).
    *
    * @return false if no skill with this name exists; true otherwise, even when the skill was not usable
    */
   public boolean castSkill(
      Entity e,
      String skillName,
      Entity trigger,
      Location origin,
      Collection<Entity> eTargets,
      Collection<Location> lTargets,
      float power,
      Consumer<SkillMetadata> transformer
   ) {
      Optional<Skill> maybeSkill = MythicBukkit.inst().getSkillManager().getSkill(skillName);
      if (!maybeSkill.isPresent()) {
         return false;
      } else {
         SkillCaster caster;
         if (e == null) {
            AbstractEntity fakeEntity;
            if (origin != null) {
               fakeEntity = new VirtualEntity(BukkitAdapter.adapt(origin));
            } else if (trigger != null) {
               fakeEntity = new VirtualEntity(BukkitAdapter.adapt(trigger.getLocation()));
            } else {
               fakeEntity = new VirtualEntity();
            }

            caster = new GenericCaster(fakeEntity);
         } else if (MythicBukkit.inst().getMobManager().isActiveMob(e.getUniqueId())) {
            caster = MythicBukkit.inst().getMobManager().getMythicMobInstance(e);
         } else {
            Optional<PlayerData> profile = MythicBukkit.inst().getPlayerManager().getProfile(e.getUniqueId());
            if (profile.isPresent()) {
               caster = profile.get();
            } else {
               caster = new GenericCaster(BukkitAdapter.adapt(e));
            }
         }

         Skill skill = maybeSkill.get();
         Collection<AbstractEntity> feTargets = Lists.newArrayList();
         Collection<AbstractLocation> flTargets = Lists.newArrayList();
         if (eTargets != null) {
            for (Entity en : eTargets) {
               feTargets.add(BukkitAdapter.adapt(en));
            }
         }

         if (lTargets != null) {
            for (Location l : lTargets) {
               flTargets.add(BukkitAdapter.adapt(l));
            }
         }

         SkillMetadata data = new SkillMetadataImpl(
            SkillTriggers.API, caster, BukkitAdapter.adapt(trigger), BukkitAdapter.adapt(origin), feTargets, flTargets, power
         );
         if (transformer != null) {
            transformer.accept(data);
         }

         if (skill.isUsable(data, SkillTriggers.API)) {
            skill.execute(data);
         }

         return true;
      }
   }

   /**
    * Adds threat for {@code target} on the mob's threat table.
    *
    * @return false if the entity is not an active Mythic mob or has no threat table
    */
   public boolean addThreat(Entity mob, LivingEntity target, double amount) {
      if (!MythicBukkit.inst().getMobManager().isActiveMob(mob.getUniqueId())) {
         return false;
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(mob);
         if (am.getThreatTable() == null) {
            return false;
         } else {
            am.getThreatTable().threatGain(BukkitAdapter.adapt(target), amount);
            return true;
         }
      }
   }

   /**
    * Removes threat for {@code target} from the mob's threat table.
    *
    * @return false if the entity is not an active Mythic mob or has no threat table
    */
   public boolean reduceThreat(Entity mob, LivingEntity target, double amount) {
      if (!MythicBukkit.inst().getMobManager().isActiveMob(mob.getUniqueId())) {
         return false;
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(mob);
         if (am.getThreatTable() == null) {
            return false;
         } else {
            am.getThreatTable().threatLoss(BukkitAdapter.adapt(target), amount);
            return true;
         }
      }
   }

   /**
    * Makes the mob's threat table taunt {@code target}.
    *
    * @return false if the entity is not an active Mythic mob or has no threat table
    */
   public boolean taunt(Entity mob, LivingEntity target) {
      if (!MythicBukkit.inst().getMobManager().isActiveMob(mob.getUniqueId())) {
         return false;
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(mob);
         if (am.getThreatTable() == null) {
            return false;
         } else {
            am.getThreatTable().Taunt(BukkitAdapter.adapt(target));
            return true;
         }
      }
   }
}
