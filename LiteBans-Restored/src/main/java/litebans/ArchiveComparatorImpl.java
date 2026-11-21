package litebans;

import java.util.Comparator;

class ArchiveComparatorImpl implements Comparator {
   final TarArchiveInputStream tarArchiveInputStream;

   ArchiveComparatorImpl(TarArchiveInputStream tarArchiveInputStream) {
      this.tarArchiveInputStream = tarArchiveInputStream;
   }

   public int computeIntValue(TarArchiveStructSparse tarArchiveStructSparse, TarArchiveStructSparse tarArchiveStructSparse2) {
      Long boxed = tarArchiveStructSparse.getLongValue();
      Long boxed2 = tarArchiveStructSparse2.getLongValue();
      return boxed.compareTo(boxed2);
   }

   @Override
   public int compare(Object obj, Object obj2) {
      return this.computeIntValue((TarArchiveStructSparse)obj, (TarArchiveStructSparse)obj2);
   }
}
