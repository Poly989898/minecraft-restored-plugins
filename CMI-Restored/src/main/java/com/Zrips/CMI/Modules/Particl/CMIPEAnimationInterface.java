package com.Zrips.CMI.Modules.Particl;

import java.awt.Color;
import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CMIPEAnimationInterface {
  boolean render(List<Player> list);

  void setCenter(Location location);

  double getDuration();

  void setDuration(double number);

  boolean isFixedLocation();

  Location getCenter();

  void setColor(Color color);

  void setColor(org.bukkit.Color color);

  void setColor(CMIChatColor cmiChatColor);

  void show();

  Player getPlayerMove();

  int getInterval();

  boolean isHideWithVanish();
}
