package litebans;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;

public final class KotlinHelper19 implements KotlinAInterface3 {
   private final AtomicReference atomicReference;

   public KotlinHelper19(@NotNull KotlinAInterface3 kotlinAInterface) {
      Intrinsics.checkParameterIsNotNull(kotlinAInterface, "sequence");
      super();
      this.atomicReference = new AtomicReference<>(kotlinAInterface);
   }

   @Override
   public Iterator obtainIterator() {
      KotlinAInterface3 andSet = (KotlinAInterface3)this.atomicReference.getAndSet(null);
      if (andSet == null) {
         throw new IllegalStateException("This sequence can be consumed only once.");
      }

      return andSet.obtainIterator();
   }
}
