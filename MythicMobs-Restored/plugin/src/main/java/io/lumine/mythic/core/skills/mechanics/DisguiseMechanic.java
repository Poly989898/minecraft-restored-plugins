package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "disguise",
   description = "Disguises the target entity"
)
public class DisguiseMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "audience",
      description = "The audience of the disguise"
   )
   protected SkillAudience audience;
   @MythicField(
      name = "disguise",
      aliases = {"type", "d"},
      description = "The disguise to set",
      defValue = "player Ashijin"
   )
   protected PlaceholderString disguise;

   public DisguiseMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.audience = mlc.getAudience("audience", null);
      this.disguise = mlc.getPlaceholderString(new String[]{"type", "disguise", "d"}, "player Ashijin");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (CompatibilityManager.LibsDisguises == null) {
         return SkillResult.MISSING_COMPATIBILITY;
      } else {
         Disguise d = CompatibilityManager.LibsDisguises.getParsedDisguise(this.disguise.get(data));
         Entity entity = data.getCaster().getEntity().getBukkitEntity();
         Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, data.getCaster().getEntity());
         if (this.audience == null && !data.hasExecutionAudience()) {
            DisguiseAPI.disguiseEntity(entity, d);
         } else {
            Collection<Player> pa = Lists.newArrayList();

            for (AbstractEntity ae : audience) {
               if (ae.isPlayer()) {
                  pa.add((Player)ae.getBukkitEntity());
               }
            }

            DisguiseAPI.disguiseToPlayers(entity, d, pa);
         }

         return SkillResult.SUCCESS;
      }
   }
}
