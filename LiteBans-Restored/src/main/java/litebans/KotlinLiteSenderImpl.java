package litebans;

import java.util.UUID;

public final class KotlinLiteSenderImpl implements LiteSender {
   final String text5;
   final String ipAddress;
   final UUID uuid;

   public KotlinLiteSenderImpl(String text, String text2, UUID uuid) {
      this.text5 = text;
      this.ipAddress = text2;
      this.uuid = uuid;
   }

   public Void computeVoid(String text) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   @Override
   public String getDisplayName() {
      return this.text5;
   }

   public Void obtainVoid() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   @Override
   public String getIpAddress() {
      return this.ipAddress;
   }

   @Override
   public String getName() {
      return this.text5;
   }

   @Override
   public String getServerName() {
      return null;
   }

   @Override
   public UUID getUuid() {
      return this.uuid;
   }

   @Override
   public String getUuidString() {
      return this.uuid.toString();
   }

   @Override
   public boolean hasPermission(String text) {
      return false;
   }

   @Override
   public boolean isConsole() {
      return false;
   }

   @Override
   public boolean isOnline() {
      return false;
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public void disconnect(String text) {
   }

   @Override
   public void sendMessage(String text) {
   }

   @Override
   public void sendRichMessage(String text) {
   }

   @Override
   public void sendPluginMessage(String text, byte[] bytes) {
   }

   @Override
   public void dispatchCommand(String text) {
      this.computeVoid(text);
   }

   @Override
   public Object getHandle() {
      return this.obtainVoid();
   }
}
