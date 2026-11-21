package litebans;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class BroadcastCommandTask extends CommandTask {
   private final String[] texts15;

   public BroadcastCommandTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.texts15 = texts;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      String[] texts = new String[]{"litebans.admin", "litebans.broadcast"};
      AltAccountPunisher.handleCommandTask(commandTask, texts);
      if (this.texts15.length <= 1) {
         CommandTask.COMMAND_OUTPUT.returnResult(LiteColor.red("Usage: /litebans broadcast <message>"));
         throw new CommandAbortException();
      }

      List mutableList = ArraysKt.toMutableList(this.texts15);
      CommandTask commandTask2 = this;
      ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> computeUnit(mutableList, this, (DatabaseSession) obj));
   }

   private static final Unit computeUnit(List list, BroadcastCommandTask kotlinCommandTaskSub, DatabaseSession databaseSession) {
      KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
      kotlinSerializable.obj = "broadcast";
      String text = "broadcast-type:";
      CollectionsKt.checkList(list, obj -> {
         String text22 = (String) obj;
         boolean flag;
         if (StringsKt.checkText(text22, text, false, 2, null)) {
            kotlinSerializable.obj = text22.substring(text.length());
            flag = false;
         } else {
            flag = true;
         }

         return flag;
      });
      String text2 = Message.computeText(CollectionsKt.joinToStringWithDefaults(list, " ", null, null, 0, null, null, 62, null), true);
      byte b = 10;
      CharSequence replacePlaceholders2 = databaseSession.replacePlaceholders(text2.substring(b));
      CommandTask commandTask = kotlinCommandTaskSub;
      BroadcastModule module = (BroadcastModule)commandTask.getPlatform().getModule(BroadcastModule.class);
      BroadcastModule.handleBroadcastModule(module, databaseSession, replacePlaceholders2, (String)kotlinSerializable.obj, null, kotlinCommandTaskSub.getSender().getServerName(), false, false, kotlinCommandTaskSub.getSender(), 52, null);
      SyncMessageType syncMessageType = SyncMessageType.BROADCAST_CUSTOM;
      if (databaseSession.getDatabaseModule().isSyncEnabled()) {
         CharSequence[] messages = new CharSequence[]{(CharSequence)kotlinSerializable.obj, replacePlaceholders2};
         CharSequence joinToString = ArraysKt.joinToStringWithDefaults(messages, "\ufeff", null, null, 0, null, null, 62, null);
         AltAccountPunisher.sendSyncWithDefaults(databaseSession, syncMessageType, joinToString, null, null, 12, null);
      }

      return Unit.UNIT;
   }
}
