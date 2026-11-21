package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Optional;
import org.bukkit.Input;
import org.bukkit.event.player.PlayerInputEvent;

public class OnPlayerInputAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onInputSkill;
   private final boolean requireForward;
   private final boolean requireBackward;
   private final boolean requireLeft;
   private final boolean requireRight;
   private final boolean requireAnyMovement;
   private final boolean requireJump;
   private final boolean requireSneak;
   private final boolean requireSprint;

   public OnPlayerInputAuraComponent(
      Aura.AuraTracker tracker,
      Optional<Skill> onInputSkill,
      boolean requireForward,
      boolean requireBackward,
      boolean requireLeft,
      boolean requireRight,
      boolean requireJump,
      boolean requireSneak,
      boolean requireSprint,
      boolean requireAnyMovement
   ) {
      this.tracker = tracker;
      this.onInputSkill = onInputSkill;
      this.requireForward = requireForward;
      this.requireBackward = requireBackward;
      this.requireLeft = requireLeft;
      this.requireRight = requireRight;
      this.requireAnyMovement = requireAnyMovement;
      this.requireJump = requireJump;
      this.requireSneak = requireSneak;
      this.requireSprint = requireSprint;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty() && maybeEntity.get().isPlayer()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(PlayerInputEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .handler(event -> {
                     Input input = event.getInput();
                     boolean forward = input.isForward();
                     boolean backward = input.isBackward();
                     boolean left = input.isLeft();
                     boolean right = input.isRight();
                     boolean jumping = input.isJump();
                     boolean sneaking = input.isSneak();
                     boolean sprinting = input.isSprint();
                     if (!this.requireForward || forward) {
                        if (!this.requireBackward || backward) {
                           if (!this.requireLeft || left) {
                              if (!this.requireRight || right) {
                                 if (!this.requireAnyMovement || forward || backward || left || right) {
                                    if (!this.requireJump || jumping) {
                                       if (!this.requireSneak || sneaking) {
                                          if (!this.requireSprint || sprinting) {
                                             SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                                             meta.setTrigger(BukkitAdapter.adapt(event.getPlayer()));
                                             meta.getVariables().putInt("input-forward", forward ? 1 : 0);
                                             meta.getVariables().putInt("input-backward", backward ? 1 : 0);
                                             meta.getVariables().putInt("input-left", left ? 1 : 0);
                                             meta.getVariables().putInt("input-right", right ? 1 : 0);
                                             meta.getVariables().putInt("input-jump", jumping ? 1 : 0);
                                             meta.getVariables().putInt("input-sneak", sneaking ? 1 : 0);
                                             meta.getVariables().putInt("input-sprint", sprinting ? 1 : 0);
                                             if (this.tracker.executeAuraSkill(this.onInputSkill, meta)) {
                                                this.tracker.consumeCharge();
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  })
            );
      }
   }

   @MythicAuraComponent(
      name = "oninput",
      aliases = {"playerinput"},
      description = "Triggers a skill on player input"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onInputSkillName;
      private Optional<Skill> onInputSkill = Optional.empty();
      private final boolean requireForward;
      private final boolean requireBackward;
      private final boolean requireLeft;
      private final boolean requireRight;
      private final boolean requireAnyMovement;
      private final boolean requireJump;
      private final boolean requireSneak;
      private final boolean requireSprint;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onInputSkillName = mlc.getString(new String[]{"oninputskill", "oninput", "oi"});
         this.requireForward = mlc.getBoolean(new String[]{"forward"}, false);
         this.requireBackward = mlc.getBoolean(new String[]{"backward"}, false);
         this.requireLeft = mlc.getBoolean(new String[]{"left"}, false);
         this.requireRight = mlc.getBoolean(new String[]{"right"}, false);
         this.requireAnyMovement = mlc.getBoolean(new String[]{"requireanymovement", "anymovement", "move"}, false);
         this.requireJump = mlc.getBoolean(new String[]{"requirejump", "jump"}, false);
         this.requireSneak = mlc.getBoolean(new String[]{"requiresneak", "sneak"}, false);
         this.requireSprint = mlc.getBoolean(new String[]{"requiresprint", "sprint"}, false);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onInputSkillName != null) {
               this.onInputSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onInputSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnPlayerInputAuraComponent(
            tracker,
            this.onInputSkill,
            this.requireForward,
            this.requireBackward,
            this.requireLeft,
            this.requireRight,
            this.requireJump,
            this.requireSneak,
            this.requireSprint,
            this.requireAnyMovement
         );
      }
   }
}
