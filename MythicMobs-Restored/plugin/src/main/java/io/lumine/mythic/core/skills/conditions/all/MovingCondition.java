package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Location;

@MythicCondition(
   author = "Ashijin",
   name = "moving",
   aliases = {"ismoving"},
   description = "If the target has a velocity greater than zero"
)
public class MovingCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "exact",
      aliases = {"e"},
      description = "Sets whether to check if the velocities are exactly the same",
      defValue = "true"
   )
   private final boolean exact;

   public MovingCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.exact = mlc.getBoolean(new String[]{"exact", "e"}, true);
      getPlugin().getPlayerManager().trackPlayerMovement();
   }

   @Override
   public boolean check(AbstractEntity entity) {
      boolean isMoving;
      if (entity.isPlayer()) {
         PlayerManager.PlayerMovementData data = getPlugin().getPlayerManager().getPlayerPositions().get(entity.getUniqueId());
         if (data == null || System.currentTimeMillis() - data.getLastMovementTime() > 50L) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Player hasn't moved recently");
            return false;
         }

         Location t = data.getTo();
         Location f = data.getFrom();
         isMoving = f.getX() != t.getX() || f.getY() != t.getY() || f.getZ() != t.getZ();
         MythicLogger.debug(
            MythicLogger.DebugLevel.CONDITION, "Player Movement {0},{1},{2} == {3},{4},{5}", f.getX(), f.getY(), f.getZ(), t.getX(), t.getY(), t.getZ()
         );
      } else if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
         try {
            isMoving = Schedulers.of(entity.getBukkitEntity())
               .supply(() -> getPlugin().getVolatileCodeHandler().getEntityHandler().isEntityInMotion(entity, this.exact))
               .join();
         } catch (Exception e) {
            isMoving = false;
         }
      } else {
         isMoving = getPlugin().getVolatileCodeHandler().getEntityHandler().isEntityInMotion(entity, this.exact);
      }

      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Is Entity Moving? {0}", String.valueOf(isMoving));
      return isMoving;
   }
}
