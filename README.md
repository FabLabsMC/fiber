# fiber
A configuration system made for [Fabric](https://github.com/FabricMC)

## Concept
Fiber provides a configuration system based on a tree-like IR (immediate representation).
An IR is, at its root, a:
* Non-leaf node (`ConfigNode`)
  * Name
  * Child value nodes (`ConfigValue[]`)
    * Name
    * Comment
    * Constraints
      * Type (`Constraints`)
        * Identifier
      * (optional) Value
    * Value
    * Default Value
  * Child non-leaf nodes (`ConfigNode[]`)
  * Caches (for deserialised values we don't know the corresponding value node of yet)

Each IR tree can be converted ( (de)serialised ) into/from:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;⇒ A copy of the IR tree<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;⇐ A series of builders<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;⇔ A schema<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;⇔ A POJO (_Plain Old Java Object_)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;⇔ A config file (_Jankson_, but could be implemented for other formats)

## Goals
Fiber strives to be capable of the following
* The ability to transfer, and synchronise, configurations
* Provide extensive (serialisable) constrainting ability to settings
* Provide a way to validate an IR

# API usage

## IR tree

### Creation
```java
ConfigNode node = new ConfigNode();
```

### Building nodes
> **Note**: There's an annotation driven system that wraps around this. See [annotations](#annotations)

Grab a builder from `ConfigValue`, set the desired properties and build:
```java
ConfigValue<Integer> myValue = ConfigValue.builder(Integer.class)
    .withName("MySettingName")
    .withComment("A comment.")
    .withListener((then, now) -> {
        System.out.println("Value changed from " + then + " to " + now);
    })
    .withParent(node)
    .withDefaultValue(10)
    .build()
```
`.withParent(Node node)` won't actually modify the setting itself, but will register the setting to the `ConfigNode` you made earlier.

### Using nodes
Quite straightforward:
```java
ConfigValue<Integer> myValue = ConfigValue.builder(Integer.class)
    .withParent(node)
    .withDefaultValue(10)
    .constraints()
    .maxNumerical(20)
    .finish()
    .build();

Integer t = myValue.getValue(); // = 10
boolean pass = myValue.setValue(30); // = false as 30 isn't within our nodes constraints. In this case, there are none. = true

String myValueName = myValue.getName();
String myValueComment = myValue.getComment();
...
```

### Constraints
It's possible to add constraints to your setting, while building:
```java
ConfigValue<Integer> mySetting = ConfigValue.builder(Integer.class)
        .withName("MySettingName")
        .constraints()
        .composite(CompositeType.INVERT)
        .minNumerical(10)
        .maxNumerical(20)
        .finishComposite()
        .minNumerical(0)
        .finish()
        .build();
```
Let's take a deeper look. The constraint is built from two parts:
```java
        .composite(CompositeType.INVERT)
        .minNumerical(10)
        .maxNumerical(20)
        .finishComposite()
```
and
```java
        .minNumerical(0)
        .finish()
```
In the first one, we're assembling a composite. This is a constraint, made up of several other constraints, and will composite the return values of the constraints.
In our example, we're using `CompsiteType.INVERT`: this means that in order to pass the constraint, the value must **not** pass the all of the composite's child constraints.

In java:
`passed = !(constraint1 && constraint2)`

The second constraint is easier to understand. To pass, we just have to have a value of `0` or higher.

For a new value to be set, it must pass all constraints, meaning that our final constraint looks like this:
```java
passed = !(constraint1 && constraint2) && constraint3
```
Apply the actual values:
```java
passed = !(value >= 10 && value <= 20) && value >= 0
```
Let's double check:
```java
System.out.println("-5:\t" + mySetting.setValue(-5));
System.out.println("0 :\t" + mySetting.setValue(0 ));
System.out.println("5 :\t" + mySetting.setValue(5 ));
System.out.println("10:\t" + mySetting.setValue(10));
System.out.println("15:\t" + mySetting.setValue(15));
System.out.println("20:\t" + mySetting.setValue(20));
System.out.println("25:\t" + mySetting.setValue(25));
```
Prints:
```
-5:	false
0 :	true
5 :	true
10:	false
15:	false
20:	false
25:	true
```

### Browsing
```java
public class Main {
    private static void printNode(String prefix, Node node) {
        System.out.println(prefix + "Node " + node.getName());
        node.getItems().forEach(item -> {
            if (item instanceof Node) {
                printNode(prefix + "  ", (Node) item);
            } else {
                System.out.println(prefix + "  Leaf " + item.getName());
            }
        });
    }
    
    static {
        ConfigNode node = new ConfigNode();
        
        ConfigValue<Integer> node1 = ConfigValue.builder(Integer.class)
                .withName("A")
                .withDefaultValue(10)
                .withParent(node)
                .build();
        ConfigValue<Integer> node2 = ConfigValue.builder(Integer.class)
                .withName("C")
                .withDefaultValue(10)
                .withParent(node.fork("B"))
                .build();

        printNode("", node);
    }
}
```
```
Node null
  Node B
    Leaf C
  Leaf A
```

## Serialisation

### Using Jankson
```java
JanksonSettings.serialize(node, Files.newOutputStream(Paths.get(myFileName + ".json5")), compress);
```

### Using annotations
See [annotations](#annotations)

## Deserialisation

### Using Jankson
```java
JanksonSettings.deserialize(node, Files.newInputStream(Paths.get(myFileName + ".json5")));
```

### Using annotations
See [annotations](#annotations)

## Annotations
To convert from and to a POJO, a extensive annotations system is used. It wraps around the builder system.

### Usage
Create a pojo class (a dummy class that doesn't need to extend/implement anything) and create a `ConfigNode` to merge the IR represented by your POJO into.
```java
ConfigNode node = new ConfigNode();

POJO myPojo = new POJO();

try {
    AnnotatedSettings.applyToNode(node, pojo);
} catch (IllegalAccessException e) {
    e.printStackTrace();
}
```

### Fields
In your POJO class, every field, unless annotated using `@Setting.Ignored`, is considered a setting.

#### Final
These fields **must** be `final`. This is to force users to provide default values and keep them from setting the values at runtime as the configuration can't pick up fields being set directly.

```java
public class POJO {
    public final int mySetting = 5;
}
```
Creates the following IR:
- Node (root)
  - Setting `mySetting`
    - Type `java.lang.Integer`
    - Default value `5`
    - Name `mySetting`

> ##### Note
> If you, for some reason, need a field to not be final, it's possible to annotate it so the deserialiser will treat it as final:
> ```java
> @Setting.NoForceFinal()
> public int a = 5;
> ```
> 
> Or, for your entire POJO  class:
> ```java
> @Settings(noForceFinals = true)
> public class MyPojo {
>     public int a = 5;
> }
> ```

#### Additional properties
Adding a comment to a setting:
```java
@Comment("A comment.")
public final int a = 5;
```
Adding constraints to a setting:
```java
@Constrain.Min(10)
@Constrain.Max(20)
public final int a = 5;
```
Setting a settings value as final (can only be modified from the source it's being deserialised from)
```java
@Setting.Final()
public final int a = 5;
```

#### Listeners
Listeners are defined as a `BiConsumer`, annotated using `@Listener(<settingName>)`
```java
public final int a = 5;

@Listener("a")
private final BiConsumer<Integer, Integer> aListener = (then, now) -> {
        System.out.println("Changed value from " + then + " to " + now);
};
```
