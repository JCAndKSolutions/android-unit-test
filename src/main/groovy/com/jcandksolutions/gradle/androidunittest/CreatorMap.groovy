package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.ConfigureDelegate
import org.gradle.util.Configurable

public abstract class CreatorMap<K, V> extends HashMap<K, V> implements Configurable {
  public V get(Object key) {
    V val = super.get(key)
    if (val == null) {
      val = createNewInstance()
      put((K) key, val)
    }
    return val
  }

  protected abstract V createNewInstance()

  @Override
  public Object configure(final Closure cl) {
    ClosureBackedAction<Configurator> action = createAction(cl)
    action.execute(new Configurator(this, cl.owner))
    return this
  }

  protected ClosureBackedAction createAction(Closure cl) {
    return new ClosureBackedAction(cl, Closure.DELEGATE_FIRST, true)
  }

  private class Configurator<K, V> extends ConfigureDelegate {
    private final CreatorMap<K, V> mMap

    private Configurator(CreatorMap<K, V> map, Object owner) {
      super(owner, map)
      mMap = map
    }

    @Override
    protected boolean _isConfigureMethod(String name, Object[] params) {
      return params.length == 1 && params[0] instanceof Closure;
    }

    @Override
    protected Object _configure(String name, Object[] params) {
      if (params.length == 0) {
        return mMap[name];
      }
      V val = mMap[name]
      ClosureBackedAction action = createAction(params[0] as Closure)
      action.execute(val)
      return val
    }
  }
}
