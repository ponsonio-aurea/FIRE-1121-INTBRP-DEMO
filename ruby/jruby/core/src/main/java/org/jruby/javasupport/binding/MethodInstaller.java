package org.jruby.javasupport.binding;

import org.jruby.RubyModule;
import org.jruby.internal.runtime.methods.DynamicMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
* Created by headius on 2/26/15.
*/
public abstract class MethodInstaller extends NamedInstaller {

    final ArrayList<Method> methods = new ArrayList<>(4);
    private List<String> aliases;
    private boolean localMethod;

    public MethodInstaller(String name, int type) { super(name, type); }

    // called only by initializing thread; no synchronization required
    final void addMethod(final Method method, final Class<?> clazz) {
        this.methods.add(method);
        localMethod |=
            clazz == method.getDeclaringClass() ||
            method.getDeclaringClass().isInterface();
    }

    // called only by initializing thread; no synchronization required
    final void addAlias(final String alias) {
        Collection<String> aliases = this.aliases;
        if (aliases == null) {
            aliases = this.aliases = new ArrayList<>(4);
        }
        if ( ! aliases.contains(alias) ) aliases.add(alias);
    }

    final void removeAlias(final String alias) {
        Collection<String> aliases = this.aliases;
        if (aliases == null) return;
        aliases.remove(alias);
    }

    final void defineMethods(RubyModule target, DynamicMethod invoker) {
        defineMethods(target, invoker, true);
    }

    protected final void defineMethods(RubyModule target, DynamicMethod invoker, boolean checkDups) {
        String oldName = this.name;
        target.addMethod(oldName, invoker);

        List<String> aliases = this.aliases;
        if ( aliases != null && isPublic() ) {
            for (int i = 0; i < aliases.size(); i++) {
                String name = aliases.get(i);
                if (checkDups && oldName.equals(name)) continue;
                target.addMethod(name, invoker);
            }
        }
    }

    @Override
    boolean hasLocalMethod () { return localMethod; }

    void setLocalMethod(boolean flag) { localMethod = flag; }

}
