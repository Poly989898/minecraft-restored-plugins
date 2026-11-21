package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class ArchiveUtil {
   public static final boolean checkCollection(Collection collection, Iterator iterator) {
      Objects.requireNonNull(collection);
      boolean flag = false;

      while (iterator.hasNext()) {
         flag |= collection.add(iterator.next());
      }

      return flag;
   }
}
