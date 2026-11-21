package litebans;

import java.util.Collections;
import java.util.HashSet;

public class Sets {
   public static final HashSet computeHashSet(Object[] objs) {
      HashSet hashSet = new HashSet(objs.length);
      Collections.addAll(hashSet, objs);
      return hashSet;
   }
}
