package io.lumine.mythic.core.skills.mechanics;

import eu.asangarin.arikeys.api.AriKeyReleaseEvent;
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
   name = "onKeyRelease",
   aliases = {"keyRelease", "kr"},
   description = "Applies an aura to the targeted entity that triggers a skill when a key is released"
)
public class OnKeyReleaseMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onRelease",
      aliases = {"or"},
      description = "The name of the skill to trigger when the key is released."
   )
   private String onReleaseSkillName;
   @MythicField(
      name = "key",
      aliases = {"k"},
      description = "The key ID that triggers the skill."
   )
   private String keyId;
   private Optional<Skill> onReleaseSkill = Optional.empty();

   public OnKeyReleaseMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.onReleaseSkillName = mlc.getString(new String[]{"onRelease", "or"});
      this.keyId = mlc.getString(new String[]{"key", "k"}).toLowerCase();
      this.keyId = SkillString.parseMessageSpecialChars(this.keyId);
      this.getManager().queueSecondPass(() -> {
         if (this.onReleaseSkillName != null) {
            this.onReleaseSkill = this.getManager().getSkill(file, this, this.onReleaseSkillName);
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
         new OnKeyReleaseMechanic.Tracker(target, data);
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
            Events.subscribe(AriKeyReleaseEvent.class)
               .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
               .filter(event -> event.getId().toString().equalsIgnoreCase(OnKeyReleaseMechanic.this.keyId))
               .handler(event -> {
                  SkillMetadata data = this.skillMetadata.deepClone();
                  data.setTrigger(BukkitAdapter.adapt(event.getPlayer()));
                  if (this.executeAuraSkill(OnKeyReleaseMechanic.this.onReleaseSkill, data)) {
                     this.consumeCharge();
                  }
               })
         );
         this.executeAuraSkill(OnKeyReleaseMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
