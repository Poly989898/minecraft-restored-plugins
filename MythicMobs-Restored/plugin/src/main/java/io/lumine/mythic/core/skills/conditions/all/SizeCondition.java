package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Slime;

@MythicCondition(
   author = "Ashijin",
   name = "size",
   aliases = {"mobSize"},
   description = "Checks against the entity's size"
)
public class SizeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "size"
   )
   private RangedInt range;

   public SizeCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.range = new RangedInt(mlc.getString(new String[]{"size", "s"}, this.conditionVar));
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (!entity.isLiving()) {
         return false;
      } else {
         LivingEntity le = (LivingEntity)entity.getBukkitEntity();
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(le)) {
            Boolean result = Schedulers.of(le).supply(() -> this.checkSize(le)).join();
            return result != null ? result : false;
         } else {
            return this.checkSize(le);
         }
      }
   }

   private boolean checkSize(LivingEntity le) {
      int size;
      if (le instanceof Slime) {
         size = ((Slime)le).getSize();
      } else {
         if (!(le instanceof Phantom)) {
            return false;
         }

         size = ((Phantom)le).getSize();
      }

      return this.range.equals(size);
   }
}
