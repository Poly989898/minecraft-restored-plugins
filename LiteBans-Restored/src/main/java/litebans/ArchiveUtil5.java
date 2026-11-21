package litebans;

import java.util.ArrayList;
import java.util.Iterator;

public class ArchiveUtil5 {
   public static final ArrayList obtainArrayList() {
      return new ArrayList();
   }

   public static final ArrayList computeArrayList(Iterator iterator) {
      ArrayList list = obtainArrayList();
      ArchiveUtil.checkCollection(list, iterator);
      return list;
   }
}
