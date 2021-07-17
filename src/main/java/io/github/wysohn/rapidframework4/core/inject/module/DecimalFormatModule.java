package io.github.wysohn.rapidframework4.core.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.text.DecimalFormat;

public class DecimalFormatModule extends AbstractModule {
    private final String format;

    public DecimalFormatModule(String format) {
        this.format = format;
    }

    @Provides
    public DecimalFormat getFormat() {
        return new DecimalFormat(format);
    }
}
