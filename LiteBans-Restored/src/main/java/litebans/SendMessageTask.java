package litebans;

public final class SendMessageTask implements Runnable {
   final LiteSender liteSender;
   final String message;

   public SendMessageTask(LiteSender liteSender, String text) {
      this.liteSender = liteSender;
      this.message = text;
   }

   @Override
   public final void run() {
      this.liteSender.dispatchCommand(this.message);
   }
}
