package it.zerono.mods.zerocore.lib.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;

public class WeakReferenceGroup<T> {

    public WeakReferenceGroup() {

        this._toBeCollected = new ReferenceQueue<>();
        this._set = new ObjectArraySet<>(16);
    }

    public int size() {

        this.purge();
        return this._set.size();
    }

    public boolean isEmpty() {

        this.purge();
        return this._set.isEmpty();
    }

    public WeakReference<T> add(final T o) {

        final WeakReference<T> ref = new WeakReference<>(o, this._toBeCollected);

        this._set.add(ref);
        return ref;
    }

    public void remove(final WeakReference<T> ref) {
        this._set.remove(ref);
    }

    public void clear() {

        this.purge();
        this._set.clear();
    }

    public List<T> values() {

        final ObjectList<T> list = new ObjectArrayList<>(this.size());

        this._set.forEach(ref -> list.add(ref.get()));
        return list;
    }

    public void forEach(final Consumer<? super T> action) {
        this.values().forEach(action);
    }

    //region internals

    private void purge() {

        for (Object remove; null != (remove = this._toBeCollected.poll());) {
            synchronized (this._toBeCollected) {
                //noinspection SuspiciousMethodCalls
                this._set.remove(remove);
            }
        }
    }

    private final ObjectSet<WeakReference<T>> _set;
    private final ReferenceQueue<T> _toBeCollected;

    //endregion
}
