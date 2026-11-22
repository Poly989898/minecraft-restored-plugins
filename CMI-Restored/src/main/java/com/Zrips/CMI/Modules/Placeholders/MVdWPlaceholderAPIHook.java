package com.Zrips.CMI.Modules.Placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import java.util.UUID;

public class MVdWPlaceholderAPIHook {
  public static boolean hook(CMI cmi) {
    if (!CMIHookType.MVdWPlaceholderAPI.isEnabled()) {
      return false;
    }

    Placeholder.CMIPlaceHolders[] cmiPlaceHolderses;
    int value = (cmiPlaceHolderses = Placeholder.CMIPlaceHolders.values()).length;

    for (int index = 0; index < value; index++) {
      Placeholder.CMIPlaceHolders cmiPlaceHolders = cmiPlaceHolderses[index];
      String string = cmiPlaceHolders.getMVdW();
      PlaceholderAPI.registerPlaceholder(
          cmi,
          string,
          new PlaceholderReplacer() {
            public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
              String placeholderName = event.getPlaceholder();
              UUID uuid = null;
              if (event.getPlayer() != null) {
                uuid = event.getPlayer().getUniqueId();
              } else if (event.getOfflinePlayer() != null) {
                uuid = event.getOfflinePlayer().getUniqueId();
              }

              Placeholder.CMIPlaceHolders placeholder =
                  Placeholder.CMIPlaceHolders.getByName("{" + placeholderName + "}");
              if (placeholder == null) {
                return placeholderName.startsWith("user_meta_") && uuid != null
                    ? CMI.getInstance()
                        .getPlayerManager()
                        .getUser(uuid)
                        .getMeta()
                        .getValue(
                            placeholderName.substring(
                                "user_meta_".length(), placeholderName.length()))
                    : null;
              } else {
                return CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .getValue(event.getPlayer(), placeholder, "{" + placeholderName + "}");
              }
            }
          });
    }

    return true;
  }
}
