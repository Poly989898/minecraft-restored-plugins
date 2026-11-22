package litebans;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;

public class LiteHelper10 {
   protected final ChatComponentDeserializer chatComponentDeserializer;

   protected void handleJsonObject(JsonObject jsonObject, ChatBaseComponent chatBaseComponent, JsonDeserializationContext jsonDeserializationContext) {
      chatBaseComponent.handleChatStyle((ChatStyle)jsonDeserializationContext.deserialize(jsonObject, ChatStyle.class));
      JsonElement jsonElement = jsonObject.get("insertion");
      if (jsonElement != null) {
         chatBaseComponent.setInsertion(jsonElement.getAsString());
      }

      JsonObject jsonObject2;
      boolean flag = (jsonObject2 = jsonObject.getAsJsonObject("click_event")) != null;
      if (!flag) {
         jsonObject2 = jsonObject.getAsJsonObject("clickEvent");
      }

      if (jsonObject2 != null) {
         ClickAction clickAction = ClickAction.computeClickAction(jsonObject2.get("action").getAsString().toUpperCase(Locale.ROOT));
         if (flag) {
            switch (GameVersionSwitchMap.value[clickAction.ordinal()]) {
               case 1:
                  chatBaseComponent.setClickEvent(new ChatClickEvent(clickAction, jsonObject2.get("url").getAsString()));
                  break;
               case 2:
               case 3:
                  chatBaseComponent.setClickEvent(new ChatClickEvent(clickAction, jsonObject2.get("command").getAsString()));
                  break;
               case 4:
                  int asInt = jsonObject2.get("page").getAsInt();
                  Preconditions.checkArgument(asInt >= 0, "Page number has to be positive");
                  chatBaseComponent.setClickEvent(new ChatClickEvent(clickAction, Integer.toString(asInt)));
                  break;
               default:
                  chatBaseComponent.setClickEvent(new ChatClickEvent(clickAction, jsonObject2.has("value") ? jsonObject2.get("value").getAsString() : ""));
            }
         } else {
            chatBaseComponent.setClickEvent(new ChatClickEvent(clickAction, jsonObject2.has("value") ? jsonObject2.get("value").getAsString() : ""));
         }
      }

      JsonObject jsonObject3;
      boolean flag2 = (jsonObject3 = jsonObject.getAsJsonObject("hover_event")) != null;
      if (!flag2) {
         jsonObject3 = jsonObject.getAsJsonObject("hoverEvent");
      }

      if (jsonObject3 != null) {
         HoverEvent hoverEvent = null;
         LiteShowTextEnum liteShowTextEnum = LiteShowTextEnum.computeLiteShowTextEnum(jsonObject3.get("action").getAsString().toUpperCase(Locale.ROOT));
         if (!flag2 && !jsonObject3.has("contents")) {
            JsonElement jsonElement2 = jsonObject3.get("value");
            if (jsonElement2 != null) {
               ChatBaseComponent[] chatBaseComponents;
               if (jsonElement2.isJsonArray()) {
                  chatBaseComponents = (ChatBaseComponent[])jsonDeserializationContext.deserialize(jsonElement2, ChatBaseComponent[].class);
               } else {
                  chatBaseComponents = new ChatBaseComponent[]{(ChatBaseComponent)jsonDeserializationContext.deserialize(jsonElement2, ChatBaseComponent.class)};
               }

               hoverEvent = new HoverEvent(liteShowTextEnum, chatBaseComponents);
            }
         } else {
            JsonElement jsonElement3 = jsonObject3.get(flag2 ? "value" : "contents");
            if (jsonElement3 != null) {
               LiteHelper4[] liteHelpers;
               if (jsonElement3.isJsonArray()) {
                  liteHelpers = (LiteHelper4[])jsonDeserializationContext.deserialize(jsonElement3, HoverEvent.computeClass(liteShowTextEnum, true));
               } else {
                  liteHelpers = new LiteHelper4[]{(LiteHelper4)jsonDeserializationContext.deserialize(jsonElement3, HoverEvent.computeClass(liteShowTextEnum, false))};
               }

               hoverEvent = new HoverEvent(liteShowTextEnum, new ArrayList<>(Arrays.asList(liteHelpers)));
            }
         }

         if (hoverEvent != null) {
            chatBaseComponent.setHoverEvent(hoverEvent);
         }
      }

      JsonElement jsonElement4 = jsonObject.get("extra");
      if (jsonElement4 != null) {
         chatBaseComponent.setExtra(Arrays.asList((ChatBaseComponent[])jsonDeserializationContext.deserialize(jsonElement4, ChatBaseComponent[].class)));
      }
   }

