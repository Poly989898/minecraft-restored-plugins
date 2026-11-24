package net.Indyuce.mmocore.command;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.RegisteredCommand;
import net.Indyuce.mmocore.command.api.ToggleableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PvpModeCommand extends RegisteredCommand {
   public static final String COOLDOWN_KEY = "PvpMode";

   public PvpModeCommand(ConfigurationSection config) {
      super(config, ToggleableCommand.PVP_MODE);
   }

   public boolean execute(CommandSender sender, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is for players only.");
         return false;
      }

      if (!sender.hasPermission("mmocore.pvpmode")) {
         ConfigMessage.fromKey("not-enough-perms").send((Player)sender);
         return false;
      }

      PlayerData playerData = PlayerData.get((Player)sender);
      if (playerData.getCooldownMap().isOnCooldown("PvpMode")) {
         ConfigMessage.fromKey(
               "pvp-mode.cooldown", "remaining", MythicLib.plugin.getMMOConfig().decimal.format(playerData.getCooldownMap().getCooldown("PvpMode"))
            )
            .send((Player)sender);
         return true;
      }

      playerData.getCombat().setPvpMode(!playerData.getCombat().isInPvpMode());
      playerData.getCooldownMap()
         .applyCooldown(
            "PvpMode",
            playerData.getCombat().isInPvpMode() ? MMOCore.plugin.configManager.pvpModeToggleOnCooldown : MMOCore.plugin.configManager.pvpModeToggleOffCooldown
         );
      if (playerData.getCombat().isInPvpMode() && MythicLib.plugin.getFlags().isFlagAllowed(playerData.getPlayer(), CustomFlag.PVP_MODE)) {
         playerData.getCombat().setInvulnerable(MMOCore.plugin.configManager.pvpModeInvulnerabilityTimeCommand);
         ConfigMessage.fromKey(
               "pvp-mode.toggle.on-invulnerable",
               "time",
               MythicLib.plugin.getMMOConfig().decimal.format(MMOCore.plugin.configManager.pvpModeInvulnerabilityTimeCommand)
            )
            .send(playerData.getPlayer());
      } else {
         ConfigMessage.fromKey("pvp-mode.toggle." + (playerData.getCombat().isInPvpMode() ? "on" : "off") + "-safe").send((Player)sender);
      }

      return true;
   }
}
