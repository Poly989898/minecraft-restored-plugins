package net.Indyuce.mmocore.skill.cast.handler;

import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.event.PlayerKeyPressEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
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

public class SkillBar extends SkillCastingHandler {
   private final Keybind mainKey;
   private final boolean ignoreSneak;
   private final boolean lowestKeybinds;

   public SkillBar(@NotNull ConfigurationSection config) {
      super(config);
      this.mainKey = Objects.requireNonNull(Keybind.fromConfig(config.get("open")), "Could not find open keybind");
      this.ignoreSneak = config.getBoolean("ignore-sneak", config.getBoolean("disable-sneak", false));
      this.lowestKeybinds = config.getBoolean("use-lowest-keybinds");
   }

   @Override
   public SkillCastingInstance newInstance(@NotNull PlayerData player) {
      return new SkillBar.CustomSkillCastingInstance(player);
   }

   @Override
   public SkillCastingMode getCastingMode() {
      return SkillCastingMode.SKILL_BAR;
   }

   @Override
   public void onSkillBound(@NotNull PlayerData player) {
      if (this.lowestKeybinds) {
         int slot = 1;

         for (BoundSkillInfo bound : player.getBoundSkills().values()) {
            if (!bound.isPassive()) {
               bound.skillBarCastSlot = slot++;
            }
         }
      } else {
         player.getBoundSkills().forEach((slotx, boundx) -> boundx.skillBarCastSlot = slotx);
      }
   }

   @EventHandler
   public void enterSkillCasting(PlayerKeyPressEvent event) {
      if (this.mainKey.matches(event)) {
         Player player = event.getPlayer();
         if (!this.ignoreSneak || !player.isSneaking()) {
            if (event.getPressed().shouldCancelEvent()) {
               event.setCancelled(true);
            }

            PlayerData playerData = event.getData();
            if (player.getGameMode() != GameMode.SPECTATOR
               && (MMOCore.plugin.configManager.canCreativeCast || player.getGameMode() != GameMode.CREATIVE)
               && !playerData.isCasting()
               && playerData.hasActiveSkillBound()
               && playerData.setSkillCasting()) {
               MMOCore.plugin.soundManager.getSound(SoundEvent.SPELL_CAST_BEGIN).playTo(player);
            }
         }
      }
   }

   public class CustomSkillCastingInstance extends SkillCastingInstance {
      private final String ready = ConfigMessage.fromKey("casting.action-bar.ready").asLine();
      private final String onCooldown = ConfigMessage.fromKey("casting.action-bar.on-cooldown").asLine();
      private final String noMana = ConfigMessage.fromKey("casting.action-bar.no-mana").asLine();
      private final String noStamina = ConfigMessage.fromKey("casting.action-bar.no-stamina").asLine();
      private final String split = ConfigMessage.fromKey("casting.split").asLine();
      private int j;

      CustomSkillCastingInstance(PlayerData playerData) {
         super(SkillBar.this, playerData);
      }

      @EventHandler
      public void onItemHeld(PlayerItemHeldEvent event) {
         if (event.getPlayer().equals(this.getCaster().getPlayer())) {
            Player player = event.getPlayer();
            if (!SkillBar.this.ignoreSneak || !player.isSneaking()) {
               if (event.getPreviousSlot() != event.getNewSlot()) {
                  event.setCancelled(true);
                  this.refreshTimeOut();
                  ClassSkill classSkill = this.findSkillToCast(player.getInventory().getHeldItemSlot(), event.getNewSlot());
                  if (classSkill != null) {
                     classSkill.toCastable(this.getCaster()).cast(this.getCaster().getMMOPlayerData());
                  }
               }
            }
         }
      }

      @Nullable
      private ClassSkill findSkillToCast(int currentSlot, int clickedSlot) {
         for (BoundSkillInfo info : this.getActiveSkills()) {
            if (info.skillBarCastSlot + (currentSlot < info.skillBarCastSlot ? 1 : 0) == 1 + clickedSlot) {
               return info.getClassSkill();
            }
         }

         return null;
      }

      @EventHandler
      public void stopCasting(PlayerKeyPressEvent event) {
         if (event.getPlayer().equals(this.getCaster().getPlayer())) {
            if (SkillBar.this.mainKey.matches(event)) {
               Player player = event.getPlayer();
               if (!SkillBar.this.ignoreSneak || !player.isSneaking()) {
                  if (this.getCaster().leaveSkillCasting()) {
                     MMOCore.plugin.soundManager.getSound(SoundEvent.SPELL_CAST_END).playTo(player);
                     ConfigMessage.fromKey("casting.no-longer").send(this.getCaster().getPlayer());
                  }
               }
            }
         }
      }

      @NotNull
      private String getFormat(PlayerData data) {
         if (!data.isOnline()) {
            return "";
         }

         StringBuilder str = new StringBuilder();

         for (BoundSkillInfo active : this.getActiveSkills()) {
            ClassSkill skill = active.getClassSkill();
            int slot = active.skillBarCastSlot;
            str.append(str.isEmpty() ? "" : this.split)
               .append(
                  (this.onCooldown(data, skill)
                        ? this.onCooldown.replace("{cooldown}", String.valueOf(data.getCooldownMap().getInfo(skill).getRemaining() / 1000L))
                        : (this.noMana(data, skill) ? this.noMana : (this.noStamina(data, skill) ? this.noStamina : this.ready)))
                     .replace("{index}", String.valueOf(slot + (data.getPlayer().getInventory().getHeldItemSlot() < slot ? 1 : 0)))
                     .replace("{skill}", skill.getSkill().getName())
               );
         }

         return MMOCore.plugin.placeholderParser.parse(data.getPlayer(), str.toString());
      }

      private boolean onCooldown(PlayerData data, ClassSkill skill) {
         return data.getCooldownMap().isOnCooldown(skill);
      }

      private boolean noMana(PlayerData data, ClassSkill skill) {
         return skill.getSkill().hasParameter("mana") && skill.getParameter("mana", data.getSkillLevel(skill.getSkill())) > data.getMana();
      }

      private boolean noStamina(PlayerData data, ClassSkill skill) {
         return skill.getSkill().hasParameter("stamina") && skill.getParameter("stamina", data.getSkillLevel(skill.getSkill())) > data.getStamina();
      }

      @Override
      public void onTick() {
         if (this.j++ % 2 == 0) {
            this.getCaster().displayActionBar(this.getFormat(this.getCaster()));
         }
      }
   }
}
