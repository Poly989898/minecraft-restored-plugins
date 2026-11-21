package litebans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;

public final class KotlinListImpl2 implements List, KMappedMarker {
   private final Platform platform;
   private final DatabaseSession databaseSession;
   private final boolean flag2;
   private final ArrayList empty;
   private PunishmentTable punishmentTable;
   private final ConfigModule configModule;

   public KotlinListImpl2(@NotNull Platform platform, @NotNull DatabaseSession databaseSession, boolean flag, @NotNull ArrayList list) {
      this.platform = platform;
      this.databaseSession = databaseSession;
      this.flag2 = flag;
      this.empty = list;
      this.punishmentTable = PunishmentTable.BANS;
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
   }

   public KotlinListImpl2(Platform platform, DatabaseSession databaseSession, boolean flag, ArrayList list, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 8) != 0) {
         list = new ArrayList();
      }

      this(platform, databaseSession, flag, list);
   }

   public final boolean obtainFlag() {
      return this.flag2;
   }

   public final ArrayList obtainArrayList() {
      return this.empty;
   }

   public final void setPunishmentTable(@NotNull PunishmentTable punishmentTable2) {
      this.punishmentTable = punishmentTable2;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   public final void handlePair(@NotNull Pair pair) {
      boolean areEqual2 = Intrinsics.areEqual(this.configModule.getSettings().getSqlDriver(), "mysql");
      if (this.flag2 || !this.databaseSession.columnExists(this.punishmentTable, (String)pair.getFirst(), areEqual2)) {
         Object obj = ((Function1)pair.getSecond()).computeObj(this.punishmentTable);
         if (obj instanceof Object[]) {
            Object[] objs = (Object[])obj;
            int value = 0;

            for (int i = objs.length; value < i; value++) {
               Object obj2 = objs[value];
               Collection collection = this.obtainArrayList();
               Intrinsics.checkNotNullExpressionValue(obj2, "");
               collection.add((SqlText)obj2);
            }
         } else {
            Collection collection2 = this.empty;
            Intrinsics.checkNotNullExpressionValue(obj, "");
            collection2.add((SqlText)obj);
         }
      }
   }

   @Override
   public boolean isEmpty() {
      return this.empty.isEmpty();
   }

   public boolean checkText(@NotNull String text) {
      return this.empty.contains(SqlText.box(text));
   }

   @Override
   public Iterator iterator() {
      return this.empty.iterator();
   }

   @Override
   public boolean containsAll(@NotNull Collection collection) {
      return this.empty.containsAll(collection);
   }

   public String computeText(int value) {
      return ((SqlText)this.empty.get(value)).unbox();
   }

   public int computeIntValue(@NotNull String text) {
      return this.empty.indexOf(SqlText.box(text));
   }

   public int computeIntValue2(@NotNull String text) {
      return this.empty.lastIndexOf(SqlText.box(text));
   }

   @Override
   public ListIterator listIterator() {
      return this.empty.listIterator();
   }

   @Override
   public ListIterator listIterator(int value) {
      return this.empty.listIterator(value);
   }

   @Override
   public List subList(int value, int value2) {
      return this.empty.subList(value, value2);
   }

   public int obtainIntValue2() {
      return this.empty.size();
   }

   @Override
   public boolean remove(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean addAll(Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean addAll(int value, Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean removeAll(Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean retainAll(Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object remove(int value) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void replaceAll(UnaryOperator unaryOperator) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void sort(Comparator comparator) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public final boolean contains(Object obj) {
      return !(obj instanceof SqlText) ? false : this.checkText(((SqlText)obj).unbox());
   }

   @Override
   public Object get(int value) {
      return SqlText.box(this.computeText(value));
   }

   @Override
   public final int indexOf(Object obj) {
      return !(obj instanceof SqlText) ? -1 : this.computeIntValue(((SqlText)obj).unbox());
   }

   @Override
   public final int lastIndexOf(Object obj) {
      return !(obj instanceof SqlText) ? -1 : this.computeIntValue2(((SqlText)obj).unbox());
   }

   @Override
   public final int size() {
      return this.obtainIntValue2();
   }

   @Override
   public boolean add(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void add(int value, Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object set(int value, Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object[] toArray(Object[] objs) {
      return KotlinHelper12.computeObjArray(this, objs);
   }

   @Override
   public Object[] toArray() {
      return KotlinHelper12.computeObjArray(this);
   }
}
