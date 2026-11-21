package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 1)
public final class BungeeAltsCommands extends BungeeLiteCommandManager {
   public BungeeAltsCommands(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public Collection obtainCollection2() {
      Function1 callback = null;
      Collection collection = super.obtainCollection2();
      collection.removeIf(obj -> (Boolean)callback.computeObj(obj));
      return collection;
   }

   @Override
   public void handleLiteCommand(@NotNull LiteCommand liteCommand, @NotNull LiteSender liteSender, @NotNull String[] texts) {
      this.handleLiteSender(liteSender, liteCommand.getName(), texts);
   }

   @Override
   public void handleLiteSender(@NotNull LiteSender liteSender, @NotNull String text, @NotNull String[] texts) {
      String[] texts2 = texts;
      int value = 0;
      int length2 = texts2.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = true;
            break;
         }

         String text2 = texts2[value];
         if (StringsKt.isBlank(text2)) {
            flag = false;
            break;
         }

         value++;
      }

      String[] texts3;
      if (flag) {
         texts3 = texts;
      } else {
         String[] texts4 = texts;
         var arrayList = new ArrayList();
         int value2 = 0;

         for (int i = texts4.length; value2 < i; value2++) {
            String text3 = texts4[value2];
            if (!StringsKt.isBlank(text3)) {
               arrayList.add(text3);
            }
         }

         Collection collection = arrayList;
         texts3 = (String[]) collection.toArray(new String[0]);
      }

      String[] texts5 = texts3;
      String text4 = text;
      String substringAfter = StringsKt.substringAfterWithDefaults(text4, "litebans:", null, 2, null);
      AbstractCommand abstractCommand = (AbstractCommand)this.computeLiteCommand(substringAfter);
      if (abstractCommand != null) {
         String permission = abstractCommand.getPermission();
         if (permission != null) {
            LiteSender liteSender2 = liteSender;
            if (!liteSender2.hasPermission(permission)) {
               LiteSender liteSender3 = liteSender;
               CharSequence message = Message.PERMISSION_ERROR;
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender3, message, null, 4, null);
               return;
            }
         }

