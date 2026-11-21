package litebans;

public final class KotlinRunnableImpl implements Runnable {
   final LiteSender liteSender;
   final KotlinSerializableImpl kotlinSerializable;

   public KotlinRunnableImpl(LiteSender liteSender, KotlinSerializableImpl kotlinSerializable) {
      this.liteSender = liteSender;
      this.kotlinSerializable = kotlinSerializable;
   }

   @Override
   public final void run() {
      this.liteSender.dispatchCommand(this.kotlinSerializable.obj.toString());
   }
}
