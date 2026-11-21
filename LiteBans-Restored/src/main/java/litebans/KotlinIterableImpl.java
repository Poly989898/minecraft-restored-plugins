package litebans;

import java.util.Iterator;

public final class KotlinIterableImpl implements Iterable, KMappedMarker {
   final KotlinAInterface3 kotlinAInterface;

   public KotlinIterableImpl(KotlinAInterface3 kotlinAInterface) {
      this.kotlinAInterface = kotlinAInterface;
   }

   @Override
   public Iterator iterator() {
      return this.kotlinAInterface.obtainIterator();
   }
}
