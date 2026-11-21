package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "messagejson",
   aliases = {"jsonmessage", "jmsg", "jm"},
   description = "Sends a JSON-formatted message to the target entity"
)
public class MessageJSONMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "message",
      aliases = {"msg", "m"},
      description = "The message in json format to send"
   )
   protected PlaceholderString message;

   public MessageJSONMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;

      try {
         this.message = mlc.getPlaceholderString(new String[]{"message", "msg", "m"}, null);
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'message' attribute is required.");
         this.message = PlaceholderString.of("INCORRECTLY CONFIGURED. SEE CONSOLE ON RELOAD.");
         e.printStackTrace();
      }

      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loaded message skill with message " + this.message);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing message skill with message: " + this.message);
      if (target.isPlayer()) {
         String m = this.message.get(data, target);
         BaseComponent[] message = ComponentSerializer.parse(m);
         ((Player)target.getBukkitEntity()).spigot().sendMessage(message);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
