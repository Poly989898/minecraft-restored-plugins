package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.InteractiveCommand.CMIInteractiveCommand;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.ListEditor;
import java.util.UUID;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Chat.listMoveDirection;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ic implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("public", " {gcu}[Public]");
    configReader.get("notPublic", " {gcn}[NotPublic]");
    configReader.get("reqPerm", " {gcu}[ReqPerm]");
    configReader.get("noPerm", " {gcn}[ReqPerm]");
    configReader.get("cancelEvent", " {gcu}[cancelEvent]");
    configReader.get("passEvent", " {gcn}[passEvent]");
    configReader.get(
        "permHover", " {gcp}Requires {gcs}[permission] {gcp}permission to place this sign");
    configReader.get("editSign", " {gcs}[EditSignLines]");
    configReader.get("publicHover", "{gcp}Change public status. \nSigns top line {gcs}[ic:[name]]");
    configReader.get("createdPublic", "{gcp}Created new interactive sign");
    configReader.get("list", "{gcp}[place]. {gcs}[name]");
    configReader.get("listType", " {gcs}[type]    ");
    configReader.get("editCommands", "{gcp}Edit commands");
    configReader.get("editBlocks", " {gcp}Edit blocks");
    configReader.get("addBlock", "{gcp}Add block you are looking at");
    configReader.get("addEntity", " {gcp}Add entity you are looking at");
    configReader.get("manageCommands", "{gcp}Switch to command editor");
    configReader.get("manageBlockEnt", "{gcp}Switch to block/entity editor");
    configReader.get("locationList", "{gcs}[type]");
    configReader.get("entityList", "{gcn}[type]");
    configReader.get("notPublicIc", "{gcw}You cant use this, its not public");
    configReader.get("block", "(Block)");
    configReader.get("entity", "(Entity)");
    configReader.get("both", "(Both)");
    configReader.get("none", "(None)");
    configReader.get("addedNew", "{gcp}Added new block/entity for {gcs}[name]");
    configReader.get("dontExist", "{gcw}Interactive command don't exist by this name");
    configReader.get("exists", "{gcw}Interactive command with this name already exists");
    configReader.get("blockExist", "{gcw}This block is already being used by {gcp}[name]");
    configReader.get("entityExist", "{gcw}This entity is already being used by {gcp}[name]");
    configReader.get("addInfo", "{gcp}Enter new command. Type {gcs}cancel {gcp}to cancel");
    configReader.get(
        "addInformationHover",
        "{gcp}Global variables supported as of sender category \n"
            + "{gcp}To include delay in commands: \n"
            + "{gcp}delay! 5 \n"
            + "{gcp}Additional variables can be used to insert variables by their place \n"
            + "{gcp}$1 will take provided variable by new command and will place in that place \n"
            + "{gcp}$1- takes all variables from this point and inserts into original commands");
    configReader.get(
        "editInfo",
        "{gcp}Click to paste old text. Type {gcs}cancel {gcp}to cancel action. Type {gcs}remove"
            + " {gcp}to remove line");
  }

  @CAnnotation(
      info = "Create interactive command",
      args = "(new) (name)",
      multiTab = {
        "new name",
        "addblock [icnames]",
        "addentity [icnames]",
        "listcb [icnames]",
        "commands [icnames]",
        "public [icnames] true,false",
        "cancelevent [icnames] true,false",
        "changepermreq [icnames] true,false",
        "delete [icnames]"
      },
      regVar = -600,
      consoleVar = -600,
      modules = "interactivecommands",
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int index = 0;
    Player player = commandSender instanceof Player ? (Player) commandSender : null;
    ic.Action action = ic.Action.none;
    CMIInteractiveCommand cmiInteractiveCommand = null;
    String string = null;
    StringBuilder stringBuilder = new StringBuilder();
    int value = 0;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          value = Integer.parseInt(arg.replace("-p:", ""));
          continue;
        } catch (Exception exception) {
        }
      }

      if (action == ic.Action.none && ic.Action.get(arg) != ic.Action.none) {
        action = ic.Action.get(arg);
      } else {
        if (cmiInteractiveCommand == null) {
          CMIInteractiveCommand command = cmi.getInteractiveCommandManager().getByName(arg);
          if (command != null) {
            cmiInteractiveCommand = command;
            continue;
          }
        }

        if (string == null) {
          string = arg;
        } else {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(" ");
          }

          stringBuilder.append(arg);
        }
      }
    }

    if (string != null) {
      StringBuilder builder = new StringBuilder(string);
      if (!stringBuilder.toString().isEmpty()) {
        builder.append(" ");
        builder.append(stringBuilder.toString());
      }

      stringBuilder = builder;
    }

    if (action.equals(ic.Action.none)) {
      action = ic.Action.list;
    }

    switch (action) {
      case none:
      default:
        break;
      case newEntry:
        if (cmiInteractiveCommand != null) {
          CMILC.info(this, commandSender, "exists");
          return true;
        }

        CMIInteractiveCommand commandCopy = new CMIInteractiveCommand(string);
        cmi.getInteractiveCommandManager().addInteractiveCommand(commandCopy);
        this.listCommandBlocks(commandSender, commandCopy, 0);
        break;
      case addBlock:
        if (cmiInteractiveCommand == null) {
          CMILC.info(this, commandSender, "dontExist");
          return true;
        }

        if (player != null && this.addBlock(player, cmiInteractiveCommand)) {
          CMILC.info(this, commandSender, "addedNew", "[name]", cmiInteractiveCommand.getName());
        }

        cmi.getInteractiveCommandManager().save();
        break;
      case addEntity:
        if (cmiInteractiveCommand == null) {
          CMILC.info(this, commandSender, "dontExist");
          return true;
        }

        if (player != null && this.addEntity(player, cmiInteractiveCommand)) {
          CMILC.info(this, commandSender, "addedNew", "[name]", cmiInteractiveCommand.getName());
        }

        cmi.getInteractiveCommandManager().save();
        break;
      case listcb:
        this.listCommandBlocks(commandSender, cmiInteractiveCommand, value);
        break;
      case list:
        this.list(commandSender, value);
        break;
      case commands:
        this.editCommands(commandSender, cmiInteractiveCommand);
        break;
      case changePublic:
        this.changePublic(commandSender, cmiInteractiveCommand, string);
        break;
      case cancelevent:
        this.changeInteraction(commandSender, cmiInteractiveCommand, string);
        break;
      case changepermreq:
        this.changePermissionRequirement(commandSender, cmiInteractiveCommand, string);
        break;
      case signlines:
        this.changePermissionRequirement(commandSender, cmiInteractiveCommand);
        break;
      case add:
        this.add(player, cmiInteractiveCommand);
        break;
      case delete:
        this.delete(player, cmiInteractiveCommand);
        break;
      case deleteentity:
        UUID uuid = null;

        try {
          uuid = UUID.fromString(string);
        } catch (Exception exception2) {
        }

        this.deleteEntity(player, cmiInteractiveCommand, uuid);
        break;
      case deleteblock:
        CMILocation cmiLocation = CMILocation.fromString(string);
        this.deleteBlock(player, cmiInteractiveCommand, cmiLocation);
        break;
      case remove:
      case moveup:
      case movedown:
      case edit:
        byte b = -1;

        try {
          index = Integer.parseInt(string);
        } catch (Exception exception3) {
          return false;
        }

        switch (action) {
          case remove:
            this.remove(player, cmiInteractiveCommand, index);
            return true;
          case moveup:
            this.moveup(player, cmiInteractiveCommand, index);
            return true;
          case movedown:
            this.movedown(player, cmiInteractiveCommand, index);
            return true;
          case edit:
            this.edit(player, cmiInteractiveCommand, stringBuilder.toString(), index);
            return true;
          default:
            return true;
        }
      case addcommand:
        this.addCommand(player, cmiInteractiveCommand, stringBuilder.toString());
    }

    return true;
  }

  private RawMessage generateHeader(CMIInteractiveCommand cmiInteractiveCommand) {
    String string = this.getClass().getSimpleName();
    RawMessage rawMessage = new RawMessage();
    rawMessage
        .addText(
            LC.info_SpliterValue.getLocale(
                    new Object[] {"[value]", cmiInteractiveCommand.getName()})
                + "\n")
        .addHover(CMILC.getIM(this, "editCommands"))
        .addCommand("cmi " + string + " commands " + cmiInteractiveCommand.getName());
    rawMessage
        .addText(
            cmiInteractiveCommand.isPublic()
                ? CMILC.getIM(this, "public")
                : CMILC.getIM(this, "notPublic"))
        .addHover(CMILC.getIM(this, "publicHover", "[name]", cmiInteractiveCommand.getName()))
        .addCommand("cmi " + string + " changepublic " + cmiInteractiveCommand.getName());
    rawMessage
        .addText(
            cmiInteractiveCommand.isSignRequiresPerm()
                ? CMILC.getIM(this, "reqPerm")
                : CMILC.getIM(this, "noPerm"))
        .addHover(
            cmiInteractiveCommand.isSignRequiresPerm()
                ? CMILC.getIM(
                    this,
                    "permHover",
                    "[permission]",
                    PermissionsManager.CMIPerm.interactivesign_$1.getPermission(
                        cmiInteractiveCommand.getName()))
                : LC.info_Click.getLocale(new Object[0]))
        .addCommand("cmi " + string + " changepermreq " + cmiInteractiveCommand.getName());
    rawMessage
        .addText(CMILC.getIM(this, "editSign"))
        .addHover(
            LC.modify_editSymbolHover.getLocale(
                new Object[] {"[text]", cmiInteractiveCommand.getName()}))
        .addCommand("cmi " + string + " signlines " + cmiInteractiveCommand.getName());
    rawMessage
        .addText(
            cmiInteractiveCommand.isCancelInteraction()
                ? CMILC.getIM(this, "cancelEvent")
                : CMILC.getIM(this, "passEvent"))
        .addHover(
            cmiInteractiveCommand.isCancelInteraction()
                ? LC.modify_enabled.getLocale(new Object[0])
                : LC.modify_disabled.getLocale(new Object[0]))
        .addCommand("cmi " + string + " cancelEvent " + cmiInteractiveCommand.getName());
    return rawMessage;
  }

  private boolean addEntity(Player player, CMIInteractiveCommand cmiInteractiveCommand) {
    Entity entity = CMI.getInstance().getUtilManager().getTargetEntity(player);
    if (entity == null) {
      LC.info_lookAtEntity.sendMessage(player, new Object[0]);
      return false;
    }

    CMIInteractiveCommand command =
        CMI.getInstance().getInteractiveCommandManager().getByUUID(entity.getUniqueId());
    if (command != null) {
      CMILC.info(this, player, "entityExist", "[name]", command.getName());
      return false;
    }

    Integer integer = null;
    UUID uuid = null;
    if (CMIHookType.Citizens.isEnabled() && entity.hasMetadata("NPC")) {
      integer = CMI.getInstance().getCitizensManager().getEntityId(entity);
      uuid = CMI.getInstance().getCitizensManager().getEntityUUID(entity);
    }

    cmiInteractiveCommand.addUUID(entity.getUniqueId(), uuid, integer);
    CMI.getInstance()
        .getInteractiveCommandManager()
        .addEntity(entity.getUniqueId(), cmiInteractiveCommand);
    CMI.getInstance()
        .getParticleManager()
        .drawLine(
            player.getLocation().clone().add(0.0, 1.5, 0.0),
            entity.getLocation(),
            CMI.getInstance().getConfigManager().getPointDefaultEffect(),
            false,
            player);
    return true;
  }

  private boolean addBlock(Player player, CMIInteractiveCommand cmiInteractiveCommand) {
    Block block = CMI.getInstance().getUtilManager().getTargetBlock(player, 15);
    if (block != null && !block.getType().equals(Material.AIR)) {
      CMIInteractiveCommand command =
          CMI.getInstance().getInteractiveCommandManager().getByLoc(block.getLocation());
      if (command != null) {
        CMILC.info(this, player, "blockExist", "[name]", command.getName());
        return true;
      } else {
        CMI.getInstance()
            .getParticleManager()
            .drawLine(
                player.getLocation().clone().add(0.0, 1.5, 0.0),
                block.getLocation().clone().add(0.5, 0.5, 0.5),
                CMI.getInstance().getConfigManager().getPointDefaultEffect(),
                false,
                player);
        cmiInteractiveCommand.addLoc(new CMILocation(block.getLocation()));
        CMI.getInstance()
            .getInteractiveCommandManager()
            .addInteractiveCommand(cmiInteractiveCommand, true, block.getLocation());
        return true;
      }
    } else {
      LC.info_lookAtBlock.sendMessage(player, new Object[0]);
      return false;
    }
  }

  private boolean parseBooleanValue(boolean flag, String string) {
    boolean silentFlag = flag;
    if (string != null) {
      if (string.equalsIgnoreCase("true")) {
        silentFlag = true;
      } else if (string.equalsIgnoreCase("false")) {
        silentFlag = false;
      } else {
        try {
          silentFlag = Boolean.valueOf(string);
        } catch (Exception exception) {
        }
      }
    }

    return silentFlag;
  }

  private boolean moveup(Player player, CMIInteractiveCommand cmiInteractiveCommand, int value) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else {
      var commandList = cmiInteractiveCommand.getCommands();
      if (value >= 0 && value + 1 <= commandList.size()) {
        cmiInteractiveCommand.setCommands(
            ListEditor.move(cmiInteractiveCommand.getCommands(), value, listMoveDirection.Up));
        CMI.getInstance().getInteractiveCommandManager().save();
        this.editCommands(player, cmiInteractiveCommand);
        return true;
      } else {
        return false;
      }
    }
  }

  private boolean addCommand(
      Player player, CMIInteractiveCommand cmiInteractiveCommand, String string) {
    if (cmiInteractiveCommand == null || string.isEmpty()) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else if (string.equalsIgnoreCase("cancel")) {
      CMI.getInstance().getCTextManager().removeChatEditor(player);
      this.editCommands(player, cmiInteractiveCommand);
      return true;
    } else {
      var commandList = cmiInteractiveCommand.getCommands();
      commandList.add(string);
      cmiInteractiveCommand.setCommands(commandList);
      this.editCommands(player, cmiInteractiveCommand);
      CMI.getInstance().getInteractiveCommandManager().save();
      return true;
    }
  }

  private boolean edit(
      Player player, CMIInteractiveCommand cmiInteractiveCommand, String string, int value) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    }

    String simpleName = this.getClass().getSimpleName();
    if (string.isEmpty()) {
      CMI.getInstance()
          .getCTextManager()
          .addChatEditor(
              player,
              "cmi " + simpleName + " " + cmiInteractiveCommand.getName() + " edit " + value + " ");
      RawMessage rawMessage = new RawMessage();
      rawMessage
          .addText(CMILC.getIM(this, "editSign"))
          .addHover(LC.info_PasteOld.getLocale(new Object[0]))
          .addSuggestion(CMIChatColor.deColorize(cmiInteractiveCommand.getCommands().get(value)));
      rawMessage.show(player);
      return true;
    }

    if (string.equalsIgnoreCase("cancel")) {
      CMI.getInstance().getCTextManager().removeChatEditor(player);
      this.editCommands(player, cmiInteractiveCommand);
      return true;
    }

    if (string.equalsIgnoreCase("remove")) {
      CMI.getInstance().getCTextManager().removeChatEditor(player);
      cmiInteractiveCommand.getCommandsOriginal().remove(value);
      this.editCommands(player, cmiInteractiveCommand);
      return true;
    }

    if (value < cmiInteractiveCommand.getCommandsOriginal().size()) {
      cmiInteractiveCommand.getCommandsOriginal().set(value, string);
    } else {
      cmiInteractiveCommand.getCommandsOriginal().add(string);
    }

    CMI.getInstance().getInteractiveCommandManager().save();
    this.editCommands(player, cmiInteractiveCommand);
    return true;
  }

  private boolean movedown(Player player, CMIInteractiveCommand cmiInteractiveCommand, int value) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else {
      var commandList = cmiInteractiveCommand.getCommands();
      if (value >= 0 && value + 1 <= commandList.size()) {
        cmiInteractiveCommand.setCommands(
            ListEditor.move(cmiInteractiveCommand.getCommands(), value, listMoveDirection.Down));
        CMI.getInstance().getInteractiveCommandManager().save();
        this.editCommands(player, cmiInteractiveCommand);
        return true;
      } else {
        return false;
      }
    }
  }

  private boolean remove(Player player, CMIInteractiveCommand cmiInteractiveCommand, int value) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else {
      var commandList = cmiInteractiveCommand.getCommands();
      if (value >= 0 && value + 1 <= commandList.size()) {
        commandList.remove(value);
        cmiInteractiveCommand.setCommands(commandList);
        CMI.getInstance().getInteractiveCommandManager().save();
        this.editCommands(player, cmiInteractiveCommand);
        return true;
      } else {
        return false;
      }
    }
  }

  private boolean deleteBlock(
      Player player, CMIInteractiveCommand cmiInteractiveCommand, CMILocation cmiLocation) {
    if (cmiInteractiveCommand != null && cmiLocation != null) {
      CMI.getInstance().getInteractiveCommandManager().removeLoc(cmiLocation);
      cmiInteractiveCommand.getLocList().remove(cmiLocation);
      this.listCommandBlocks(player, cmiInteractiveCommand, 0);
      CMI.getInstance().getInteractiveCommandManager().save();
      return true;
    } else {
      CMILC.info(this, player, "dontExist");
      return true;
    }
  }

  private boolean deleteEntity(
      Player player, CMIInteractiveCommand cmiInteractiveCommand, UUID uuid) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else {
      cmiInteractiveCommand.removeEntity(uuid);
      this.listCommandBlocks(player, cmiInteractiveCommand, 0);
      CMI.getInstance().getInteractiveCommandManager().save();
      return true;
    }
  }

  private boolean delete(Player player, CMIInteractiveCommand cmiInteractiveCommand) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else {
      CMI.getInstance().getInteractiveCommandManager().deleteIC(cmiInteractiveCommand);
      this.list(player, 0);
      CMI.getInstance().getInteractiveCommandManager().save();
      return true;
    }
  }

  private boolean add(Player player, CMIInteractiveCommand cmiInteractiveCommand) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, player, "dontExist");
      return true;
    } else {
      String string = this.getClass().getSimpleName();
      CMI.getInstance()
          .getCTextManager()
          .addChatEditor(player, "cmi " + string + " " + cmiInteractiveCommand.getName() + " add ");
      RawMessage rawMessage = new RawMessage();
      rawMessage
          .addText(CMILC.getIM(this, "addInfo"))
          .addHover(CMILC.getIM(this, "addInformationHover"));
      rawMessage.show(player);
      CMI.getInstance().getInteractiveCommandManager().save();
      return true;
    }
  }

  private boolean changePermissionRequirement(
      CommandSender commandSender, final CMIInteractiveCommand cmiInteractiveCommand) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, commandSender, "dontExist");
      return true;
    } else {
      RawMessage rawMessage = this.generateHeader(cmiInteractiveCommand);
      String string = this.getClass().getSimpleName();
      ChatMessageListEdit chatMessageListEdit =
          new ChatMessageListEdit(
              commandSender, cmiInteractiveCommand.getSignLines(), ChatEditType.String, 4) {
            public void onUpdate() {
              for (Location location : cmiInteractiveCommand.getLocList()) {
                Block block = location.getBlock();
                if (CMIMaterial.isSign(block.getType())) {
                  cmiInteractiveCommand.updateSignText(block);
                }
              }

              CMI.getInstance().getInteractiveCommandManager().save();
            }
          };
      chatMessageListEdit.setTopLine(rawMessage);
      chatMessageListEdit.setIdentifyEmptylines(true);
      chatMessageListEdit.setFillWithEmpty(true);
      rawMessage = new RawMessage();
      rawMessage
          .addText(" " + LC.modify_extraEditSymbol.getLocale(new Object[0]) + " ")
          .addHover(CMILC.getIM(this, "manageBlockEnt"))
          .addCommand("cmi " + string + " listcb " + cmiInteractiveCommand.getName());
      chatMessageListEdit.setBottomLine(rawMessage);
      chatMessageListEdit.print();
      return true;
    }
  }

  private boolean changePermissionRequirement(
      CommandSender commandSender, CMIInteractiveCommand cmiInteractiveCommand, String string) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, commandSender, "dontExist");
      return false;
    } else {
      boolean flag = this.parseBooleanValue(!cmiInteractiveCommand.isSignRequiresPerm(), string);
      cmiInteractiveCommand.setSignRequiresPerm(flag);
      CMI.getInstance().getInteractiveCommandManager().save();
      this.listCommandBlocks(commandSender, cmiInteractiveCommand, 0);
      return true;
    }
  }

  private boolean changeInteraction(
      CommandSender commandSender, CMIInteractiveCommand cmiInteractiveCommand, String string) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, commandSender, "dontExist");
      return false;
    } else {
      boolean flag = this.parseBooleanValue(!cmiInteractiveCommand.isCancelInteraction(), string);
      cmiInteractiveCommand.setCancelInteraction(flag);
      CMI.getInstance().getInteractiveCommandManager().save();
      this.listCommandBlocks(commandSender, cmiInteractiveCommand, 0);
      return true;
    }
  }

  private boolean changePublic(
      CommandSender commandSender, CMIInteractiveCommand cmiInteractiveCommand, String string) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, commandSender, "dontExist");
      return false;
    } else {
      boolean flag = this.parseBooleanValue(!cmiInteractiveCommand.isPublic(), string);
      cmiInteractiveCommand.setPublic(flag);
      CMI.getInstance().getInteractiveCommandManager().save();
      this.listCommandBlocks(commandSender, cmiInteractiveCommand, 0);
      return true;
    }
  }

  private boolean editCommands(
      CommandSender commandSender, CMIInteractiveCommand cmiInteractiveCommand) {
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, commandSender, "dontExist");
      return false;
    } else {
      RawMessage rawMessage = this.generateHeader(cmiInteractiveCommand);
      String string = this.getClass().getSimpleName();
      ChatMessageListEdit chatMessageListEdit =
          new ChatMessageListEdit(
              commandSender, cmiInteractiveCommand.getCommandsOriginal(), ChatEditType.String) {
            public void onUpdate() {
              CMI.getInstance().getInteractiveCommandManager().save();
            }
          };
      chatMessageListEdit.setTopLine(rawMessage);
      rawMessage = new RawMessage();
      rawMessage
          .addText(" " + LC.modify_extraEditSymbol.getLocale(new Object[0]) + " ")
          .addHover(CMILC.getIM(this, "manageBlockEnt"))
          .addCommand("cmi " + string + " listcb " + cmiInteractiveCommand.getName());
      chatMessageListEdit.setBottomLine(rawMessage);
      chatMessageListEdit.print();
      return true;
    }
  }

  private boolean list(final CommandSender commandSender, int value) {
    if (value < 0) {
      value = 1;
    }

    Player player = commandSender instanceof Player ? (Player) commandSender : null;
    final String string = this.getClass().getSimpleName();
    Location location = new Location((World) Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0);
    if (player != null) {
      location = player.getLocation();
    }

    var set = CMI.getInstance().getInteractiveCommandManager().getSortedByDistance(location);
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    PageInfo pageInfo = new PageInfo(10, set.size(), value);
    ChatMessageObjectEdit chatMessageObjectEdit =
        new ChatMessageObjectEdit(commandSender, pageInfo) {
          public void newAdd(String newName) {
            if (!newName.contains(" ")) {
              newName = CMIChatColor.stripColor(newName);
              CMIInteractiveCommand cmiInteractiveCommand =
                  CMI.getInstance().getInteractiveCommandManager().getByName(newName);
              if (cmiInteractiveCommand != null) {
                CMILC.info(ic.class, commandSender, "exists");
              } else {
                CMIInteractiveCommand newCommand = new CMIInteractiveCommand(newName);
                CMI.getInstance().getInteractiveCommandManager().addInteractiveCommand(newCommand);
                ic.this.listCommandBlocks(commandSender, newCommand, 1);
              }
            }
          }
        };

    for (final CMIInteractiveCommand interactiveCommand : set) {
      if (pageInfo.isEntryOk()) {
        ChatEditorObject chatEditorObject =
            new ChatEditorObject(interactiveCommand.getName()) {
              public void onDelete() {
                CMI.getInstance()
                    .performCommand(
                        commandSender,
                        CommandsHandler.getLabel()
                            + " "
                            + string
                            + " "
                            + interactiveCommand.getName()
                            + " delete");
              }

              public void onClick() {
                ic.this.listCommandBlocks(commandSender, interactiveCommand, 1);
              }
            };
        chatEditorObject.setHover(CMILC.getIM(this, "editBlocks"));
        chatMessageObjectEdit.addline(chatEditorObject);
      }
    }

    chatMessageObjectEdit.print();
    pageInfo.autoPagination(commandSender, CommandsHandler.getLabel() + " " + string, "-p:");
    return true;
  }

  private boolean listCommandBlocks(
      final CommandSender commandSender,
      final CMIInteractiveCommand cmiInteractiveCommand,
      int value) {
    final String string = this.getClass().getSimpleName();
    if (cmiInteractiveCommand == null) {
      CMILC.info(this, commandSender, "dontExist");
      return false;
    }

    RawMessage rawMessage = this.generateHeader(cmiInteractiveCommand);
    rawMessage.show(commandSender);
    if (value <= 0) {
      value = 1;
    }

    PageInfo pageInfo =
        new PageInfo(
            10,
            cmiInteractiveCommand.getLocList().size() + cmiInteractiveCommand.getUUIDMap().size(),
            value);
    ChatMessageObjectEdit chatMessageObjectEdit =
        new ChatMessageObjectEdit(commandSender, pageInfo) {
          public void newAdd(String newName) {}
        };
    chatMessageObjectEdit.setAddNew(false);

    for (final CMILocation cmiLocation : cmiInteractiveCommand.getLocList()) {
      if (pageInfo.isBreak()) {
        break;
      }

      if (pageInfo.isEntryOk()) {
        ChatEditorObject chatEditorObject =
            new ChatEditorObject(
                CMILC.getIM(
                    this, "locationList", "[type]", CMILocation.toString(cmiLocation, true))) {
              public void onDelete() {
                CMI.getInstance()
                    .performCommand(
                        commandSender,
                        CommandsHandler.getLabel()
                            + " "
                            + string
                            + " "
                            + cmiInteractiveCommand.getName()
                            + " deleteblock "
                            + CMILocation.toString(cmiLocation, true));
              }

              public void onClick() {
                CMI.getInstance()
                    .performCommand(
                        commandSender,
                        CommandsHandler.getLabel()
                            + " "
                            + point.class.getSimpleName()
                            + " "
                            + CMI.getInstance()
                                .getPlayerManager()
                                .convertLocToString(
                                    cmiLocation.getBukkitLoc().clone().add(0.5, 0.5, 0.5)));
              }
            };
        if (cmiLocation.isValid()) {
          chatEditorObject.setHover(
              LC.Location_Full.getLocale(
                  new Object[] {
                    "[world]",
                    cmiLocation.getWorld().getName(),
                    "[x]",
                    cmiLocation.getBlockX(),
                    "[y]",
                    cmiLocation.getBlockY(),
                    "[z]",
                    cmiLocation.getBlockZ()
                  }));
        }

        chatMessageObjectEdit.addline(chatEditorObject);
      }
    }

    for (var entry : cmiInteractiveCommand.getUUIDMap().entrySet()) {
      if (pageInfo.isBreak()) {
        break;
      }

      if (pageInfo.isEntryOk()) {
        final Entity entity =
            CMI.getInstance().getInteractiveCommandManager().getEntityByUUID((UUID) entry.getKey());
        if (entity != null && !entity.isDead()) {
          Location location = entity.getLocation();
          ChatEditorObject editorObject =
              new ChatEditorObject(
                  CMILC.getIM(
                      this, "entityList", "[type]", new CMIEntity(entity).getCustomName())) {
                public void onDelete() {
                  CMI.getInstance()
                      .performCommand(
                          commandSender,
                          CommandsHandler.getLabel()
                              + " "
                              + string
                              + " "
                              + cmiInteractiveCommand.getName()
                              + " deleteentity "
                              + entity.getUniqueId().toString());
                }

                public void onClick() {
                  CMI.getInstance()
                      .performCommand(
                          commandSender,
                          CommandsHandler.getLabel()
                              + " "
                              + point.class.getSimpleName()
                              + " "
                              + CMI.getInstance()
                                  .getPlayerManager()
                                  .convertLocToString(entity.getLocation()));
                }
              };
          editorObject.setHover(
              LC.Location_Full.getLocale(
                  new Object[] {
                    "[world]",
                    location.getWorld().getName(),
                    "[x]",
                    location.getBlockX(),
                    "[y]",
                    location.getBlockY(),
                    "[z]",
                    location.getBlockZ()
                  }));
          chatMessageObjectEdit.addline(editorObject);
        }
      }
    }

    chatMessageObjectEdit.print();
    rawMessage = new RawMessage();
    rawMessage
        .addText(" " + LC.modify_addSymbol.getLocale(new Object[0]))
        .addHover(CMILC.getIM(this, "addBlock"))
        .addCommand(
            CommandsHandler.getLabel()
                + " "
                + string
                + " addblock "
                + cmiInteractiveCommand.getName());
    rawMessage.addText("   ");
    rawMessage
        .addText(LC.modify_addSymbol)
        .addHover(CMILC.getIM(this, "addEntity"))
        .addCommand(
            CommandsHandler.getLabel()
                + " "
                + string
                + " addentity "
                + cmiInteractiveCommand.getName());
    rawMessage.addText("   ");
    rawMessage
        .addText(LC.modify_extraEditSymbol.getLocale(new Object[0]) + " ")
        .addHover(CMILC.getIM(this, "manageCommands"))
        .addCommand(
            CommandsHandler.getLabel()
                + " "
                + string
                + " commands "
                + cmiInteractiveCommand.getName());
    rawMessage.show(commandSender);
    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + this.getClass().getSimpleName()
            + " listcb "
            + cmiInteractiveCommand.getName(),
        "-p:");
    return true;
  }

  private enum Action {
    none,
    newEntry("new"),
    addBlock,
    addEntity,
    listcb,
    list,
    commands,
    changePublic("public"),
    cancelevent,
    changepermreq,
    signlines,
    add,
    delete,
    deleteentity,
    deleteblock,
    remove,
    moveup,
    movedown,
    edit,
    addcommand;

    String name;

    Action(String string) {
      this.name = string;
    }

    Action() {
      this.name = this.toString();
    }

    public static ic.Action get(String string) {
      ic.Action[] actions;
      int value = (actions = values()).length;

      for (int actionIndex = 0; actionIndex < value; actionIndex++) {
        ic.Action action = actions[actionIndex];
        if (action.name.equalsIgnoreCase(string) || action.toString().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return none;
    }
  }
}
