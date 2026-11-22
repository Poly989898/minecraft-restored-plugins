package com.Zrips.CMI.Modules.DynMap;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import org.bukkit.entity.Player;
import org.dynmap.DynmapCommonAPI;

public class DynMapManager {
  private CMI plugin;
  private DynmapCommonAPI dynmap;

  public DynMapManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void init() {
    if (CMIHookType.dynmap.isEnabled()) {
      try {
        this.dynmap = (DynmapCommonAPI) CMIHookType.dynmap.getPlugin();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        CMIHookType.dynmap.setEnabled(false);
      }
    }
  }

  public void processPlayer(Player player) {
    this.processPlayer(this.plugin.getPlayerManager().getUser(player));
  }

  public void processPlayer(CMIUser cmiUser) {
    if (CMIHookType.dynmap.isEnabled() && cmiUser != null && this.dynmap != null) {
      if (!cmiUser.isVanished()
          || cmiUser.hasPermission(PermissionsManager.CMIPerm.dynmap_hidden)) {
        String string = cmiUser.getName();
        if (string != null) {
          this.dynmap.assertPlayerInvisibility(string, cmiUser.isVanished(), "CMIVanish");
        }
      }
    }
  }

  public void sentMessage(Player player, String string) {
    if (CMIHookType.dynmap.isEnabled()) {
      try {
        this.dynmap.postPlayerMessageToWeb(player.getName(), player.getName(), string);
      } catch (Exception exception) {
      }
    }
  }
}
