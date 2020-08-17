package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;

import java.util.Arrays;

public class EnumArgumentMapper<E extends Enum> implements ArgumentMapper<E> {
    private final Class<? extends E> clazz;
    private final boolean toUpper;

    public EnumArgumentMapper(Class<? extends E> clazz, boolean toUpper) {
        this.clazz = clazz;
        this.toUpper = toUpper;
    }

    @Override
    public E apply(String arg) throws InvalidArgumentException {
        try {
            return (E) Enum.valueOf(clazz, toUpper ? arg.toUpperCase() : arg);
        } catch (IllegalArgumentException ex) {
            throw new InvalidArgumentException(DefaultLangs.General_EnumNotMatching, ((sen, langman) ->
                    langman.addString(arg)
                            .addString(String.join("&8, ", Arrays.stream(clazz.getEnumConstants())
                                    .map(Enum::name)
                                    .map(name -> "&a" + name)
                                    .toArray(String[]::new)))));
        }
    }

//    enum Test {
//        Temp;
//    }
//
//    public static void main(String[] ar) throws InvalidArgumentException {
//        EnumArgumentMapper<Test> mapper = new EnumArgumentMapper<>(Test.class);
//
//        System.out.println(mapper.apply("Temp"));
//        System.out.println(mapper.apply("meh"));
//    }
}
