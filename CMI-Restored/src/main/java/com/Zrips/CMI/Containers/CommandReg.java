package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.aliaseditor;
import com.Zrips.CMI.commands.list.attachcommand;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public abstract class CommandReg implements CommandExecutor, TabExecutor {
  protected final String command;
  protected static CommandMap cmap;
  private CommandAlias alias;

  public CommandReg(String string, CommandAlias commandAlias) {
    this.alias = commandAlias;
    this.command = string.toLowerCase();
  }

  public boolean register() {
    return this.register(null);
  }

  private static void unRegisterBukkitCommand(Command commandValue) {
    try {
      Field field =
          Version.isCurrentEqualOrHigher(Version.v1_13_R1)
              ? getCommandMap().getClass().getSuperclass().getDeclaredField("knownCommands")
              : getCommandMap().getClass().getDeclaredField("knownCommands");
      field.setAccessible(true);
      Object object = field.get(getCommandMap());
      field.setAccessible(false);
      HashMap hashMap = (HashMap) object;
      Command commandSecond = (Command) hashMap.remove(commandValue.getName());

      for (String string : commandValue.getAliases()) {
        if (hashMap.containsKey(string)
            && ((Command) hashMap.get(string)).toString().contains(commandValue.getName())) {
          hashMap.remove(string);
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public boolean register(String string) {
    CommandReg.ReflectCommand reflectCommand =
        new CommandReg.ReflectCommand(this.command, string, this.alias);
    reflectCommand.setExecutor(this);
    Command commandValue = getCommandMap().getCommand(this.command);
    if (commandValue != null
        && this.alias != null
        && this.alias.getType().equals(CommandAliasType.custom)) {
      this.alias.setOverrideTab(true);
    }

    return getCommandMap().register("cmi", reflectCommand);
  }

  static final CommandMap getCommandMap() {
    if (cmap == null) {
      try {
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        cmap = (CommandMap) field.get(Bukkit.getServer());
        return cmap;
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return cmap;
  }

  public boolean onCommand(
      CommandSender commandSender, Command commandValue, String string, String[] strings) {
    CMICommand cmiCommand = CMI.getInstance().getCommandManager().getCommands().get(string);
    return cmiCommand != null
        ? cmiCommand.getCmdClass().perform(CMI.getInstance(), commandSender, strings)
        : true;
  }

  public List<String> onTabComplete(
      CommandSender commandSender, Command commandValue, String string, String[] strings) {
    if (!this.alias.isTabComplete()) {
      return new ArrayList<>();
    } else {
      return this.alias.getType() == CommandAliasType.subbase
          ? CMI.getInstance()
              .getTab()
              .get(commandSender, this.alias.getCmiCommandName(), "cmi", strings)
          : CMI.getInstance().getTab().get(commandSender, this.command, "cmi", strings);
    }
  }

  private final class ReflectCommand extends BukkitCommand {
    private CommandReg exe = null;
    private CommandAlias alias;

    protected ReflectCommand(String string, String text, CommandAlias commandAlias) {
      super(string);
      this.alias = commandAlias;
      if (text != null) {
        this.setPermission(text);
      }
    }

    @Deprecated
    protected ReflectCommand(String string) {
      super(string);
    }

    public void setExecutor(CommandReg commandReg) {
      this.exe = commandReg;
    }

    public boolean execute(CommandSender commandSender, String string, String[] strings) {
      if (!this.alias.getState()) {
        return false;
      }

      if (this.exe == null) {
        return true;
      }

      if (this.alias.isExact()) {
        return false;
      }

      if (this.alias.isRequiresPerm() && commandSender instanceof Player) {
        Player player = (Player) commandSender;
        CMI cmi = CMI.getInstance();
        if (this.alias.getType() == CommandAliasType.custom
                && !PermissionsManager.CMIPerm.customalias_$1.hasPermission(
                    player, this.alias.getAliasAsOneWordNS())
            || this.alias.getType() != CommandAliasType.custom
                && !PermissionsManager.CMIPerm.command_$1.hasPermission(
                    player, this.alias.getCmiCommandName())) {
          boolean flag =
              cmi.getConfigManager().isPermisionOnError()
                  || PermissionsManager.CMIPerm.permisiononerror.hasPermission(player);
          RawMessage rawMessage = new RawMessage();
          cmi.getCommandManager();
          String text =
              this.alias.getType() == CommandAliasType.custom
                  ? "customalias." + this.alias.getAliasAsOneWordNS()
                  : "command." + this.alias.getCmiCommandName();
          rawMessage
              .addText(LC.info_NoPermission.getLocale(new Object[0]))
              .addHover(flag ? "&2" + CommandsHandler.getLabel() + "." + text : null);
          rawMessage.show(player);
          ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
          Snd snd = new Snd().setSender(consoleCommandSender).setTarget(player);
          LC.info_NoPlayerPermission.sendMessage(
              consoleCommandSender,
              new Object[] {snd, "[permission]", CommandsHandler.getLabel() + "." + text});
          return false;
        }
      }

      ArrayList<String> arrayList = new ArrayList();
      arrayList.add(string);
      arrayList.addAll(Arrays.asList(strings));
      List<String> list = new ArrayList<>(this.alias.getCommands());
      list = CMI.getInstance().getAliasManager().updateCommands(this.alias, arrayList, list);
      Snd sndSecond = new Snd().setSender(commandSender).setTarget(commandSender);

      for (int value = 0; value < list.size(); value++) {
        String textSecond = (String) list.get(value);
        if (!textSecond.startsWith(
                CommandsHandler.getLabel() + " " + attachcommand.class.getSimpleName())
            && !textSecond.startsWith(
                CommandsHandler.getLabel() + " " + aliaseditor.class.getSimpleName())) {
          textSecond = CMI.getInstance().getLM().updateSnd(sndSecond, textSecond);
        }

        list.set(value, textSecond);
      }

      if (commandSender instanceof Player
          && !list.isEmpty()
          && list.size() == 1
          && !CMI.getInstance()
              .getSpecializedCommandManager()
              .isSpecializedCommand((String) list.get(0))) {
        CMICommand cmiCommand = null;
        if (this.alias.getCmiCommandName() != null) {
          cmiCommand =
              CMI.getInstance()
                  .getCommandManager()
                  .getCommands()
                  .get(this.alias.getCmiCommandName());
        }

        String textThird = CMIArray.toString(list.toArray(new String[list.size()]));
        boolean flagSecond =
            CMI.getInstance().getCooldownManager().canUseCmd(commandSender, textThird, true, true);
        if (!flagSecond) {
          return false;
        }

        String textFourth = this.alias.getCleanCommand();
        textFourth =
            textFourth.toLowerCase().startsWith(CommandsHandler.getLabel())
                ? textFourth.substring(CommandsHandler.getLabel().length() + 1, textFourth.length())
                : textFourth;
        String[] parts = textFourth.split(" ", 2);
        textFourth = parts[0];
        if (!textFourth.isEmpty() && cmiCommand == null) {
          cmiCommand = CMI.getInstance().getCommandManager().getCommands().get(textFourth);
        }

        try {
          if (cmiCommand != null && cmiCommand.getCmdClass() != null) {
            String textFifth = CMIArray.toString(strings);
            strings =
                (cmiCommand.getCmdClass().getClass().getSimpleName()
                        + (textFifth.isEmpty() ? "" : " " + textFifth)
                        + (parts.length > 1 ? " " + parts[1] : ""))
                    .split(" ");
            return CMI.getInstance()
                .getCommandManager()
                .onCommand(commandSender, this, this.alias.getType().toString(), strings);
          }
        } catch (Throwable throwable) {
          CMI.getInstance()
              .consoleMessage(
                  (CMI.getInstance() == null)
                      + " "
                      + (commandSender == null)
                      + " "
                      + (strings == null));
          throwable.printStackTrace();
        }
      }

      if (commandSender instanceof Player) {
        CMI.getInstance()
            .getPlayerManager()
            .sendMessageToCommandSpies(
                (Player) commandSender, string + " " + CMIArray.toString(strings));
      }

      if (commandSender instanceof Player) {
        CMI.getInstance()
            .getSpecializedCommandManager()
            .processAliasCmdsAsPlayer(this.alias.getAlias(), list, (Player) commandSender);
      } else if (commandSender instanceof BlockCommandSender) {
        BlockCommandSender blockCommandSender = (BlockCommandSender) commandSender;
        Block block = blockCommandSender.getBlock();
        if (block != null) {
          BlockState blockState = block.getState();
          if (blockState instanceof CommandBlock) {
            CMI.getInstance().getSpecializedCommandManager().processCmds(list);
          }
        }
      } else {
        CMI.getInstance().getSpecializedCommandManager().processCmds(list, commandSender);
      }

      return true;
    }

    public List<String> tabComplete(CommandSender commandSender, String string, String[] strings) {
      if (this.alias.getType() == CommandAliasType.subbase) {
        return null;
      } else {
        return this.exe != null
            ? this.exe.onTabComplete(commandSender, this, string, strings)
            : null;
      }
    }
  }
}
