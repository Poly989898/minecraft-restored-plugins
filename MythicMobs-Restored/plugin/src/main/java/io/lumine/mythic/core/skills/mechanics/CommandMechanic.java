package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   name = "command",
   aliases = {"cmd"},
   description = "Executes a command"
)
public class CommandMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill, INoTargetSkill {
   @MythicField(
      name = "command",
      aliases = {"cmd", "c"},
      description = "The command to execute"
   )
   protected PlaceholderString command;
   @MythicField(
      name = "asCaster",
      aliases = {"caster", "ac", "sudo", "asmob"},
      description = "Whether to execute the command as the caster of the skill",
      defValue = "false"
   )
   protected boolean asCaster;
   @MythicField(
      name = "asTarget",
      aliases = {"target", "at", "sudotarget"},
      description = "Whether to execute the command as the targeted entity of the skill",
      defValue = "false"
   )
   protected boolean asTarget;
   @MythicField(
      name = "requireTarget",
      aliases = {"rt"},
      description = "Execute the command if the skill has a target",
      defValue = "false"
   )
   protected boolean requireTarget;
   @MythicField(
      name = "asOp",
      aliases = {"op"},
      description = "Whether to execute the command with all permissions",
      defValue = "false"
   )
   protected boolean asOperator;

   public CommandMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.command = mlc.getPlaceholderString(new String[]{"command", "cmd", "c"}, "say command error");
      this.asCaster = mlc.getBoolean(new String[]{"ascaster", "caster", "ac", "sudo", "asmob"}, false);
      this.asTarget = mlc.getBoolean(new String[]{"astarget", "target", "at", "sudotarget"}, false);
      this.requireTarget = mlc.getBoolean(new String[]{"requiretarget", "rt"}, this.asTarget);
      this.asOperator = mlc.getBoolean(new String[]{"asop", "op"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String parsedCommand = this.command.get(data, target);
      if (this.asCaster) {
         if (data.getCaster().getEntity().isPlayer()) {
            Player player = (Player)BukkitAdapter.adapt(data.getCaster().getEntity());
            if (this.asOperator) {
               boolean op = player.isOp();

               try {
                  player.setOp(true);
                  player.performCommand(parsedCommand);
               } catch (Exception e) {
                  Log.error("An exception occured while executing a command through a mob.");
                  e.printStackTrace();
               } finally {
                  player.setOp(op);
               }
            } else {
               player.performCommand(parsedCommand);
            }
         } else {
            Runnable cmdRunnable = () -> {
               if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
                  this.getPlugin()
                     .getBootstrap()
                     .dispatchCommand(
                        "minecraft:execute as "
                           + data.getCaster().getEntity().getUniqueId()
                           + " at "
                           + data.getCaster().getEntity().getUniqueId()
                           + " run "
                           + parsedCommand
                     );
               } else {
                  this.getPlugin()
                     .getBootstrap()
                     .dispatchCommand("minecraft:execute " + data.getCaster().getEntity().getUniqueId() + " ~ ~ ~ " + parsedCommand);
               }
            };
            if (ServerVersion.isFolia()) {
               Schedulers.sync().run(cmdRunnable);
            } else {
               cmdRunnable.run();
            }
         }
      } else if (this.asTarget) {
         if (target.isPlayer()) {
            Player player = (Player)BukkitAdapter.adapt(target);
            if (this.asOperator) {
               boolean op = player.isOp();

               try {
                  player.setOp(true);
                  player.performCommand(parsedCommand);
               } catch (Exception ex) {
                  Log.error("An exception occured while executing a command through a mob.");
                  ex.printStackTrace();
               } finally {
                  player.setOp(op);
               }
            } else {
               player.performCommand(parsedCommand);
            }
         } else {
            Runnable cmdRunnable = () -> {
               if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
                  this.getPlugin()
                     .getBootstrap()
                     .dispatchCommand("minecraft:execute as " + target.getUniqueId() + " at " + target.getUniqueId() + " run " + parsedCommand);
               } else {
                  this.getPlugin().getBootstrap().dispatchCommand("minecraft:execute " + target.getUniqueId() + " ~ ~ ~ " + parsedCommand);
               }
            };
            if (ServerVersion.isFolia()) {
               Schedulers.sync().run(cmdRunnable);
            } else {
               cmdRunnable.run();
            }
         }
      } else if (ServerVersion.isFolia()) {
         Schedulers.sync().run(() -> this.getPlugin().getBootstrap().dispatchCommand(parsedCommand));
      } else {
         this.getPlugin().getBootstrap().dispatchCommand(parsedCommand);
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.requireTarget) {
         return SkillResult.INVALID_TARGET;
      } else {
         String parsedCommand = this.command.get(data);
         if (this.asCaster) {
            if (data.getCaster().getEntity().isPlayer()) {
               Player player = (Player)BukkitAdapter.adapt(data.getCaster().getEntity());
               if (this.asOperator) {
                  boolean op = player.isOp();

                  try {
                     player.setOp(true);
                     player.performCommand(parsedCommand);
                  } catch (Exception e) {
                     Log.error("An exception occured while executing a command through a mob.");
                     e.printStackTrace();
                  } finally {
                     player.setOp(op);
                  }
               } else {
                  player.performCommand(parsedCommand);
               }
            } else {
               Runnable cmdRunnable = () -> {
                  if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
                     this.getPlugin()
                        .getBootstrap()
                        .dispatchCommand(
                           "minecraft:execute as "
                              + data.getCaster().getEntity().getUniqueId()
                              + " at "
                              + data.getCaster().getEntity().getUniqueId()
                              + " run "
                              + parsedCommand
                        );
                  } else {
                     this.getPlugin()
                        .getBootstrap()
                        .dispatchCommand("minecraft:execute " + data.getCaster().getEntity().getUniqueId() + " ~ ~ ~ " + parsedCommand);
                  }
               };
               if (ServerVersion.isFolia()) {
                  Schedulers.sync().run(cmdRunnable);
               } else {
                  cmdRunnable.run();
               }
            }
         } else if (ServerVersion.isFolia()) {
            Schedulers.sync().run(() -> this.getPlugin().getBootstrap().dispatchCommand(parsedCommand));
         } else {
            this.getPlugin().getBootstrap().dispatchCommand(parsedCommand);
         }

