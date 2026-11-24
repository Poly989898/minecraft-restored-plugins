package net.Indyuce.mmocore.skill.cast.handler;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.util.SoundObject;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.PlayerKeyPressEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.cast.Keybind;
import net.Indyuce.mmocore.skill.cast.SkillCastingHandler;
import net.Indyuce.mmocore.skill.cast.SkillCastingInstance;
import net.Indyuce.mmocore.skill.cast.SkillCastingMode;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillScroller extends SkillCastingHandler {
   private final Keybind enterKey;
   private final Keybind castKey;
   private final Keybind scrollKey;
   private final Keybind scrollBackKey;
   @Nullable
   private final SoundObject enterSound;
   @Nullable
   private final SoundObject changeSound;
   @Nullable
   private final SoundObject changeBackSound;
   @Nullable
   private final SoundObject leaveSound;
   private final String actionBarFormat;
   private final boolean ignoreSneak;
   private final boolean quitOnCast;
   private final boolean quitOnSwitchEmpty;

   public SkillScroller(@NotNull ConfigurationSection config) {
      super(config);
      this.enterSound = config.contains("sound.enter") ? new SoundObject(config.getConfigurationSection("sound.enter")) : null;
      this.changeSound = config.contains("sound.change") ? new SoundObject(config.getConfigurationSection("sound.change")) : null;
      this.changeBackSound = config.contains("sound.change-back") ? new SoundObject(config.getConfigurationSection("sound.change-back")) : null;
      this.leaveSound = config.contains("sound.leave") ? new SoundObject(config.getConfigurationSection("sound.leave")) : null;
      this.actionBarFormat = config.getString("action-bar-format", "CLICK TO CAST: {selected}");
      this.ignoreSneak = config.getBoolean("ignore-sneak");
      this.quitOnCast = config.getBoolean("quit-on-cast");
      this.quitOnSwitchEmpty = config.getBoolean("quit-on-switch-empty-hand");
      this.enterKey = Objects.requireNonNull(Keybind.fromConfig(config.get("enter-key")), "Could not find enter key");
      this.castKey = Objects.requireNonNull(Keybind.fromConfig(config.get("cast-key")), "Could not find cast key");
      this.scrollKey = Keybind.fromConfig(config.get("scroll-key"));
      this.scrollBackKey = Keybind.fromConfig(config.get("scroll-back-key"));
   }

   @Override
   public SkillCastingInstance newInstance(@NotNull PlayerData player) {
      return new SkillScroller.CustomSkillCastingInstance(player);
   }

   @Override
   public SkillCastingMode getCastingMode() {
      return SkillCastingMode.SKILL_SCROLLER;
   }

   @EventHandler
   public void whenPressingKey(PlayerKeyPressEvent event) {
      PlayerData playerData = event.getData();
      Player player = playerData.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE || MMOCore.plugin.configManager.canCreativeCast) {
         if (!this.ignoreSneak || !player.isSneaking()) {
            if (this.enterKey.matches(event)) {
               if (playerData.isCasting()) {
                  if (event.getPressed().shouldCancelEvent()) {
                     event.setCancelled(true);
                  }

                  if (!playerData.leaveSkillCasting()) {
                     return;
                  }

                  if (this.leaveSound != null) {
                     this.leaveSound.playTo(player);
                  }

                  return;
               }

               if (!playerData.hasActiveSkillBound()) {
                  return;
               }

               if (event.getPressed().shouldCancelEvent()) {
                  event.setCancelled(true);
               }

               if (!playerData.setSkillCasting()) {
                  return;
               }

               if (this.enterSound != null) {
                  this.enterSound.playTo(player);
               }
            }

            if (this.castKey.matches(event) && playerData.isCasting()) {
               if (event.getPressed().shouldCancelEvent()) {
                  event.setCancelled(true);
               }

               SkillScroller.CustomSkillCastingInstance casting = (SkillScroller.CustomSkillCastingInstance)playerData.getSkillCasting();
               PlayerMetadata caster = playerData.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
               SkillResult result = casting.getSelected().toCastable(playerData).cast(new TriggerMetadata(caster, null, null));
               if (result.isSuccessful() && this.quitOnCast) {
                  playerData.leaveSkillCasting();
               }
            }
         }
      }
   }

   private static int remainder(int x, int n) {
      while (x < 0) {
         x += n;
      }

      while (x >= n) {
         x -= n;
      }

      return x;
   }

   public class CustomSkillCastingInstance extends SkillCastingInstance {
      CustomSkillCastingInstance(PlayerData caster) {
         super(SkillScroller.this, caster);
      }

      @Override
      public void onTick() {
         String skillName = this.getSelected().getSkill().getName();
         String actionBarFormat = MythicLib.plugin
            .getPlaceholderParser()
            .parse(this.getCaster().getPlayer(), SkillScroller.this.actionBarFormat.replace("{selected}", skillName));
         this.getCaster().displayActionBar(actionBarFormat);
      }

      public ClassSkill getSelected() {
         return this.getActiveSkills().get(this.caster.permSkillScrollIndex).getClassSkill();
      }

      @EventHandler
      public void onKeyPress(PlayerKeyPressEvent event) {
         if (SkillScroller.this.scrollKey != null) {
            if (event.getData().equals(this.getCaster())) {
               if (!SkillScroller.this.ignoreSneak || !event.getPlayer().isSneaking()) {
                  if (!this.caster.hasActiveSkillBound()) {
                     this.caster.leaveSkillCasting(true);
                  } else {
                     int delta;
                     if (SkillScroller.this.scrollKey.matches(event)) {
                        delta = 1;
                     } else {
                        if (SkillScroller.this.scrollBackKey == null || !SkillScroller.this.scrollBackKey.matches(event)) {
                           return;
                        }

                        delta = -1;
                     }

                     event.setCancelled(true);
                     this.scrollOf(delta);
                  }
               }
            }
         }
      }

      @EventHandler
      public void onScroll(PlayerItemHeldEvent event) {
         if (event.getPlayer().equals(this.getCaster().getPlayer())) {
            if (SkillScroller.this.quitOnSwitchEmpty && UtilityMethods.isAir(event.getPlayer().getInventory().getItem(event.getNewSlot()))) {
               this.caster.leaveSkillCasting(true);
            } else if (SkillScroller.this.scrollKey == null) {
               if (!SkillScroller.this.ignoreSneak || !event.getPlayer().isSneaking()) {
                  if (!this.caster.hasActiveSkillBound()) {
                     this.caster.leaveSkillCasting(true);
                  } else {
                     event.setCancelled(true);
                     int previous = event.getPreviousSlot();
                     int current = event.getNewSlot();
                     int dist1 = 9 + current - previous;
                     int dist2 = current - previous;
                     int dist3 = current - previous - 9;
                     int change = Math.abs(dist1) < Math.abs(dist2)
                        ? (Math.abs(dist1) < Math.abs(dist3) ? dist1 : dist3)
                        : (Math.abs(dist3) < Math.abs(dist2) ? dist3 : dist2);
                     this.scrollOf(change);
                  }
               }
            }
         }
      }

      private void scrollOf(int delta) {
         if (delta != 0) {
            this.caster.permSkillScrollIndex = SkillScroller.remainder(this.caster.permSkillScrollIndex + delta, this.getActiveSkills().size());
            this.onTick();
            this.refreshTimeOut();
            SoundObject sound = delta <= 0 && SkillScroller.this.changeBackSound != null ? SkillScroller.this.changeBackSound : SkillScroller.this.changeSound;
            if (sound != null) {
               sound.playTo(this.caster.getPlayer());
            }
         }
      }
   }
}
