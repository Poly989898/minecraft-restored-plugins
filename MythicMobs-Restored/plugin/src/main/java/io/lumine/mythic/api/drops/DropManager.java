package io.lumine.mythic.api.drops;

import io.lumine.mythic.core.drops.DropTable;
import java.util.Collection;
import java.util.Optional;

public interface DropManager {
   Optional<DropTable> getDropTable(String str);

   Collection<DropTable> getDropTables();
}
