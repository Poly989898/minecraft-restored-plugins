package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class CMIPlayerGlowData {
  private ChatColor glow;
  private static HashMap<UUID, CMIPlayerGlowData> glowData = new HashMap<>();

  public CMIPlayerGlowData(ChatColor chatColor) {
    this.setGlow(chatColor);
  }

  public ChatColor getGlow() {
    return this.glow;
  }

  public void setGlow(ChatColor chatColor) {
    this.glow = chatColor;
  }

  public static ChatColor getGlow(UUID uuid) {
    CMIPlayerGlowData cmiPlayerGlowData = glowData.get(uuid);
    return cmiPlayerGlowData == null ? null : cmiPlayerGlowData.getGlow();
  }

  public static void setGlow(UUID uuid, ChatColor chatColor) {
    glowData
        .computeIfAbsent(uuid, playerUuid -> new CMIPlayerGlowData(chatColor))
        .setGlow(chatColor);
  }

  public static void applyGlow(UUID uuid) {
    CMIPlayerGlowData cmiPlayerGlowData = glowData.get(uuid);
    if (cmiPlayerGlowData != null) {
      ChatColor chatColor = cmiPlayerGlowData.getGlow();
      if (!Config.DisableTeamManagement && !Version.isFolia()) {
        CMIUser cmiUser = CMIUser.getUser(uuid);
        if (cmiUser != null && cmiUser.isOnline()) {
          if (chatColor != null) {
            Team team = CMI.getInstance().getSB().getPlayerTeam(cmiUser.getPlayer());
            if (team == null) {
              team = CMI.getInstance().getSB().getTeam("CMINP" + cmiUser.getId());
            }

            try {
              if (team != null) {
                if (!team.hasEntry(cmiUser.getName(false))) {
                  team.addEntry(cmiUser.getName());
                }

                cmiUser.reapplyNamePlate();
                return;
              }

              Bukkit.getScoreboardManager()
                  .getMainScoreboard()
                  .registerNewTeam("CMINP" + cmiUser.getId());
              team = CMI.getInstance().getSB().getTeam("CMINP" + cmiUser.getId());
              if (team == null) {
                return;
              }

              team.setCanSeeFriendlyInvisibles(false);
              if (!team.hasEntry(cmiUser.getName(false))) {
                team.addEntry(cmiUser.getName());
              }

              cmiUser.reapplyNamePlate();
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
          } else {
            Team playerTeam = CMI.getInstance().getSB().getPlayerTeam(cmiUser.getPlayer());
            if (playerTeam == null) {
              playerTeam = CMI.getInstance().getSB().getTeam("CMINP" + cmiUser.getId());
            }

            try {
              if (playerTeam != null) {
                playerTeam.setColor(ChatColor.WHITE);
              }
            } catch (Throwable throwable2) {
            }

            cmiUser.reapplyNamePlate();
          }
        }
      }
    }
  }
}
