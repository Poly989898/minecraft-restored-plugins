package com.Zrips.CMI.Modules.Dialogs;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMICustomDialogCondition {
  private String permission = null;
  private CMICustomDialogConditionPlaceholder placeholder =
      new CMICustomDialogConditionPlaceholder();

  public String getPermission() {
    return this.permission;
  }

  public CMICustomDialogCondition setPermission(String string) {
    this.permission = string;
    return this;
  }

  public CMICustomDialogCondition setPlaceholder(
      @NotNull CMICustomDialogConditionPlaceholder cmiCustomDialogConditionPlaceholder) {
    this.placeholder = cmiCustomDialogConditionPlaceholder;
    return this;
  }

  public boolean hasPermission(Player player) {
    if (player == null) {
      return false;
    } else if (this.getPermission() == null) {
      return true;
    } else {
      return this.getPermission().startsWith("!")
          ? !player.hasPermission(this.getPermission().substring(1))
          : player.hasPermission(this.getPermission());
    }
  }

  public boolean isPlaceholderValid(Player player) {
    return this.isPlaceholderValid(player, null);
  }

  public boolean isPlaceholderValid(Player player, @Nullable Map<String, String> map) {
    return player == null ? false : this.placeholder.isPlaceholderValid(player, map);
  }

  public boolean isConditionValid(Player player, @Nullable CMICustomDialog cmiCustomDialog) {
    return this.isConditionValid(
        player, cmiCustomDialog == null ? null : cmiCustomDialog.getVariables());
  }

  public boolean isConditionValid(Player player) {
    return this.isConditionValid(player, new HashMap<>());
  }

  public boolean isConditionValid(Player player, @Nullable Map<String, String> map) {
    return this.hasPermission(player) && this.isPlaceholderValid(player, map);
  }

  public static CMICustomDialogCondition deserialize(Map<String, Object> map) {
    CMICustomDialogCondition cmiCustomDialogCondition = new CMICustomDialogCondition();
    if (map.containsKey("Permission")) {
      cmiCustomDialogCondition.setPermission((String) map.get("Permission"));
    }

    cmiCustomDialogCondition.setPlaceholder(CMICustomDialogConditionPlaceholder.deserialize(map));
    return cmiCustomDialogCondition;
  }
}
