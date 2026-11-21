package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillManager;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Duration;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.conditions.ConditionAction;
import io.lumine.mythic.core.skills.cooldowns.ChargeRestoreOrder;
import io.lumine.mythic.core.skills.cooldowns.CooldownMode;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSkill implements SkillHolder {
   @Getter @Setter
   protected ThreadSafetyLevel threadSafetyLevel = ThreadSafetyLevel.EITHER;
   @Getter
   private final MythicBukkit plugin;
   @Getter
   private final SkillExecutor manager;
   private static final Object COOLDOWN_LOCK_FAILED = new Object();
   @Getter
   protected final UUID uuid;
   @Getter
   protected final File file;
   @Getter @Setter
   protected Pack pack = null;
   @Getter
   @MythicField(
      name = "cooldown",
      aliases = {"cd"},
      description = "The cooldown of the mechanic. Bare numbers are seconds.",
      defValue = "0"
   )
   protected PlaceholderDuration cooldown = null;
   @Getter
   @MythicField(
      name = "charges",
      description = "The number of uses the skill can store before it becomes unavailable.",
      defValue = "1"
   )
   protected int charges = 1;
   @Getter
   @MythicField(
      name = "cooldownMode",
      aliases = {"chargeCooldownMode"},
      description = "How spent charges recharge back to the skill.",
      defValue = "SEQUENTIAL"
   )
   protected CooldownMode cooldownMode = CooldownMode.SEQUENTIAL;
   @MythicField(
      name = "power",
      description = "The power multiplier of the mechanic",
      defValue = "1"
   )
   protected float power = 1.0F;
   @MythicField(
      name = "delay",
      description = "Delays the execution of the mechanic. Bare numbers are ticks.",
      defValue = "0"
   )
   protected PlaceholderDuration delay = PlaceholderDuration.of("0", DurationUnit.TICKS);
   @MythicField(
      name = "repeat",
      description = "The amount of repetitions the mechanic will do"
   )
   protected PlaceholderInt repeat = PlaceholderInt.of("0");
   @MythicField(
      name = "repeatInterval",
      aliases = {"repeatI"},
      description = "The duration that must elapse between repetitions. Bare numbers are ticks."
   )
   protected PlaceholderDuration repeatInterval = PlaceholderDuration.of("0", DurationUnit.TICKS);
   @MythicField(
      name = "targetInterval",
      aliases = {"targetI"},
      description = "The duration that must elapse between target selection. Bare numbers are ticks."
   )
   protected PlaceholderDuration targetInterval = PlaceholderDuration.of("0", DurationUnit.TICKS);
   @MythicField(
      name = "chance",
      description = "The likelihood of the mechanic executing. 1 = 100%, 0.5 = 50%"
   )
   protected PlaceholderFloat chance = PlaceholderFloat.of("1");
   @MythicField(
      name = "splitPower",
      aliases = {"powersplitbetweentargets", "powersplit"},
      description = "Whether to split the power between targets"
   )
   protected boolean powerSplitBetweenTargets = false;
   @MythicField(
      name = "sourceIsOrigin"
   )
   protected boolean sourceIsOrigin = false;
   @MythicField(
      name = "faulty",
      description = "Whether to use the old faulty vector calculation."
   )
   protected boolean faulty = true;
   @Getter
   protected Optional<SkillTargeter> targeter = Optional.empty();
   @MythicField(
      name = "targetCreative",
      description = "Whether to target creative players"
   )
   protected boolean target_creative = false;
   @Getter
   protected List<SkillCondition> conditions = null;
   @Getter
   protected List<SkillCondition> conditionsTarget = null;
   @Getter
   protected List<SkillCondition> conditionsTrigger = null;

   public AbstractSkill(SkillManager manager, File file) {
      this.plugin = (MythicBukkit)manager.getPlugin();
      this.manager = (SkillExecutor)manager;
      this.file = file;
      this.uuid = UUID.randomUUID();
   }

   public String getFilePath() {
      return this.file == null ? "Unknown" : this.file.getAbsolutePath();
   }

   public void triggerCooldown(SkillMetadata data) {
      if (this.cooldown != null) {
         this.setCooldown(data.getCaster(), this.cooldown.get(data));
      }
   }

   protected Object tryAcquireCooldownLock(SkillMetadata data) {
      if (!this.usesCooldownState(data)) {
         return null;
      } else {
         long deadline = System.nanoTime() + 1000000L;
         int spins = 0;

         while (true) {
            Object token = this.manager.tryAcquireCooldownLock(data.getCaster(), this);
            if (token != null) {
               return token;
            }

            if (System.nanoTime() >= deadline) {
               return COOLDOWN_LOCK_FAILED;
            }

            if ((++spins & 7) == 0) {
               LockSupport.parkNanos(50000L);
            } else {
               Thread.yield();
            }
         }
      }
   }

   protected boolean isCooldownLockFailed(Object token) {
      return token == COOLDOWN_LOCK_FAILED;
   }

   protected void releaseCooldownLock(SkillMetadata data, Object token) {
      if (token != null && token != COOLDOWN_LOCK_FAILED) {
         this.manager.releaseCooldownLock(data.getCaster(), this, token);
      }
   }

   public boolean onCooldown(SkillCaster skillCaster) {
      return skillCaster.isSkillOnCooldown(this);
   }

   public boolean isRecharging(SkillCaster skillCaster) {
      return this.manager.isSkillRecharging(skillCaster, this);
   }

   public double getCooldown(SkillCaster skillCaster) {
      long remaining = this.getCooldownMillis(skillCaster);
      if (remaining == Long.MAX_VALUE) {
         return Double.MAX_VALUE;
      } else {
         return remaining > 0L ? remaining / 1000.0 : 0.0;
      }
   }

   public long getCooldownMillis(SkillCaster skillCaster) {
      return (long)skillCaster.getSkillCooldownRemainingMillis(this);
   }

   public void setCooldown(SkillCaster skillCaster, double cooldown) {
      skillCaster.setSkillCooldownMillis(this, Duration.ofSeconds(cooldown).getMilliseconds());
   }

   public void setCooldown(SkillCaster skillCaster, Duration cooldown) {
      skillCaster.setSkillCooldownMillis(this, cooldown.getMilliseconds());
   }

   public int getAvailableCharges(SkillCaster skillCaster) {
      return this.manager.getAvailableSkillCharges(skillCaster, this);
   }

   public int getOverflowCharges(SkillCaster skillCaster) {
      return this.manager.getOverflowSkillCharges(skillCaster, this);
   }

   public void addCharges(SkillCaster skillCaster, int amount, ChargeRestoreOrder order, boolean overflow) {
      this.manager.addSkillCharges(skillCaster, this, amount, order, overflow);
   }

   public void addCharges(SkillCaster skillCaster, int amount) {
      this.addCharges(skillCaster, amount, ChargeRestoreOrder.LATEST, false);
   }

   protected void configureChargeCooldown(int charges, CooldownMode cooldownMode) {
      this.charges = Math.max(1, charges);
      this.cooldownMode = cooldownMode == null ? CooldownMode.SEQUENTIAL : cooldownMode;
   }

   protected long resolveCooldownMillis(SkillMetadata data) {
      return this.cooldown == null ? 0L : this.cooldown.get(data).getMilliseconds();
   }

   protected boolean usesCooldownState(SkillMetadata data) {
      return this.cooldownMode == CooldownMode.NONE || this.resolveCooldownMillis(data) > 0L;
   }

   protected boolean consumeCharge(SkillMetadata data) {
      return !this.usesCooldownState(data) ? true : this.manager.consumeSkillCharge(data.getCaster(), this, this.resolveCooldownMillis(data));
   }

   protected boolean rollChance(SkillMetadata data) {
      float chance = this.chance.get(data);
      return chance == 1.0F ? true : !(Numbers.randomDouble() > chance);
   }

   protected SkillTargeter parseSkillTargeter(String strTarget) {
      return this.getPlugin().getSkillManager().getTargeter(strTarget);
   }

   protected SkillCondition parseSkillCondition(String strCondition) {
      String search = strCondition.substring(1);
      boolean n = false;
      if (search.startsWith("!")) {
         search = search.substring(1);
         n = true;
      }

      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, ": Parsing SkillCondition {0}", search);
      SkillCondition cond = this.getPlugin().getSkillManager().getCondition(search);
      if (cond != null && n) {
         cond.setAction(ConditionAction.CANCEL);
      }

      return cond;
   }

   public void setTargetsCreativePlayers(boolean b) {
      this.target_creative = b;
   }

   public boolean targetsCreativePlayers() {
      return this.target_creative;
   }

   public SkillMetadata evaluateTargets(SkillMetadata data) {
      return this.targeter.isPresent() ? this.evaluateTargets(data, this.targeter.get()) : data;
   }

   protected SkillMetadata evaluateTargets(SkillMetadata data, SkillTargeter targeter) {
      return this.getPlugin().getSkillManager().processTargets(data, targeter, this.targetsCreativePlayers(), this.powerSplitBetweenTargets);
   }

   protected static class DelayedSkill implements Runnable {
      private SkillMetadata data;
      private Queue<SkillMechanic> skills;
      private boolean cancelled;

      public DelayedSkill(SkillMetadata data, Queue<SkillMechanic> skills) {
         this.data = data;
         this.skills = skills;
         this.cancelled = false;
      }

      public void cancel() {
         this.skills = null;
         this.cancelled = true;
      }

      @Override
      public void run() {
         if (!this.cancelled) {
            try {
               if (this.data.getCaster().getEntity().isDead()
                  && !this.data.getCause().equals(SkillTriggers.DEATH)
                  && !this.data.getCause().equals(SkillTriggers.DESPAWNED)
                  && !this.data.getCause().equals(SkillTriggers.EXPLODE)) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "! Mob is dead, cancelling skill (cause = {0})", this.data.getCause());
                  this.cancel();
                  return;
               }

               MetaSkill.execute(this.data, this.skills);
            } catch (NullPointerException e) {
               this.cancel();
               e.printStackTrace();
            }
         }
      }
   }
}
