package com.Zrips.CMI.Modules.Dialogs;

import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

public class CMIDialogInputSingletons extends CMIDialogInputField {
  List<CMIDialogInputSingleton> singletons = new ArrayList<>();
  private boolean defaultSet = false;
  private CMIDialogInputSingleton first = null;
  private CMIDialogAction action = null;

  public CMIDialogInputSingletons addEntry(CMIDialogInputSingleton cmiDialogInputSingleton) {
    this.add(cmiDialogInputSingleton);
    return this;
  }

  public CMIDialogInputSingletons addEntry(String string, String entryValue) {
    return this.addEntry(string, entryValue, false);
  }

  public CMIDialogInputSingletons addEntry(String string, String entryValue, boolean flag) {
    CMIDialogInputSingleton cmiDialogInputSingleton =
        new CMIDialogInputSingleton(string, entryValue, flag);
    this.add(cmiDialogInputSingleton);
    return this;
  }

  @Override
  public BiConsumer<Player, String> getConsumer() {
    return (BiConsumer<Player, String>) this.consumer;
  }

  private void add(CMIDialogInputSingleton cmiDialogInputSingleton) {
    if (!this.defaultSet && cmiDialogInputSingleton.isDefault()) {
      this.defaultSet = true;
    } else if (this.defaultSet && cmiDialogInputSingleton.isDefault()) {
      cmiDialogInputSingleton.setDefault(false);
    }

    if (this.first == null) {
      this.first = cmiDialogInputSingleton;
    }

    this.singletons.add(cmiDialogInputSingleton);
  }

  public CMIDialogInputSingleton getEntry(String string) {
    for (CMIDialogInputSingleton cmiDialogInputSingleton : this.singletons) {
      if (cmiDialogInputSingleton.getId().equalsIgnoreCase(string)) {
        return cmiDialogInputSingleton;
      }
    }

    return null;
  }

  public List<OptionEntry> getEntries(Player player, @Nullable String string) {
    return this.getEntries(player, string, new HashMap<>());
  }

  public List<OptionEntry> getEntries(
      Player player, @Nullable String string, CMICustomDialog cmiCustomDialog) {
    return this.getEntries(
        player, string, cmiCustomDialog == null ? null : cmiCustomDialog.getVariables());
  }

  public List<OptionEntry> getEntries(
      Player player, @Nullable String string, Map<String, String> map) {
    ArrayList<io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry>
        arrayList = new ArrayList();
    if (!this.defaultSet && !this.singletons.isEmpty()) {
      this.singletons.set(0, this.singletons.get(0).setDefault(true));
      this.defaultSet = true;
    }

    if (string != null) {
      boolean flag = false;

      for (CMIDialogInputSingleton cmiDialogInputSingleton : this.singletons) {
        if (cmiDialogInputSingleton.getId().equals(string)) {
          flag = true;
          break;
        }
      }

      if (flag) {
        for (CMIDialogInputSingleton singleton : this.singletons) {
          arrayList.add(singleton.getOptionEntry(player, singleton.getId().equals(string), map));
        }

        return arrayList;
      }
    }

    boolean isFirst = true;

    for (CMIDialogInputSingleton candidate : this.singletons) {
      OptionEntry optionEntry = candidate.getOptionEntryLimited(player, isFirst, map);
      if (optionEntry.initial()) {
        isFirst = false;
      }

      arrayList.add(optionEntry);
    }

    return arrayList;
  }

  public static CMIDialogInputSingletons deserialize(Map<String, Object> map) {
    CMIDialogInputSingletons cmiDialogInputSingletons = new CMIDialogInputSingletons();
    if (map.containsKey("Options")) {
      for (Map optionMap : (List<Map>) map.get("Options")) {
        cmiDialogInputSingletons.addEntry(CMIDialogInputSingleton.deserialize(optionMap));
      }
    }

    cmiDialogInputSingletons.setAction(CMIDialogAction.deserialize(map));
    return cmiDialogInputSingletons;
  }

  public CMIDialogAction getAction() {
    return this.action;
  }

  public void setAction(CMIDialogAction cmiDialogAction) {
    this.action = cmiDialogAction;
  }
}
