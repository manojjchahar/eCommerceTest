package com.eCommerceTest.screenplay;

import java.util.HashMap;
import java.util.Map;

public class Actor {
    private final String name;
    private final Map<Class<? extends Ability>, Ability> abilities = new HashMap<>();

    public Actor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Actor named(String name) {
        return new Actor(name);
    }

    public <T extends Ability> Actor can(T ability) {
        abilities.put(ability.getClass(), ability);
        return this;
    }

    public <T extends Ability> T abilityTo(Class<T> abilityClass) {
        return abilityClass.cast(abilities.get(abilityClass));
    }

    public void attemptsTo(Task... tasks) {
        for (Task task : tasks) {
            task.performAs(this);
        }
    }
}
