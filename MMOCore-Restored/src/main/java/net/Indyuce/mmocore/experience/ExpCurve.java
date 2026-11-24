package net.Indyuce.mmocore.experience;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;

public class ExpCurve {
   private final String id;
   private final List<Long> experience = new ArrayList<>();
   public static final ExpCurve DEFAULT = new ExpCurve("default", 100L, 200L, 300L, 400L, 500L);

   public ExpCurve(File file) {
      this.id = file.getName().replace(".txt", "").toLowerCase().replace("_", "-").replace(" ", "-");

      try {
         BufferedReader reader = new BufferedReader(new FileReader(file));

         String readLine;
         while ((readLine = reader.readLine()) != null) {
            this.experience.add(Long.valueOf(readLine));
         }

         reader.close();
         Validate.isTrue(!this.experience.isEmpty(), "There must be at least one exp value in your exp curve");
      } catch (Throwable throwable) {
         throw new RuntimeException(throwable);
      }
   }

   public ExpCurve(String id, long... values) {
      this.id = id;

      for (long value : values) {
         this.experience.add(value);
      }

      Validate.isTrue(!this.experience.isEmpty(), "There must be at least one exp value in your exp curve");
   }

   public String getId() {
      return this.id;
   }

   public long getExperience(int level) {
      Validate.isTrue(level > 0, "Level must be stricly positive");
      return this.experience.get(Math.min(level, this.experience.size()) - 1);
   }
}
