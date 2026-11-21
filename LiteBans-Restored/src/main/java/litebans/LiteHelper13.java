package litebans;

public final class LiteHelper13 implements LiteAInterface {
   final PunishmentRequest punishmentRequest;
   final SyncModule syncModule;
   final LiteSender liteSender;

   LiteHelper13(PunishmentRequest punishmentRequest, SyncModule syncModule, LiteSender liteSender) {
      this.punishmentRequest = punishmentRequest;
      this.syncModule = syncModule;
      this.liteSender = liteSender;
   }

   @Override
   public boolean checkUserRecord(UserRecord userRecord, SyncMessageType syncMessageType, String text) {
      if (syncMessageType == SyncMessageType.KICK_SUCCESS && Intrinsics.areEqual(text, this.punishmentRequest.getReason()) && this.syncModule.removeListener(this)) {
         CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, this.liteSender, Placeholders.replacePlaceholder(Message.RESPONSE.computeText("player", this.punishmentRequest.getReason()), "executor", this.punishmentRequest.getExecutorName()), null, 4, null);
      }

      return true;
   }
}
