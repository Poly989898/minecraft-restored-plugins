package net.Indyuce.mmocore.skill.list;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.EntityLocationType;
import io.lumine.mythic.lib.util.ParabolicProjectile;
import io.lumine.mythic.lib.version.VParticle;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Ambers extends SkillHandler<SimpleSkillResult> implements Listener {
   private final boolean legacyParticles = MythicLib.plugin.getVersion().isUnder(new int[]{1, 20, 5});

   public Ambers() {
      super(false);
      this.registerModifiers(new String[]{"percent"});
   }

   @NotNull
   public SimpleSkillResult getResult(SkillMetadata meta) {
      return new SimpleSkillResult(meta.hasAttackSource() && meta.hasTargetEntity() && meta.getTargetEntityOrNull() instanceof LivingEntity);
   }

   public void whenCast(SimpleSkillResult result, SkillMetadata skillMeta) {
      LivingEntity target = (LivingEntity)skillMeta.getTargetEntityOrNull();
      Location loc = target.getLocation();
      double a = RANDOM.nextDouble() * 2.0 * Math.PI;
      new Ambers.Amber(
         skillMeta.getCaster().getData(),
         EntityLocationType.BODY.getLocation(target),
         loc.clone().add(4.0 * Math.cos(a), 0.0, 4.0 * Math.sin(a)),
         skillMeta.getParameter("percent")
      );
   }

   @EventHandler
   public void spawnAmber(PlayerAttackEvent event) {
      MMOPlayerData data = event.getAttacker().getData();
      if (event.getAttack().getDamage().hasType(DamageType.SKILL)) {
         PassiveSkill passive = data.getPassiveSkillMap().getSkill(this);
         if (passive != null) {
            passive.getTriggeredSkill().cast(new TriggerMetadata(event, TriggerType.API));
         }
      }
   }

   class Amber extends BukkitRunnable {
      private final Location loc;
      private final MMOPlayerData data;
      private final double percent;
      private int j;
      private static final double RADIUS_SQUARED = 3.0;

      private Amber(MMOPlayerData data, Location source, Location loc, double percent) {
         this.loc = loc;
         this.data = data;
         this.percent = percent / 100.0;
         Ambers.Amber amber = this;
         new ParabolicProjectile(source, loc, VParticle.REDSTONE.get(), () -> amber.runTaskTimer(MythicLib.plugin, 0L, 3L), 1, Color.ORANGE, 1.3F);
      }

      public void run() {
         if (this.j++ <= 66 && this.data.isOnline() && this.data.getPlayer().getWorld().equals(this.loc.getWorld())) {
            if (this.data.getPlayer().getLocation().add(0.0, 1.0, 0.0).distanceSquared(this.loc) < 3.0) {
               this.data.getPlayer().playSound(this.data.getPlayer().getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0F, 1.0F);
               PlayerData playerData = PlayerData.get(this.data);
               double missingMana = this.data.getStatMap().getStat("MAX_MANA") - playerData.getMana();
               playerData.giveMana(missingMana * this.percent, PlayerResourceUpdateEvent.UpdateReason.SKILL_REGENERATION);
               this.cancel();
            } else {
               for (int j = 0; j < 5; j++) {
                  if (Ambers.this.legacyParticles) {
                     this.loc.getWorld().spawnParticle(VParticle.ENTITY_EFFECT.get(), this.loc, 0, 1.0, 0.647, 0.0, 1.0);
                  } else {
                     this.loc.getWorld().spawnParticle(VParticle.ENTITY_EFFECT.get(), this.loc, 0, 1.0, 0.647, 0.0, 1.0, Color.ORANGE);
                  }
               }

               this.loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), this.loc, 1, new DustOptions(Color.ORANGE, 1.3F));
            }
         } else {
            this.cancel();
         }
      }
   }
}
