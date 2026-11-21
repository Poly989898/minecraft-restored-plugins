package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.targeters.CustomTargeter;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class TargeterAudience extends SkillAudience {
   private SkillTargeter targeter;
   private boolean warned = false;

   public TargeterAudience(MythicLineConfig mlc, String search) {
      super(mlc);
      SkillTargeter targeter = getPlugin().getSkillManager().getTargeter(search);
      if (!(targeter instanceof IEntitySelector) && !(targeter instanceof CustomTargeter)) {
         MythicLogger.errorGenericConfig(mlc, "Audience targeter is not supported: " + targeter.getClass().getSimpleName());
      } else {
         this.targeter = targeter;
      }
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      IEntitySelector targeter = this.resolveTargeter();
      if (targeter == null) {
         return new HashSet<>();
      } else {
         Collection<AbstractPlayer> ret = Lists.newArrayList();
         SkillMetadata audienceData = data.deepClone();

         try {
            audienceData.setEntityTargets(targeter.getEntities(data));
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, ": TargeterAudience found {0} targets", audienceData.getEntityTargets().size());
            targeter.filter(audienceData, true);
         } catch (IllegalArgumentException e) {
            audienceData.setEntityTargets(Sets.newHashSet());
            MythicLogger.handleMinorError(e);
         }

         for (AbstractEntity t : audienceData.getEntityTargets()) {
            if (t.isPlayer()) {
               ret.add(t.asPlayer());
            }
         }

         return ret;
      }
   }

   private IEntitySelector resolveTargeter() {
      if (this.targeter instanceof IEntitySelector selector) {
         return selector;
      } else {
         if (this.targeter instanceof CustomTargeter custom) {
            Optional<SkillTargeter> resolved = custom.getTargeter();
            if (resolved.isPresent()) {
               if (resolved.get() instanceof IEntitySelector selector) {
                  return selector;
               }

               if (!this.warned) {
                  this.warned = true;
                  MythicLogger.errorGenericConfig("Audience targeter is not supported: " + resolved.get().getClass().getSimpleName());
               }
            }
         }

         if (!this.warned) {
            this.warned = true;
            MythicLogger.errorGenericConfig("Audience targeter is not supported: " + this.targeter.getClass().getSimpleName());
         }

         return null;
      }
   }
}
