package net.Indyuce.mmocore.skilltree;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntegerCoordinates {
   private final int x;
   private final int y;

   public IntegerCoordinates(int x, int y) {
      this.x = x;
      this.y = y;
   }

   @Deprecated
   public IntegerCoordinates(String str) {
      String[] split = str.split("\\.");
      Validate.isTrue(split.length == 2, "Invalid format");
      this.x = Integer.parseInt(split[0]);
      this.y = Integer.parseInt(split[1]);
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   @NotNull
   public IntegerCoordinates add(@NotNull IntegerCoordinates other) {
      return new IntegerCoordinates(this.x + other.x, this.y + other.y);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         IntegerCoordinates that = (IntegerCoordinates)o;
         return this.x == that.x && this.y == that.y;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y);
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ")";
   }

   @NotNull
   public static IntegerCoordinates from(@Nullable Object object) {
      Validate.notNull(object, "Could not read coordinates");
      if (object instanceof ConfigurationSection) {
         ConfigurationSection config = (ConfigurationSection)object;
         return new IntegerCoordinates(config.getInt("x"), config.getInt("y"));
      } else if (object instanceof String) {
         String[] split = ((String)object).split("[:.,]");
         Validate.isTrue(split.length > 1, "Must provide two coordinates, X and Y, got " + Arrays.<String>asList(split));
         return new IntegerCoordinates(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
      } else {
         throw new RuntimeException("Needs either a string or configuration section");
      }
   }
}
