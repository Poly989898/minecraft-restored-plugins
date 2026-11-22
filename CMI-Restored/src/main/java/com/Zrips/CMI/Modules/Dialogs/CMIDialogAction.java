package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Player;

public class CMIDialogAction {
  private List<String> actions = null;
  private CMIDialogButtonType type = CMIDialogButtonType.COMMANDS;

  public CMIDialogAction(Object object, CMIDialogButtonType cmiDialogButtonType) {
    if (object instanceof List) {
      this.actions = (List<String>) object;
    } else if (object instanceof String) {
      this.actions = new ArrayList<>(Arrays.asList((String) object));
    }

    this.type = cmiDialogButtonType;
  }

  public static CMIDialogAction deserialize(Map<String, Object> map) {
    CMIDialogButtonType[] cmiDialogButtonTypes;
    int value = (cmiDialogButtonTypes = CMIDialogButtonType.values()).length;

    for (int index = 0; index < value; index++) {
      CMIDialogButtonType cmiDialogButtonType = cmiDialogButtonTypes[index];
      if (cmiDialogButtonType.getConfigEntry() != null
          && map.containsKey(cmiDialogButtonType.getConfigEntry())) {
        return new CMIDialogAction(
            map.get(cmiDialogButtonType.getConfigEntry()), cmiDialogButtonType);
      }
    }

    return null;
  }

  @Nullable
  public List<String> getActions() {
    return this.getActions(null);
  }

  @Nullable
  public List<String> getActions(String string) {
    if (string == null) {
      return new ArrayList<>(this.actions);
    }

    var arrayList = new ArrayList<>(this.actions);

    for (int value = 0; value < this.actions.size(); value++) {
      arrayList.set(value, this.actions.get(value).replace("[value]", string));
    }

    return arrayList;
  }

  public CMIDialogButtonType getType() {
    return this.type;
  }

  public void perform(CMIDialog cmiDialog, Player player) {
    this.perform(cmiDialog, player, null);
  }

  public void perform(CMIDialog cmiDialog, Player player, @Nullable String string) {
    switch (this.getType()) {
      case CUSTOM:
      case URL:
      case SUGGESTION:
      default:
        break;
      case COMMANDS:
        cmiDialog.show(player);
        CMI.getInstance()
            .getSpecializedCommandManager()
            .processAliasCmdsAsPlayer(
                "Dialogs", CMIDialog.updateVariable(player, this.getActions(string)), player);
        break;
      case OPENDIALOG:
        CMIScheduler.runTaskAsynchronously(
            CMI.getInstance(),
            () -> {
              CMICustomDialog cmiCustomDialog =
                  CMI.getInstance()
                      .getDialogManager()
                      .getDialog(this.getActions(string).get(0), false);
              if (cmiCustomDialog != null) {
                cmiCustomDialog.show(player);
              }
            });
    }
  }
}
