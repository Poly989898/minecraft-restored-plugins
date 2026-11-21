package litebans;

import java.util.concurrent.atomic.AtomicLong;

// $VF: synthetic class
final class EnumSwitchMap4 extends KotlinHelper36 implements Function1 {
   public static final EnumSwitchMap4 ENUM_SWITCH_MAP = new EnumSwitchMap4();

   EnumSwitchMap4() {
      super(1, KotlinStub14.class, "reset", "reset(Ljava/util/concurrent/atomic/AtomicLong;)V", 1);
   }

   public final void handleAtomicLong(AtomicLong atomicLong) {
      atomicLong.set(0L);
   }

   @Override
   public Object computeObj(Object obj) {
      this.handleAtomicLong((AtomicLong)obj);
      return Unit.UNIT;
   }
}
