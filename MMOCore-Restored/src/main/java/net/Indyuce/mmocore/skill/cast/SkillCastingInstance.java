package net.Indyuce.mmocore.skill.cast;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.List;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public abstract class SkillCastingInstance extends BukkitRunnable implements Listener {
   protected final PlayerData caster;
   private final SkillCastingHandler handler;
   private static final int RUNNABLE_PERIOD = 10;
   private List<BoundSkillInfo> activeSkills;
   private boolean open = true;
   private int j;
   private int sinceLastActivity;
   private static final int PARTICLES_PER_TICK = 2;

   public SkillCastingInstance(@NotNull SkillCastingHandler handler, @NotNull PlayerData caster) {
      this.handler = handler;
      this.caster = caster;
      this.runTaskTimer(MMOCore.plugin, 0L, 1L);
      Bukkit.getPluginManager().registerEvents(this, MMOCore.plugin);
   }

   public PlayerData getCaster() {
      return this.caster;
   }

   public void close() {
      Validate.isTrue(this.open, "Skill casting already closed");
      this.open = false;
      HandlerList.unregisterAll(this);
      this.cancel();
   }

   public void refreshTimeOut() {
      this.sinceLastActivity = 0;
   }

   @NotNull
   public List<BoundSkillInfo> getActiveSkills() {
      if (this.activeSkills == null) {
         this.activeSkills = this.caster.getBoundSkills().values().stream().filter(bound -> !bound.isPassive()).collect(Collectors.toList());
      }

      return this.activeSkills;
   }

   public void run() {
      if (!UtilityMethods.isInvalidated(this.caster.getMMOPlayerData()) && this.caster.hasActiveSkillBound()) {
         if (this.handler.doesTimeOut() && this.sinceLastActivity++ > this.handler.getTimeoutDelay()) {
            this.caster.leaveSkillCasting(true);
         } else {
            if (this.caster.getProfess().getCastParticle() != null) {
               for (int k = 0; k < 2; k++) {
                  double a = (2 * this.j + k) / 4.0;
                  this.caster
                     .getProfess()
                     .getCastParticle()
                     .display(this.caster.getPlayer().getLocation().add(Math.cos(a), 1.0 + Math.sin(a / 3.0) / 1.3, Math.sin(a)));
               }
            }

            if (this.j++ % 10 == 0) {
               this.activeSkills = null;
               this.onTick();
            }
         }
      } else {
         this.caster.leaveSkillCasting(true);
      }
   }

   public abstract void onTick();
}
