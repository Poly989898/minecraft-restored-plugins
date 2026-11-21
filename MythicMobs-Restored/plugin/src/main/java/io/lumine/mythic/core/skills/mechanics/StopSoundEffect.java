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
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.sound.Sound.Source;

@MythicMechanic(
   author = "Phil",
   name = "effect:stopsound",
   aliases = {"stopsound", "e:ss", "ss"},
   description = "Stops a sound from playing for the targeted entity"
)
public class StopSoundEffect extends SkillMechanic implements ITargetedEntitySkill {
   private PlaceholderString sound;
   private Source soundCategory;

   public StopSoundEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.sound = mlc.getPlaceholderString(new String[]{"sound", "s"}, "entity.zombie.attack_iron_door");
      String soundCategoryString = mlc.getString(new String[]{"soundcategory", "source", "sc", "category"}, null);
      if (soundCategoryString != null) {
         for (Source sc : Source.values()) {
            if (soundCategoryString.equalsIgnoreCase(sc.toString()) || soundCategoryString.equalsIgnoreCase(sc + "s")) {
               this.soundCategory = sc;
               break;
            }
         }

         if (this.soundCategory == null) {
            MythicLogger.errorMechanicConfig(this, mlc, "Invalid SoundCategory specified");
         }
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Key soundKey = Key.key(this.sound.get(data, target));
         SoundStop stop;
         if (this.soundCategory != null) {
            stop = SoundStop.namedOnSource(soundKey, this.soundCategory);
         } else {
            stop = SoundStop.named(soundKey);
         }

         target.getBukkitEntity().stopSound(stop);
         return SkillResult.SUCCESS;
      }
   }
}
