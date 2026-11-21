package litebans;

import java.util.ArrayList;
import java.util.List;

public class TarArchiveSparseEntry implements TarConstants {
   private final boolean flag10;
   private List list2;

   public TarArchiveSparseEntry(byte[] bytes) {
      int value = 0;
      this.list2 = new ArrayList();

      for (int i = 0; i < 21; i++) {
         TarArchiveStructSparse sparse = ArchiveUtil7.parseSparse(bytes, value + i * 24);
         if (sparse.getLongValue() > 0L || sparse.getLongValue2() > 0L) {
            this.list2.add(sparse);
         }
      }

      value += 504;
      this.flag10 = ArchiveUtil7.parseBoolean(bytes, value);
   }

   public boolean isFlag10() {
      return this.flag10;
   }

   public List getList2() {
      return this.list2;
   }
}
