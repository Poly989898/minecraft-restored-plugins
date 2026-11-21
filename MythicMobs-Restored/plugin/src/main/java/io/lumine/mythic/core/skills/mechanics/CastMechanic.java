package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.core.holograms.types.CastBar;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "cast",
   description = "Casts a metaskill with various options"
)
public class CastMechanic extends Aura implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "onCast",
      aliases = {"oc"},
      version = "4.6",
      description = "Skill to execute if the cast finishes successfully"
   )
   protected Optional<Skill> onCastSkill = Optional.empty();
   @MythicField(
      name = "onInterrupted",
      aliases = {"oi"},
      version = "4.6",
      description = "Skill to execute if the cast is interrupted"
   )
   protected Optional<Skill> onInterruptedSkill = Optional.empty();
   @MythicField(
      name = "onNoTarget",
      aliases = {"ont"},
      version = "4.6",
      description = "Skill to execute if no target is found"
   )
   protected Optional<Skill> onNoTargetsSkill = Optional.empty();
   protected String onCastSkillName;
   protected String onInterruptedSkillName;
   protected String onNoTargetsSkillName;
   @MythicField(
      name = "skillname",
      aliases = {"sn"},
      version = "4.6",
      description = "Display name of the spell in the cast bar"
   )
   protected PlaceholderString spellName;
   @MythicField(
      name = "showCastBar",
      aliases = {"cb"},
      defValue = "true",
      version = "4.6",
      description = "Whether to show the cast bar. Requires a compatible hologram plugin."
   )
   protected boolean showCastBar;
   @MythicField(
      name = "cancelOnMove",
      aliases = {"com"},
      defValue = "false",
      version = "4.6",
      description = "Whether to cancel the aura if the caster moves"
   )
   protected boolean cancelOnMove;
   protected String castingText;

   public CastMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onCastSkillName = mlc.getString(new String[]{"oncastskill", "oncast", "oc"});
      this.onInterruptedSkillName = mlc.getString(new String[]{"oninterruptedskill", "oninterrupted", "oninterrupt", "oi"});
      this.onNoTargetsSkillName = mlc.getString(new String[]{"onnotargetsskill", "onnotargets", "onnotarget", "ont"});
      this.spellName = mlc.getPlaceholderString(new String[]{"skillname", "spellname", "sn"}, null);
      this.showCastBar = mlc.getBoolean(new String[]{"showcastbar", "castbar", "cb"}, true);
      this.cancelOnMove = mlc.getBoolean(new String[]{"cancelonmove", "com"}, false);
      this.auraName = Optional.of(PlaceholderString.of("#casting"));
      this.charges = PlaceholderInt.of("1");
      this.maxStacks = PlaceholderInt.of("1");
      this.mergeAll = true;
      if (this.spellName == null) {
         this.castingText = ColorString.get("&eCasting...");
      } else {
         this.castingText = ColorString.get("&eCasting &6" + this.spellName.toString() + "&e...");
      }

      this.getManager().queueSecondPass(() -> {
         if (this.onCastSkillName != null) {
            this.onCastSkill = this.getManager().getSkill(file, this, this.onCastSkillName);
         }

         if (this.onInterruptedSkillName != null) {
            this.onInterruptedSkill = this.getManager().getSkill(file, this, this.onInterruptedSkillName);
         }

         if (this.onNoTargetsSkillName != null) {
            this.onNoTargetsSkill = this.getManager().getSkill(file, this, this.onNoTargetsSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new CastMechanic.CastTracker(target, data);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new CastMechanic.CastTracker(target, data);
      return SkillResult.SUCCESS;
   }

   public void failNoTargets(SkillMetadata data) {
      if (this.onNoTargetsSkill != null) {
         if (this.onNoTargetsSkill.isPresent()) {
            data = data.deepClone();
            data.setEntityTarget(data.getCaster().getEntity());
            if (this.onNoTargetsSkill.get().isUsable(data)) {
               this.onNoTargetsSkill.get().execute(data);
            }
         }
      }
   }

   public class CastTracker extends Aura.AuraTracker {
      private CastBar castBar = null;

      public CastTracker(AbstractEntity target, SkillMetadata data) {
         super(data.getCaster(), target, data);
         this.start();
      }

      public CastTracker(AbstractLocation target, SkillMetadata data) {
         super(data.getCaster(), target, data);
         this.start();
      }

      @Override
      public void auraStart() {
         if (CastMechanic.this.cancelOnMove && this.skillMetadata.getCaster().getEntity().isPlayer()) {
            this.components.accept(Events.subscribe(PlayerMoveEvent.class).handler(event -> {
               if (event.getPlayer().getUniqueId().equals(this.getCasterUUID())) {
                  Location from = event.getFrom();
                  Location to = event.getTo();
                  if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
                     this.terminate();
                  }
               }
            }));
         }

         if (CastMechanic.this.showCastBar) {
            Schedulers.sync().run(() -> {
               if (!this.hasTerminated()) {
                  this.castBar = CastMechanic.this.getPlugin().getHologramManager().createCastBar(this, CastMechanic.this.castingText);
                  this.components.accept(this.castBar);
               }
            });
         }

         this.executeAuraSkill(CastMechanic.this.onStartSkill, this.skillMetadata);
      }

      @Override
      public void auraTick() {
         if (CastMechanic.this.showCastBar && this.castBar != null) {
            this.castBar.setCastPercent((double)this.ticksRemaining / this.startDuration);
         }

         this.executeAuraSkill(CastMechanic.this.onTickSkill, this.skillMetadata, true);
      }

      @Override
      public void auraStop() {
         if (this.ticksRemaining <= 0) {
            if (CastMechanic.this.showCastBar && this.castBar != null) {
               this.castBar.setFinished();
            }

            this.onCast();
         } else {
            if (CastMechanic.this.showCastBar && this.castBar != null) {
               this.castBar.setInterrupted();
            }

            this.onInterrupted();
         }

         if (CastMechanic.this.doEndSkillOnTerminate) {
            this.executeAuraSkill(CastMechanic.this.onEndSkill, this.skillMetadata);
         }
      }

      public void onCast() {
         this.executeAuraSkill(CastMechanic.this.onCastSkill, this.skillMetadata);
      }

      public void onInterrupted() {
         this.executeAuraSkill(CastMechanic.this.onInterruptedSkill, this.skillMetadata);
      }
   }
}
