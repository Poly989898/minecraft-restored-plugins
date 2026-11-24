package net.Indyuce.mmocore.comp.region.pvpmode;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler.Factory;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import io.lumine.mythic.lib.comp.flags.WorldGuardFlags;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;

public class PvPModeHandler extends MMOCoreFlagHandler {
   public static final Factory FACTORY = new Factory() {
      final WorldGuardFlags wgFlags = Objects.requireNonNull(
         (WorldGuardFlags)MythicLib.plugin.getFlags().getHandler(WorldGuardFlags.class), "Could not reach ML compatibility class for WG"
      );

      public PvPModeHandler create(Session session) {
         return new PvPModeHandler(session, this.wgFlags.toWorldGuard(CustomFlag.PVP_MODE));
      }
   };

   public PvPModeHandler(Session session, StateFlag flag) {
      super(session, flag);
   }

   @Override
   public State getDefaultState() {
      return State.ALLOW;
   }

   protected boolean onSetValue(
      LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, State currentValue, State lastValue, MoveType moveType
   ) {
      if (this.isInvalid()) {
         return true;
      }

      boolean newPvpMode = this.toBoolean(currentValue);
      boolean lastPvpMode = this.toBoolean(lastValue);
      if (!newPvpMode && lastPvpMode) {
         this.playerData.getMMOPlayerData().getCooldownMap().applyCooldown("PvpMode", MMOCore.plugin.configManager.pvpModeRegionLeaveCooldown);
         if (this.canSendMessage()) {
            boolean pvpFlag = toSet.queryState(null, new StateFlag[]{Flags.PVP}) != State.DENY;
            if (this.playerData.getCombat().isInPvpMode() && !pvpFlag) {
               this.playerData.getCombat().close();
            }

            boolean pvpEnabled = this.playerData.getCombat().isInPvpMode() && !this.playerData.getCombat().canQuitPvpMode() && pvpFlag;
            this.lastMessage = System.currentTimeMillis();
            double remaining = (
                  this.playerData.getCombat().getLastHit() + MMOCore.plugin.configManager.pvpModeCombatTimeout * 1000.0 - System.currentTimeMillis()
               )
               / 1000.0;
            ConfigMessage.fromKey(
                  "pvp-mode.leave.pvp-" + (pvpEnabled ? "allowed" : "denied"), "remaining", MythicLib.plugin.getMMOConfig().decimal.format(remaining)
               )
               .send(this.playerData.getPlayer());
         }
      } else if (newPvpMode && !lastPvpMode) {
         this.playerData.getMMOPlayerData().getCooldownMap().applyCooldown("PvpMode", MMOCore.plugin.configManager.pvpModeRegionEnterCooldown);
         boolean applyInvulnerability = this.playerData.getCombat().isInPvpMode() && this.playerData.getCombat().canQuitPvpMode();
         if (applyInvulnerability) {
            this.playerData.getCombat().setInvulnerable(MMOCore.plugin.configManager.pvpModeInvulnerabilityTimeRegionChange);
         }

         if (this.canSendMessage()) {
            this.lastMessage = System.currentTimeMillis();
            ConfigMessage.fromKey(
                  "pvp-mode.enter.pvp-mode-" + (applyInvulnerability ? "on" : "off"),
                  "time",
                  MythicLib.plugin.getMMOConfig().decimal.format(MMOCore.plugin.configManager.pvpModeInvulnerabilityTimeRegionChange)
               )
               .send(this.playerData.getPlayer());
         }
      }

      return true;
   }
}
