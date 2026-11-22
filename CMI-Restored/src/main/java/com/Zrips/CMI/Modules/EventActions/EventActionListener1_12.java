package com.Zrips.CMI.Modules.EventActions;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentDeserializer;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class EventActionListener1_12 implements Listener {
  private CMI plugin;
  static boolean existingMethod = true;

  public EventActionListener1_12(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void PlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent playerAdvancementDoneEvent) {
    if (EventActionManager.eventAction.advancementDone.isEnabled()) {
      Player player = playerAdvancementDoneEvent.getPlayer();
      String string = playerAdvancementDoneEvent.getAdvancement().getKey().getKey();
      if (existingMethod
          && playerAdvancementDoneEvent.getAdvancement().getDisplay() != null
          && Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
        if (Version.isPaperBranch()) {
          string =
              CMIComponentDeserializer.serialize(
                  playerAdvancementDoneEvent.getAdvancement().getDisplay().title());
        } else {
          try {
            string =
                (String)
                    playerAdvancementDoneEvent
                        .getAdvancement()
                        .getDisplay()
                        .getClass()
                        .getMethod("getTitle")
                        .invoke(playerAdvancementDoneEvent.getAdvancement().getDisplay());
          } catch (Throwable throwable) {
            existingMethod = false;
          }
        }
      }

      this.plugin
          .getEventActionManager()
          .performCommands(EventActionManager.eventAction.advancementDone, player, string);
    }
  }
}
