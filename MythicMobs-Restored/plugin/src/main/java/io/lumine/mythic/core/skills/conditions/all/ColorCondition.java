package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.TraderLlama;
import org.bukkit.material.Colorable;

@MythicCondition(
   author = "Phil",
   name = "color",
   aliases = {"clr"},
   description = "Checks for entity's color"
)
public class ColorCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "color",
      aliases = {"clr", "c"},
      description = "The color to check"
   )
   private final PlaceholderString color;

   public ColorCondition(String line, MythicLineConfig config) {
      super(line);
      this.color = config.getPlaceholderString(new String[]{"clr", "color", "c"}, "WHITE");
   }

   @Override
   public boolean check(AbstractEntity e) {
      Entity target = e.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(target)) {
         Boolean result = Schedulers.of(target).supply(() -> this.checkColor(e, target)).join();
         return result != null ? result : false;
      } else {
         return this.checkColor(e, target);
      }
   }

   private boolean checkColor(AbstractEntity e, Entity target) {
      return switch (target.getType()) {
         case SHEEP, SHULKER -> this.color.get(e).equalsIgnoreCase(String.valueOf(((Colorable)target).getColor()));
         case HORSE -> this.color.get(e).equalsIgnoreCase(String.valueOf(((Horse)target).getColor()));
         case LLAMA -> this.color.get(e).equalsIgnoreCase(String.valueOf(((Llama)target).getColor()));
         case TRADER_LLAMA -> this.color.get(e).equalsIgnoreCase(String.valueOf(((TraderLlama)target).getColor()));
         case PARROT -> this.color.get(e).equalsIgnoreCase(String.valueOf(((Parrot)target).getVariant()));
         case CAT -> this.color.get(e).equalsIgnoreCase(String.valueOf(((Cat)target).getCatType()));
         default -> false;
      };
   }
}
