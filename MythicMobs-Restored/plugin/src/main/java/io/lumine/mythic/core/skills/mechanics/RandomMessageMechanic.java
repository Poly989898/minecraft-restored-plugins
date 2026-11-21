package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@MythicMechanic(
   author = "Ashijin",
   name = "randommessage",
   aliases = {"randommsg", "rmsg", "rm"},
   description = "Sends a random message to the target player"
)
public class RandomMessageMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected static boolean noloop = false;
   protected List<PlaceholderString> messages = new ArrayList<>();

   public RandomMessageMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;

      try {
         String m = mlc.getString(new String[]{"messages", "message", "msg", "msgs", "m"});

         for (String s : m.split(",")) {
            s = s.trim();
            this.messages.add(new PlaceholderStringImpl(s));
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded RandomMessage " + s.substring(1, s.length() - 1));
         }
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'messages' attribute is required.");
         this.messages.add(new PlaceholderStringImpl("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD."));
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      PlaceholderString message = this.messages.get(Numbers.randomInt(this.messages.size()));
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing RandomMessage skill with message: " + message);
      if (target.isPlayer()) {
         target.asPlayer().sendMessage(message.get(data, target));
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
