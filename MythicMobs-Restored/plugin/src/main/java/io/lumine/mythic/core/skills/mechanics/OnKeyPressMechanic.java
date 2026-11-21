package io.lumine.mythic.core.skills.mechanics;

import eu.asangarin.arikeys.api.AriKeyPressEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Phil",
   name = "onKeyPress",
   aliases = {"keyPress", "kp"},
   description = "Applies an aura to the targeted entity that triggers a skill when a key is pressed"
)
public class OnKeyPressMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onPress",
      aliases = {"op"},
      description = "The name of the skill to trigger when the key is pressed."
   )
   private String onPressSkillName;
   @MythicField(
      name = "key",
      aliases = {"k"},
      description = "The key ID that triggers the skill."
   )
   private String keyId;
   private Optional<Skill> onPressSkill = Optional.empty();

   public OnKeyPressMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.onPressSkillName = mlc.getString(new String[]{"onPress", "op"});
      this.keyId = mlc.getString(new String[]{"key", "k"}).toLowerCase();
      this.keyId = SkillString.parseMessageSpecialChars(this.keyId);
      this.getManager().queueSecondPass(() -> {
         if (this.onPressSkillName != null) {
            this.onPressSkill = this.getManager().getSkill(file, this, this.onPressSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.getPlugin().getCompatibility().getAriKeys().isEmpty()) {
         return SkillResult.MISSING_COMPATIBILITY;
      } else if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         new OnKeyPressMechanic.Tracker(target, data);
         return SkillResult.SUCCESS;
      }
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(AbstractEntity entity, SkillMetadata data) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(AriKeyPressEvent.class)
               .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
               .filter(event -> event.getId().toString().equalsIgnoreCase(OnKeyPressMechanic.this.keyId))
               .handler(event -> {
                  SkillMetadata data = this.skillMetadata.deepClone();
                  data.setTrigger(BukkitAdapter.adapt(event.getPlayer()));
                  if (this.executeAuraSkill(OnKeyPressMechanic.this.onPressSkill, data)) {
                     this.consumeCharge();
                  }
               })
         );
         this.executeAuraSkill(OnKeyPressMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
