package com.nexomc.nexo.recipes;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/recipes/RecipeType;", "", "id", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "getId", "()Ljava/lang/String;", "SHAPED", "SHAPELESS", "FURNACE", "BLASTING", "SMOKER", "BREWING", "STONECUTTING", "CAMPFIRE", "com.nexomc:core"}
)
public enum RecipeType {
   SHAPED("shaped"),
   SHAPELESS("shapeless"),
   FURNACE("furnace"),
   BLASTING("blasting"),
   SMOKER("smoking"),
   BREWING("brewing"),
   STONECUTTING("stonecutting"),
   CAMPFIRE("campfire");

   @NotNull
   private final String id;

   private RecipeType(String id) {
      this.id = id;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

}
