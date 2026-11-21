package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PlaceholderVector;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.adapters.BukkitParticle;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.function.Function;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:particles",
   aliases = {"effect:particle", "particles", "particle", "e:particles", "e:particle", "e:p"},
   description = "Plays a particle effect at the target location"
)
public class ParticleEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected String strParticle;
   protected String strMob;
   protected BukkitEntityType me;
   protected MythicMob mm;
   protected boolean isMob;
   protected BukkitParticle particle;
   protected Function<PlaceholderMeta, Object> particleData = null;
   protected SkillAudience audience;
   protected PlaceholderFloat xSpread;
   protected PlaceholderFloat zSpread;
   protected PlaceholderFloat vSpread;
   protected PlaceholderFloat pSpeed;
   protected PlaceholderFloat yOffset;
   protected float startForwardOffset;
   protected PlaceholderFloat startSideOffset;
   protected boolean useEyeLocation;
   protected PlaceholderInt amount;
   protected int viewDistance;
   protected boolean directional;
   protected boolean directionReversed;
   protected PlaceholderVector direction;
   protected float yaw;
   protected float pitch;
   protected boolean setYaw = false;
   protected boolean setPitch = false;
   protected boolean exactOffsets = false;
   protected PlaceholderString color = null;
   protected Chroma staticColor = null;
   boolean fromOrigin;

   @Deprecated
   public ParticleEffect(SkillExecutor manager, String file, MythicLineConfig mlc) {
      this(manager, new File(file), mlc.getLine(), mlc);
   }

   public ParticleEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;
      this.strParticle = mlc.getString(new String[]{"particle", "p"}, "reddust");
      this.strMob = mlc.getString(new String[]{"mob", "m", "t"}, "null");
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
         if (!this.strParticle.equals("mob")) {
            this.isMob = false;
            this.particle = BukkitParticle.get(this.strParticle);
            if (this.particle == null || this.particle.toBukkitParticle() == null) {
               MythicLogger.errorMechanicConfig(this, mlc, "Particle '" + this.strParticle + "' is not available on this server version");
            } else if (this.particle.requiresData()) {
               this.particleData = this.particle.parseDataOptions(mlc);
            }
         } else {
            this.isMob = true;
            this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
            this.getPlugin().getSkillManager().queueSecondPass(() -> {
               this.mm = this.getPlugin().getMobManager().getMythicMob(this.strMob).orElseGet(() -> null);
               if (this.mm == null) {
                  this.me = BukkitEntityType.getMythicEntity(this.strMob);
                  if (this.me == null) {
                     MythicLogger.errorMechanicConfig(this, mlc, "The 'Mob' attribute must be a valid MythicMob or MythicEntity type.");
                  }
               }
            });
         }
      }

      this.audience = mlc.getAudience("audience", "nearby");
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a", "count"}, 10);
      String spread = mlc.getString(new String[]{"spread", "offset"}, "0");
      String hSpread = mlc.getString(new String[]{"hspread", "hs"}, spread);
      this.xSpread = PlaceholderFloat.of(mlc.getString(new String[]{"xspread", "xs"}, hSpread));
      this.zSpread = PlaceholderFloat.of(mlc.getString(new String[]{"zspread", "zs"}, hSpread));
      this.vSpread = mlc.getPlaceholderFloat(new String[]{"vspread", "vs", "yspread", "ys"}, spread);
      this.pSpeed = mlc.getPlaceholderFloat(new String[]{"speed", "s"}, 0.0F);
      this.yOffset = mlc.getPlaceholderFloat(new String[]{"yoffset", "y"}, 0.0F);
      this.startForwardOffset = mlc.getFloat(new String[]{"forwardoffset", "startfoffset", "sfo"}, 0.0F);
      this.startSideOffset = mlc.getPlaceholderFloat(new String[]{"sideoffset", "soffset", "sso"}, 0.0F);
      this.useEyeLocation = mlc.getBoolean(new String[]{"useeyelocation", "uel"}, false);
      this.exactOffsets = mlc.getBoolean(new String[]{"exactoffsets", "eo"}, false);
      this.viewDistance = mlc.getInteger(new String[]{"viewdistance", "vd"}, 128);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.directional = mlc.getBoolean(new String[]{"directional", "d"}, false);
      this.directionReversed = mlc.getBoolean(new String[]{"directionreversed", "dr"}, false);
      this.direction = mlc.getPlaceholderVector(new String[]{"direction", "dir"}, null);
      this.yaw = mlc.getFloat(new String[]{"fixedyaw", "yaw"}, -1111.0F);
      if (this.yaw != -1111.0F) {
         this.setYaw = true;
      }

      this.pitch = mlc.getFloat(new String[]{"fixedpitch", "pitch"}, -1111.0F);
      if (this.pitch != -1111.0F) {
         this.setPitch = true;
      }

      this.color = mlc.getPlaceholderString(new String[]{"color", "c"}, null);
      if (this.color != null && this.color.isStatic()) {
         this.staticColor = Chroma.of(this.color.get());
      }

      this.startForwardOffset *= -1.0F;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, null);
      this.playEffect(data, target, audience);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      AbstractLocation startLocation;
      if (this.useEyeLocation) {
         startLocation = target.getEyeLocation();
      } else {
         startLocation = target.getLocation();
      }

      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      this.playEffect(data, startLocation, audience);
      return SkillResult.SUCCESS;
   }

   protected void playEffect(SkillMetadata data, AbstractLocation target, Collection<AbstractPlayer> audience) {
      if (this.setYaw) {
         target.setYaw(this.yaw);
      }

      if (this.setPitch) {
         target.setPitch(this.pitch);
      }

      if (this.startForwardOffset != 0.0F) {
         target = MythicUtil.move(this.faulty, target, this.startForwardOffset, 0.0, 0.0);
      }

      if (this.startSideOffset.get(data) != 0.0F) {
         target = MythicUtil.move(this.faulty, target, 0.0, 0.0, this.startSideOffset.get(data));
      }

      if (this.directional) {
         this.playDirectionalParticleEffect(data, data.getOrigin(), target, audience);
      } else {
         this.playParticleEffect(data, target, audience);
      }
   }

   protected void playParticleEffect(SkillMetadata data, AbstractLocation target, Collection<AbstractPlayer> audience) {
      AbstractLocation ln = target.clone().add(0.0, this.yOffset.get(data), 0.0);
      if (!this.isMob) {
         if (this.particleData == null) {
            if (this.color != null) {
               Chroma color = this.staticColor == null ? Chroma.of(this.color.get(data)) : this.staticColor;
               this.particle
                  .sendLegacyColored(
                     audience, ln, this.pSpeed.get(data), this.amount.get(data), this.xSpread.get(data), this.vSpread.get(data), this.zSpread.get(data), color
                  );
            } else {
               this.particle
                  .send(audience, ln, this.pSpeed.get(data), this.amount.get(data), this.xSpread.get(data), this.vSpread.get(data), this.zSpread.get(data));
            }
         } else {
            this.particle
               .send(
                  audience,
                  ln,
                  this.pSpeed.get(data),
                  this.amount.get(data),
                  this.xSpread.get(data),
                  this.vSpread.get(data),
                  this.zSpread.get(data),
                  this.particleData.apply(data),
                  this.exactOffsets
               );
         }
      } else {
         ln.add(
            Math.random() * this.xSpread.get(data) * 2.0 - this.xSpread.get(data),
            Math.random() * this.vSpread.get(data) * 2.0 - this.vSpread.get(data),
            Math.random() * this.zSpread.get(data) * 2.0 - this.zSpread.get(data)
         );
         Schedulers.sync().run(() -> MythicBukkit.inst().getMobManager().spawnMob(this.strMob, ln));
      }
   }

   protected void playDirectionalParticleEffect(SkillMetadata data, AbstractLocation origin, AbstractLocation target, Collection<AbstractPlayer> audience) {
      AbstractVector direction;
      AbstractLocation spawnLocation;
      if (this.direction != null) {
         direction = this.direction.get(data);
         spawnLocation = target;
      } else if (this.directionReversed) {
         direction = origin.toVector().subtract(target.clone().toVector()).normalize();
         spawnLocation = origin;
      } else {
         direction = target.toVector().subtract(origin.clone().toVector()).normalize();
         spawnLocation = origin;
      }

      if (this.particleData == null) {
         this.particle
            .sendDirectional(
               audience,
               spawnLocation,
               this.pSpeed.get(data),
               this.amount.get(data),
               this.xSpread.get(data),
               this.vSpread.get(data),
               this.zSpread.get(data),
               direction
            );
      }
   }
}
