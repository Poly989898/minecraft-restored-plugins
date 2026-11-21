package litebans;

import java.security.PrivilegedAction;
import java.util.SortedMap;
import java.util.TreeMap;

final class ArchivePrivilegedActionImpl implements PrivilegedAction {
   public SortedMap obtainSortedMap() {
      TreeMap treeMap = new TreeMap();
      ArchiveHelper5.handleSet(ArchiveHelper5.getDefault().getInputStreamArchiveNames(), ArchiveHelper5.getDefault(), treeMap);

      for (ArchiveAInterface archiveAInterface : (Iterable<ArchiveAInterface>) ArchiveHelper5.obtainArrayList2()) {
         ArchiveHelper5.handleSet(archiveAInterface.getInputStreamArchiveNames(), archiveAInterface, treeMap);
      }

      return treeMap;
   }

   @Override
   public Object run() {
      return this.obtainSortedMap();
   }
}
