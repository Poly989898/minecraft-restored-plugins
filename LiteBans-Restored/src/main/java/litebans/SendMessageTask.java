package litebans;

public final class SendMessageTask implements Runnable {
   final LiteSender liteSender;
   final String text2;

   public SendMessageTask(LiteSender liteSender, String text) {
      this.liteSender = liteSender;
      this.text2 = text;
   }

   @Override
   public final void run() {
      this.liteSender.dispatchCommand(this.text2);
   }
}
