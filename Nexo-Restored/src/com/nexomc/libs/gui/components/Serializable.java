package com.nexomc.libs.gui.components;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface Serializable {
   List<String> encodeGui();

   void decodeGui(@NotNull List<String> var1);
}
