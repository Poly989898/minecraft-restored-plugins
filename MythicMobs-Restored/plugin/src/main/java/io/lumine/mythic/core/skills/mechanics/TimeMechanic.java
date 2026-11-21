package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.World;

@MythicMechanic(
   name = "time",
   aliases = {"setTime"},
   description = "Sets the world's absolute time"
)
public class TimeMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "mode",
      aliases = {"m"},
      description = "The mode to use for time mechanic.\nValid Mode:\n- SET -> Sets the current time of the world\n- ADD -> Sets the current time of the world with an offset\n- RESET -> Re-syncs the target's world time with the server world time.\n           This only works if the player's client time is not synced with the server's\n",
      defValue = "ADD"
   )
   private final TimeMechanic.Mode mode;
   @MythicField(
      name = "amount",
      aliases = {"amt", "ticks", "t"},
      description = "The amount to set",
      defValue = "20"
   )
   private final PlaceholderInt amount;
   @MythicField(
      name = "person",
      description = "Sets whether to change the global time or the player's client time",
      defValue = "false"
   )
   private final boolean personal;
   @MythicField(
      name = "relative",
      description = "Sets whether to keep the player's time synchronized to its world time with an offset",
      defValue = "false"
   )
   private final boolean relative;

   public TimeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "ticks", "t", "amt", "time"}, 20);
      this.mode = mlc.getEnum(
         new String[]{"mode", "m"}, TimeMechanic.Mode.class, TimeMechanic.Mode.ADD, "Invalid input for 'mode' attribute. Valid inputs are: SET, ADD, or RESET"
      );
      this.personal = mlc.getBoolean(new String[]{"personal"}, false);
      this.relative = mlc.getBoolean(new String[]{"relative"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         AbstractPlayer player = target.asPlayer();
         World world = BukkitAdapter.adapt(target.getWorld());
         int amount = this.amount.get(data, target);
         switch (this.mode) {
            case SET:
               if (this.personal) {
                  player.setPersonalTime(amount, this.relative);
               } else {
                  world.setTime(amount);
               }
               break;
            case ADD:
               if (this.personal) {
                  player.setPersonalTime(world.getTime() + amount, this.relative);
               } else {
                  world.setTime(world.getTime() + amount);
               }
               break;
            case RESET:
               player.resetPersonalTime();
         }

         return SkillResult.SUCCESS;
      }
   }

   public static enum Mode {
      SET,
      ADD,
      RESET;
   }
}
