package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.function.BiFunction;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.command.api.CommandVerbose;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommandTreeNode extends CommandTreeNode {
   public SkillCommandTreeNode(CommandTreeNode parent) {
      super(parent, "skill");
      this.addChild(new SkillCommandTreeNode.LockSkillCommandTreeNode(this, "lock", true));
      this.addChild(new SkillCommandTreeNode.LockSkillCommandTreeNode(this, "unlock", false));
      this.addChild(new SkillCommandTreeNode.LevelCommandTreeNode(this, "give", (old, amount) -> old + amount));
      this.addChild(new SkillCommandTreeNode.LevelCommandTreeNode(this, "set", (old, amount) -> amount));
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return CommandResult.THROW_USAGE;
   }

   public class LevelCommandTreeNode extends CommandTreeNode {
      private final BiFunction<Integer, Integer, Integer> change;

      public LevelCommandTreeNode(CommandTreeNode parent, String type, BiFunction<Integer, Integer, Integer> change) {
         super(parent, type);
         this.change = change;
         this.addParameter(Parameter.PLAYER);
         this.addParameter(
            new Parameter(
               "<skill>", (explorer, list) -> MMOCore.plugin.skillManager.getAll().forEach(skill -> list.add(skill.getHandler().getId().toUpperCase()))
            )
         );
         this.addParameter(Parameter.AMOUNT);
      }

      public CommandResult execute(CommandSender sender, String[] args) {
         if (args.length < 6) {
            return CommandResult.THROW_USAGE;
         }

         Player player = Bukkit.getPlayer(args[3]);
         if (player == null) {
            sender.sendMessage(ChatColor.RED + "Could not find the player called " + args[3] + ".");
            return CommandResult.FAILURE;
         }

         PlayerData playerData = PlayerData.get(player);
         RegisteredSkill skill = MMOCore.plugin.skillManager.getSkill(args[4]);
         if (skill == null) {
            sender.sendMessage(ChatColor.RED + "Could not find the skill called " + args[4] + ".");
            return CommandResult.FAILURE;
         }

         ClassSkill classSkill = null;

         for (ClassSkill var : playerData.getProfess().getSkills()) {
            if (var.getSkill().equals(skill)) {
               classSkill = var;
            }
         }

         if (classSkill != null && classSkill.getUnlockLevel() <= playerData.getLevel()) {
            int amount;
            try {
               amount = Integer.parseInt(args[5]);
            } catch (Exception e) {
               sender.sendMessage(ChatColor.RED + args[5] + " is not a valid number.");
               return CommandResult.FAILURE;
            }

            int value = this.change.apply(playerData.getSkillLevel(skill), amount);
            playerData.setSkillLevel(skill, value);
            CommandVerbose.verbose(
               sender,
               CommandVerbose.CommandType.SKILL,
               ChatColor.YELLOW
                  + player.getName()
                  + ChatColor.YELLOW
                  + " is now level  "
                  + ChatColor.GOLD
                  + value
                  + ChatColor.YELLOW
                  + " for "
                  + skill.getName()
                  + "."
            );
            return CommandResult.SUCCESS;
         } else {
            sender.sendMessage(ChatColor.RED + skill.getName() + " is not unlockable for " + player.getName() + ".");
            return CommandResult.FAILURE;
         }
      }
   }

   public class LockSkillCommandTreeNode extends CommandTreeNode {
      private final boolean lock;

      public LockSkillCommandTreeNode(CommandTreeNode parent, String id, boolean lock) {
         super(parent, id);
         this.lock = lock;
         this.addParameter(Parameter.PLAYER);
         this.addParameter(
            new Parameter(
               "<skill>", (explorer, list) -> MMOCore.plugin.skillManager.getAll().forEach(skill -> list.add(skill.getHandler().getId().toUpperCase()))
            )
         );
      }

      public CommandResult execute(CommandSender sender, String[] args) {
         if (args.length < 5) {
            return CommandResult.THROW_USAGE;
         }

         Player player = Bukkit.getPlayer(args[3]);
         if (player == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player called " + args[3] + ".");
            return CommandResult.FAILURE;
         }

         PlayerData playerData = PlayerData.get(player);
         ClassSkill skill = playerData.getProfess().getSkill(args[4]);
         if (skill == null) {
            sender.sendMessage(ChatColor.RED + "Class doesn't have a skill called  " + args[4] + ".");
            return CommandResult.FAILURE;
         }

         if (this.lock) {
            if (!playerData.hasUnlocked(skill)) {
               CommandVerbose.verbose(
                  sender, CommandVerbose.CommandType.SKILL, ChatColor.RED + "Skill " + skill.getSkill().getName() + " already locked for " + player.getName()
               );
               return CommandResult.SUCCESS;
            }

            playerData.lock(skill);
         } else {
            if (playerData.hasUnlocked(skill)) {
               CommandVerbose.verbose(
                  sender, CommandVerbose.CommandType.SKILL, ChatColor.RED + "Skill " + skill.getSkill().getName() + " already unlocked for " + player.getName()
               );
               return CommandResult.SUCCESS;
            }

            playerData.unlock(skill);
         }

         CommandVerbose.verbose(
            sender,
            CommandVerbose.CommandType.SKILL,
            ChatColor.YELLOW
               + "Skill "
               + ChatColor.GOLD
               + skill.getSkill().getName()
               + ChatColor.YELLOW
               + " now "
               + (this.lock ? "locked" : "unlocked")
               + " for "
               + ChatColor.GOLD
               + player.getName()
         );
         return CommandResult.SUCCESS;
      }
   }
}
