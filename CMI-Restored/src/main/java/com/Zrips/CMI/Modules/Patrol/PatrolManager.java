package com.Zrips.CMI.Modules.Patrol;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PatrolManager {
  private CMI plugin;

  public PatrolManager(CMI cmi) {
    this.plugin = cmi;
  }

  public CMIUser getNextUser() {
    ArrayList arrayList = new ArrayList(Bukkit.getOnlinePlayers());
    int value = arrayList.size();
    CMIUser cmiUser = null;
    int index = 0;
    Collections.shuffle(arrayList);
    Long oldestPatrol = System.currentTimeMillis();

    while (index < value) {
      Player player = (Player) arrayList.get(index);
      index++;
      if (!PermissionsManager.CMIPerm.command_patrol_bypass.hasPermission(player)) {
        CMIUser candidateUser = this.plugin.getPlayerManager().getUser(player);
        if (candidateUser.getLastPatrol() == 0L) {
          cmiUser = candidateUser;
          break;
        }

        if (candidateUser.getLastPatrol() < oldestPatrol) {
          cmiUser = candidateUser;
          oldestPatrol = candidateUser.getLastPatrol();
        }
      }
    }

    return cmiUser;
  }
}
