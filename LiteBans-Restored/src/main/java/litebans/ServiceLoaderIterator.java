package litebans;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ServiceLoaderIterator implements Iterator {
   private Object obj2;
   private final Class class3;
   private final Iterator iterator2;

   public ServiceLoaderIterator(Class class2) {
      this(class2, ClassLoader.getSystemClassLoader());
   }

   public ServiceLoaderIterator(Class class2, ClassLoader classLoader) {
      this.class3 = class2;
      ServiceLoader load2 = ServiceLoader.load(class2, classLoader);
      this.iterator2 = load2.iterator();
      this.obj2 = null;
   }

   private final boolean obtainFlag() {
      while (this.obj2 == null) {
         try {
            if (!this.iterator2.hasNext()) {
               return false;
            }

            this.obj2 = this.iterator2.next();
         } catch (ServiceConfigurationError e) {
            if (!(e.getCause() instanceof SecurityException)) {
               throw e;
            }
         }
      }

      return true;
   }

   @Override
   public boolean hasNext() {
      return this.obtainFlag();
   }

   @Override
   public Object next() {
      if (!this.obtainFlag()) {
         throw new NoSuchElementException("No more elements for service " + this.class3.getName());
      }

      Object obj = this.obj2;
      this.obj2 = null;
      return obj;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("service=" + this.class3.getName());
   }
}
