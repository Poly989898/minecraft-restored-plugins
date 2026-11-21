package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonMessageModule extends PluginModule {
   public static final KotlinData9 KOTLIN_DATA = new KotlinData9(null);
   private static boolean return2 = true;
   public static final boolean FLAG = false;

   public JsonMessageModule(@NotNull Platform platform) {
      super(platform);
   }

   public final String processMessage(@NotNull LiteSender liteSender, @NotNull String text) {
      JsonMessageTemplate jsonMessageTemplate = null;
      JsonMessageEntry entry = this.findEntry(text, liteSender);
      JsonMessageTemplate[] jsonMessageTemplates = entry.getJsonMessageTemplates();
      int value = 0;
      int length2 = jsonMessageTemplates.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = false;
            break;
         }

         JsonMessageTemplate jsonMessageTemplate2 = jsonMessageTemplates[value];
         if (jsonMessageTemplate2.isFlag3()) {
            flag = true;
            break;
         }

         value++;
      }

      if (flag && entry.getJSONObject() != null) {
         if (liteSender.isConsole()) {
            JsonMessageTemplate[] jsonMessageTemplates2 = entry.getJsonMessageTemplates();
            int value2 = 0;
            int length3 = jsonMessageTemplates2.length;

            while (true) {
               if (value2 >= length3) {
                  jsonMessageTemplate = null;
                  break;
               }

               JsonMessageTemplate jsonMessageTemplate3 = jsonMessageTemplates2[value2];
               if (jsonMessageTemplate3.isFlag3() && Intrinsics.areEqual(jsonMessageTemplate3.getText6(), this.getClickEventKey(liteSender))) {
                  jsonMessageTemplate = jsonMessageTemplate3;
                  break;
               }

               value2++;
            }

            JsonMessageTemplate jsonMessageTemplate4 = jsonMessageTemplate;
            if (jsonMessageTemplate4 != null) {
               liteSender.sendMessage(entry.getText2());
               liteSender.sendMessage("=> " + jsonMessageTemplate4.getText8());
               return null;
            }
         }

         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         if (module.isVerbose()) {
            module.logDebug(entry.getJSONObject());
         }

         label101: {
            if (liteSender.isPlayer()) {
               JsonMessageTemplate[] jsonMessageTemplates3 = entry.getJsonMessageTemplates();
               int value3 = 0;
               int length4 = jsonMessageTemplates3.length;

               while (true) {
                  if (value3 >= length4) {
                     throw new NoSuchElementException("Array contains no element matching the predicate.");
                  }

                  JsonMessageTemplate jsonMessageTemplate5 = jsonMessageTemplates3[value3];
                  if (Intrinsics.areEqual(jsonMessageTemplate5.getText6(), this.getHoverEventKey(liteSender))) {
                     if (!jsonMessageTemplate5.isFlag3()) {
                        break label101;
                     }

                     LiteSender liteSender2 = liteSender;
                     String text2 = "litebans.json.hover_text";
                     if (liteSender2.hasPermission(text2)) {
                        break label101;
                     }
                     break;
                  }

                  value3++;
               }
            }

            return entry.getText2();
         }

         liteSender.sendRichMessage(entry.getJSONObject().toString());
         return null;
      } else {
         return KOTLIN_DATA.computeText(text);
      }
   }

   public final JsonMessageTemplate buildTemplate(@NotNull String text, @NotNull String text2, @NotNull String text3, @Nullable JsonMessageTemplate jsonMessageTemplate) {
      String text4 = '{' + text2 + ':';
      if (jsonMessageTemplate != null
         && !StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)text4, false, 2, null)
         && StringsKt.containsWithDefaults(text, '}', false, 2, null)
         && jsonMessageTemplate.isFlag4()
         && Intrinsics.areEqual(jsonMessageTemplate.getText5(), text2)) {
         String text5 = text4 + ' ' + text;
         return this.buildTemplate(text5, text2, text3, null);
      }

      if (!StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)text4, false, 2, null)) {
         return new JsonMessageTemplate(text2, text3, text, null, false, false, 56, null);
      }

      String text6 = text;
      char[] chars = new char[]{' '};
      List split = StringsKt.splitWithDefaults(text6, chars, false, 0, 6, null);
      List mutableList = CollectionsKt.toMutableList(split);
      int value = 0;
      int value2 = 1;
      boolean flag = false;
      boolean flag2 = false;
      boolean flag3 = false;
      ArrayList arrayList = new ArrayList(mutableList.size());
      Iterable iterable = split;
      int value3 = 0;

      for (Object obj : iterable) {
         int value4 = value3++;
         if (value4 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String string = (String)obj;
         if (!flag && StringsKt.checkText(string, text4, false, 2, null)) {
            flag = true;
            flag2 = true;
            value = value4;
         } else if (flag2) {
            value2++;
            if (StringsKt.containsWithDefaults(string, '}', false, 2, null) && !StringsKt.containsWithDefaults(string, '{', false, 2, null)) {
               String text7 = StringsKt.substringBeforeLastWithDefaults(string, '}', null, 2, null);
               if (text7.length() > 0) {
                  arrayList.add(text7);
               }

               String substringAfterLast = StringsKt.substringAfterLastWithDefaults(string, '}', null, 2, null);
               if (substringAfterLast.length() > 0) {
                  int value5 = value4 + 1;
                  if (mutableList.size() > value5) {
                     mutableList.set(value5, substringAfterLast + (String)mutableList.get(value5));
                  } else {
                     mutableList.add(substringAfterLast);
                  }
               }

               flag2 = false;
               flag3 = true;
               break;
            }

            arrayList.add(string);
         }
      }

      int value6 = Math.min(mutableList.size() - 1, value);
      Iterable iterable2 = KotlinRangesKtSub.until(0, value2);
      Iterator iterator2 = iterable2.iterator();

      while (iterator2.hasNext()) {
         int value7 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
         mutableList.remove(value6);
      }

      text6 = text2;
      String text8 = text3;
      String joinToString = CollectionsKt.joinToStringWithDefaults(mutableList, " ", null, null, 0, null, null, 62, null);
      List list;
      if (Intrinsics.areEqual(text2, "url")) {
         Iterable iterable3 = arrayList;
         String text9 = joinToString;
         String text10 = text3;
         String text11 = text2;
         var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable3, 10));

         for (Object obj2 : iterable3) {
            String string2 = (String)obj2;
            arrayList2.add(StringsKt.substringBeforeWithDefaults(string2, '§', null, 2, null));
         }

         List list2 = arrayList2;
         text6 = text11;
         text8 = text10;
         joinToString = text9;
         list = list2;
      } else {
         list = arrayList;
      }

      String joinToString2 = CollectionsKt.joinToStringWithDefaults(list, " ", null, null, 0, null, null, 62, null);
      boolean flag4 = flag2;
      boolean flag5 = flag3;
      String text12 = joinToString;
      String text13 = text8;
      String text14 = text6;
      return new JsonMessageTemplate(text14, text13, text12, joinToString2, flag5, flag4);
   }

   public static JsonMessageTemplate buildTemplateWithDefaults(JsonMessageModule jsonMessageModule, String text, String text2, String text3, JsonMessageTemplate jsonMessageTemplate, int value, Object obj) {
      if ((value & 8) != 0) {
         jsonMessageTemplate = null;
      }

      return jsonMessageModule.buildTemplate(text, text2, text3, jsonMessageTemplate);
   }

   public final JsonMessageEntry findEntry(@NotNull String text, @NotNull LiteSender liteSender) {
      String text2 = KOTLIN_DATA.computeText(text);
      String hoverEventKey = this.getHoverEventKey(liteSender);
      String clickEventKey = this.getClickEventKey(liteSender);
      JsonMessageTemplate template = buildTemplateWithDefaults(this, text2, "hoverText", hoverEventKey, null, 8, null);
      JsonMessageTemplate template2 = buildTemplateWithDefaults(this, template.getText7(), "run", clickEventKey, null, 8, null);
      JsonMessageTemplate template3 = buildTemplateWithDefaults(this, template2.getText7(), "url", clickEventKey, null, 8, null);
      JsonMessageTemplate template4 = buildTemplateWithDefaults(this, template3.getText7(), "suggest", clickEventKey, null, 8, null);
      JsonMessageTemplate template5 = buildTemplateWithDefaults(this, template4.getText7(), "copy", clickEventKey, null, 8, null);
      if (!template.isFlag3() && !template2.isFlag3() && !template3.isFlag3() && !template4.isFlag3() && !template5.isFlag3()) {
         return new JsonMessageEntry(text2, null, new JsonMessageTemplate[0]);
      }

      JSONObject jsonObject = null;
      JSONObject jsonObject2 = null;
      int value = 0;
      if (template.isFlag3()) {
         JSONArray jsonArray = new JSONArray();
         String text3 = template.getText8();
         JSONObject component = buildComponentWithDefaults(this, text3, liteSender, null, null, null, 28, null);
         jsonArray.add(component);
         jsonObject2 = new JSONObject();
         jsonObject2.put("action", "show_text");
         jsonObject2.put("value", jsonArray);
         value++;
      }

      if (template2.isFlag3()) {
         jsonObject = new JSONObject();
         jsonObject.put("action", "run_command");
         if (this.usesSnakeCaseKeys(liteSender)) {
            jsonObject.put("command", template2.getText8());
         } else {
            jsonObject.put("value", template2.getText8());
         }

         value++;
      }

      if (template4.isFlag3()) {
         jsonObject = new JSONObject();
         jsonObject.put("action", "suggest_command");
         if (this.usesSnakeCaseKeys(liteSender)) {
            jsonObject.put("command", template4.getText8());
         } else {
            jsonObject.put("value", template4.getText8());
         }

         value++;
      }

      if (template5.isFlag3()) {
         jsonObject = new JSONObject();
         jsonObject.put("action", "copy_to_clipboard");
         jsonObject.put("value", template4.getText8());
         value++;
      }

      if (template3.isFlag3() && StringsKt.checkText(template3.getText8(), "https:", false, 2, null)) {
         jsonObject = new JSONObject();
         jsonObject.put("action", "open_url");
         if (this.usesSnakeCaseKeys(liteSender)) {
            jsonObject.put("url", template3.getText8());
         } else {
            jsonObject.put("value", template3.getText8());
         }

         value++;
      }

      JSONObject jsonObject3 = null;
      String text4 = text2;
      Pair[] pairs = new Pair[]{TuplesKt.computePair(template, jsonObject2), TuplesKt.computePair(template2, jsonObject), TuplesKt.computePair(template3, jsonObject), TuplesKt.computePair(template4, jsonObject), TuplesKt.computePair(template5, jsonObject)};
      int value2 = 0;

      for (int i = pairs.length; value2 < i; value2++) {
         Pair pair = pairs[value2];
         JsonMessageTemplate jsonMessageTemplate = (JsonMessageTemplate)pair.getFirst();
         JSONObject jsonObject4 = (JSONObject)pair.getSecond();
         if (jsonMessageTemplate.isFlag3()) {
            jsonObject3 = this.buildComponent(jsonMessageTemplate.getText7(), liteSender, jsonObject4, jsonMessageTemplate.getText6(), jsonObject3);
            text4 = jsonMessageTemplate.getText7();
         }
      }

      if (value > 1 && jsonObject3 != null) {
         jsonObject3 = this.applyTemplates(jsonObject3, liteSender);
      }

      JsonMessageTemplate[] jsonMessageTemplates = new JsonMessageTemplate[]{template, template2, template3, template4, template5};
      return new JsonMessageEntry(text4, jsonObject3, jsonMessageTemplates);
   }

   public final String getClickEventKey(@NotNull LiteSender liteSender) {
      return this.usesSnakeCaseKeys(liteSender) ? "click_event" : "clickEvent";
   }

   public final String getHoverEventKey(@NotNull LiteSender liteSender) {
      return this.usesSnakeCaseKeys(liteSender) ? "hover_event" : "hoverEvent";
   }

   public final JSONObject buildComponent(@NotNull String text, @NotNull LiteSender liteSender, @Nullable JSONObject jsonObject, @NotNull String text2, @Nullable JSONObject jsonObject2) {
      JSONObject jsonObject3 = jsonObject2;
      Object obj = null;

      try {
         boolean flag = jsonObject2 == null;
         if (flag) {
            GameVersion gameVersion = GameVersion.V1_21_5;
            int value;
            if (this.platform.getPlatformId() == 1) {
               Intrinsics.checkNotNullExpressionValue(liteSender, "");
               value = ((BungeeConsoleSender)liteSender).obtainIntValue2();
            } else if (this.platform.getPlatformId() == 2) {
               Intrinsics.checkNotNullExpressionValue(liteSender, "");
               value = ((VelocityConsoleSender)liteSender).obtainIntValue();
            } else {
               value = !this.usesSnakeCaseKeys(liteSender) ? 0 : 770;
            }

            int value2 = value;
            if (value2 < 770) {
               gameVersion = GameVersion.V1_16;
            }

            ChatComponentDeserializer chatComponentDeserializer = ChatComponentDeserializer.computeChatComponentDeserializer(gameVersion);
            ChatBaseComponent[] chatBaseComponents = LiteHelper7.computeChatBaseComponentArray(text);
            String text3 = chatComponentDeserializer.computeText(Arrays.copyOf(chatBaseComponents, chatBaseComponents.length));
            Object obj2 = new JSONParser().computeObj(text3);
            Intrinsics.checkNotNullExpressionValue(obj2, "");
            jsonObject3 = (JSONObject)obj2;
            if (!this.checkJsonObject(jsonObject, jsonObject3, text2)) {
               Object obj3 = jsonObject3.get("contents");
               if (Intrinsics.areEqual(obj3, "null")) {
                  JSONObject jsonObject4 = jsonObject3;
                  ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
                  CommandArguments commandArguments;
                  Intrinsics.checkNotNull(module);
                  String string = (String)jsonObject4.get("contents");
                  Intrinsics.checkNotNullExpressionValue(string, "");
                  String[] texts;
                  if (Intrinsics.areEqual(string, "sort")) {
                     String[] texts2 = new String[]{"00"};
                     texts = texts2;
                  } else {
                     String[] texts3 = new String[]{"01:" + string};
                     texts = texts3;
                  }

                  String[] texts4 = texts;
                  commandArguments = new CommandArguments(module, Arrays.copyOf(texts4, texts4.length));
                  if (commandArguments.obtainIntValue() == module.hashCode()) {
                     throw new IllegalArgumentException(jsonObject4.toString());
                  }
               } else if (Intrinsics.areEqual(obj3, "true") || Intrinsics.areEqual(obj3, "false")) {
                  this.applyTemplates(jsonObject3, liteSender);
               }
            }
         } else {
            JSONObject jsonObject5 = jsonObject3;
            if (jsonObject3 == null) {
               jsonObject5 = new JSONObject();
            }

            jsonObject3 = jsonObject5;
            if (flag) {
               jsonObject3.put("text", text);
            }

            if (jsonObject != null) {
               jsonObject3.put(text2, jsonObject);
            }
         }

         return jsonObject3;
      } catch (Exception e) {
         throw new RuntimeException("Failed to build JSON: \"" + jsonObject3 + "\"; last result: " + obj, e);
      }
   }

   public static JSONObject buildComponentWithDefaults(JsonMessageModule jsonMessageModule, String text, LiteSender liteSender, JSONObject jsonObject, String text2, JSONObject jsonObject2, int value, Object obj) {
      if ((value & 4) != 0) {
         jsonObject = null;
      }

      if ((value & 8) != 0) {
         text2 = "";
      }

      if ((value & 16) != 0) {
         jsonObject2 = null;
      }

      return jsonMessageModule.buildComponent(text, liteSender, jsonObject, text2, jsonObject2);
   }

   private final boolean checkJsonObject(JSONObject jsonObject, JSONObject jsonObject2, String text) {
      if (jsonObject == null) {
         return false;
      }

      JSONArray jsonArray = (JSONArray)jsonObject2.get("extra");
      if (jsonArray != null) {
         Iterable iterable = jsonArray;
         var arrayList = new ArrayList();

         for (Object obj : iterable) {
            if (obj instanceof JSONObject) {
               arrayList.add(obj);
            }
         }


         for (Object obj2 : arrayList) {
            JSONObject jsonObject3 = (JSONObject)obj2;
            jsonObject3.put(text, jsonObject);
         }
      }

      jsonObject2.put(text, jsonObject);
      return true;
   }

   public final JSONObject applyTemplates(@NotNull JSONObject jsonObject, @NotNull LiteSender liteSender) {
      if (!Intrinsics.areEqual(jsonObject.get("text"), "")) {
         Map map = jsonObject;
         String text = "text";
         JsonMessageTemplate template2 = parseTemplateWithDefaults(this, (String)jsonObject.get("text"), null, 2, null);
         String text2 = template2 != null ? template2.getText7() : null;
         map.put(text, text2);
      }

      JSONArray jsonArray = (JSONArray)jsonObject.get("extra");
      JsonMessageTemplate jsonMessageTemplate = null;
      if (jsonArray != null) {
         Iterable iterable = jsonArray;
         var arrayList = new ArrayList();

         for (Object obj : iterable) {
            if (obj instanceof JSONObject) {
               arrayList.add(obj);
            }
         }


         for (Object obj2 : arrayList) {
            JSONObject jsonObject2 = (JSONObject)obj2;
            JsonMessageTemplate template = this.parseTemplate((String)jsonObject2.get("text"), jsonMessageTemplate);
            jsonMessageTemplate = template;
            jsonObject2.put("text", template != null ? template.getText7() : null);
            String text3 = "click_event";
            String text4 = "clickEvent";
            if (!this.usesSnakeCaseKeys(liteSender)) {
               String text5 = text3;
               text3 = text4;
               text4 = text5;
            }

            if (jsonObject2.containsKey(text3) && jsonObject2.containsKey(text4)) {
               jsonObject2.remove(text4);
            }

            String text6 = "hover_event";
            String text7 = "hoverEvent";
            if (!this.usesSnakeCaseKeys(liteSender)) {
               String text8 = text6;
               text6 = text7;
               text7 = text8;
            }

            if (jsonObject2.containsKey(text6) && jsonObject2.containsKey(text7)) {
               jsonObject2.remove(text7);
            }
         }
      }

      return jsonObject;
   }

   private final JsonMessageTemplate parseTemplate(String text, JsonMessageTemplate jsonMessageTemplate) {
      if (text == null) {
         return null;
      } else {
         JsonMessageTemplate template = this.buildTemplate(text, "hoverText", "", jsonMessageTemplate);
         JsonMessageTemplate template2 = this.buildTemplate(template.getText7(), "run", "", jsonMessageTemplate);
         JsonMessageTemplate template3 = this.buildTemplate(template2.getText7(), "url", "", jsonMessageTemplate);
         JsonMessageTemplate template4 = this.buildTemplate(template3.getText7(), "suggest", "", jsonMessageTemplate);
         JsonMessageTemplate template5 = this.buildTemplate(template4.getText7(), "copy", "", jsonMessageTemplate);
         if (template5.isFlag3() || template5.isFlag4()) {
            return template5;
         } else if (template4.isFlag3() || template4.isFlag4()) {
            return template4;
         } else if (template3.isFlag3() || template3.isFlag4()) {
            return template3;
         } else {
            return !template2.isFlag3() && !template2.isFlag4() ? template : template2;
         }
      }
   }

   static JsonMessageTemplate parseTemplateWithDefaults(JsonMessageModule jsonMessageModule, String text, JsonMessageTemplate jsonMessageTemplate, int value, Object obj) {
      if ((value & 2) != 0) {
         jsonMessageTemplate = null;
      }

      return jsonMessageModule.parseTemplate(text, jsonMessageTemplate);
   }

   @Override
   public void enable() {
   }

   public final boolean usesSnakeCaseKeys(@NotNull LiteSender liteSender) {
      if (this.platform.getPlatformId() == 4) {
         return return2;
      } else {
         LiteData liteData = LiteHelper5.computeLiteData(LiteData.LITE_HELPER, this.platform.createBridge().getServerVersion(), null, 2, null);
         if (this.platform.getPlatformId() == 1) {
            Intrinsics.checkNotNullExpressionValue(liteSender, "");
            return ((BungeeConsoleSender)liteSender).obtainIntValue2() >= 770;
         } else if (this.platform.getPlatformId() == 2) {
            Intrinsics.checkNotNullExpressionValue(liteSender, "");
            return ((VelocityConsoleSender)liteSender).obtainIntValue() >= 770;
         } else {
            return liteData.checkIntValue(1, 21, 5);
         }
      }
   }
}
