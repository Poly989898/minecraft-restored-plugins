package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public final class TrackedPlayerMap extends AbstractMap<AbstractPlayer, Boolean> {
   AbstractPlayer[] keys;
   boolean[] vals;
   int count;
   private final Object2IntOpenHashMap<AbstractPlayer> index = new Object2IntOpenHashMap();

   public TrackedPlayerMap() {
      this.keys = new AbstractPlayer[8];
      this.vals = new boolean[8];
      this.index.defaultReturnValue(-1);
   }

   private void ensureCapacity(int min) {
      if (min > this.keys.length) {
         int cap = this.keys.length << 1;
         if (cap < min) {
            cap = min;
         }

         AbstractPlayer[] nk = new AbstractPlayer[cap];
         boolean[] nv = new boolean[cap];
         System.arraycopy(this.keys, 0, nk, 0, this.count);
         System.arraycopy(this.vals, 0, nv, 0, this.count);
         this.keys = nk;
         this.vals = nv;
      }
   }

   private void removeAt(int p) {
      AbstractPlayer removed = this.keys[p];
      int last = --this.count;
      if (p != last) {
         AbstractPlayer moved = this.keys[last];
         this.keys[p] = moved;
         this.vals[p] = this.vals[last];
         this.index.put(moved, p);
      }

      this.keys[last] = null;
      this.index.removeInt(removed);
   }

   void addIfAbsent(AbstractPlayer key) {
      if (this.index.getInt(key) < 0) {
         this.ensureCapacity(this.count + 1);
         this.keys[this.count] = key;
         this.vals[this.count] = false;
         this.index.put(key, this.count);
         this.count++;
      }
   }

   void retainViewers(Collection<AbstractPlayer> viewers) {
      int i = 0;

      while (i < this.count) {
         if (!viewers.contains(this.keys[i])) {
            this.removeAt(i);
         } else {
            i++;
         }
      }
   }

   public Boolean put(AbstractPlayer key, Boolean value) {
      boolean v = value != null && value;
      int p = this.index.getInt(key);
      if (p >= 0) {
         boolean old = this.vals[p];
         this.vals[p] = v;
         return old;
      } else {
         this.ensureCapacity(this.count + 1);
         this.keys[this.count] = key;
         this.vals[this.count] = v;
         this.index.put(key, this.count);
         this.count++;
         return null;
      }
   }

   public Boolean putIfAbsent(AbstractPlayer key, Boolean value) {
      int p = this.index.getInt(key);
      if (p >= 0) {
         return this.vals[p];
      } else {
         this.ensureCapacity(this.count + 1);
         this.keys[this.count] = key;
         this.vals[this.count] = value != null && value;
         this.index.put(key, this.count);
         this.count++;
         return null;
      }
   }

   public Boolean get(Object key) {
      int p = this.index.getInt((AbstractPlayer)key);
      return p >= 0 ? this.vals[p] : null;
   }

   @Override
   public boolean containsKey(Object key) {
      return this.index.getInt((AbstractPlayer)key) >= 0;
   }

   public Boolean remove(Object key) {
      int p = this.index.getInt((AbstractPlayer)key);
      if (p < 0) {
         return null;
      } else {
         boolean old = this.vals[p];
         this.removeAt(p);
         return old;
      }
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.count; i++) {
         this.keys[i] = null;
      }

      this.count = 0;
      this.index.clear();
   }

   @Override
   public int size() {
      return this.count;
   }

   @Override
   public boolean isEmpty() {
      return this.count == 0;
   }

   @Override
   public Set<AbstractPlayer> keySet() {
      return new AbstractSet<AbstractPlayer>() {
         @Override
         public int size() {
            return TrackedPlayerMap.this.count;
         }

         @Override
         public boolean contains(Object o) {
            return TrackedPlayerMap.this.containsKey(o);
         }

         @Override
         public Iterator<AbstractPlayer> iterator() {
            return new Iterator<AbstractPlayer>() {
               int i = 0;

               @Override
               public boolean hasNext() {
                  return this.i < TrackedPlayerMap.this.count;
               }

               public AbstractPlayer next() {
                  if (this.i >= TrackedPlayerMap.this.count) {
                     throw new NoSuchElementException();
                  } else {
                     return TrackedPlayerMap.this.keys[this.i++];
                  }
               }

               @Override
               public void remove() {
                  if (this.i <= 0) {
                     throw new IllegalStateException();
                  } else {
                     TrackedPlayerMap.this.removeAt(--this.i);
                  }
               }
            };
         }
      };
   }

   @Override
   public Set<java.util.Map.Entry<AbstractPlayer, Boolean>> entrySet() {
      return new AbstractSet<java.util.Map.Entry<AbstractPlayer, Boolean>>() {
         @Override
         public int size() {
            return TrackedPlayerMap.this.count;
         }

         @Override
         public Iterator<java.util.Map.Entry<AbstractPlayer, Boolean>> iterator() {
            return new Iterator<java.util.Map.Entry<AbstractPlayer, Boolean>>() {
               int i = 0;

               @Override
               public boolean hasNext() {
                  return this.i < TrackedPlayerMap.this.count;
               }

               public java.util.Map.Entry<AbstractPlayer, Boolean> next() {
                  if (this.i >= TrackedPlayerMap.this.count) {
                     throw new NoSuchElementException();
                  } else {
                     int idx = this.i++;
                     return TrackedPlayerMap.this.new Entry(idx);
                  }
               }

               @Override
               public void remove() {
                  if (this.i <= 0) {
                     throw new IllegalStateException();
                  } else {
                     TrackedPlayerMap.this.removeAt(--this.i);
                  }
               }
            };
         }
      };
   }

   private final class Entry implements java.util.Map.Entry<AbstractPlayer, Boolean> {
      private final int idx;

      Entry(int idx) {
         this.idx = idx;
      }

      public AbstractPlayer getKey() {
         return TrackedPlayerMap.this.keys[this.idx];
      }

      public Boolean getValue() {
         return TrackedPlayerMap.this.vals[this.idx];
      }

      public Boolean setValue(Boolean value) {
         boolean old = TrackedPlayerMap.this.vals[this.idx];
         TrackedPlayerMap.this.vals[this.idx] = value != null && value;
         return old;
      }
   }
}
