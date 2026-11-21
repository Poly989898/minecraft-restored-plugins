package io.lumine.mythic.bukkit.utils.text;

import java.util.Arrays;

public class LevenshteinDistance {
   private static final LevenshteinDistance INSTANCE = new LevenshteinDistance();
   private final Integer threshold;

   public static LevenshteinDistance getDefaultInstance() {
      return INSTANCE;
   }

   private static int limitedCompare(CharSequence left, CharSequence right, int threshold) {
      if (left != null && right != null) {
         if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
         } else {
            int n = left.length();
            int m = right.length();
            if (n == 0) {
               return m <= threshold ? m : -1;
            } else if (m == 0) {
               return n <= threshold ? n : -1;
            } else {
               if (n > m) {
                  CharSequence tmp = left;
                  left = right;
                  right = tmp;
                  n = m;
                  m = tmp.length();
               }

               if (m - n > threshold) {
                  return -1;
               } else {
                  int[] p = new int[n + 1];
                  int[] d = new int[n + 1];
                  int boundary = Math.min(n, threshold) + 1;
                  int i = 0;

                  while (i < boundary) {
                     p[i] = i++;
                  }

                  Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
                  Arrays.fill(d, Integer.MAX_VALUE);

                  for (int j = 1; j <= m; j++) {
                     char rightJ = right.charAt(j - 1);
                     d[0] = j;
                     int min = Math.max(1, j - threshold);
                     int max = j > Integer.MAX_VALUE - threshold ? n : Math.min(n, j + threshold);
                     if (min > 1) {
                        d[min - 1] = Integer.MAX_VALUE;
                     }

                     int lowerBound = Integer.MAX_VALUE;

                     for (int ix = min; ix <= max; ix++) {
                        if (left.charAt(ix - 1) == rightJ) {
                           d[ix] = p[ix - 1];
                        } else {
                           d[ix] = 1 + Math.min(Math.min(d[ix - 1], p[ix]), p[ix - 1]);
                        }

                        lowerBound = Math.min(lowerBound, d[ix]);
                     }

                     if (lowerBound > threshold) {
                        return -1;
                     }

                     int[] tempD = p;
                     p = d;
                     d = tempD;
                  }

                  return p[n] <= threshold ? p[n] : -1;
               }
            }
         }
      } else {
         throw new IllegalArgumentException("CharSequences must not be null");
      }
   }

   private static int unlimitedCompare(CharSequence left, CharSequence right) {
      if (left != null && right != null) {
         int n = left.length();
         int m = right.length();
         if (n == 0) {
            return m;
         } else if (m == 0) {
            return n;
         } else {
            if (n > m) {
               CharSequence tmp = left;
               left = right;
               right = tmp;
               n = m;
               m = tmp.length();
            }

            int[] p = new int[n + 1];
            int i = 0;

            while (i <= n) {
               p[i] = i++;
            }

            for (int j = 1; j <= m; j++) {
               int upperLeft = p[0];
               char rightJ = right.charAt(j - 1);
               p[0] = j;

               for (int k = 1; k <= n; k++) {
                  int upper = p[k];
                  int cost = left.charAt(k - 1) == rightJ ? 0 : 1;
                  p[k] = Math.min(Math.min(p[k - 1] + 1, p[k] + 1), upperLeft + cost);
                  upperLeft = upper;
               }
            }

            return p[n];
         }
      } else {
         throw new IllegalArgumentException("CharSequences must not be null");
      }
   }

   public LevenshteinDistance() {
      this(null);
   }

   public LevenshteinDistance(Integer threshold) {
      if (threshold != null && threshold < 0) {
         throw new IllegalArgumentException("Threshold must not be negative");
      } else {
         this.threshold = threshold;
      }
   }

   public Integer apply(CharSequence left, CharSequence right) {
      return this.threshold != null ? limitedCompare(left, right, this.threshold) : unlimitedCompare(left, right);
   }

   public Integer getThreshold() {
      return this.threshold;
   }
}
