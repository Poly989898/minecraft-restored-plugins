package com.Zrips.CMI.Modules.EventActions;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.events.CMIEventCommandEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventActionManager {
  private CMI plugin;
  private String fileName = "EventCommands.yml";

  public EventActionManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void performCommands(EventActionManager.eventAction eventActionValue, Player player) {
    this.performCommands(eventActionValue, player, null, null);
  }

  public void performCommands(
      EventActionManager.eventAction eventActionValue, Player player, String string) {
    this.performCommands(eventActionValue, player, null, string);
  }

  public void performCommands(
      EventActionManager.eventAction eventActionValue, Player player, Player playerSecond) {
    this.performCommands(eventActionValue, player, playerSecond, null);
  }

  public void performCommands(
      EventActionManager.eventAction eventActionValue,
      Player player,
      Player playerSecond,
      String string) {
    this.performCommands(eventActionValue, player, playerSecond, string, null);
  }

  public void performCommands(
      EventActionManager.eventAction eventActionValue,
      Player player,
      Player playerSecond,
      Object object,
      Object value) {
    if (player != null
        && player.isOnline()
        && eventActionValue != null
        && eventActionValue.isEnabled()) {
      String string = "";
      if (object != null) {
        switch (eventActionValue) {
          case sneakingSwapHandItems:
          case swapHandItems:
            string = CMIMaterial.get((ItemStack) object).getTranslatedName();
            break;
          default:
            try {
              string = (String) object;
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
        }
      }

      var list = eventActionValue.getCommands();
      var varSecond = eventActionValue.getSourceCommands();
      CMIEventCommandEvent cmiEventCommandEvent =
          new CMIEventCommandEvent(player, playerSecond, eventActionValue, list, varSecond);
      Bukkit.getPluginManager().callEvent(cmiEventCommandEvent);
      if (!cmiEventCommandEvent.isCancelled()) {
        Snd snd = new Snd().setTarget(player).setSource(cmiEventCommandEvent.getSource());
        ArrayList<String> arrayList = new ArrayList();

        for (String text : cmiEventCommandEvent.getCommands()) {
          String textSecond = text;
          textSecond = textSecond.replace("[value]", string);
          textSecond = this.plugin.getLM().updateSnd(snd, textSecond);
          arrayList.add(textSecond);
        }

        this.plugin.getSpecializedCommandManager().processCmds(arrayList, player);
        if (eventActionValue.isIncludeSource() && playerSecond != null) {
          arrayList = new ArrayList();

          for (String textThird : cmiEventCommandEvent.getSourceCommands()) {
            String textFourth = textThird;
            textFourth = textFourth.replace("[value]", string);
            textFourth = this.plugin.getLM().updateSnd(snd, textFourth);
            arrayList.add(textFourth);
          }

          this.plugin.getSpecializedCommandManager().processCmds(arrayList, playerSecond);
        }
      }
    }
  }

  public void load() {
    Long time = System.currentTimeMillis();
    this.plugin.safeRenameFile("eventCommands.yml", "Settings" + File.separator + this.fileName);
    File file = new File(this.plugin.getDataFolder(), "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      configReader.getC().options().copyDefaults(true);
      configReader.addComment(
          "firstJoinServer",
          new String[] {
            "Commands performed on particular event",
            "Full color code support and some variables",
            "If command starts with msg! and then player name is given, simple message will be sent"
                + " to that player if he is online.",
            "If command starts with broadcast! then message will be sent to everyone on server in"
                + " simple manear",
            "If command starts with actionbar! then all players will get action bar message defined"
                + " after this variable",
            "If command starts with title! then all players will get title message defined after"
                + " this variable",
            "If command starts with subtitle! then all players will get subtitle message defined"
                + " after this variable",
            "If command starts with kickall! then all players will be kicked from server with"
                + " defined message. Usefull before server stop.",
            "If command starts with perm:[permissionNode]! then command will get performed if"
                + " player has permission node. This will not work when we don't know who is target"
                + " player. In example: perm:cmi.announce.vip! broadcast! Hello vip guys",
            "[playerName] to insert target player name",
            "[playerDisplayName] to insert player display name",
            "[lvl] to insert player level",
            "[exp] to insert player total exp",
            "[hp] to insert player health",
            "[maxHp] to insert player max health",
            "[hunger] to insert player hunger level",
            "[gameMode] to insert player gamemode",
            "Keep in mind that joinServer commands will not be performd on firstJoinServer event",
            "",
            "ATTENTION!",
            "Extreme usage of heavy commands could result in increased load for your server. Try"
                + " using as little of them as possible for one event",
            "and if possible spread them out with a delay by using \"delay! 0.3\" or similar"
                + " variable",
            "This will allow server to catch up with his calculation instead of forcing him to do"
                + " all of those commands at once"
          });
      configReader.addComment(
          EventActionManager.eventAction.playerKillPlayer.name() + ".Enabled",
          new String[] {
            "[sourceName] to insert source player name",
            "[sourceDisplayName] to insert player display name",
            "[sourceLvl] to insert player level",
            "[sourceExp] to insert player total exp",
            "[sourceHp] to insert player health",
            "[sourceMaxHp] to insert player max health",
            "[sourceHunger] to insert player hunger level",
            "[sourceGameMode] to insert player gamemode",
            "Keep in mind that in case you are using specialized commands, then Commands section"
                + " will be performed on player who died and SourceCommands will be performed on"
                + " player who killed"
          });
      configReader.addComment(
          EventActionManager.eventAction.advancementDone.name() + ".Enabled",
          new String[] {
            "use [value] variable to insert advancements identificator",
            "This returns path to advancement like recipes/building_blocks/emerald_block which can"
                + " be used in commands in case you want to filter out specific ones"
          });
      configReader.addComment(
          EventActionManager.eventAction.sneakingSwapHandItems.name() + ".CancelEvent",
          new String[] {
            "Item swap event can be canceled if needed. This only works if event monitor is enabled"
          });
      int value = 0;
      EventActionManager.eventAction[] eventActions;
      int count = (eventActions = EventActionManager.eventAction.values()).length;

      for (int countSecond = 0; countSecond < count; countSecond++) {
        EventActionManager.eventAction eventActionValue = eventActions[countSecond];
        eventActionValue.setEnabled(
            configReader.get(
                eventActionValue.name() + ".Enabled",
                configReader.getC().getBoolean(eventActionValue.name() + ".Use", false)));
        if (eventActionValue.equals(EventActionManager.eventAction.sneakingSwapHandItems)) {
          eventActionValue.setDisableEvent(
              configReader.get(eventActionValue.name() + ".CancelEvent", false));
        }

        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("cmi msg [playerName] !This is event command example!");
        arrayList.add(
            "cmi msg [playerName] !Originates from "
                + EventActionManager.eventAction.sneakingSwapHandItems.name()
                + " event!");
        eventActionValue.setCommands(
            configReader.get(eventActionValue.name() + ".Commands", arrayList));
        if (eventActionValue.isIncludeSource()) {
          eventActionValue.setSourceCommands(
              configReader.get(eventActionValue.name() + ".SourceCommands", new ArrayList()));
        }

        if (eventActionValue.isEnabled()) {
          value +=
              eventActionValue.getCommands().size() + eventActionValue.getSourceCommands().size();
        }
      }

      configReader.save();
      this.plugin.loadMessage(value, "event action commands", System.currentTimeMillis() - time);
    }
  }

  public enum eventAction {
    firstJoinServer,
    joinServer,
    quitServer,
    playerDeath(true),
    playerKillPlayer(true),
    playerRespawn,
    playerTeleport,
    bedLeave,
    bedEnter,
    playerWorldChange,
    playerPreWorldChange,
    playerGameModeChange,
    playerKick,
    playerBan,
    playerLevelChange,
    voidFall,
    elytraStartGlide,
    advancementDone,
    elytraEndGlide,
    sneakingSwapHandItems,
    swapHandItems,
    pvpstart,
    pvpend;

    private List<String> commands = new ArrayList<>();
    private List<String> sourceCommands = new ArrayList<>();
    private boolean secondary = false;
    private boolean enabled = false;
    private boolean disableEvent = false;

    eventAction() {}

    eventAction(boolean flag) {
      this.secondary = flag;
    }

    public List<String> getCommands() {
      return this.commands;
    }

    public List<String> getSourceCommands() {
      return this.sourceCommands;
    }

    public void setCommands(List<String> list) {
      this.commands = list;
    }

    public void setSourceCommands(List<String> list) {
      this.sourceCommands = list;
    }

    public void clear() {
      this.commands.clear();
      this.sourceCommands.clear();
    }

    public boolean isIncludeSource() {
      return this.secondary;
    }

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean flag) {
      this.enabled = flag;
    }

    public boolean isDisableEvent() {
      return this.disableEvent;
    }

    public void setDisableEvent(boolean flag) {
      this.disableEvent = flag;
    }
  }
}