         return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      String parsedCommand = this.command.get(data, target);
      if (this.asCaster) {
         if (data.getCaster().getEntity().isPlayer()) {
            Player player = (Player)BukkitAdapter.adapt(data.getCaster().getEntity());
            if (this.asOperator) {
               boolean op = player.isOp();

               try {
                  player.setOp(true);
                  player.performCommand(parsedCommand);
               } catch (Exception e) {
                  Log.error("An exception occured while executing a command through a mob.");
                  e.printStackTrace();
               } finally {
                  player.setOp(op);
               }
            } else {
               player.performCommand(parsedCommand);
            }
         } else {
            Runnable cmdRunnable = () -> {
               if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
                  this.getPlugin()
                     .getBootstrap()
                     .dispatchCommand(
                        "minecraft:execute as "
                           + data.getCaster().getEntity().getUniqueId()
                           + " at "
                           + data.getCaster().getEntity().getUniqueId()
                           + " run "
                           + parsedCommand
                     );
               } else {
                  this.getPlugin()
                     .getBootstrap()
                     .dispatchCommand("minecraft:execute " + data.getCaster().getEntity().getUniqueId() + " ~ ~ ~ " + parsedCommand);
               }
            };
            if (ServerVersion.isFolia()) {
               Schedulers.sync().run(cmdRunnable);
            } else {
               cmdRunnable.run();
            }
         }
      } else if (ServerVersion.isFolia()) {
         Schedulers.sync().run(() -> this.getPlugin().getBootstrap().dispatchCommand(parsedCommand));
      } else {
         this.getPlugin().getBootstrap().dispatchCommand(parsedCommand);
      }

      return SkillResult.SUCCESS;
   }
}
