package io.lumine.mythic.lib.skill;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.player.cooldown.CooldownObject;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public abstract class Skill implements CooldownObject {
   private final TriggerType trigger;
   private final SkillHandler<?> handler;

   public Skill(@NotNull TriggerType trigger) {
      this.trigger = Objects.requireNonNull(trigger, "Trigger cannot be null");
      this.handler = null;
   }

   /** A skill that runs the given handler; its trigger is {@link TriggerType#API} unless {@link #getTrigger()} is overridden. */
   public Skill(@NotNull SkillHandler<?> handler) {
      this.trigger = TriggerType.API;
      this.handler = Objects.requireNonNull(handler, "Skill handler cannot be null");
   }

   /** For subclasses which override both {@link #getHandler()} and {@link #getTrigger()}. */
   public Skill() {
      this.trigger = TriggerType.API;
      this.handler = null;
   }

   @NotNull
   public SkillResult cast(@NotNull MMOPlayerData caster, @NotNull TriggerType trigger) {
      return this.cast(new TriggerMetadata(caster, trigger));
   }

   @NotNull
   public SkillResult cast(@NotNull MMOPlayerData caster) {
      return this.cast(new TriggerMetadata(caster, this.trigger));
   }

   @NotNull
   public SkillResult cast(@NotNull SynchronizedDataHolder caster) {
      return this.cast(caster.getMMOPlayerData());
   }

   @NotNull
   public SkillResult cast(@NotNull TriggerMetadata triggerMeta) {
      return this.cast(triggerMeta.toSkillMetadata(this));
   }

   @NotNull
   public <T extends SkillResult> SkillResult cast(@NotNull SkillMetadata meta) {
      SkillHandler<T> handler = (SkillHandler<T>)this.getHandler();
      T result = handler.getResult(meta);
      if (!result.isSuccessful()) {
         return result;
      }

      if (!this.getResult(meta)) {
         return result;
      }

      PlayerCastSkillEvent called = new PlayerCastSkillEvent(meta, result);
      Bukkit.getPluginManager().callEvent(called);
      if (called.isCancelled()) {
         return result;
      }

      int delayTicks = (int)(meta.getParameter("delay") * 20.0);
      if (delayTicks <= 0) {
         this.castInstantly(meta, result);
      } else {
         new CastingDelayHandler(meta, result);
      }

      return result;
   }

   @NotNull
   public <T extends SkillResult> void castInstantly(@NotNull SkillMetadata meta, @NotNull T result) {
      this.whenCast(meta);
      ((SkillHandler<T>)this.getHandler()).whenCast(result, meta);
      Bukkit.getPluginManager().callEvent(new SkillCastEvent(meta, result));
   }

   @NotNull
   public abstract boolean getResult(@NotNull SkillMetadata var1);

   public abstract void whenCast(@NotNull SkillMetadata var1);

   @NotNull
   public SkillHandler<?> getHandler() {
      if (this.handler == null) {
         throw new UnsupportedOperationException("This skill does not provide a skill handler");
      }

      return this.handler;
   }

   @NotNull
   public TriggerType getTrigger() {
      return this.trigger;
   }

   @Deprecated
   public double getModifier(String path) {
      return this.getParameter(path);
   }

   public double getParameter(String path) {
      return this.getModifier(path);
   }

   @Override
   public String getCooldownPath() {
      return "skill_" + this.getHandler().getId();
   }
}