   protected void handleJsonObject(JsonObject jsonObject, ChatBaseComponent chatBaseComponent, JsonSerializationContext jsonSerializationContext) {
      boolean flag = false;
      if (ChatComponentDeserializer.THREAD_LOCAL.get() == null) {
         flag = true;
         ChatComponentDeserializer.THREAD_LOCAL.set(Collections.newSetFromMap(new IdentityHashMap()));
      }

      try {
         Preconditions.checkArgument(!((Set)ChatComponentDeserializer.THREAD_LOCAL.get()).contains(chatBaseComponent), "Component loop");
         ((Set)ChatComponentDeserializer.THREAD_LOCAL.get()).add(chatBaseComponent);
         ChatStyleSerializer.handleChatStyle(chatBaseComponent.getStyle(), jsonObject);
         if (chatBaseComponent.getInsertion() != null) {
            jsonObject.addProperty("insertion", chatBaseComponent.getInsertion());
         }

         if (chatBaseComponent.getClickEvent() != null) {
            JsonObject jsonObject2 = new JsonObject();
            String lowerCase = chatBaseComponent.getClickEvent().getClickAction().toString().toLowerCase(Locale.ROOT);
            jsonObject2.addProperty("action", lowerCase.toLowerCase(Locale.ROOT));
            switch (GameVersionSwitchMap.INTS_2[this.chatComponentDeserializer.getGameVersion().ordinal()]) {
               case 1:
                  ClickAction clickAction = ClickAction.computeClickAction(lowerCase.toUpperCase(Locale.ROOT));
                  switch (GameVersionSwitchMap.value[clickAction.ordinal()]) {
                     case 1:
                        jsonObject2.addProperty("url", chatBaseComponent.getClickEvent().getValue());
                        break;
                     case 2:
                     case 3:
                        jsonObject2.addProperty("command", chatBaseComponent.getClickEvent().getValue());
                        break;
                     case 4:
                        jsonObject2.addProperty("page", Integer.parseInt(chatBaseComponent.getClickEvent().getValue()));
                        break;
                     default:
                        jsonObject2.addProperty("value", chatBaseComponent.getClickEvent().getValue());
                  }

                  jsonObject.add("click_event", jsonObject2);
                  break;
               case 2:
                  jsonObject2.addProperty("value", chatBaseComponent.getClickEvent().getValue());
                  jsonObject.add("clickEvent", jsonObject2);
                  break;
               default:
                  throw new IllegalArgumentException("Unknown version " + this.chatComponentDeserializer.getGameVersion());
            }
         }

         if (chatBaseComponent.getHoverEvent() != null) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("action", chatBaseComponent.getHoverEvent().getLiteShowTextEnum().toString().toLowerCase(Locale.ROOT));
            if (chatBaseComponent.getHoverEvent().isFlag2()) {
               jsonObject3.add("value", jsonSerializationContext.serialize(chatBaseComponent.getHoverEvent().getList3().get(0)));
            } else {
               switch (GameVersionSwitchMap.INTS_2[this.chatComponentDeserializer.getGameVersion().ordinal()]) {
                  case 1:
                     jsonObject3.add("value", jsonSerializationContext.serialize(chatBaseComponent.getHoverEvent().getList3().size() == 1 ? chatBaseComponent.getHoverEvent().getList3().get(0) : chatBaseComponent.getHoverEvent().getList3()));
                     break;
                  case 2:
                     jsonObject3.add("contents", jsonSerializationContext.serialize(chatBaseComponent.getHoverEvent().getList3().size() == 1 ? chatBaseComponent.getHoverEvent().getList3().get(0) : chatBaseComponent.getHoverEvent().getList3()));
                     break;
                  default:
                     throw new IllegalArgumentException("Unknown version " + this.chatComponentDeserializer.getGameVersion());
               }
            }

            switch (GameVersionSwitchMap.INTS_2[this.chatComponentDeserializer.getGameVersion().ordinal()]) {
               case 1:
                  jsonObject.add("hover_event", jsonObject3);
                  break;
               case 2:
                  jsonObject.add("hoverEvent", jsonObject3);
                  break;
               default:
                  throw new IllegalArgumentException("Unknown version " + this.chatComponentDeserializer.getGameVersion());
            }
         }

         if (chatBaseComponent.getExtra() != null) {
            jsonObject.add("extra", jsonSerializationContext.serialize(chatBaseComponent.getExtra()));
         }
      } finally {
         ((Set)ChatComponentDeserializer.THREAD_LOCAL.get()).remove(chatBaseComponent);
         if (flag) {
            ChatComponentDeserializer.THREAD_LOCAL.set(null);
         }
      }
   }

   public LiteHelper10(ChatComponentDeserializer chatComponentDeserializer) {
      this.chatComponentDeserializer = chatComponentDeserializer;
   }
}