         this.platform.execute(new LiteRunnableImpl2(liteSender, texts5, abstractCommand, substringAfter));
      } else {
         String[] texts6 = AltAccountPunisher.computeTexts(AbstractCommand.MUTED_COMMAND_FILTER, substringAfter, texts5);
         AbstractCommand abstractCommand2 = (AbstractCommand)this.computeLiteCommand("ban");
         if (abstractCommand2 == null) {
            StandardKt.notImplemented();
            throw new CommandAbortException();
         }

         this.platform.execute(new LiteRunnableImpl2(liteSender, texts6, abstractCommand2, substringAfter));
      }
   }

   @Override
   public void load() {
      BungeeAltsCommandWrapper bungeeAltsCommandWrapper = null;
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      super.load();
      this.platform.setCommandManager(this);
      Iterable iterable = this.getCollection4();
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         LiteCommand liteCommand;
         ArrayList list;
         BungeeAltsCommandWrapper bungeeAltsCommandWrapper2;
         Platform platform;
         String text;
         String[] texts;
         label109: {
            liteCommand = (LiteCommand)obj;
            list = arrayList;
            platform = this.platform;
            text = liteCommand.getPermission();
            String name = liteCommand.getName();
            switch (name.hashCode()) {
               case -1320563219:
                  if (name.equals("dupeip")) {
                     String[] texts2 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                     texts = texts2;
                     break label109;
                  }
                  break;
               case 712910260:
                  if (name.equals("staffhistory")) {
                     String[] texts3 = new String[]{"staffhist", "litebans:staffhistory"};
                     texts = texts3;
                     break label109;
                  }
                  break;
               case 926934164:
                  if (name.equals("history")) {
                     String[] texts4 = new String[]{"hist", "litebans:history"};
                     texts = texts4;
                     break label109;
                  }
            }

            String[] texts5 = new String[]{"litebans:" + name};
            texts = texts5;
         }

         String[] texts6 = texts;
         bungeeAltsCommandWrapper2 = new BungeeAltsCommandWrapper(liteCommand, platform, text, Arrays.copyOf(texts6, texts6.length));
         list.add(bungeeAltsCommandWrapper2);
      }


      for (Object obj2 : arrayList) {
         LiteCommand liteCommand2 = (LiteCommand)obj2;
         this.handleLiteCommand3(liteCommand2);
      }

      Intrinsics.checkNotNull(module);
      String[] texts7 = AbstractCommand.MUTED_COMMAND_FILTER.computeTexts(module);
      int value = 0;

      for (int i = texts7.length; value < i; value++) {
         String text2;
         BungeeAltsCommands bungeeAltsCommands;
         StringBuilder builder;
         String text3;
         label90: {
            label89: {
               label88: {
                  String text4 = texts7[value];
                  text2 = text4;
                  bungeeAltsCommands = this;
                  builder = new StringBuilder().append("litebans.");
                  String text5 = text2;
                  switch (text5.hashCode()) {
                     case -1396405339:
                        if (text5.equals("ban-ip")) {
                           break label88;
                        }
                        break;
                     case -1062781696:
                        if (text5.equals("muteip")) {
                           break label89;
                        }
                        break;
                     case -840405966:
                        if (text5.equals("unmute")) {
                           text3 = "unmute.own";
                           break label90;
                        }
                        break;
                     case -840127329:
                        if (text5.equals("unwarn")) {
                           text3 = "unwarn.own";
                           break label90;
                        }
                        break;
                     case 93503862:
                        if (text5.equals("banip")) {
                           break label88;
                        }
                        break;
                     case 111426262:
                        if (text5.equals("unban")) {
                           text3 = "unban.own";
                           break label90;
                        }
                        break;
                     case 1231784084:
                        if (text5.equals("tempipmute")) {
                           break label89;
                        }
                        break;
                     case 1351111124:
                        if (text5.equals("tempmuteip")) {
                           break label89;
                        }
                        break;
                     case 1972486690:
                        if (text5.equals("tempbanip")) {
                           break label88;
                        }
                        break;
                     case 1979386420:
                        if (text5.equals("tempipban")) {
                           break label88;
                        }
                  }

                  text3 = text2;
                  break label90;
               }

               text3 = "ipban";
               break label90;
            }

            text3 = "ipmute";
         }

         LiteCommand liteCommand3;
         Platform platform2;
         String text6;
         String[] texts8;
         label71: {
            String string = builder.append(text3).toString();
            BungeeAltsCommands bungeeAltsCommands2 = bungeeAltsCommands;
            liteCommand3 = new BungeeAltsCommand(text2, string, bungeeAltsCommands);
            platform2 = bungeeAltsCommands2.platform;
            text6 = liteCommand3.getPermission();
            String name2 = liteCommand3.getName();
            switch (name2.hashCode()) {
               case -1320563219:
                  if (name2.equals("dupeip")) {
                     String[] texts9 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                     texts8 = texts9;
                     break label71;
                  }
                  break;
               case 712910260:
                  if (name2.equals("staffhistory")) {
                     String[] texts10 = new String[]{"staffhist", "litebans:staffhistory"};
                     texts8 = texts10;
                     break label71;
                  }
                  break;
               case 926934164:
                  if (name2.equals("history")) {
                     String[] texts11 = new String[]{"hist", "litebans:history"};
                     texts8 = texts11;
                     break label71;
                  }
            }

            String[] texts12 = new String[]{"litebans:" + name2};
            texts8 = texts12;
         }

         String[] texts13 = texts8;
         bungeeAltsCommandWrapper = new BungeeAltsCommandWrapper(liteCommand3, platform2, text6, Arrays.copyOf(texts13, texts13.length));
         bungeeAltsCommands.handleLiteCommand3(bungeeAltsCommandWrapper);
      }
   }

   @Override
   public void enable() {
   }

   @Override
   public void reloadConfig() {
      Iterable iterable = this.getCollection4();
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         LiteCommand liteCommand;
         ArrayList list;
         BungeeAltsCommandWrapper bungeeAltsCommandWrapper;
         Platform platform;
         String text;
         String[] texts;
         label36: {
            liteCommand = (LiteCommand)obj;
            list = arrayList;
            platform = this.platform;
            text = liteCommand.getPermission();
            String name = liteCommand.getName();
            switch (name.hashCode()) {
               case -1320563219:
                  if (name.equals("dupeip")) {
                     String[] texts2 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                     texts = texts2;
                     break label36;
                  }
                  break;
               case 712910260:
                  if (name.equals("staffhistory")) {
                     String[] texts3 = new String[]{"staffhist", "litebans:staffhistory"};
                     texts = texts3;
                     break label36;
                  }
                  break;
               case 926934164:
                  if (name.equals("history")) {
                     String[] texts4 = new String[]{"hist", "litebans:history"};
                     texts = texts4;
                     break label36;
                  }
            }

            String[] texts5 = new String[]{"litebans:" + name};
            texts = texts5;
         }

         String[] texts6 = texts;
         bungeeAltsCommandWrapper = new BungeeAltsCommandWrapper(liteCommand, platform, text, Arrays.copyOf(texts6, texts6.length));
         list.add(bungeeAltsCommandWrapper);
      }


      for (Object obj2 : arrayList) {
         LiteCommand liteCommand2 = (LiteCommand)obj2;
         this.handleLiteCommand3(liteCommand2);
      }
   }

   @Override
   public void handleLiteCommand3(@Nullable LiteCommand liteCommand) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      if (liteCommand instanceof BungeeAltsCommandWrapper && module.isCommandEnabled(((BungeeAltsCommandWrapper)liteCommand).getName())) {
         super.handleLiteCommand3(liteCommand);
      }
   }

   @Override
   public List computeList(@Nullable LiteCommand liteCommand, @NotNull String text, @NotNull LiteSender liteSender, @NotNull String[] texts) {
      if (texts.length != 0) {
         LiteSender liteSender2 = liteSender;
         String text2 = "litebans.tabcomplete";
         if (liteSender2.hasPermission(text2)) {
            return AbstractCommand.MUTED_COMMAND_FILTER.computeList(liteCommand, text, liteSender, this.platform, texts);
         }
      }

      return CollectionsKt.emptyList();
   }

   private static final boolean checkLiteCommand(LiteCommand liteCommand) {
      return Intrinsics.areEqual(liteCommand.getName(), "togglechat");
   }

}
