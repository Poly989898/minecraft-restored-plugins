package com.Zrips.CMI.Modules.PlayTimeRewards;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class PlayTimeReward {
  private String name;
  private String displayName;
  private List<String> description;
  private List<String> commands;
  private long range;
  private boolean autoClaim = false;

  public PlayTimeReward(String string) {
    this.name = string;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(String string) {
    this.displayName = string;
  }

  public List<String> getDescription() {
    return this.description;
  }

  public void setDescription(List<String> list) {
    this.description = list;
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public void setCommands(List<String> list) {
    this.commands = list;
  }

  public void performCommands(Player player) {
    Snd snd = new Snd();
    snd.setTarget(player);
    var list = CMI.getInstance().getLM().updateSnd(snd, new ArrayList<>(this.getCommands()));
    CMI.getInstance().getSpecializedCommandManager().processCmds(list, player);
  }

  public boolean isAutoClaim() {
    return this.autoClaim;
  }

  public void setAutoClaim(boolean flag) {
    this.autoClaim = flag;
  }

  public Long getRange() {
    return this.range;
  }

  public void setRange(Long newRange) {
    this.range = newRange;
  }
}
