package me.zeroeightsix.fiber.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this field or method is a listener listening to changes of another field's value.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    /**
     * The name of the setting this listener is listening for.
     * <br>
     *
     * <p> Note that this must be equal to the resolved name of the setting: if you have specified a naming convention for your settings, or you have set a custom name for the setting, this value must be equal to the name of the setting you're listening for after that naming convention was applied!
     *
     * <p> For example, if you are using {@code snake_case}:
     * <pre>
     * {@code
     * @Settings(namingConvention = UnderscoredLowerCaseConvention.class)
     * class MySettings {
     *     private int fooBar = 5;
     *
     *     @Listener("foo_bar") // foo_bar not fooBar
     *     public void fooBarListener(int newValue) {
     *         System.out.println("Changed to " + newValue);
     *     }
     * }
     * }
     * </pre>
     * @return the name of the setting this listener listens for
     */
    String value();
}
