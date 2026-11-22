package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.util.HashMap;

public class CMIMessageReplies {
  private static HashMap<String, CMIReplyResponder> replyMapBySender = new HashMap<>();
  private static HashMap<String, CMIReplyResponder> replyMapByReceiver = new HashMap<>();

  public static String getMessageReplyTo(String string) {
    if (CMI.getInstance().getChatManager().isChatReplyToLastMessenger()) {
      CMIReplyResponder cmiReplyResponder = replyMapByReceiver.get(string);
      if (cmiReplyResponder == null) {
        cmiReplyResponder = replyMapBySender.get(string);
      }

      return cmiReplyResponder == null ? null : cmiReplyResponder.getName();
    } else {
      CMIReplyResponder responder = replyMapBySender.get(string);
      if (responder == null
          || responder.getTime()
                  + CMI.getInstance().getChatManager().getLastMessengerTimeOut() * 1000L
              < System.currentTimeMillis()) {
        responder = replyMapByReceiver.get(string);
        if (responder == null) {
          responder = replyMapBySender.get(string);
        }
      }

      return responder == null ? null : responder.getName();
    }
  }

  public static void removeMessageReplyTo(String string) {
    CMIReplyResponder cmiReplyResponder = replyMapByReceiver.remove(string);
    if (cmiReplyResponder != null) {
      CMIReplyResponder responder = replyMapBySender.get(cmiReplyResponder.getName());
      if (responder != null && responder.getName().equalsIgnoreCase(string)) {
        replyMapBySender.remove(cmiReplyResponder.getName());
      }
    }
  }

  public static void addMessageReplyTo(String string, String otherName) {
    replyMapBySender.put(otherName, new CMIReplyResponder(string, System.currentTimeMillis()));
    replyMapByReceiver.remove(otherName);
    replyMapByReceiver.put(string, new CMIReplyResponder(otherName, System.currentTimeMillis()));
    if (CMI.getInstance().getBungeeCordManager().isBungeeCord()) {
      CMI.getInstance().getBungeeCordManager().talksWith(otherName, string);
    }
  }

  public static void addMessageReplyToBySender(String string, String otherName) {
    replyMapBySender.put(otherName, new CMIReplyResponder(string, System.currentTimeMillis()));
  }
}
