package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.ConfigureDelegate
import org.gradle.util.Configurable

/**
 * Map Implementation that creates a new instance when accessing a key that doesn't exists.
 * @param <V> Type of the Value.
 */
public abstract class CreatorMap<V> implements Configurable {
  private Map<String, V> mMap = new LinkedHashMap<>();
  /**
   * Returns the value to which the specified key is mapped, or creates a new instance if this map
   * contains no mapping for the key.
   * @param name The key whose associated value is to be returned.
   * @return The value to which the specified key is mapped, or creates a new instance if this map
   * contains no mapping for the key.
   */
  @Override
  public Object getProperty(String name) {
    V val = mMap[name]
    if (val == null) {
      val = createNewInstance()
      mMap[name] = val
    }
    return val
  }

  @Override
  public void setProperty(String name, Object value) {
    mMap[name] = value
  }

  /**
   * Returns a new instance of the value type.
   * @return A new instance.
   */
  protected abstract V createNewInstance()

  @Override
  public Object configure(final Closure cl) {
    ClosureBackedAction<Configurator<V>> action = createAction(cl)
    action.execute(new Configurator<V>(this, cl.owner))
    return this
  }

  /**
   * Returns a new Action to configure the created instance.
   * @param cl The closure to instantiate the action with.
   * @return The Action.
   */
  protected ClosureBackedAction createAction(Closure cl) {
    return new ClosureBackedAction(cl, Closure.DELEGATE_FIRST, true)
  }

  private class Configurator<T> extends ConfigureDelegate {
    private final CreatorMap<T> mMap

    private Configurator(CreatorMap<T> map, Object owner) {
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
        return mMap.getProperty(name);
      }
      T val = mMap.getProperty(name) as T
      ClosureBackedAction action = createAction(params[0] as Closure)
      action.execute(val)
      return val
    }
  }
}
