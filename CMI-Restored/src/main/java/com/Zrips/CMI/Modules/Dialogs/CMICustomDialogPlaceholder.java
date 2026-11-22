package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import java.util.Map;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

public class CMICustomDialogPlaceholder {
  private String placeholder = null;
  private Object validPlaceholderState = null;
  private CMIDialogConditionActionType actionType = null;

  public String getPlaceholder() {
    return this.placeholder;
  }

  public CMICustomDialogPlaceholder setPlaceholder(@Nullable String string) {
    this.placeholder = string;
    return this;
  }

  public Object getValidPlaceholderState() {
    return this.validPlaceholderState;
  }

  public CMICustomDialogPlaceholder setValidPlaceholderState(@Nullable Object object) {
    this.validPlaceholderState = object;
    return this;
  }

  public CMIDialogConditionActionType getActionType() {
    return this.actionType;
  }

  public CMICustomDialogPlaceholder setActionType(
      CMIDialogConditionActionType cmiDialogConditionActionType) {
    this.actionType = cmiDialogConditionActionType;
    return this;
  }

  public boolean isPlaceholderValid(Player player) {
    return this.isPlaceholderValid(player, null);
  }

  public boolean isPlaceholderValid(Player player, @Nullable Map<String, String> map) {
    if (player == null) {
      return false;
    }

    if (this.getPlaceholder() == null) {
      return true;
    }

    String string = this.getPlaceholder();
    if (map != null) {
      for (var entry : map.entrySet()) {
        string = string.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
      }
    }

    string = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
    if (string.isEmpty()) {
      return false;
    }

    if (this.actionType == null) {
      String lowerCaseValue;
      switch (lowerCaseValue = string.toLowerCase()) {
        case "enabled":
        case "enable":
        case "1":
        case "on":
        case "yes":
        case "true":
          return true;
      }

      return false;
    } else {
      switch (this.actionType) {
        case Equal:
          return string.equalsIgnoreCase(String.valueOf(this.getValidPlaceholderState()));
        case MoreEqual:
        case LessEqual:
        case More:
        case Less:
          try {
            double number = Double.parseDouble(string);
            double expectedValue = 0.0;
            if (this.getValidPlaceholderState() instanceof String) {
              String placeholderValue = (String) this.getValidPlaceholderState();
              if (placeholderValue == null || placeholderValue.isEmpty()) {
                return false;
              }

              if (map != null) {
                for (var placeholderEntry : map.entrySet()) {
                  placeholderValue =
                      placeholderValue.replace(
                          (CharSequence) placeholderEntry.getKey(),
                          (CharSequence) placeholderEntry.getValue());
                }
              }

              String resolvedValue =
                  CMI.getInstance()
                      .getPlaceholderAPIManager()
                      .updatePlaceHolders(player, placeholderValue);
              if (resolvedValue == null || resolvedValue.isEmpty()) {
                return false;
              }

              expectedValue =
                  Double.parseDouble(
                      CMI.getInstance()
                          .getPlaceholderAPIManager()
                          .updatePlaceHolders(player, placeholderValue));
            } else {
              expectedValue = (Double) this.getValidPlaceholderState();
            }

            if (this.getValidPlaceholderState() != null) {
              switch (this.actionType) {
                case MoreEqual:
                  if (number >= expectedValue) {
                    return true;
                  }

                  return false;
                case LessEqual:
                  if (number <= expectedValue) {
                    return true;
                  }

                  return false;
                case More:
                  if (number > expectedValue) {
                    return true;
                  }

                  return false;
                case Less:
                  if (number < expectedValue) {
                    return true;
                  }

                  return false;
              }
            }
          } catch (Exception exception) {
            exception.printStackTrace();
          }

          return false;
        default:
          return false;
      }
    }
  }
}
