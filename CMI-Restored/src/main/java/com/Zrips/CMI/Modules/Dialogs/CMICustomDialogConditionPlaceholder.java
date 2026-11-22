package com.Zrips.CMI.Modules.Dialogs;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

public class CMICustomDialogConditionPlaceholder {
  private Set<CMICustomDialogPlaceholder> placeholders = new HashSet<>();

  public Set<CMICustomDialogPlaceholder> getPlaceholders() {
    return this.placeholders;
  }

  public void setPlaceholders(Set<CMICustomDialogPlaceholder> set) {
    this.placeholders = set;
  }

  public void addPlaceholder(CMICustomDialogPlaceholder cmiCustomDialogPlaceholder) {
    this.placeholders.add(cmiCustomDialogPlaceholder);
  }

  public boolean isPlaceholderValid(Player player) {
    return this.isPlaceholderValid(player, null);
  }

  public boolean isPlaceholderValid(Player player, @Nullable Map<String, String> map) {
    for (CMICustomDialogPlaceholder cmiCustomDialogPlaceholder : this.placeholders) {
      if (!cmiCustomDialogPlaceholder.isPlaceholderValid(player, map)) {
        return false;
      }
    }

    return true;
  }

  @Nonnull
  public static CMICustomDialogConditionPlaceholder deserialize(Map<String, Object> map) {
    HashSet<CMICustomDialogPlaceholder> hashSet = new HashSet();
    if (map.containsKey("Placeholder")) {
      if (map.get("Placeholder") instanceof String) {
        hashSet.add(get((String) map.get("Placeholder")));
      } else if (map.get("Placeholder") instanceof List) {
        for (String string : (List<String>) map.get("Placeholder")) {
          hashSet.add(get(string));
        }
      }
    }

    CMICustomDialogConditionPlaceholder cmiCustomDialogConditionPlaceholder =
        new CMICustomDialogConditionPlaceholder();
    cmiCustomDialogConditionPlaceholder.setPlaceholders(hashSet);
    return cmiCustomDialogConditionPlaceholder;
  }

  private static CMICustomDialogPlaceholder get(String string) {
    CMIDialogConditionActionType cmiDialogConditionActionType =
        CMIDialogConditionActionType.get(string);
    CMICustomDialogPlaceholder cmiCustomDialogPlaceholder = new CMICustomDialogPlaceholder();
    if (cmiDialogConditionActionType != null) {
      String[] strings = string.split(cmiDialogConditionActionType.getAction(), 2);
      cmiCustomDialogPlaceholder.setPlaceholder(strings[0]);
      if (strings.length > 1) {
        switch (cmiDialogConditionActionType) {
          case Equal:
            cmiCustomDialogPlaceholder.setValidPlaceholderState(strings[1]);
            break;
          case MoreEqual:
          case LessEqual:
          case More:
          case Less:
            try {
              cmiCustomDialogPlaceholder.setValidPlaceholderState(Double.parseDouble(strings[1]));
            } catch (Exception exception) {
              cmiCustomDialogPlaceholder.setValidPlaceholderState(strings[1]);
            }
        }
      }

      cmiCustomDialogPlaceholder.setActionType(cmiDialogConditionActionType);
    } else {
      cmiCustomDialogPlaceholder.setPlaceholder(string);
    }

    return cmiCustomDialogPlaceholder;
  }
}
