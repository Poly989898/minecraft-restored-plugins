package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.events.MythicAudienceLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.Bukkit;

public class CustomAudience extends SkillAudience {
   protected final String audienceName;
   protected final MythicLineConfig config;
   protected volatile SkillAudience audience = null;
   protected volatile boolean loaded = false;
   private volatile boolean resolveAttempted = false;

   public CustomAudience(String audience, MythicLineConfig mlc) {
      super(mlc);
      this.audienceName = audience;
      this.config = mlc;
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loading CustomAudience with name " + audience);
   }

   public Optional<SkillAudience> getAudience() {
      return Optional.ofNullable(this.resolve());
   }

   public String getAudienceName() {
      return this.audienceName;
   }

   private SkillAudience resolve() {
      SkillAudience resolved = this.audience;
      if (resolved != null) {
         return resolved;
      } else if (this.resolveAttempted) {
         return null;
      } else if (!Bukkit.isPrimaryThread()) {
         Schedulers.sync().run(this::resolve);
         return null;
      } else {
         this.resolveAttempted = true;
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Attempting to Register CustomAudience: " + this.audienceName);
         MythicAudienceLoadEvent event = new MythicAudienceLoadEvent(this, this.audienceName, this.config);
         Bukkit.getServer().getPluginManager().callEvent(event);
         if (event.getAudience().isPresent()) {
            this.audience = event.getAudience().get();
            this.loaded = true;
            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loaded Custom Audience {0}", this.audienceName);
         } else {
            MythicLogger.error("Failed to load custom audience " + this.audienceName);
         }

         return this.audience;
      }
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      SkillAudience resolved = this.resolve();
      if (resolved == null) {
         Collection<AbstractPlayer> targets = Lists.newArrayList();
         if (target != null && target.isPlayer()) {
            targets.add(target.asPlayer());
         }

         return targets;
      } else {
         return resolved.get(data, target);
      }
   }
}
