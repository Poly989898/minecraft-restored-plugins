package litebans;

public class LiteHelper14 {
   public final String channel;
   public final byte[] payload;

   public LiteHelper14(String text, byte[] bytes) {
      this.channel = text;
      this.payload = bytes;
   }

   public LiteHelper14() {
      this.channel = "";
      this.payload = new byte[0];
   }
}
