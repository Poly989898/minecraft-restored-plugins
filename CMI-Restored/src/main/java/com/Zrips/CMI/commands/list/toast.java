package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Advancements.AdvancementBackground;
import net.Zrips.CMILib.Advancements.AdvancementFrameType;
import net.Zrips.CMILib.Advancements.CMIAdvancement;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class toast implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "&f[message]");
  }

  @CAnnotation(
      info = "Sends toast message to player",
      args = "[playerName/all] (-t:[advType]) (-icon:[material]) [message]",
      tab = {"playername", "-t:goal%%-t:challenge%%-t:task", "%%-icon:stone"},
      regVar = {-100, -1},
      consoleVar = {-100, -1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_12_R1)) {
      CMILC.sendMessage(
          commandSender, "&cCant use this command on your server version! Only 1.12 and above");
      return null;
    }

    String string = strings[0];
    ArrayList<Player> arrayList = new ArrayList<>();
    Object object = null;
    if (!string.equalsIgnoreCase("all")) {
      object = CMIUser.getOnlinePlayer(string, true);
      if (object == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }

      arrayList.add((Player) object);
    } else {
      for (Player player : Bukkit.getOnlinePlayers()) {
        arrayList.add(player);
      }
    }

    var list = Arrays.asList(Arrays.copyOfRange(strings, 1, strings.length));
    String messageText = "";
    CMIItemStack cmiItemStack = CMIMaterial.PAPER.newCMIItemStack();
    AdvancementFrameType advancementFrameType = null;

    for (String part : list) {
      if (part.startsWith("-icon:")) {
        cmiItemStack = CMIItemStack.deserialize(part.substring("-icon:".length()));
      } else if (part.startsWith("-t:")) {
        advancementFrameType = AdvancementFrameType.getFromString(part.substring("-t:".length()));
      } else {
        if (!messageText.isEmpty()) {
          messageText = messageText + " ";
        }

        messageText = messageText + part;
      }
    }

    if (cmiItemStack == null
        || cmiItemStack.getType() == null
        || cmiItemStack.getCMIType().equals(CMIMaterial.NONE)
        || cmiItemStack.getCMIType().equals(CMIMaterial.AIR)) {
      cmiItemStack = CMIMaterial.PAPER.newCMIItemStack();
    }

    if (advancementFrameType == null) {
      advancementFrameType = AdvancementFrameType.TASK;
    }

    if (PermissionsManager.CMIPerm.actionbar_colors.hasPermission(commandSender)) {
      messageText = CMIChatColor.translate(messageText);
    }

    if (arrayList.size() == 1) {
      messageText =
          cmi.getPlaceholderAPIManager().updatePlaceHolders((Player) arrayList.get(0), messageText);
    }

    CMIAdvancement cmiAdvancement =
        new CMIAdvancement()
            .setId(new NamespacedKey(cmi, "cmi/commandToast"))
            .setDescription("_")
            .setItem(cmiItemStack.getItemStack())
            .setAnnounce(false)
            .setHidden(false)
            .setToast(true)
            .setBackground(AdvancementBackground.ADVENTURE)
            .setFrame(advancementFrameType)
            .setTitle(
                CMILC.getIM(this, "message", "[messsage]", messageText, "[message]", messageText));

    try {
      cmiAdvancement.setItem(cmiItemStack.getItemStack());
    } catch (Throwable throwable) {
    }

    Integer integer = Util.getCustomModelData(cmiItemStack.getItemStack());
    if (integer != null) {
      cmiAdvancement.setCustomModelData(integer);
    }

    try {
      cmiAdvancement.show(arrayList.toArray(new Player[arrayList.size()]));
    } catch (Exception | Error value) {
      String iconName =
          cmiItemStack.getBukkitName() == null
              ? "stone"
              : cmiItemStack.getBukkitName().toLowerCase().replace(" ", "_");
      cmiAdvancement.setIcon(iconName);
      cmiAdvancement.show(arrayList.toArray(new Player[arrayList.size()]));
    }

    return true;
  }
}
