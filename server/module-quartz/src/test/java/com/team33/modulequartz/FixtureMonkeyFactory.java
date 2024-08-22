package com.team33.modulequartz;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;

public abstract class FixtureMonkeyFactory {

      private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .plugin(new JavaxValidationPlugin())
        .defaultNotNull(true)
        .build();

    private FixtureMonkeyFactory() {
    }

    public static FixtureMonkey get() {
        return FIXTURE_MONKEY;
    }
}
