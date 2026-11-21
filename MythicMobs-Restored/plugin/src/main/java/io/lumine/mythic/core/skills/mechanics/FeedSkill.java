package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "feed",
   description = "Feeds the target entity"
)
public class FeedSkill extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of hunger restore",
      defValue = "1"
   )
   protected PlaceholderInt amount;
   @MythicField(
      name = "saturation",
      aliases = {"s"},
      description = "The amount of saturation to restore",
      defValue = "0"
   )
   protected PlaceholderFloat saturation;
   @MythicField(
      name = "overFeed",
      aliases = {"of", "o"},
      description = "Whether or note to overfeed",
      defValue = "false"
   )
   protected boolean overfeed;

   public FeedSkill(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
      this.saturation = mlc.getPlaceholderFloat(new String[]{"saturation", "s"}, 0.0F);
      this.overfeed = mlc.getBoolean(new String[]{"overfeed", "of", "o"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && target.isPlayer()) {
         int amountToAdd = this.amount.get(data, target);
         float saturationToAdd = this.saturation.get(data, target);
         Runnable applyRunnable = () -> {
            int currentFood = target.asPlayer().getFoodLevel();
            int food = currentFood + amountToAdd;
            float saturation = target.asPlayer().getFoodSaturation() + saturationToAdd;
            if (food >= 20) {
               if (this.overfeed) {
                  target.asPlayer().setFoodLevel(food);
                  if (saturation > food) {
                     saturation = food;
                  }
               } else {
                  target.asPlayer().setFoodLevel(20);
                  if (saturation > 20.0F) {
                     saturation = 20.0F;
                  }
               }
            } else {
               target.asPlayer().setFoodLevel(food);
               if (!this.overfeed && saturation > food) {
                  saturation = food;
               }
            }

            target.asPlayer().setFoodSaturation(saturation);
         };
         Schedulers.runOnRegion(target.getBukkitEntity(), applyRunnable);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
