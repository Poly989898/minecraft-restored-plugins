package io.lumine.mythic.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Timing {
   private static final ConcurrentHashMap<String, Timing.TimingResults> timings = new ConcurrentHashMap<>();
   private final String type;
   private final long start = System.nanoTime();
   private long end;

   public Timing(String type) {
      this.type = type;
   }

   public void end() {
      this.end = System.nanoTime();
      timings.computeIfAbsent(this.type, k -> new Timing.TimingResults()).addTiming(this);
   }

   public long getNanoTime() {
      return this.end - this.start;
   }

   public static ConcurrentHashMap<String, Timing.TimingResults> getTimings() {
      return timings;
   }

   public static class TimingResults {
      private final List<Timing> timings = new ArrayList<>();

      public void addTiming(Timing timing) {
         this.timings.add(timing);
      }

      public long getAverage() {
         long average = this.getTotal();
         return average / this.timings.size();
      }

      public long getTotal() {
         long total = 0L;

         for (Timing timing : this.timings) {
            total += timing.getNanoTime();
         }

         return total;
      }
   }
}
