package io.github.wysohn.rapidframework4.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework4.core.main.ManagerConfig;
import io.github.wysohn.rapidframework4.utils.Pair;

import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockConfigModule extends AbstractModule {
    public final ManagerConfig config = mock(ManagerConfig.class);

    @SafeVarargs
    public MockConfigModule(Pair<String, Object>... pairs) {
        when(config.get(eq("dbType"))).thenReturn(Optional.of("file"));

        for (Pair<String, Object> pair : pairs) {
            when(config.get(eq(pair.key))).thenReturn(Optional.of(pair.value));
        }
    }

    @Provides
    ManagerConfig config() {
        return config;
    }
}
