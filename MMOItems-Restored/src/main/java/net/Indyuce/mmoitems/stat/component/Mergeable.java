package net.Indyuce.mmoitems.stat.component;

@Deprecated
public interface Mergeable<T extends StatComponent> {
   void merge(T value);
}
