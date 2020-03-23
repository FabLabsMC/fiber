/*
 * MIT License
 *
 * Copyright (c) 2018-2019 Falkreon (Isaac Ellingson)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.zeroeightsix.fiber.annotation.magic;

import javax.annotation.Nullable;
import java.lang.reflect.*;

/**
 * Copied from <a href=https://github.com/falkreon/Jankson/blob/780ed546ac/src/main/java/blue/endless/jankson/magic/TypeMagic.java>Jankson's source code</a>,
 * licensed by Falkreon under the MIT license.
 */
public final class TypeMagic {

    /**
     * This is a surprisingly intractable problem in Java: "Type" pretty much represents all possible states of reified
     * and unreified type information, and each kind of Type has different, mutually exclusive, and often unintended
     * ways of uncovering its (un-reified) class.
     *
     * <p>Generally it's much safer to use this for the type from a *field* than a blind type from an argument.
     *
     * @param t the type to check
     * @return the class of {@code t}
     */
    @Nullable
    public static Class<?> classForType(Type t) {
        if (t instanceof Class) return (Class<?>) t;

        if (t instanceof ParameterizedType) {
            Type subtype = ((ParameterizedType) t).getRawType();

            /* Testing for kind of a unicorn case here. Because getRawType returns a Type, there's always the nasty
             * possibility we get a recursively parameterized type. Now, that's not supposed to happen, but let's not
             * rely on "supposed to".
             */
            if (subtype instanceof Class) {
                return (Class<?>) subtype;
            } else {
                /* We're here at the unicorn case, against all odds. Let's take a lexical approach: The typeName will
                 * always start with the FQN of the class, followed by
                 */

                String className = t.getTypeName();
                int typeParamStart = className.indexOf('<');
                if (typeParamStart >= 0) {
                    className = className.substring(0, typeParamStart);
                }

                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }

        if (t instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) t).getUpperBounds();
            if (upperBounds.length == 0) return Object.class; //Well, we know it's an Object class.....
            return classForType(upperBounds[0]); //I'm skeptical about multiple bounds on this one, but so far it's been okay.
        }

        if (t instanceof TypeVariable) {
            return Object.class;
			/*//This gets us into all kinds of trouble with multiple bounds, it turns out
			Type[] types = ((TypeVariable<?>)t).getBounds();
			if (types.length==0) return Object.class;
			return classForType(types[0]);*/
        }

        if (t instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) t;
            /* ComponentClass will in practice return a TypeVariable, which will resolve to Object.
             * This is actually okay, because *any time* you try and create a T[], you'll wind up making an Object[]
             * instead and stuffing it into the T[]. And then it'll work.
             *
             * And if Java magically improves their reflection system and/or less-partially reifies generics down the line,
             * we can improve the TypeVariable case and wind up with more correctly-typed classes here.
             */
            Class<?> componentClass = classForType(arrayType.getGenericComponentType());
            try {
                //We can always retrieve the class under a "dots" version of the binary name, as long as componentClass wound up resolving to a valid Object type
                assert componentClass != null;
                return Class.forName("[L" + componentClass.getCanonicalName() + ";");
            } catch (ClassNotFoundException ex2) {
                return Object[].class; //This is probably what we're serving up anyway, so we might as well give the known-at-compile-time one out as a last resort.
            }
        }

        return null;
    }
}
