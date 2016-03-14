## FORCED-TODO

Simple compiler plugin that enables you finer control of TODOs
by using @todo annotation, which can fail compilation (i.e. if some todo is overdue).

Useful if you are one of people that prefer working in way of first sketching high level components and their interactions
before getting down to specific details.

Before using it please understand all implications :) (collaboration with colleagues with different locales,
build server...)

Example:
```scala
import forcedtodo.todo

@todo("Fix something") // always compiles since no date specified
class Foo {

    @todo("Implement real side effect", "2015-12-01") // will fail to compile after that day
    def bar(): Unit = ()

    @todo("Find better value", until = "bad_format") // will fail to compile because of invalid format
    val x = 123456
}
```

Date format has to be in YEAR-MONTH-DATE, such as in examples.

## INSTALLATION
For scala 2.11
```scala
libraryDependencies += "com.marekkadek" %% "forcedtodo" % "0.0.1" % "provided"

autoCompilerPlugins := true

addCompilerPlugin("com.marekkadek" %% "forcedtodo" % "0.0.1")
```

For scala 2.10
```scala
"org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided" // needed for 2.10.x only
```

TODO:
- user can specify format of dates TODOs
- user can specify violating todos should be errors or warnings
- user can specify what is limit of unresolved TODOs after which it fails to compile (disregarding date)
- problem with named reversed arguments
- improve tests (rather than just check if problem was in file)

Credits to https://github.com/lihaoyi/acyclic, some stuff stolen from there :)
