package io.lumine.mythic.lib.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.event.mitigation.PlayerBlockEvent;
import io.lumine.mythic.lib.api.event.mitigation.PlayerDodgeEvent;
import io.lumine.mythic.lib.api.event.mitigation.PlayerParryEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.player.cooldown.CooldownType;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VSound;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MitigationMechanics implements Listener {
   private static final Random RANDOM = new Random();
   private static final List<DamageCause> MITIGATION_CAUSES = Arrays.asList(
      DamageCause.PROJECTILE, DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_EXPLOSION, DamageCause.ENTITY_SWEEP_ATTACK
   );
   private boolean actionBarMessage;
   private double dodgeKnockback;
   private double parryKnockback;
   private double parryDefaultCooldown;
   private double blockDefaultCooldown;
   private double dodgeDefaultCooldown;
   private String parryMessage;
   private String blockMessage;
   private String dodgeMessage;

   public MitigationMechanics() {
      this.reload();
   }

   public void reload() {
      this.dodgeKnockback = MythicLib.plugin.getConfig().getDouble("mitigation.dodge.knockback");
      this.parryKnockback = MythicLib.plugin.getConfig().getDouble("mitigation.parry.knockback");
      this.parryDefaultCooldown = MythicLib.plugin.getConfig().getDouble("mitigation.parry.cooldown");
      this.blockDefaultCooldown = MythicLib.plugin.getConfig().getDouble("mitigation.block.cooldown");
      this.dodgeDefaultCooldown = MythicLib.plugin.getConfig().getDouble("mitigation.dodge.cooldown");
      this.parryMessage = MythicLib.plugin.getConfig().getString("mitigation.message.parry");
      this.dodgeMessage = MythicLib.plugin.getConfig().getString("mitigation.message.dodge");
      this.blockMessage = MythicLib.plugin.getConfig().getString("mitigation.message.block");
      this.actionBarMessage = MythicLib.plugin.getConfig().getBoolean("mitigation.message.action-bar");
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void applyMitigation(AttackEvent event) {
      if (MITIGATION_CAUSES.contains(event.toBukkit().getCause())) {
         MMOPlayerData playerData = MMOPlayerData.getOrNull(event.getEntity());
         if (playerData != null) {
            Player player = (Player)event.getEntity();
            StatMap stats = playerData.getStatMap();
            double dodgeRating = stats.getStat("DODGE_RATING") / 100.0;
            if (RANDOM.nextDouble() < dodgeRating && !playerData.isOnCooldown(CooldownType.DODGE)) {
               PlayerDodgeEvent mitigationEvent = new PlayerDodgeEvent(playerData, event.toBukkit());
               Bukkit.getPluginManager().callEvent(mitigationEvent);
               if (!mitigationEvent.isCancelled()) {
                  this.sendMessage(player, this.dodgeMessage, "damage", MythicLib.plugin.getMMOConfig().decimal.format(event.getDamage().getDamage()));
                  playerData.applyCooldown(CooldownType.DODGE, this.calculateCooldown(this.dodgeDefaultCooldown, stats.getStat("DODGE_COOLDOWN_REDUCTION")));
                  event.setCancelled(true);
                  player.setNoDamageTicks(10);
                  player.getWorld().playSound(player.getLocation(), VSound.ENTITY_ENDER_DRAGON_FLAP.get(), 2.0F, 1.0F);
                  player.getWorld().spawnParticle(VParticle.EXPLOSION.get(), player.getLocation(), 16, 0.0, 0.0, 0.0, 0.06);
                  if (this.dodgeKnockback > 0.0) {
                     player.setVelocity(this.getVector(player, event).multiply(-0.85 * this.dodgeKnockback).setY(0.3));
                  }
               }
            } else {
               double parryRating = stats.getStat("PARRY_RATING") / 100.0;
               if (RANDOM.nextDouble() < parryRating && !playerData.isOnCooldown(CooldownType.PARRY)) {
                  PlayerParryEvent mitigationEvent = new PlayerParryEvent(playerData, event.toBukkit());
                  Bukkit.getPluginManager().callEvent(mitigationEvent);
                  if (!mitigationEvent.isCancelled()) {
                     playerData.applyCooldown(CooldownType.PARRY, this.calculateCooldown(this.parryDefaultCooldown, stats.getStat("PARRY_COOLDOWN_REDUCTION")));
                     event.setCancelled(true);
                     player.setNoDamageTicks(10);
                     this.sendMessage(player, this.parryMessage, "damage", MythicLib.plugin.getMMOConfig().decimal.format(event.getDamage().getDamage()));
                     player.getWorld().playSound(player.getLocation(), VSound.ENTITY_ENDER_DRAGON_FLAP.get(), 2.0F, 1.0F);
                     player.getWorld().spawnParticle(VParticle.EXPLOSION.get(), player.getLocation(), 16, 0.0, 0.0, 0.0, 0.06);
                     if (this.parryKnockback > 0.0
                        && event.toBukkit() instanceof EntityDamageByEntityEvent
                        && ((EntityDamageByEntityEvent)event.toBukkit()).getDamager() instanceof LivingEntity) {
                        LivingEntity attacker = (LivingEntity)((EntityDamageByEntityEvent)event.toBukkit()).getDamager();
                        attacker.setVelocity(
                           UtilityMethods.safeNormalize(attacker.getLocation().toVector().subtract(player.getLocation().toVector()))
                              .setY(0.35)
                              .multiply(this.parryKnockback)
                        );
                     }
                  }
               } else {
                  double blockRating = stats.getStat("BLOCK_RATING") / 100.0;
                  if (RANDOM.nextDouble() < blockRating && !playerData.isOnCooldown(CooldownType.BLOCK)) {
                     double blockPower = stats.getStat("BLOCK_POWER") / 100.0;
                     PlayerBlockEvent mitigationEvent = new PlayerBlockEvent(playerData, event.toBukkit(), blockPower);
                     Bukkit.getPluginManager().callEvent(mitigationEvent);
                     if (mitigationEvent.isCancelled()) {
                        return;
                     }

                     playerData.applyCooldown(CooldownType.BLOCK, this.calculateCooldown(this.blockDefaultCooldown, stats.getStat("BLOCK_COOLDOWN_REDUCTION")));
                     this.sendMessage(
                        player,
                        this.blockMessage,
                        "damage",
                        MythicLib.plugin.getMMOConfig().decimal.format(mitigationEvent.getDamageBlocked()),
                        "power",
                        MythicLib.plugin.getMMOConfig().decimal.format(mitigationEvent.getPower() * 100.0)
                     );
                     event.getDamage().multiplicativeModifier(1.0 - mitigationEvent.getPower());
                     player.getWorld().playSound(player.getLocation(), VSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.get(), 2.0F, 1.0F);
                     double yaw = this.getYaw(player, this.getVector(player, event)) + 90.0;

                     for (double j = yaw - 90.0; j < yaw + 90.0; j += 5.0) {
                        for (double y = 0.0; y < 2.0; y += 0.1) {
                           player.getWorld()
                              .spawnParticle(
                                 VParticle.REDSTONE.get(),
                                 player.getLocation().clone().add(Math.cos(Math.toRadians(j)) * 0.7, y, Math.sin(Math.toRadians(j)) * 0.7),
                                 1,
                                 new DustOptions(Color.GRAY, 1.0F)
                              );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void sendMessage(Player player, String format, Object... placeholders) {
      if (format != null && !format.isEmpty()) {
         format = ChatColor.translateAlternateColorCodes('&', format);

         for (int i = 0; i < placeholders.length; i += 2) {
            format = format.replace("#" + placeholders[i].toString() + "#", placeholders[i + 1].toString());
         }

         if (this.actionBarMessage) {
            MythicLib.plugin.getVersion().getWrapper().sendActionBar(player, format);
         } else {
            player.sendMessage(format);
         }
      }
   }

   private double calculateCooldown(double cooldown, double reductionStat) {
      return cooldown * (1.0 - reductionStat / 100.0);
   }

   @NotNull
   private Vector getVector(LivingEntity victim, AttackEvent event) {
      StatProvider attacker = event.getAttack().getAttacker();
      if (attacker == null) {
         Entity damager = event.toBukkit() instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent)event.toBukkit()).getDamager() : null;
         return damager == null
            ? UtilityMethods.safeNormalize(damager.getLocation().subtract(victim.getLocation()).toVector())
            : victim.getEyeLocation().getDirection();
      } else {
         return UtilityMethods.safeNormalize(attacker.getEntity().getLocation().subtract(victim.getLocation()).toVector());
      }
   }

   private double getYaw(Entity player, Vector vec) {
      return new Location(player.getWorld(), vec.getX(), vec.getY(), vec.getZ()).setDirection(vec).getYaw();
   }
}
