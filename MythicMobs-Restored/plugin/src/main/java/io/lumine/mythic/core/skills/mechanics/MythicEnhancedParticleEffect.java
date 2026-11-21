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
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.FriendlyByteBuf;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.util.Collection;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ticxo",
   name = "mythicenhanced:particles",
   aliases = {"mythicenhanced:particle", "me:particles", "me:particle", "mod:particles", "mod:particle", "aux:emitter"},
   description = "Plays a particle effect at the target location using the MythicEnhanced mod"
)
public class MythicEnhancedParticleEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   public static final String CHANNEL_NAME = "mythicenhanced:particles";
   @MythicField(
      name = "emitter",
      aliases = {"e", "emit", "emitter"},
      description = "The emitter of the particle effect."
   )
   private final PlaceholderString emitter;
   @MythicField(
      name = "force",
      aliases = {"f"},
      description = "Whether to force the particle effect to play."
   )
   private final boolean force;
   @MythicField(
      name = "age",
      description = "The age of the particle effect."
   )
   private final PlaceholderInt age;
   @MythicField(
      name = "audience",
      description = "The audience for the particle effect."
   )
   private final SkillAudience audience;

   public MythicEnhancedParticleEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.ASYNC_ONLY;

      try {
         Bukkit.getMessenger().registerOutgoingPluginChannel(this.getPlugin(), "mythicenhanced:particles");
      } catch (Error | Exception e) {
      }

      this.emitter = mlc.getPlaceholderString(new String[]{"e", "emit", "emitter"}, null);
      this.force = mlc.getBoolean(new String[]{"f", "force"}, false);
      this.age = mlc.getPlaceholderInteger(new String[]{"age"}, 60);
      this.audience = mlc.getAudience("audience", "world");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
      if (this.emitter == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         String emitterName = this.emitter.get(skillMetadata, abstractEntity);
         int maxAge = this.age.get(skillMetadata, abstractEntity);
         if (this.audience == null) {
            return SkillResult.INVALID_CONFIG;
         } else {
            this.sendToAudience(() -> SkillAudience.getEffective(this.audience, skillMetadata, abstractEntity), emitterName, abstractEntity, maxAge);
            return SkillResult.SUCCESS;
         }
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
      if (this.emitter == null) {
         return SkillResult.INVALID_CONFIG;
      } else {
         String emitterName = this.emitter.get(skillMetadata);
         int maxAge = this.age.get(skillMetadata);
         if (this.audience == null) {
            return SkillResult.INVALID_CONFIG;
         } else {
            this.sendToAudience(() -> SkillAudience.getEffective(this.audience, skillMetadata, null), emitterName, abstractLocation, maxAge);
            return SkillResult.SUCCESS;
         }
      }
   }

   private void sendToAudience(Supplier<Collection<AbstractPlayer>> entities, String emitter, AbstractLocation loc, int age) {
      double x = loc.getX();
      double y = loc.getY();
      double z = loc.getZ();
      byte[] emitterPacket = createEmitterPacket(emitter, this.force, x, y, z, age);

      for (AbstractPlayer entity : entities.get()) {
         if (entity.getBukkitEntity() instanceof Player player && player.getListeningPluginChannels().contains("mythicenhanced:particles")) {
            player.sendPluginMessage(this.getPlugin(), "mythicenhanced:particles", emitterPacket);
         }
      }
   }

   private void sendToAudience(Supplier<Collection<AbstractPlayer>> entities, String emitter, AbstractEntity attached, int age) {
      byte[] emitterPacket = createEmitterPacket(emitter, this.force, attached.getBukkitEntity(), age);

      for (AbstractPlayer entity : entities.get()) {
         if (entity.getBukkitEntity() instanceof Player player && player.getListeningPluginChannels().contains("mythicenhanced:particles")) {
            player.sendPluginMessage(this.getPlugin(), "mythicenhanced:particles", emitterPacket);
         }
      }
   }

   private static byte[] createEmitterPacket(String emitter, boolean isLongRange, double x, double y, double z, int age) {
      FriendlyByteBuf spawn = new FriendlyByteBuf(Unpooled.buffer());
      spawn.writeByte(1);
      spawn.writeUtf(emitter);
      spawn.writeBoolean(isLongRange);
      spawn.writeVarInt(age);
      spawn.writeBoolean(false);
      spawn.writeDouble(x);
      spawn.writeDouble(y);
      spawn.writeDouble(z);
      return spawn.array();
   }

   private static byte[] createEmitterPacket(String emitter, boolean isLongRange, Entity entity, int age) {
      FriendlyByteBuf spawn = new FriendlyByteBuf(Unpooled.buffer());
      spawn.writeByte(1);
      spawn.writeUtf(emitter);
      spawn.writeBoolean(isLongRange);
      spawn.writeVarInt(age);
      spawn.writeBoolean(true);
      spawn.writeVarInt(entity.getEntityId());
      return spawn.array();
   }
}
