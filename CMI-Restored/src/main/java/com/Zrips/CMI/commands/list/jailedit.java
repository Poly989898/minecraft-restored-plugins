package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Jail.CMIJailCell;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.Modules.Selection.CMISelectionVisualizer;
import com.Zrips.CMI.Modules.Selection.SelectionManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.HashMap;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class jailedit implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("notInArea", "{gcw}You are not standing in jail area");
    configReader.get("noName", "{gcw}No jail by this name");
    configReader.get("noCell", "{gcw}No cell by this id");
    configReader.get("exist", "{gcw}Jail with this name already exist");
    configReader.get("createdJail", "{gcp}Successfully created new jail ({gcs}[jail]{gcp})");
    configReader.get(
        "createdCell",
        "{gcp}Successfully created new jail cell ({gcs}[jail]{gcp}:{gcs}[cellId]{gcp})");
    configReader.get("collides", "{gcw}Collides with {gcs}[jailName] {gcw}jail");
    configReader.get("editCells", "{gcp}Edit cell list");
    configReader.get("cList", "{gcp}Id: {gcs}[id] {gcp}Jailed: {gcs}[amount]");
    configReader.get("jailListTpHover", "{gcp}Set outside teleport location");
    configReader.get("jailListAreaHover", "{gcp}Redefine area");
    configReader.get("jailRemoved", "{gcp}Removed ({gcs}[jail]{gcp}) jail");
    configReader.get("jailOutside", "{gcp}Set new outside location");
    configReader.get("jailArea", "{gcp}Redefined area for {gcs}[jail]");
    configReader.get("cellRemoved", "{gcp}Removed ({gcs}[cellId]{gcp}) cell from {gcs}[jail]");
    configReader.get("list", "{gcp}Cells: {gcs}[cells] {gcp}Jailed: {gcs}[amount] {gcn}[jail]");
    configReader.get("listTp", "{gcp}[tp] ");
    configReader.get("listArea", "{gcp}[area] ");
    configReader.get("areaShow", "{gcp}[S] ");
  }

  @CAnnotation(
      info = "Edit jails",
      regVar = {0, 1, 2, 3},
      consoleVar = 666,
      modules = "jail")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    jailedit.Action action = null;
    CMIJail cmiJail = null;
    Integer integer = null;
    Player player = (Player) commandSender;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (action == null) {
        action = jailedit.Action.getByName(string);
        if (action != null) {
          continue;
        }
      }

      if (cmiJail == null) {
        cmiJail = cmi.getJailManager().getByName(string);
        if (cmiJail != null) {
          continue;
        }
      }

      if (integer == null) {
        try {
          integer = Integer.parseInt(string);
        } catch (Exception exception) {
        }
      }
    }

    if (action == null) {
      action = jailedit.Action.jaillist;
    }

    switch (action) {
      case addjail:
        String jailName = strings[1];
        if (cmiJail != null) {
          CMILC.info(this, commandSender, "exist");
          return null;
        }

        return this.addJail(commandSender, jailName);
      case delete:
        if (cmiJail == null) {
          CMILC.info(this, commandSender, "noName");
          return null;
        }

        return this.removeJail(commandSender, cmiJail);
      case jaillist:
        return this.jailList(commandSender);
      case celllist:
        if (cmiJail == null) {
          CMILC.info(this, commandSender, "noName");
          return null;
        }

        this.cellList(commandSender, cmiJail);
        return true;
      case addCell:
        Location location = player.getLocation();
        if (cmiJail == null) {
          CMILC.info(this, commandSender, "noName");
          return null;
        }

        this.addCell(commandSender, cmiJail, location);
        return true;
      case removeCell:
        if (cmiJail == null) {
          CMILC.info(this, commandSender, "noName");
          return null;
        }

        return this.removeCell(commandSender, cmiJail, integer);
      case redefineJail:
        if (cmiJail == null) {
          CMILC.info(this, commandSender, "noName");
          return null;
        }

        return this.redefineJail(commandSender, cmiJail);
      case outsideLoc:
        if (cmiJail == null) {
          CMILC.info(this, commandSender, "noName");
          return null;
        }

        cmiJail.setOutsideLocation(player.getLocation());
        cmi.getJailManager().save();
        CMILC.info(this, commandSender, "jailOutside", "[jail]", cmiJail.getName());
        return true;
      default:
        return false;
    }
  }

  private Boolean redefineJail(CommandSender commandSender, CMIJail cmiJail) {
    if (!(commandSender instanceof Player)) {
      return null;
    }

    CMISelectionVisualizer cmiSelectionVisualizer =
        SelectionManager.getPlayerSelection((Player) commandSender);
    if (cmiSelectionVisualizer != null && cmiSelectionVisualizer.placedBothPoints()) {
      CMICuboidArea cmiCuboidArea = cmiSelectionVisualizer.getSelectedCuboid().clone();
      CMIJail collidingJail =
          CMI.getInstance()
              .getJailManager()
              .collidesWithJail(
                  new CuboidArea(
                      cmiCuboidArea.getWorld(),
                      cmiCuboidArea.getLowPoint(),
                      cmiCuboidArea.getHighPoint()),
                  cmiJail);
      if (collidingJail != null) {
        CMILC.info(this, commandSender, "collides", "[jailName]", collidingJail.getName());
        return null;
      }

      cmiJail.setArea(
          new CuboidArea(
              cmiCuboidArea.getWorld(), cmiCuboidArea.getLowPoint(), cmiCuboidArea.getHighPoint()));

      for (var entry : new HashMap<>(cmiJail.getCells()).entrySet()) {
        if (!cmiJail
            .getArea()
            .containsLoc(((CMIJailCell) entry.getValue()).getTeleportInLocation())) {
          cmiJail.getCells().remove(entry.getKey());
        }
      }

      CMI.getInstance().getJailManager().recalculateChunks(cmiJail);
      CMI.getInstance().getJailManager().save();
      CMILC.info(this, commandSender, "jailArea", "[jail]", cmiJail.getName());
      return true;
    } else {
      LC.Selection_SelectPoints.sendMessage(
          commandSender,
          new Object[] {
            "[tool]", CMI.getInstance().getConfigManager().getSelectionTool().getRealName()
          });
      return false;
    }
  }

  private Boolean removeJail(CommandSender commandSender, CMIJail cmiJail) {
    CMI.getInstance().getJailManager().removeJail(cmiJail);
    CMI.getInstance().getJailManager().save();
    CMILC.info(this, commandSender, "jailRemoved", "[jail]", cmiJail.getName());
    this.jailList(commandSender);
    return true;
  }

  private Boolean removeCell(CommandSender commandSender, CMIJail cmiJail, Integer integer) {
    if (integer != null && cmiJail.getCells().get(integer) != null) {
      cmiJail.getCells().remove(integer);
      CMI.getInstance().getJailManager().save();
      CMILC.info(
          this, commandSender, "cellRemoved", "[cellId]", integer, "[jail]", cmiJail.getName());
      this.cellList(commandSender, cmiJail);
      return true;
    } else {
      CMILC.info(this, commandSender, "noCell");
      return null;
    }
  }

  private Boolean addCell(CommandSender commandSender, CMIJail cmiJail, Location location) {
    if (!(commandSender instanceof Player)) {
      return null;
    } else if (cmiJail != null && location != null && cmiJail.containsLoc(location)) {
      CMIJailCell cmiJailCell = new CMIJailCell(cmiJail);
      cmiJailCell.setTeleportInLocation(location);
      cmiJail.addCell(cmiJailCell);
      CMI.getInstance().getJailManager().addJail(cmiJail);
      CMI.getInstance().getJailManager().save();
      CMILC.info(
          this,
          commandSender,
          "createdCell",
          "[jail]",
          cmiJail.getName(),
          "[cellId]",
          cmiJailCell.getId());
      this.cellList(commandSender, cmiJail);
      return true;
    } else {
      CMILC.info(this, commandSender, "notInArea");
      return null;
    }
  }

  private Boolean cellList(final CommandSender commandSender, final CMIJail cmiJail) {
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    PageInfo pageInfo = new PageInfo(100, cmiJail.getCells().size(), 1);
    ChatMessageObjectEdit chatMessageObjectEdit =
        new ChatMessageObjectEdit(commandSender, pageInfo) {
          public void newAdd(String string) {
            jailedit.this.addCell(commandSender, cmiJail, ((Player) commandSender).getLocation());
          }
        };
    chatMessageObjectEdit.setBasicNew(true);

    for (final CMIJailCell cmiJailCell : cmiJail.getCells().values()) {
      ChatEditorObject chatEditorObject =
          new ChatEditorObject(String.valueOf(cmiJailCell.getId())) {
            public void onDelete() {
              jailedit.this.removeCell(commandSender, cmiJail, cmiJailCell.getId());
            }
          };
      RawMessageCommand rawMessageCommand =
          new RawMessageCommand() {
            public void run(CommandSender runSender) {
              jaillist.showCellInfo(runSender, cmiJailCell);
            }
          };
      rawMessageCommand.setKeep(true);
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(
          CMILC.getIM(
              this,
              "cList",
              "[id]",
              cmiJailCell.getId(),
              "[amount]",
              cmiJailCell.getJailed().size()));
      rawMessage.addHover(LC.info_Click);
      rawMessage.addCommand(rawMessageCommand);
      chatEditorObject.setLine(rawMessage);
      chatMessageObjectEdit.addline(chatEditorObject);
    }

    chatMessageObjectEdit.print();
    return true;
  }

  private Boolean jailList(final CommandSender commandSender) {
    if (!(commandSender instanceof Player)) {
      return null;
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    PageInfo pageInfo = new PageInfo(100, CMI.getInstance().getJailManager().getJails().size(), 1);
    ChatMessageObjectEdit chatMessageObjectEdit =
        new ChatMessageObjectEdit(commandSender, pageInfo) {
          public void newAdd(String string) {
            if (CMIText.isValidString(CMIChatColor.deColorize(string))) {
              jailedit.this.addJail(
                  commandSender, CMIText.getFirstVariable(CMIChatColor.stripColor(string)));
            }
          }
        };

    for (final CMIJail cmiJail : CMI.getInstance().getJailManager().getJails().values()) {
      ChatEditorObject chatEditorObject =
          new ChatEditorObject(cmiJail.getName()) {
            public void onDelete() {
              jailedit.this.removeJail(commandSender, cmiJail);
            }
          };
      RawMessage rawMessage = new RawMessage();
      ArrayList<String> arrayList = new ArrayList();
      arrayList.add(CMILC.getIM(this, "jailListTpHover"));
      if (cmiJail.getOutsideLocation() != null) {
        arrayList.add(LC.Location_Full.getLocale(new Object[] {cmiJail.getOutsideLocation()}));
      }

      rawMessage
          .addText(
              (cmiJail.getOutsideLocation() == null
                      ? LC.modify_disabledColor.getLocale(new Object[0])
                      : LC.modify_enabledColor.getLocale(new Object[0]))
                  + CMILC.getIM(this, "listTp"))
          .addHover(arrayList)
          .addCommand("cmi jailedit outsideLoc " + cmiJail.getName());
      arrayList.clear();
      arrayList.add(CMILC.getIM(this, "jailListAreaHover"));
      arrayList.add(LC.Location_Full.getLocale(new Object[] {cmiJail.getArea().getLowLoc()}));
      arrayList.add(LC.Location_Full.getLocale(new Object[] {cmiJail.getArea().getHighLoc()}));
      rawMessage
          .addText(CMILC.getIM(this, "listArea"))
          .addHover(arrayList)
          .addCommand("cmi jailedit redefineJail " + cmiJail.getName());
      RawMessageCommand rawMessageCommand =
          new RawMessageCommand() {
            public void run(CommandSender runSender) {
              CMI.getInstance()
                  .getSelectionManager()
                  .setSelectionCuboid((Player) runSender, cmiJail.getArea().clone());
              CMI.getInstance()
                  .getSelectionManager()
                  .afterSelectionUpdate((Player) runSender, true);
            }
          };
      rawMessageCommand.setKeep(true);
      rawMessage
          .addText(CMILC.getIM(this, "areaShow"))
          .addHover(LC.info_Show)
          .addCommand(rawMessageCommand);
      rawMessage
          .addText(
              CMILC.getIM(
                  this,
                  "list",
                  "[jail]",
                  cmiJail.getName(),
                  "[cells]",
                  cmiJail.getCells().size(),
                  "[amount]",
                  cmiJail.getCountInCells()))
          .addHover(CMILC.getIM(this, "editCells"))
          .addCommand("cmi jailedit celllist " + cmiJail.getName());
      chatEditorObject.setLine(rawMessage);
      chatMessageObjectEdit.addline(chatEditorObject);
    }

    chatMessageObjectEdit.print();
    return true;
  }

  private Boolean addJail(CommandSender commandSender, String string) {
    if (!(commandSender instanceof Player)) {
      return null;
    }

    CMISelectionVisualizer cmiSelectionVisualizer =
        SelectionManager.getPlayerSelection((Player) commandSender);
    if (cmiSelectionVisualizer != null && cmiSelectionVisualizer.placedBothPoints()) {
      CMICuboidArea cmiCuboidArea = cmiSelectionVisualizer.getSelectedCuboid().clone();
      CMIJail cmiJail =
          CMI.getInstance()
              .getJailManager()
              .collidesWithJail(
                  new CuboidArea(
                      cmiCuboidArea.getWorld(),
                      cmiCuboidArea.getLowPoint(),
                      cmiCuboidArea.getHighPoint()));
      if (cmiJail != null) {
        CMILC.info(this, commandSender, "collides", "[jailName]", cmiJail.getName());
        return null;
      } else {
        CMIJail newJail = new CMIJail();
        newJail.setArea(
            new CuboidArea(
                cmiCuboidArea.getWorld(),
                cmiCuboidArea.getLowPoint(),
                cmiCuboidArea.getHighPoint()));
        newJail.setName(string);
        CMI.getInstance().getJailManager().addJail(newJail);
        CMI.getInstance().getJailManager().save();
        CMILC.info(this, commandSender, "createdJail", "[jail]", newJail.getName());
        this.jailList(commandSender);
        return true;
      }
    } else {
      LC.Selection_SelectPoints.sendMessage(
          commandSender,
          new Object[] {
            "[tool]", CMI.getInstance().getConfigManager().getSelectionTool().getRealName()
          });
      return null;
    }
  }

  private enum Action {
    addjail,
    delete,
    jaillist,
    celllist,
    addCell,
    removeCell,
    redefineJail,
    outsideLoc;

    public static jailedit.Action getByName(String string) {
      jailedit.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        jailedit.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
