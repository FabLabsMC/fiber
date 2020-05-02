![logo](https://i.imgur.com/apskb0q.png)

A configuration system made for [Fabric](https://github.com/FabricMC)

## Examples

In both examples we define a custom `ConfigType` for `FiberId` (a generic `Identifier`) like this:
```java
StringConfigType<FiberId> FIBER_ID = ConfigTypes.STRING.derive(
        FiberId.class,
        s -> new FiberId(s.substring(0, s.indexOf(':')), s.substring(s.indexOf(':') + 1)),
        FiberId::toString
);
```

### Builders

Creating the immediate representation of your configuration using builders:
```java
// Constructing the immediate representation
PropertyMirror<FiberId> someIdentifier = PropertyMirror.create(FIBER_ID);
ConfigTree tree = ConfigTree.builder()
        .beginValue("some_identifier", FIBER_ID, new FiberId("some", "identifier"))
        .withComment("This is a comment attached to some_identifier!")
        .finishValue(someIdentifier::mirror)
        .fork("gui")
            .withValue("opacity", ConfigTypes.FLOAT.withValidRange(0, 1, 0.1), 1f)
            .finishBranch()
        .build();

// Interacting with the configuration
System.out.println(someIdentifier.getValue());
```

### Annotations

Create a class representing the configuration:
```java
@Settings(namingConvention = SnakeCaseConvention.class)
private static class MyPojo {
    FiberId someIdentifier = new FiberId("some", "identifier");

    @Setting.Group
    GuiGroup gui = new GuiGroup();

    private static class GuiGroup {
        public @Setting.Constrain.Range(min = 0, max = 1, step = 0.1) float opacity = 1f;
    }
}
```

And to turn it into an immediate representation:
```java
// Registering the custom type to the annotation processor
AnnotatedSettings settings = AnnotatedSettings.create();
settings.registerTypeMapping(FiberId.class, FIBER_ID);

// Creating the immediate representation from the annotated POJO
MyPojo pojo = new MyPojo();
ConfigTree tree = ConfigTree.builder().applyFromPojo(pojo, settings).build();
System.out.println(pojo.someIdentifier);
```

## Getting it

Add the following to your dependencies (`build.gradle`):
```gradle
dependencies {
    ...
    implementation "me.zeroeightsix:fiber:${project.fiber_version}"
}
```

Add `fiber_version` to your `gradle.properties`:
```properties
fiber_version = <VERSION HERE>
```
Replace `<VERSION HERE>` with the latest version of fiber, which you can find [here](http://maven.modmuss50.me/me/zeroeightsix/fiber/).

If you're using fiber in a project without fabric, you may have to add the repository as well:
```gradle
repositories {
    maven {
        url = "https://maven.modmuss50.me/"
    }
}
```