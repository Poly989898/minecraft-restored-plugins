package io.lumine.mythic.bukkit.utils.particles.reflection.mappings.files;

import io.lumine.mythic.bukkit.utils.particles.reflection.mappings.Mappings;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface MappingType {
   @NotNull
   Mappings parse(@NotNull List<String> strs);

   void write(@NotNull BufferedWriter bufferedWriter, @NotNull Mappings mappings) throws IOException;
}
