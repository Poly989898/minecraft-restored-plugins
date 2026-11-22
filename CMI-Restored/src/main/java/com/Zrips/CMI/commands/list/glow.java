package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class glow implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set [color]glow {gcp}to {gcs}[boolean]{gcp} for"
            + " {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your [color]glow {gcp}mode set to {gcs}[boolean]{gcp} by"
            + " {gcs}[senderDisplayName]{gcp}.");
    configReader.get("title", "{gcp}Pick glow color");
    configReader.get("incorrect", "{gcw}Incorrect color name");
    configReader.get("lore", "{gcu}Click to select color");
    configReader.get("noPerm", "{gcw}No permission yet");
  }

  @CAnnotation(
      info = "Set players glow mode",
      args = "(playerName) [true/false/color/gui]",
      tab = {"colors%%playername", "colors%%true%%false%%gui"},
      explanation = {
        "Example: /glow Zrips red",
        "Permissions: cmi.command.glow.color.[color] - allows to set particular glow color"
      },
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_9_R1)) {
      commandSender.sendMessage(ChatColor.RED + "Only for 1.9+ servers");
      return false;
    }

    boolean flag = false;
    String string = null;
    Boolean flagSecond = null;
    boolean flagThird = false;
    CMIChatColor cmiChatColor = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (string == null) {
        Player player = CMIUser.getOnlinePlayer(commandSender, text, true);
        if (player != null) {
          string = text;
          continue;
        }
      }

      if (cmiChatColor == null) {
        cmiChatColor = CMIChatColor.getColor(text);
        if (cmiChatColor != null) {
          continue;
        }
      }

      if (text.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (text.equalsIgnoreCase("false") || text.equalsIgnoreCase("off")) {
        flagSecond = false;
      } else if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("on")) {
        flagSecond = true;
      } else if (text.equalsIgnoreCase("gui")) {
        flagThird = true;
      } else if (string == null) {
        string = text;
      }
    }

    if (cmiChatColor != null && cmiChatColor.getHex() != null
        || cmiChatColor != null && cmiChatColor.isFormat()
        || cmiChatColor != null && cmiChatColor.isReset()) {
      CMILC.info(this, commandSender, "incorrect");
      return null;
    }

    if (cmiChatColor != null
        && !PermissionsManager.CMIPerm.command_glow_color_$1.hasPermission(
            commandSender, true, cmiChatColor.getName().toLowerCase())) {
      return null;
    }

    Player playerSecond = cmi.getTarget(commandSender, string, this);
    if (playerSecond == null) {
      return null;
    }

    if (!Config.DisableTeamManagement) {
      if (Version.isFolia()) {
        commandSender.sendMessage(ChatColor.RED + "Doesn't work on Folia type servers");
        return null;
      }

      if (strings.length == 0
          || cmiChatColor == null && flagSecond == null && playerSecond.isOnline() && flagThird) {
        this.openGui(commandSender, playerSecond);
        return true;
      }
    }

    if (cmiChatColor != null && Config.DisableTeamManagement) {
      if (!flag) {
        CMILC.info_TeamManagementDisabled.sendMessage(commandSender);
      }

      cmiChatColor = null;
    }

    if (cmiChatColor != null) {
      if (Version.isFolia()) {
        commandSender.sendMessage(ChatColor.RED + "Doesn't work on Folia type servers");
        return null;
      }

      flagSecond = true;
    }

    if (flagSecond == null) {
      flagSecond = !playerSecond.isGlowing();
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(playerSecond);
    playerSecond.setGlowing(flagSecond);
    cmiUser.setGlow(cmiChatColor == null ? null : cmiChatColor.getColor(), false);
    cmiUser.addForDelayedSave();
    Snd snd = new Snd().setSender(commandSender).setTarget(playerSecond);
    cmi.save(playerSecond);
    if (!flag) {
      CMILC.info(
          this,
          commandSender,
          "feedback",
          snd,
          "[color]",
          cmiChatColor == null ? "" : cmiChatColor.getColorCode(),
          "[boolean]",
          flagSecond);
    }

    if (!playerSecond.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(
          this,
          playerSecond,
          "targetfeedback",
          "[boolean]",
          flagSecond,
          "[color]",
          cmiChatColor == null ? "" : cmiChatColor.getColorCode(),
          snd);
    }

    return true;
  }

  private void openGui(final CommandSender commandSender, final Player player) {
    CMIGui cmiGui = new CMIGui(player);
    cmiGui.setTitle(CMILC.getIM(this, "title"));
    final org.bukkit.ChatColor chatColor =
        CMI.getInstance().getPlayerManager().getUser(player).getGlow();
    int value = 0;

    for (final var entry : CMIChatColor.getByName().entrySet()) {
      if (((CMIChatColor) entry.getValue()).isColor()) {
        if (value == 9) {
          value = 10;
        }

        ItemStack itemStack = CMIMaterial.LEATHER_CHESTPLATE.newItemStack();
        final boolean flag =
            PermissionsManager.CMIPerm.command_glow_color_$1.hasPermission(
                commandSender, ((CMIChatColor) entry.getValue()).getName().toLowerCase());
        if (!flag) {
          itemStack = CMIMaterial.CHAINMAIL_CHESTPLATE.newItemStack();
        }

        if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {
          LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
          leatherArmorMeta.setColor(((CMIChatColor) entry.getValue()).getRGBColor());
          itemStack.setItemMeta(leatherArmorMeta);
        }

        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(value, itemStack) {
              public void click(GUIClickType guiClickType) {
                if (flag) {
                  if (chatColor != null
                      && chatColor.equals(((CMIChatColor) entry.getValue()).getColor())) {
                    CMI.getInstance()
                        .performCommand(
                            commandSender,
                            CommandsHandler.getLabel()
                                + " "
                                + glow.class.getSimpleName()
                                + " "
                                + player.getName()
                                + " false");
                  } else {
                    CMI.getInstance()
                        .performCommand(
                            commandSender,
                            CommandsHandler.getLabel()
                                + " "
                                + glow.class.getSimpleName()
                                + " "
                                + player.getName()
                                + " "
                                + ((String) entry.getKey()).toString());
                  }
                }

                glow.this.openGui(commandSender, player);
              }
            };
        cmiGuiButton.setName(
            ((CMIChatColor) entry.getValue()).getBukkitColorCode()
                + CMIText.firstToUpperCase(((String) entry.getKey()).toString().replace("_", " ")));
        cmiGuiButton.hideItemFlags();
        if (chatColor != null && chatColor.equals(((CMIChatColor) entry.getValue()).getColor())) {
          cmiGuiButton.setGlowing();
        }

        cmiGuiButton.addLore(flag ? CMILC.getIM(this, "lore") : CMILC.getIM(this, "noPerm"));
        cmiGui.addButton(cmiGuiButton);
        value++;
      }
    }

    CMIGuiButton cMIGuiButtonSecond =
        new CMIGuiButton(22, CMIMaterial.PAPER) {
          public void click(GUIClickType gUIClickTypeSecond) {
            CMI.getInstance()
                .performCommand(
                    commandSender,
                    CommandsHandler.getLabel()
                        + " "
                        + glow.class.getSimpleName()
                        + " "
                        + player.getName()
                        + " false");
            glow.this.openGui(commandSender, player);
          }
        };
    cMIGuiButtonSecond.setName(LC.info_clear.getLocale(new Object[0]));
    cmiGui.addButton(cMIGuiButtonSecond);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }
}
