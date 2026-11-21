package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderLong;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.audience.WorldAudience;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Builder;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:sound",
   aliases = {"sound", "s", "e:sound", "e:s"},
   description = "Plays a sound at the target location"
)
public class SoundEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private PlaceholderString sound;
   private SkillAudience audience;
   private PlaceholderFloat pitch;
   private PlaceholderFloat volume;
   private double radiusSq;
   private Source soundCategory = null;
   private SoundEffect.EmitterType emitter = null;
   private PlaceholderLong seed;

   public SoundEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.sound = mlc.getPlaceholderString(new String[]{"sound", "s"}, "entity.zombie.attack_iron_door");
      this.audience = mlc.getAudience("audience", "world");
      this.pitch = mlc.getPlaceholderFloat(new String[]{"pitch", "p"}, 1.0F);
      this.volume = mlc.getPlaceholderFloat(new String[]{"volume", "v"}, 1.0F);
      this.radiusSq = mlc.getDouble(new String[]{"radius", "r"}, this.volume.get() * 16.0F);
      this.radiusSq = Math.pow(this.radiusSq, 2.0);
      this.seed = PlaceholderLong.of(mlc.getString(new String[]{"seed"}, null));
      String emitterString = mlc.getString(new String[]{"emitter", "em"}, null);
      if (emitterString != null) {
         try {
            this.emitter = SoundEffect.EmitterType.valueOf(emitterString.toUpperCase());
         } catch (Exception e) {
         }
      }

      String soundCategoryString = mlc.getString(new String[]{"soundcategory", "category", "sc"}, null);
      if (soundCategoryString != null) {
         for (Source sc : Source.values()) {
            if (soundCategoryString.equalsIgnoreCase(sc.toString()) || soundCategoryString.equalsIgnoreCase(sc + "s")) {
               this.soundCategory = sc;
               break;
            }
         }

         if (this.soundCategory == null) {
            MythicLogger.errorMechanicConfig(this, mlc, "Invalid SoundCategory specified");
         }
      }

      if (this.sound.isStatic()) {
         String test = this.sound.get();
         this.getPlugin().getCompatibility().getCrucible().ifPresent(crucible -> {
            if (crucible.isGeneratedSound(test) && !test.startsWith("mythic:")) {
               this.sound = PlaceholderString.of("mythic:" + test);
            }
         });
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience;
      if (this.audience instanceof WorldAudience && !data.hasExecutionAudience()) {
         audience = null;
      } else {
         audience = SkillAudience.getEffective(this.audience, data, null);
      }

      this.playSound(data, target, audience);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience;
      if (this.audience instanceof WorldAudience && !data.hasExecutionAudience()) {
         audience = null;
      } else {
         audience = SkillAudience.getEffective(this.audience, data, target);
      }

      this.playSound(data, target, audience);
      return SkillResult.SUCCESS;
   }

   protected Sound getSound(SkillMetadata data) {
      Builder sound = Sound.sound()
         .source(this.soundCategory == null ? Source.MASTER : this.soundCategory)
         .volume(this.volume.get(data))
         .pitch(this.pitch.get(data))
         .type(Key.key(this.sound.get(data)));
      if (this.seed != null) {
         sound.seed(this.seed.get(data));
      }

      return (Sound)sound.build();
   }

   protected void playSound(SkillMetadata data, AbstractLocation target, Collection<AbstractPlayer> audience) {
      Location l = BukkitAdapter.adapt(target);
      Sound finalSound = this.getSound(data);
      AbstractEntity caster = data.getCaster().getEntity();
      if (audience == null) {
         if (this.emitter != null) {
            switch (this.emitter) {
               case CASTER:
               case BOTH:
                  l.getWorld().playSound(finalSound, caster.getBukkitEntity());
            }
         }

         if (this.emitter != SoundEffect.EmitterType.CASTER) {
            l.getWorld().playSound(finalSound, l.x(), l.y(), l.z());
         }
      } else {
         for (AbstractEntity entity : audience) {
            Entity bukkitEntity = entity.getBukkitEntity();
            if (this.emitter != null) {
               switch (this.emitter) {
                  case CASTER:
                  case BOTH:
                     bukkitEntity.playSound(finalSound, caster.getBukkitEntity());
                  case TARGET:
                  default:
                     break;
                  case AUDIENCE:
                     bukkitEntity.playSound(finalSound, bukkitEntity);
               }
            }

            if (this.emitter != SoundEffect.EmitterType.CASTER && this.emitter != SoundEffect.EmitterType.AUDIENCE) {
               bukkitEntity.playSound(finalSound, l.x(), l.y(), l.z());
            }
         }
      }
   }

   protected void playSound(SkillMetadata data, AbstractEntity target, Collection<AbstractPlayer> audience) {
      Entity e = BukkitAdapter.adapt(target);
      Location l = e.getLocation();
      Sound finalSound = this.getSound(data);
      AbstractEntity caster = data.getCaster().getEntity();
      if (audience == null) {
         World world = e.getWorld();
         if (this.emitter != null) {
            switch (this.emitter) {
               case CASTER:
                  world.playSound(finalSound, caster.getBukkitEntity());
                  break;
               case TARGET:
                  world.playSound(finalSound, e);
                  break;
               case BOTH:
                  world.playSound(finalSound, caster.getBukkitEntity());
                  world.playSound(finalSound, e);
                  break;
               case AUDIENCE:
                  for (Player p : world.getPlayers()) {
                     world.playSound(finalSound, p);
                  }
            }
         } else {
            world.playSound(finalSound, l.x(), l.y(), l.z());
         }
      } else {
         for (AbstractEntity entity : audience) {
            Entity bukkitEntity = entity.getBukkitEntity();
            if (this.emitter != null) {
               switch (this.emitter) {
                  case CASTER:
                     bukkitEntity.playSound(finalSound, caster.getBukkitEntity());
                     break;
                  case TARGET:
                     bukkitEntity.playSound(finalSound, e);
                     break;
                  case BOTH:
                     bukkitEntity.playSound(finalSound, caster.getBukkitEntity());
                     bukkitEntity.playSound(finalSound, e);
                     break;
                  case AUDIENCE:
                     bukkitEntity.playSound(finalSound, bukkitEntity);
               }
            } else {
               bukkitEntity.playSound(finalSound, l.x(), l.y(), l.z());
            }
         }
      }
   }

   static enum EmitterType {
      CASTER,
      TARGET,
      BOTH,
      AUDIENCE;
   }
}
