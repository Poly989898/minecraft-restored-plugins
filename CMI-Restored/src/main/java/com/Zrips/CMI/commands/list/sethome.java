package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Homes.CmiHome;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Set;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.MaterialPicker;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class sethome implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "set", "{gcp}You correctly created the home with name of {gcs}[homeName]{gcp}!");
    configReader.get("setBed", "{gcp}Created bed respawn location");
    configReader.get("private", " {gcu}(private)");
    configReader.get(
        "maxFavorites", "{gcw}You can only have {gcp}[amount] {gcw}favorite homes at one time");
    configReader.get("noMore", "{gcw}You have reached max sethome limit");
    configReader.get(
        "exist",
        "{gcw}Home location with this name ({gcs}[name]{gcw}) already exists! \n"
            + "{gcp}Click to confirm overwrite.");
    configReader.get(
        "noOverwrite",
        "{gcw}You don't have permission to overwrite existing home location. Remove old one or pick"
            + " different home name.");
    configReader.get(
        "slotTaken",
        "!actionbar!{gcw}Home slot is already been used by &7[homeName]{gcw}, consider another"
            + " one");
    configReader.get("setIcon", "!actionbar:3!&7Set home icon as {gcs}[material] &7material");
    configReader.get(
        "tooManyInWorld",
        "{gcw}You cant have any more homes in this world. Max {gcs}[max]{gcw} allowed, you have"
            + " {gcs}[current]");
  }

  @CAnnotation(
      priority = 115,
      info = "Sets home location",
      args =
          "(homeName) (playerName) (-p) (-l:worldName;x;y;z) (block/Material) (slotNumber)"
              + " (-overwrite)",
      tab = {"", "-p"},
      explanation = {
        "cmi.command.sethome.unlimited",
        "-l will define custom location for new home location, this requires"
            + " cmi.command.sethome.customloc permission node"
      },
      regVar = {0, 1, 2, 3, 4, 5, 6},
      consoleVar = {2, 3, 4, 5, 6},
      others = true)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    CMILocation cmiLocation = null;
    String homeName = null;
    CMIMaterial cmiMaterial = null;
    boolean overwrite = false;
    boolean silent = false;
    Integer integer = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-p")) {
        flag = true;
      } else {
        if (arg.startsWith("-l:")) {
          cmiLocation =
              cmi.getPlayerManager().convertStringToLocation(arg.substring("-l:".length()));
          if (cmiLocation != null) {
            continue;
          }
        }

        if (arg.equalsIgnoreCase("-overwrite")) {
          silent = true;
        } else if (homeName == null) {
          homeName = arg;
        } else {
          if (cmiMaterial == null) {
            cmiMaterial = CMIMaterial.get(arg);
            if (cmiMaterial != null && !cmiMaterial.isNone()) {
              continue;
            }
          }

          if (cmi.getPlayerManager().getUser(arg) != null) {
            string = arg;
          } else if (arg.equalsIgnoreCase("block")) {
            overwrite = true;
          } else {
            try {
              integer = Integer.parseInt(arg);
            } catch (Throwable throwable) {
            }
          }
        }
      }
    }

    if (integer != null && integer > CMI.getInstance().getHomeManager().getHomesMaxGuiSlots()) {
      LC.info_NoMoreThan.sendMessage(
          commandSender,
          new Object[] {"[amount]", CMI.getInstance().getHomeManager().getHomesMaxGuiSlots()});
      return null;
    }

    if (homeName == null) {
      homeName = CMI.getInstance().getHomeManager().getDefaultHomeName();
    }

    final CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    Location object =
        commandSender.getName().equalsIgnoreCase(cmiUser.getName())
            ? cmiUser.getLocation()
            : (commandSender instanceof Player
                ? ((Player) commandSender).getLocation()
                : cmiUser.getLocation());
    if (cmiLocation != null) {
      if (!PermissionsManager.CMIPerm.command_sethome_customloc.hasPermission(
          commandSender, true)) {
        return null;
      }

      object = cmiLocation.getBukkitLoc();
    }

    if (overwrite) {
      cmiMaterial = CMIMaterial.get(object.clone().add(0.0, 0.0, 0.0).getBlock());
      if (cmiMaterial == null || cmiMaterial.equals(CMIMaterial.NONE) || cmiMaterial.isAir()) {
        cmiMaterial = CMIMaterial.get(object.clone().add(0.0, -1.0, 0.0).getBlock());
      }
    }

    value = cmi.getHomeManager().getMaxHomes(commandSender);
    homeName = homeName.replace("\\$-", "");
    CmiHome cmiHome = cmiUser.getHome(homeName);
    int homeCount = cmiUser.getValidHomeCount();
    if (homeCount >= value && cmiHome == null || homeCount > value && cmiHome != null) {
      CMILC.info(this, commandSender, "noMore");
      return null;
    }

    if (cmiHome != null
        && !PermissionsManager.CMIPerm.command_sethome_overwrite.hasPermission(commandSender)) {
      CMILC.info(this, commandSender, "noOverwrite");
      return null;
    }

    if (integer != null && cmiUser.getHomesBySlot(integer) != null) {
      for (CmiHome home : cmiUser.getHomesBySlot(integer)) {
        if (!home.getName().equalsIgnoreCase(homeName)) {
          CMILC.info(this, commandSender, "slotTaken", "[homeName]", home.getName());
        }
      }
    }

    if (cmi.getHomeManager().isCheckBlockbreak()
        && !PermissionsManager.CMIPerm.command_sethome_bypass.hasPermission(commandSender)) {
      Location location = object.clone();
      location.setY(object.getBlockY() + 1);
      Material material = location.getBlock().getType();
      if (!CMIMaterial.isAir(material) && !material.isBlock()) {
        material = location.add(0.0, 1.0, 0.0).getBlock().getType();
      }

      if (!CMIMaterial.isAir(material) && !material.isBlock()) {
        material = location.add(0.0, 1.0, 0.0).getBlock().getType();
      }

      if (!CMIMaterial.isAir(material) && !material.isBlock()) {
        CMILC.Information_BadArea.sendMessage(commandSender);
        return null;
      }

      BlockBreakEvent blockBreakEvent =
          new BlockBreakEvent(
              location.clone().add(0.0, 0.0, 0.0).getBlock(), (Player) commandSender);
      Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
      if (blockBreakEvent.isCancelled()) {
        CMILC.Information_BadArea.sendMessage(commandSender);
        return null;
      }
    }

    if (!cmi.getUtilManager().validName(cmi.getHomeManager().getHomeNameRegex(), homeName)) {
      LC.info_invalidName.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (cmi.getHomeManager().isConfirmation()
        && cmiHome != null
        && !silent
        && commandSender instanceof Player) {
      final String[] argsWithOverwrite = CMIArray.addLast(strings, "-overwrite");
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM(this, "exist", "[name]", cmiHome.getName()));
      rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
      RawMessageCommand rawMessageCommand =
          new RawMessageCommand() {
            public void run(CommandSender sender) {
              sethome.this.perform(cmi, sender, argsWithOverwrite);
            }
          };
      rawMessage.addCommand(rawMessageCommand);
      rawMessage.show(commandSender);
      return null;
    }

    if (cmi.getHomeManager().isHomesWorldLimits()
        && commandSender instanceof Player
        && !PermissionsManager.CMIPerm.command_sethome_bypasslimit.hasSetPermission(
            commandSender)) {
      int worldLimit = cmi.getHomeManager().getWorldLimit(object.getWorld().getName());
      if (worldLimit >= 0) {
        Set set = cmiUser.getHomesFromWorld(object.getWorld().getName());
        if (set.size() >= worldLimit) {
          CMILC.info(
              this, commandSender, "tooManyInWorld", "[max]", worldLimit, "[current]", set.size());
          return null;
        }
      }
    }

    if (cmiHome != null) {
      cmiUser.removeHome(cmiHome);
    }

    if (integer != null && integer < 1) {
      integer = 1;
    }

    if (integer != null && integer > 900) {
      integer = 900;
    }

    final CmiHome newHome = new CmiHome(homeName, new CMILocation((Location) object), flag);
    if (cmiMaterial != null && cmiMaterial.isValidItem()) {
      newHome.setMaterial(cmiMaterial);
    }

    if (integer != null) {
      newHome.setSlot(integer);
    }

    cmiUser.addHome(newHome, true);
    CMILC.sendMessage(
        commandSender,
        CMILC.getIM(this, "set", "[homeName]", newHome.getName())
            + (flag ? CMILC.getIM(this, "private") : ""));
    if (commandSender instanceof Player
        && ((Player) commandSender).isOnline()
        && (cmiMaterial == null || cmiMaterial.equals(CMIMaterial.NONE))
        && PermissionsManager.CMIPerm.command_sethome_iconpickeroncreation.hasSetPermission(
            commandSender)) {
      MaterialPicker materialPicker =
          new MaterialPicker((Player) commandSender) {
            public void pickedMaterial(GUIClickType guiClickType, CMIMaterial chosenMaterial) {
              newHome.setMaterial(chosenMaterial);
              CMILC.info(
                  sethome.class.getSimpleName(),
                  commandSender,
                  "setIcon",
                  "[material]",
                  chosenMaterial.getName());
              CMIUser.closeInventory(cmiUser.getPlayer());
            }
          };
      materialPicker.openPicker(1, LC.info_pickIcon.getLocale(new Object[0]));
    }

    return true;
  }
}
