package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "Ashijin",
   name = "playAnimation",
   aliases = {"effect:playanimation", "e:playanimation", "playarmanimation"},
   description = "Forces the entity to play an animation"
)
public class PlayAnimationEffect extends SkillMechanic implements ITargetedEntitySkill {
   private SkillAudience audience;
   protected PlaceholderInt animation;

   public PlayAnimationEffect(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.audience = mlc.getAudience("audience", "world");
      this.animation = mlc.getPlaceholderInteger(new String[]{"animation", "a", "effect", "e"}, 1);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      byte b = (byte)this.animation.get(data, target);
      this.getPlugin().getVolatileCodeHandler().getEntityHandler().playEntityAnimation(target, b, audience);
      return SkillResult.SUCCESS;
   }
}
