package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ICommandDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.Drop;
import org.bukkit.entity.Player;

public class CommandDrop extends Drop implements ICommandDrop {
   protected PlaceholderString command;
   protected boolean asCaster;
   protected boolean asTrigger;
   protected boolean asOperator;

   public CommandDrop(String line, MythicLineConfig config) {
      super(line, config);
      this.command = PlaceholderString.of(config.getString(new String[]{"command", "cmd"}));
      this.asCaster = config.getBoolean(new String[]{"ascaster", "caster", "ac", "sudo", "asmob"}, false);
      this.asTrigger = config.getBoolean(new String[]{"astrigger", "trigger", "at"}, false);
      this.asOperator = config.getBoolean(new String[]{"asop", "op", "operator"}, false);
   }

   public CommandDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
      this.command = PlaceholderString.of(config.getString(new String[]{"command", "cmd"}));
      this.asCaster = config.getBoolean(new String[]{"ascaster", "caster", "ac", "sudo", "asmob"}, false);
      this.asTrigger = config.getBoolean(new String[]{"astrigger", "trigger", "at"}, false);
      this.asOperator = config.getBoolean(new String[]{"asop", "op", "operator"}, false);
   }

   @Override
   public void dispatchCommand(AbstractPlayer target, DropMetadata data, double amount) {
      String parsedCommand = target == null ? this.command.get(data) : this.command.get(data, target);
      parsedCommand = parsedCommand.replace("<drop.amount>", String.valueOf((int)amount));
      if (this.asCaster) {
         if (data.getCaster().getEntity().isPlayer()) {
            Player player = BukkitAdapter.adapt(data.getCaster().getEntity().asPlayer());
            if (this.asOperator) {
               boolean isOp = player.isOp();

               try {
                  player.setOp(true);
                  player.performCommand(parsedCommand);
               } finally {
                  player.setOp(isOp);
               }
            } else {
               MythicBukkit.inst().getBootstrap().dispatchCommand(parsedCommand);
            }
         } else if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
            MythicBukkit.inst()
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
            MythicBukkit.inst().getBootstrap().dispatchCommand("minecraft:execute " + data.getCaster().getEntity().getUniqueId() + " ~ ~ ~ " + parsedCommand);
         }
      } else if (this.asTrigger) {
         if (data.getTrigger().isPlayer()) {
            Player player = BukkitAdapter.adapt(data.getTrigger().asPlayer());
            if (this.asOperator) {
               boolean isOp = player.isOp();

               try {
                  player.setOp(true);
                  player.performCommand(parsedCommand);
               } finally {
                  player.setOp(isOp);
               }
            } else {
               MythicBukkit.inst().getBootstrap().dispatchCommand(parsedCommand);
            }
         } else if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_13)) {
            MythicBukkit.inst()
               .getBootstrap()
               .dispatchCommand("minecraft:execute as " + data.getTrigger().getUniqueId() + " at " + data.getTrigger().getUniqueId() + " run " + parsedCommand);
         } else {
            MythicBukkit.inst().getBootstrap().dispatchCommand("minecraft:execute " + data.getTrigger().getUniqueId() + " ~ ~ ~ " + parsedCommand);
         }
      } else {
         MythicBukkit.inst().getBootstrap().dispatchCommand(parsedCommand);
      }
   }
}
