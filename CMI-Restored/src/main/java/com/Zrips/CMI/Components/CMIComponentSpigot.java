package com.Zrips.CMI.Components;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class CMIComponentSpigot extends CMIComponentBase {
  public CMIComponentSpigot(boolean flag) {
    super(flag);
  }

  @Override
  public void sendMessage(CommandSender commandSender) {
    RawMessage rawMessage = this.combine();
    rawMessage.show(commandSender);
  }

  private RawMessage combine() {
    var map = this.getComponents();
    List list = map.getOrDefault(0, new ArrayList());
    RawMessage rawMessage = new RawMessage();

    for (CMIComponentRoot cmiComponentRoot : ((Iterable<CMIComponentRoot>) list)) {
      if (cmiComponentRoot instanceof CMIComponentText) {
        CMIComponentText cmiComponentText = (CMIComponentText) cmiComponentRoot;
        rawMessage.addText(cmiComponentText.getText());
        rawMessage.addHover(cmiComponentText.getHoverText());
        switch (cmiComponentText.getAction().getClickActionType()) {
          case none:
          case copy_to_clipboard:
          default:
            break;
          case run_command:
            rawMessage.addCommand(cmiComponentText.getAction().getClickAction());
            break;
          case open_url:
            rawMessage.addUrl(cmiComponentText.getAction().getClickAction());
            break;
          case suggest_command:
            rawMessage.addInsertion(cmiComponentText.getAction().getClickAction());
        }
      } else if (cmiComponentRoot instanceof CMIComponentItem) {
        CMIComponentItem cmiComponentItem = (CMIComponentItem) cmiComponentRoot;
        rawMessage.addText(cmiComponentItem.getText());
        rawMessage.addItem(cmiComponentItem.getItem());
      }
    }

    return rawMessage;
  }
}
