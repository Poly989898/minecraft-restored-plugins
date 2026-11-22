package litebans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class ResetDatabaseTask extends CommandTask {
   private final String[] texts;
   private boolean globalScope;
   private String serverScope;
   private KotlinDeleteDeactivatePruneEnum kotlinDeleteDeactivatePruneEnum;
   private DatabaseSession session;

   public ResetDatabaseTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.texts = texts;
      this.serverScope = "global";
      this.kotlinDeleteDeactivatePruneEnum = KotlinDeleteDeactivatePruneEnum.DELETE;
   }

   @Override
   public void run() {
      this.serverScope = AltAccountPunisher.getReasonArgument(this, this.getSender(), this.texts, this.serverScope);
      CommandTask commandTask = this;
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      boolean isConsole2 = commandTask.getSender().isConsole();
      CharSequence message = Message.ERROR_CONSOLE_ONLY;
      boolean flag = !isConsole2;
      if (flag) {
         commandOutput.returnResult(message);
         throw new CommandAbortException();
      }

      CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
      boolean flag2 = !ArraysKt.contains(this.texts, "confirm");
      if (flag2) {
         commandOutput2.returnResult(Message.RESET_DATABASE_CONFIRM_QUOTED.computeText("serverScope", this.serverScope));
         throw new CommandAbortException();
      }

      CommandTask commandTask2 = this;
      ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         KotlinDeleteDeactivatePruneEnum value = null;
         CommandOutput commandOutput3 = CommandTask.COMMAND_OUTPUT;
         boolean isClosed2 = databaseSession.isClosed();
         CharSequence message2 = Message.ERROR_NO_SQL_CONNECTION;
         if (isClosed2) {
            commandOutput3.returnResult(message2);
            throw new CommandAbortException();
         }

         List list = TextUtil.computeList(ArraysKt.toMutableList(this.texts));
         Iterable iterable = list;
         Iterator iterator2 = iterable.iterator();

         Object obj2;
         while (true) {
            if (!iterator2.hasNext()) {
               obj2 = null;
               break;
            }

            Object obj22 = iterator2.next();
            String string = (String)obj22;
            if (!Intrinsics.areEqual(string, "reset-database")
               && !StringsKt.checkText(string, "exclude:", false, 2, null)
               && !StringsKt.checkText(string, "mode:", false, 2, null)
               && !StringsKt.checkText(string, "server:", false, 2, null)
               && !Intrinsics.areEqual(string, "-g")
               && !Intrinsics.areEqual(string, "confirm")) {
               obj2 = obj22;
               break;
            }
         }

         String string2 = (String)obj2;
         CommandOutput commandOutput22 = CommandTask.COMMAND_OUTPUT;
         boolean flag3 = string2 != null;
         CharSequence message22 = LiteColor.red("Unrecognized parameter: " + LiteColor.yellow(string2));
         if (flag3) {
            commandOutput22.returnResult(message22);
            throw new CommandAbortException();
         }

         this.session = databaseSession;
         this.globalScope = ServerScopes.SERVER_SCOPE.isGlobal(this.serverScope);
         String text = this.globalScope ? "__ALL__" : this.serverScope;
         Iterable iterable2 = list;
         var arrayList = new ArrayList();

         for (Object obj3 : iterable2) {
            String string3 = (String)obj3;
            if (StringsKt.checkText(string3, "exclude:reason:", false, 2, null)) {
               arrayList.add(obj3);
            }
         }

         Iterable iterable3 = arrayList;
         var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList, 10));

         for (Object obj4 : iterable3) {
            String string4 = (String)obj4;
            arrayList2.add(StringsKt.substringAfterWithDefaults(string4, "exclude:reason:", null, 2, null));
         }

         Iterable iterable4 = list;
         var arrayList3 = new ArrayList();

         for (Object obj5 : iterable4) {
            String string5 = (String)obj5;
            if (StringsKt.checkText(string5, "exclude:", false, 2, null)) {
               arrayList3.add(obj5);
            }
         }

         Iterable iterable5 = arrayList3;
         var arrayList4 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList3, 10));

         for (Object obj6 : iterable5) {
            String string6 = (String)obj6;
            arrayList4.add(StringsKt.substringAfterWithDefaults(string6, "exclude:", null, 2, null));
         }

         Iterable iterable6 = arrayList4;
         var arrayList5 = new ArrayList();

         for (Object obj7 : iterable6) {
            String string7 = (String)obj7;
            if (!StringsKt.containsWithDefaults(string7, ':', false, 2, null)) {
               arrayList5.add(obj7);
            }
         }

         List mutableList = KotlinUtil30.asMutableList(arrayList5);
         if (!mutableList.isEmpty()) {
            mutableList.add("history");
            mutableList.add("config");
         }

         Iterable iterable7 = list;
         Iterator iterator3 = iterable7.iterator();

         while (true) {
            if (iterator3.hasNext()) {
               Object obj8 = iterator3.next();
               String string8 = (String)obj8;
               if (!StringsKt.checkText(string8, "mode:", false, 2, null)) {
                  continue;
               }

               obj2 = obj8;
               break;
            }

            obj2 = null;
            break;
         }

         String text2 = (String)obj2 != null ? StringsKt.substringAfterWithDefaults((String)obj2, ':', null, 2, null) : null;
         if (text2 != null) {
            KotlinDeleteDeactivatePruneEnum[] value2 = KotlinDeleteDeactivatePruneEnum.values();
            int value3 = 0;
            int length2 = value2.length;

            while (true) {
               if (value3 >= length2) {
                  value = null;
                  break;
               }

               KotlinDeleteDeactivatePruneEnum value4 = value2[value3];
               if (StringsKt.checkText(value4.name(), text2, true)) {
                  value = value4;
                  break;
               }

               value3++;
            }

            KotlinDeleteDeactivatePruneEnum value5 = value;
            if (value == null) {
               value5 = KotlinDeleteDeactivatePruneEnum.DELETE;
            }

            this.kotlinDeleteDeactivatePruneEnum = value5;
         }

         List list2;
         if (this.globalScope) {
            PunishmentTable[] punishmentTables = PunishmentTable.values();
            var arrayList6 = new ArrayList();
            byte b2 = 0;

            for (int i = punishmentTables.length; b2 < i; b2 += 1) {
               PunishmentTable punishmentTable = punishmentTables[b2];
               if (punishmentTable.obtainFlag() || punishmentTable == PunishmentTable.HISTORY) {
                  arrayList6.add(punishmentTable);
               }
            }

            list2 = arrayList6;
         } else {
            list2 = PunishmentTable.PUNISHMENT_TABLE_COMPANION.obtainList();
         }

         Iterable iterable8 = list2;
         var arrayList7 = new ArrayList();

         for (Object obj9 : iterable8) {
            PunishmentTable punishmentTable2 = (PunishmentTable)obj9;
            if (!mutableList.contains(punishmentTable2.toString().toLowerCase(Locale.ROOT))) {
               arrayList7.add(obj9);
            }
         }

         Iterable iterable9 = arrayList7;
         var arrayList8 = new ArrayList();

         for (Object obj10 : iterable9) {
            PunishmentTable punishmentTable3 = (PunishmentTable)obj10;
            if (!mutableList.contains(punishmentTable3.name().toLowerCase(Locale.ROOT))) {
               arrayList8.add(obj10);
            }
         }

         Iterable iterable10 = arrayList8;
         var arrayList9 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList8, 10));

         for (Object obj11 : iterable10) {
            PunishmentTable punishmentTable4 = (PunishmentTable)obj11;
            arrayList9.add(TuplesKt.computePair(punishmentTable4, PunishmentQueries.countEntriesWithDefaults(databaseSession, punishmentTable4, text, false, 4, null)));
         }

         Iterable iterable11 = arrayList9;

         for (Object obj12 : iterable11) {
            Pair pair = (Pair)obj12;
            DatabaseSession databaseSession2 = this.session;
            if (this.session == null) {
               Intrinsics.throwUninitializedProperty("");
               databaseSession2 = null;
            }

            databaseSession2.getEntries((PunishmentTable)pair.getFirst(), this.serverScope, this.kotlinDeleteDeactivatePruneEnum.ordinal());
         }


         for (Object obj13 : iterable11) {
            Pair pair2 = (Pair)obj13;
            CommandTask commandTask3 = this;
            CharSequence message3 = Message.RESET_DATABASE_RESULT.computeText("serverScope", this.serverScope);
            CharSequence[] messages = new CharSequence[]{"amount", String.valueOf(((Number)pair2.getSecond()).longValue())};
            message3 = TextUtil.replacePairs(message3, messages);
            CharSequence[] messages2 = new CharSequence[]{"type", ((PunishmentTable)pair2.getFirst()).name().toLowerCase(Locale.ROOT)};
            AltAccountPunisher.sendMessage(commandTask3, (CharSequence) (TextUtil.replacePairs(message3, messages2)));
         }

         DatabaseModule.connectWithDefaults(databaseSession.getDatabaseModule(), 0L, true, true, false, 1, null);
         return Unit.UNIT;
      });
   }

}
