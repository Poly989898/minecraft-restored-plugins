package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 2)
public final class VelocityAltsCommands extends VelocityLiteCommandManager {
   public VelocityAltsCommands(@NotNull Platform platform) {
      super(platform);
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
      VelocityAltsCommandWrapper velocityAltsCommandWrapper = null;
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      super.load();
      this.platform.setCommandManager(this);
      Iterable iterable = this.getCollection4();
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         LiteCommand liteCommand;
         ArrayList list;
         VelocityAltsCommandWrapper velocityAltsCommandWrapper2;
         Platform platform;
         String[] texts;
         label109: {
            liteCommand = (LiteCommand)obj;
            list = arrayList;
            platform = this.platform;
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
         velocityAltsCommandWrapper2 = new VelocityAltsCommandWrapper(liteCommand, platform, null, Arrays.copyOf(texts6, texts6.length), 4, null);
         list.add(velocityAltsCommandWrapper2);
      }


      for (Object obj2 : arrayList) {
         LiteCommand liteCommand2 = (LiteCommand)obj2;
         this.handleLiteCommand(liteCommand2);
      }

      Intrinsics.checkNotNull(module);
      String[] texts7 = AbstractCommand.MUTED_COMMAND_FILTER.computeTexts(module);
      int value = 0;

      for (int i = texts7.length; value < i; value++) {
         String text;
         VelocityAltsCommands velocityAltsCommands;
         StringBuilder builder;
         String text2;
         label90: {
            label89: {
               label88: {
                  String text3 = texts7[value];
                  text = text3;
                  velocityAltsCommands = this;
                  builder = new StringBuilder().append("litebans.");
                  String text4 = text;
                  switch (text4.hashCode()) {
                     case -1396405339:
                        if (text4.equals("ban-ip")) {
                           break label88;
                        }
                        break;
                     case -1062781696:
                        if (text4.equals("muteip")) {
                           break label89;
                        }
                        break;
                     case -840405966:
                        if (text4.equals("unmute")) {
                           text2 = "unmute.own";
                           break label90;
                        }
                        break;
                     case -840127329:
                        if (text4.equals("unwarn")) {
                           text2 = "unwarn.own";
                           break label90;
                        }
                        break;
                     case 93503862:
                        if (text4.equals("banip")) {
                           break label88;
                        }
                        break;
                     case 111426262:
                        if (text4.equals("unban")) {
                           text2 = "unban.own";
                           break label90;
                        }
                        break;
                     case 1231784084:
                        if (text4.equals("tempipmute")) {
                           break label89;
                        }
                        break;
                     case 1351111124:
                        if (text4.equals("tempmuteip")) {
                           break label89;
                        }
                        break;
                     case 1972486690:
                        if (text4.equals("tempbanip")) {
                           break label88;
                        }
                        break;
                     case 1979386420:
                        if (text4.equals("tempipban")) {
                           break label88;
                        }
                  }

                  text2 = text;
                  break label90;
               }

               text2 = "ipban";
               break label90;
            }

            text2 = "ipmute";
         }

         LiteCommand liteCommand3;
         Platform platform2;
         String[] texts8;
         label71: {
            String string = builder.append(text2).toString();
            VelocityAltsCommands velocityAltsCommands2 = velocityAltsCommands;
            liteCommand3 = new VelocityAltsCommand(text, string, velocityAltsCommands);
            platform2 = velocityAltsCommands2.platform;
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
         velocityAltsCommandWrapper = new VelocityAltsCommandWrapper(liteCommand3, platform2, null, Arrays.copyOf(texts13, texts13.length), 4, null);
         velocityAltsCommands.handleLiteCommand(velocityAltsCommandWrapper);
      }
   }

   @Override
   public void handleLiteCommand(@Nullable LiteCommand liteCommand) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      if (liteCommand instanceof VelocityAltsCommandWrapper && module.isCommandEnabled(((VelocityAltsCommandWrapper)liteCommand).getName())) {
         super.handleLiteCommand(liteCommand);
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
}
