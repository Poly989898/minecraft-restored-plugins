package io.lumine.mythic.bukkit.utils.numbers;

public enum DurationUnit {
   MILLISECONDS {
      @Override
      public double toMilliseconds(double value) {
         return value;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"ms", "milli", "millis", "millisecond", "milliseconds"};
      }
   },
   TICKS {
      @Override
      public double toMilliseconds(double value) {
         return value * 50.0;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds / 50.0;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"t", "tick", "ticks"};
      }
   },
   SECONDS {
      @Override
      public double toMilliseconds(double value) {
         return value * 1000.0;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds / 1000.0;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"s", "sec", "secs", "second", "seconds"};
      }
   },
   MINUTES {
      @Override
      public double toMilliseconds(double value) {
         return value * 60000.0;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds / 60000.0;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"m", "min", "mins", "minute", "minutes"};
      }
   },
   HOURS {
      @Override
      public double toMilliseconds(double value) {
         return value * 3600000.0;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds / 3600000.0;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"h", "hr", "hrs", "hour", "hours"};
      }
   },
   DAYS {
      @Override
      public double toMilliseconds(double value) {
         return value * 8.64E7;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds / 8.64E7;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"d", "day", "days"};
      }
   },
   WEEKS {
      @Override
      public double toMilliseconds(double value) {
         return value * 6.048E8;
      }

      @Override
      public double fromMilliseconds(long milliseconds) {
         return milliseconds / 6.048E8;
      }

      @Override
      public String[] suffixes() {
         return new String[]{"w", "week", "weeks"};
      }
   };

   public static final long MILLIS_PER_TICK = 50L;
   public static final long MILLIS_PER_SECOND = 1000L;
   public static final long MILLIS_PER_MINUTE = 60000L;
   public static final long MILLIS_PER_HOUR = 3600000L;
   public static final long MILLIS_PER_DAY = 86400000L;
   public static final long MILLIS_PER_WEEK = 604800000L;

   public abstract double toMilliseconds(double value);

   public abstract double fromMilliseconds(long value);

   public abstract String[] suffixes();

   public static DurationUnit fromSuffix(String suffix) {
      if (suffix == null) {
         return null;
      } else {
         String s = suffix.toLowerCase();

         for (DurationUnit u : values()) {
            for (String suf : u.suffixes()) {
               if (s.equals(suf)) {
                  return u;
               }
            }
         }

         return null;
      }
   }
}
