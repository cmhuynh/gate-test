# Embedded Unit Test made Easy #

Congratulate to your Natural Language Processing solution using [GATE] (https://gate.ac.uk/).
If you employ [GATE embedded] (http://search.maven.org/#search%7Cga%7C1%7Cgate), 
and are looking for the missing piece of a GATE test artifact to improve the code quality of your solution, 
then this library is to serve you right.

### How do I get set up? ###

* Dependencies
```xml
<dependency>
	<groupId>com.github.cmhuynh</groupId>
	<artifactId>gate-test</artifactId>
	<version>${version}</version>
	<scope>test</scope>
</dependency>
```

* Deployment instructions

This library depends on gate-core version 8.0
```xml
<dependency>
    <groupId>uk.ac.gate</groupId>
    <artifactId>gate-core</artifactId>
    <version>8.0</version>
</dependency>
```

This library is compiled on Java 8

### Contribution guidelines ###

* Writing tests

Please help this unit test library to be full unit test coverage please.

* Code review

All PRs are welcomed.
 
* Other guidelines

No custom code style is required.

### Who do I talk to? ###

* Repo owner or admin

## Examples 

Initiate a new Annotation instance:
```java
String TYPE_VALUE = "some type";
String INST_VALUE = "some instance value";
String fKey = "some feature";
Set<Integer> fValue = new HashSet<>(Arrays.asList(1, 2, 3));
Annotation annotation = MockedAnnotation.builder().withInstance(INST_VALUE)
        .withType(TYPE_VALUE)
        .withOffset(10, 20)
        .withFeature(fKey, fValue)
        .mock();
```

Supply a GATE's DocumentImpl using a mock library of your choice  (Mockito for example) 
```java
@Mock
DocumentImpl document;
```

Mock an AnnotationSet instance
```java
AnnotationSet annotationSet = MockedAnnotationSet.builder()
                .withDocument(document)
                .addAnnotations(Arrays.asList(annotation1, annotation2, annotation3))
                .mock();
```

##Development
* [Project page / source code repository](https://github.com/cmhuynh/gate-test)
* Continuous integration: NA 
* Test coverage: N/A
* [Issue tracking](https://github.com/cmhuynh/gate-test/issues)
