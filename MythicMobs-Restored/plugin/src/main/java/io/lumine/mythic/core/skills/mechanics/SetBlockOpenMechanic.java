package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;

@MythicMechanic(
   author = "Ashijin",
   name = "setblockopen",
   aliases = {"setdoor"},
   description = "Opens the targeted openable block's open state"
)
public class SetBlockOpenMechanic extends SkillMechanic implements ITargetedLocationSkill {
   @MythicField(
      name = "open",
      aliases = {"o"},
      description = "Whether the target block should be open",
      defValue = "true"
   )
   private boolean open;

   public SetBlockOpenMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.open = mlc.getBoolean(new String[]{"open", "o"}, true);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location bukkitLocation = BukkitAdapter.adapt(target);
      BlockState blockState = bukkitLocation.getBlock().getState();
      if (blockState.getBlockData() instanceof Openable openable) {
         openable.setOpen(this.open);
         blockState.setBlockData(openable);
         blockState.update();
         return SkillResult.SUCCESS;
      } else {
         MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC,
            "Target location {0} {1} is not an openable block",
            target.serializeShortForm(),
            bukkitLocation.getBlock().getType().toString()
         );
         return SkillResult.INVALID_TARGET;
      }
   }
}
