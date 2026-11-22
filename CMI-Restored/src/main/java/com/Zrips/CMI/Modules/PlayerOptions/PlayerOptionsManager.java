package com.Zrips.CMI.Modules.PlayerOptions;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.homes;
import com.Zrips.CMI.commands.list.options;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;

public class PlayerOptionsManager {
  private CMI plugin;
  private Boolean guiCloseButton;
  private int guiCloseButtonSlot;
  private CMIItemStack guiCloseButtonItem;
  private List<String> guiCloseButtonCommands;
  private boolean guiInfoButton;
  private int guiInfoButtonSlot;
  private CMIItemStack guiInfoButtonItem;
  private List<String> guiInfoButtonCommands;
  public static String defaultString = "";

  public PlayerOptionsManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    String string = "Player.Options.CloseButton.";
    this.guiCloseButton = configReader.get(string + "Use", true);
    this.guiCloseButtonSlot = configReader.get(string + "Slot", 9) - 1;
    this.guiCloseButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0="));
    this.guiCloseButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
    string = "Player.Options.InfoButton.";
    configReader.addComment(
        string + "Use",
        new String[] {
          "Extra button to be used in case you want to provide any aditional information when"
              + " clicking on it"
        });
    this.guiInfoButton = configReader.get(string + "Use", false);
    this.guiInfoButtonSlot = configReader.get(string + "Slot", 1) - 1;
    this.guiInfoButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ=="));
    this.guiInfoButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
    defaultString = "";
    configReader.addComment(
        "Player.Options.Defaults",
        new String[] {
          "Defines default states for player options",
          "This will not have any effect if player already edited his options with /cmi options"
              + " command"
        });
    PlayerOption[] playerOptions;
    int value = (playerOptions = PlayerOption.values()).length;

    for (int index = 0; index < value; index++) {
      PlayerOption playerOption = playerOptions[index];
      if (!defaultString.isEmpty()) {
        defaultString = defaultString + ":";
      }

      defaultString = defaultString + (playerOption.isEnabled() ? "1" : "0");
      switch (playerOption) {
        case chatSpy:
        case cmdSpy:
        case signSpy:
          configReader.addComment(
              "Player.Options.Defaults." + playerOption.toString(),
              new String[] {"!Strongly not recommended to be enabled!"});
        default:
          playerOption.setEnabled(
              configReader.get(
                  "Player.Options.Defaults." + playerOption.toString(), playerOption.isEnabled()));
      }
    }

    value = (playerOptions = PlayerOption.values()).length;

    for (int index = 0; index < value; index++) {
      PlayerOption optionToEdit = playerOptions[index];
      CMIItemStack cmiItemStack =
          CMILib.getInstance()
              .getItemManager()
              .getItem(
                  configReader.get(
                      "Player.Options.Icons." + optionToEdit.toString(),
                      optionToEdit.getIcon().getType().toString()));
      if (cmiItemStack == null) {
        cmiItemStack = CMIMaterial.STONE.newCMIItemStack();
      }

      optionToEdit.setIcon(cmiItemStack.getItemStack());
    }
  }

  public void openOptionGUI(CMIUser cmiUser) {
    this.openOptionGUI(cmiUser.getPlayer(), cmiUser);
  }

  public void openOptionGUI(final Player player, final CMIUser cmiUser) {
    if (player.isOnline()) {
      final Player optionPlayer = cmiUser.getPlayer();
      final CMIGui cmiGui = new CMIGui(player);
      cmiGui.addLock(InvType.Gui);
      cmiGui.setTitle(CMILC.getIM(options.class, "title"));
      cmiGui.setInvSize(GUIRows.r6);
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(this.guiCloseButtonSlot, this.guiCloseButtonItem) {
            public void click(GUIClickType guiClickType) {
              Snd snd = new Snd();
              snd.setTarget(optionPlayer);
              snd.setSender(optionPlayer);
              snd.setSource(player);
              PlayerOptionsManager.this
                  .plugin
                  .getSpecializedCommandManager()
                  .processCmds(
                      PlayerOptionsManager.this
                          .plugin
                          .getLM()
                          .updateSnd(
                              snd,
                              new ArrayList<>(PlayerOptionsManager.this.guiCloseButtonCommands)),
                      player);
            }
          };
      cmiGuiButton.setName(CMILC.getIM(homes.class, "closeButton"));
      if (this.guiCloseButton) {
        cmiGui.addButton(cmiGuiButton);
      }

      cmiGuiButton =
          new CMIGuiButton(this.guiInfoButtonSlot, this.guiInfoButtonItem) {
            public void click(GUIClickType clickType) {
              Snd optionSnd = new Snd();
              optionSnd.setTarget(optionPlayer);
              optionSnd.setSender(optionPlayer);
              optionSnd.setSource(player);
              PlayerOptionsManager.this
                  .plugin
                  .getSpecializedCommandManager()
                  .processCmds(
                      PlayerOptionsManager.this
                          .plugin
                          .getLM()
                          .updateSnd(
                              optionSnd,
                              new ArrayList<>(PlayerOptionsManager.this.guiInfoButtonCommands)),
                      player);
            }
          };
      cmiGuiButton.setName(CMILC.getIM(homes.class, "infoButton"));
      if (this.guiInfoButton) {
        cmiGui.addButton(cmiGuiButton);
      }

      cmiGui.fillInEdges();
      PlayerOption[] playerOptions;
      int value = (playerOptions = PlayerOption.values()).length;

      for (int index = 0; index < value; index++) {
        final PlayerOption playerOption = playerOptions[index];
        if (playerOption.isGlobalEnabled()
            && PermissionsManager.CMIPerm.command_options_$1.hasPermission(
                player, playerOption.toString())
            && (playerOption.getVersionFrom() == null
                || Version.isCurrentEqualOrHigher(playerOption.getVersionFrom()))) {
          cmiGuiButton =
              new CMIGuiButton(playerOption.getIcon()) {
                public void click(GUIClickType clickType) {
                  cmiUser.setOptionState(playerOption, !cmiUser.getOptionState(playerOption));
                  this.updateLooks();
                  this.update(cmiGui);
                  cmiUser.addForDelayedSave();
                }

                public void updateLooks() {
                  this.setName(playerOption.getDesc());
                  this.clearLore();
                  this.addLore(
                      CMILC.getIM(
                          options.class, "state", "[state]", cmiUser.getOptionState(playerOption)));
                }
              };
          cmiGuiButton.hideItemFlags();
          cmiGui.addButton(cmiGuiButton);
        }
      }

      cmiGui.fillEmptyButtons();
      cmiGui.open();
    }
  }
}
