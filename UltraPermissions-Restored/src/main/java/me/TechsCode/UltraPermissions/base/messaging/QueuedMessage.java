package me.TechsCode.UltraPermissions.base.messaging;

import com.google.gson.JsonObject;

public abstract class QueuedMessage extends Message {
   public QueuedMessage(String text, JsonObject jsonObject) {
      super(text, jsonObject);
   }

   public abstract void onSend();
}
