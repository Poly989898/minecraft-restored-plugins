package litebans;

import java.util.ArrayList;
import java.util.List;

public final class PunishmentTableCompanion {
   private PunishmentTableCompanion() {
   }

   public final List obtainList() {
      PunishmentTable[] punishmentTables = PunishmentTable.values();
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = punishmentTables.length; value < i; value++) {
         PunishmentTable punishmentTable = punishmentTables[value];
         if (punishmentTable.obtainFlag()) {
            arrayList.add(punishmentTable);
         }
      }

      return arrayList;
   }

   public PunishmentTableCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
