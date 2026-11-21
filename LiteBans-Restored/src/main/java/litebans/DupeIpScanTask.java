package litebans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DupeIpScanTask extends CommandTask {
   private final String target;
   private final DatabaseSession databaseSession;
   private final LiteSender sender;
   private final boolean quiet;
   private int intValue;
   private boolean failed;
   private boolean bannedFound;
   private boolean mutedFound;
   private boolean multipleAddresses;
   private int addressCount;
   private int resultCount;
   private final ArrayList results;
   private LoginRecord loginRecord;
   private String ip;
   private String resultText;

   public DupeIpScanTask(@NotNull String text, @NotNull DatabaseSession databaseSession, @Nullable LiteSender liteSender, boolean flag, int value) {
      super(databaseSession.getPlatform());
      this.target = text;
      this.databaseSession = databaseSession;
      this.sender = liteSender;
      this.quiet = flag;
      this.intValue = value;
      this.results = new ArrayList();
      this.resultText = "";
   }

   public DupeIpScanTask(String text, DatabaseSession databaseSession, LiteSender liteSender, boolean flag, int value, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 4) != 0) {
         liteSender = null;
      }

      if ((value2 & 8) != 0) {
         flag = true;
      }

      if ((value2 & 16) != 0) {
         value = 0;
      }

      this(text, databaseSession, liteSender, flag, value);
   }

   public final boolean isFailed() {
      return this.failed;
   }

   public final boolean isBannedFound() {
      return this.bannedFound;
   }

   public final boolean isMutedFound() {
      return this.mutedFound;
   }

   public final boolean hasMultipleAddresses() {
      return this.multipleAddresses;
   }

   public final int getAddressCount() {
      return this.addressCount;
   }

   public final int getResultCount() {
      return this.resultCount;
   }

   public final ArrayList getResults() {
      return this.results;
   }

   public final LoginRecord getLoginRecord() {
      return this.loginRecord;
   }

   public final String getText43() {
      return this.ip;
   }

   public final String getResultText() {
      return this.resultText;
   }

   public final DupeIpScanTask obtainPunishmentApplyTask() {
      try {
         if (this.intValue == 0) {
            CommandTask commandTask = this;
            this.intValue = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getNotifyDupeipScanLimit();
         }

         String text = this.target;
         String lowerCase = text.toLowerCase(Locale.ENGLISH);
         List loginRecords = this.databaseSession.findLoginRecords(text, this.intValue);
         LoginRecord firstOrNull2 = (LoginRecord)CollectionsKt.firstOrNull(loginRecords);
         if (firstOrNull2 == null) {
            this.obtainVoid();
            throw new CommandAbortException();
         }

         this.loginRecord = firstOrNull2;
         this.handleText(lowerCase);
         this.handleText(firstOrNull2.getUuid());
         HashSet hashSet = new HashSet(loginRecords.size());
         Iterator iterator2 = loginRecords.iterator();

         while (iterator2.hasNext()) {
            String component32 = ((LoginRecord)iterator2.next()).component3();
            if (component32 != null && !StringsKt.startsWithWithDefaults(component32, '#', false, 2, null)) {
               hashSet.add(component32);
            }
         }

         boolean flag;
         label61: {
            this.addressCount = hashSet.size();
            this.ip = firstOrNull2.getIp();
            CommandTask commandTask2 = this;
            if (((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().isDupeipScanAllIps()) {
               CommandTask commandTask3 = this;
               if (!((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().isDeletePreviousHistory() && this.addressCount > 1) {
                  flag = true;
                  break label61;
               }
            }

            flag = false;
         }

         this.multipleAddresses = flag;
         List list;
         if (TargetParser.TARGET_PARSER.checkText(text)) {
            list = loginRecords;
            this.ip = text;
         } else {
            list = this.databaseSession.findLoginRecords(this.ip, this.intValue);
         }

         if (list.isEmpty()) {
            this.obtainVoid();
            throw new CommandAbortException();
         }

         LiteSender[] onlineSenders = this.getPlatform().getOnlineSenders();
         HashSet hashSet2 = new HashSet(8);
         HashMap hashMap = new HashMap(8);
         List list2 = this.results;
         Intrinsics.checkNotNull(onlineSenders);
         this.handleHashSet(hashSet2, hashMap, list, list2, onlineSenders);
         if (this.multipleAddresses) {
            this.handleList(loginRecords, hashSet2, hashMap, onlineSenders);
         }

         this.resultCount = this.results.size();
         this.resultText = CollectionsKt.joinToStringWithDefaults(this.results, Message.SEPARATOR, null, null, 0, null, obj -> {
            DupeIpResult dupeIpResult = (DupeIpResult) obj;
            CharSequence charSequence = dupeIpResult.getCharSequence();
            CharSequence message = dupeIpResult.getText3();
            return "" + charSequence + message;
         }, 30, null);
      } catch (ResultException e) {
         if (!this.quiet && this.sender != null) {
            CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, this.sender, String.valueOf(e.getMessage()), null, 4, null);
         }

         this.failed = true;
      }

      return this;
   }

   private final void handleList(List list, HashSet set, HashMap map, LiteSender[] liteSenders) {
      int value = 1;

      for (int i = this.addressCount; value < i; value++) {
         String component32 = ((LoginRecord)list.get(value)).component3();
         List loginRecords = this.databaseSession.findLoginRecords(component32, this.intValue);
         if (!loginRecords.isEmpty()) {
            this.handleHashSet(set, map, loginRecords, this.results, liteSenders);
         }
      }
   }

   private final void handleText(String text) {
      CommandTask commandTask = this;
      Set hiddenDupeipPlayers = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getHiddenDupeipPlayers();
      if (!hiddenDupeipPlayers.isEmpty()) {
         if (CollectionsKt___CollectionsKt.contains((Iterable) hiddenDupeipPlayers, text)) {
            this.obtainVoid2();
            throw new CommandAbortException();
         }
      }
   }

   private final Void obtainVoid() {
      throw new EmptyMarker();
   }

   private final Void obtainVoid2() {
      boolean flag = false;
      LoginRecord loginRecord2 = this.loginRecord;
      if (loginRecord2 != null) {
         String uuid = loginRecord2.getUuid();
         if (uuid != null) {
            LiteSender[] onlineSenders = this.getPlatform().getOnlineSenders();
            Intrinsics.checkNotNull(onlineSenders);
            int value = 0;
            int length2 = onlineSenders.length;

            while (true) {
               if (value >= length2) {
                  flag = false;
                  break;
               }

               LiteSender liteSender = onlineSenders[value];
               if (Intrinsics.areEqual(liteSender.getUuidString(), uuid)) {
                  flag = true;
                  break;
               }

               value++;
            }

            boolean flag2 = flag;
            Message message = flag2 ? Message.DUPEIP_ONLINE : Message.DUPEIP_OFFLINE;
            if (this.sender != null) {
               DatabaseSession databaseSession = this.databaseSession;
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, this.sender, PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.START_NO_IP, loginRecord2, false, 2, null), null, 4, null);
               CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
               LiteSender liteSender2 = this.sender;
               CharSequence message2 = message;
               CharSequence name = loginRecord2.getName();
               CommandOutput.sendWithDefaults(commandOutput, liteSender2, "" + message2 + name, null, 4, null);
            }

            throw new ResultException(Message.DUPEIP_END);
         }
      }

      throw new EmptyMarker();
   }

   public final void handleHashSet(@NotNull HashSet set, @NotNull HashMap map, @NotNull List list, @NotNull List list2, @NotNull LiteSender[] liteSenders) {
      LiteSender liteSender = null;
      String text = null;
      CommandTask commandTask = this;
      Settings settings = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings();
      Set hiddenDupeipPlayers = settings.getHiddenDupeipPlayers();
      Iterator iterator2 = list.iterator();

      while (true) {
         String text2;
         String text3;
         Date date;
         String text4;
         boolean flag;
         label146:
         do {
            while (true) {
               if (!iterator2.hasNext()) {
                  return;
               }

               LoginRecord loginRecord2 = (LoginRecord)iterator2.next();
               text4 = loginRecord2.component1();
               text2 = loginRecord2.component2();
               text3 = loginRecord2.component3();
               date = loginRecord2.component4();
               if (text2 != null
                  && text4 != null
                  && (Intrinsics.areEqual(text2, this.loginRecord != null ? this.loginRecord.getUuid() : null) || System.currentTimeMillis() - date.getTime() <= settings.getNotifyDupeipScanFilterDuration())) {
                  String string = (String)map.get(text2);
                  if (string == null) {
                     map.put(text2, text4);
                     break;
                  }

                  if (Intrinsics.areEqual(text4, string)) {
                     break;
                  }
               }
            }

            if (hiddenDupeipPlayers.isEmpty()) {
               break;
            }

            String[] texts = new String[]{text4.toLowerCase(Locale.ROOT), text2, text3};
            int value = 0;

            for (int i = texts.length; value < i; value++) {
               String text5 = texts[value];
               if (CollectionsKt___CollectionsKt.contains((Iterable) hiddenDupeipPlayers, text5)) {
                  flag = true;
                  continue label146;
               }
            }

            flag = false;
         } while (flag);

         boolean flag2 = false;
         boolean flag3 = false;
         boolean flag4 = false;
         LiteSender[] liteSenders2 = liteSenders;
         int value2 = 0;
         int length2 = liteSenders2.length;

         while (true) {
            if (value2 >= length2) {
               liteSender = null;
               break;
            }

            LiteSender liteSender2 = liteSenders2[value2];
            if (Intrinsics.areEqual(liteSender2.getUuidString(), text2)) {
               liteSender = liteSender2;
               break;
            }

            value2++;
         }

         LiteSender liteSender3;
         label172: {
            liteSender3 = liteSender;
            if (this.sender != null) {
               text = this.sender.getServerName();
               if (text != null) {
                  break label172;
               }
            }

            CommandTask commandTask2 = this;
            text = ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).getServerName();
         }

         String text6 = text;
         if (liteSender3 != null) {
            String name = liteSender3.getName();
            if (Intrinsics.areEqual(text4, name)) {
               if (!set.contains(name)) {
                  Punishment punishment = null;
                  if (settings.isNotifyDupeipShowMutedAccounts()) {
                     punishment = PunishmentQueries.findPunishmentWithDefaults(this.databaseSession, text2, text3, PunishmentType.MUTE, text6, false, false, 48, null);
                     flag4 = punishment != null;
                  }

                  Message message = flag4 ? Message.DUPEIP_MUTED : Message.DUPEIP_ONLINE;
                  list2.add(new DupeIpResult(message, name, text2, date, null, punishment));
                  set.add(name);
               } else {
                  flag3 = true;
               }
            } else if (!set.contains(text4)) {
               list2.add(new DupeIpResult(Message.DUPEIP_OFFLINE, text4, text2, date, null, null));
               set.add(text4);
            }

            flag2 = true;
         }

         if (set.contains(text4)) {
            flag3 = true;
         }

         if (!flag3 && !flag2) {
            Punishment punishment3 = PunishmentQueries.findPunishmentWithDefaults(this.databaseSession, text2, text3, null, text6, false, false, 52, null);
            Punishment punishment2 = null;
            boolean flag5 = punishment3 != null;
            boolean flag6 = punishment3 != null && !Intrinsics.areEqual(punishment3.getUuid(), text2);
            if (settings.isNotifyDupeipShowMutedAccounts()) {
               punishment2 = PunishmentQueries.findPunishmentWithDefaults(this.databaseSession, text2, text3, PunishmentType.MUTE, text6, false, false, 48, null);
               flag4 = punishment2 != null;
            }

            if (flag5) {
               this.bannedFound = true;
            }

            if (flag4) {
               this.mutedFound = true;
            }

            Message message2 = flag6 ? Message.DUPEIP_IP_BANNED : (flag5 ? Message.DUPEIP_BANNED : (flag4 ? Message.DUPEIP_MUTED : Message.DUPEIP_OFFLINE));
            list2.add(new DupeIpResult(message2, text4, text2, date, punishment3, punishment2));
            set.add(text4);
         }
      }
   }

}
