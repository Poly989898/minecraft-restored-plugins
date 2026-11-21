package litebans;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class FastList implements List, RandomAccess, Serializable {
   private static final long VALUE_MINUS_4598088075242913858 = -4598088075242913858L;
   private final Class class3;
   private Object[] objArray;
   private int intValue;

   public FastList(Class class2, int value) {
      this.objArray = (Object[])Array.newInstance(class2, value);
      this.class3 = class2;
   }

   @Override
   public boolean add(Object obj) {
      if (this.intValue < this.objArray.length) {
         this.objArray[this.intValue++] = obj;
      } else {
         int length2 = this.objArray.length;
         int value = length2 << 1;
         Object[] instance = (Object[])Array.newInstance(this.class3, value);
         System.arraycopy(this.objArray, 0, instance, 0, length2);
         instance[this.intValue++] = obj;
         this.objArray = instance;
      }

      return true;
   }

   @Override
   public Object get(int value) {
      return this.objArray[value];
   }

   @Override
   public boolean remove(Object obj) {
      for (int i = this.intValue - 1; i >= 0; i--) {
         if (obj == this.objArray[i]) {
            int value = this.intValue - i - 1;
            if (value > 0) {
               System.arraycopy(this.objArray, i + 1, this.objArray, i, value);
            }

            this.objArray[--this.intValue] = null;
            return true;
         }
      }

      return false;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.intValue; i++) {
         this.objArray[i] = null;
      }

      this.intValue = 0;
   }

   @Override
   public int size() {
      return this.intValue;
   }

   @Override
   public boolean isEmpty() {
      return this.intValue == 0;
   }

   @Override
   public Object set(int value, Object obj) {
      Object obj2 = this.objArray[value];
      this.objArray[value] = obj;
      return obj2;
   }

   @Override
   public Object remove(int value) {
      if (this.intValue == 0) {
         return null;
      }

      Object obj = this.objArray[value];
      int value2 = this.intValue - value - 1;
      if (value2 > 0) {
         System.arraycopy(this.objArray, value + 1, this.objArray, value, value2);
      }

      this.objArray[--this.intValue] = null;
      return obj;
   }

   @Override
   public boolean contains(Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator iterator() {
      return new KotlinIteratorImpl5(this);
   }

   @Override
   public Object[] toArray() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object[] toArray(Object[] objs) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection collection) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(Collection collection) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int value, Collection collection) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(Collection collection) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection collection) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(int value, Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int indexOf(Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int lastIndexOf(Object obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListIterator listIterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListIterator listIterator(int value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List subList(int value, int value2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object clone() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void forEach(Consumer consumer) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Spliterator spliterator() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeIf(Predicate predicate) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void replaceAll(UnaryOperator unaryOperator) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void sort(Comparator comparator) {
      throw new UnsupportedOperationException();
   }

   static int access$intValue(FastList fastList) {
      return fastList.intValue;
   }

   static Object[] access$objArray(FastList fastList) {
      return fastList.objArray;
   }
}
