package litebans;

public final class KotlinThreadLocalSub extends ThreadLocal {
   final ConfigModule configModule;

   KotlinThreadLocalSub(ConfigModule configModule) {
      this.configModule = configModule;
   }

   protected Pair obtainPair() {
      return TuplesKt.computePair(Message.DATE_FORMAT.toString(), ConfigModule.createDateFormatWithDefaults(this.configModule, Message.DATE_FORMAT, null, 2, null));
   }

   @Override
   public Object initialValue() {
      return this.obtainPair();
   }
}
