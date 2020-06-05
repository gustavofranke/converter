## Match Time Converter

[![Build Status](https://travis-ci.org/gustavofranke/converter.svg?branch)](https://travis-ci.org/gustavofranke/converter)
### Welcome!
This program takes a string representing a match time in one format,
and converts it to a string representing match time in another format.

### How generate the jar with sbt
1. Run `sbt assembly`
2. Some output indicates that the source files are compiled, and the tests run.
3. Its last line should look like `[success] Total time: 6 s, completed 03-Jun-2020 01:09:36`

### How run the jar
1. From the projects root directory, run `java -jar target/scala-2.13/converter-assembly-0.1.jar "[PM] 0:00.000"`
2. The result of running that command prints out `00:00 – PRE_MATCH`

### How to use it from sbt
1. In a command line utility, open an sbt interactive session by typing `sbt` <enter>
2. Then, type `run` the-input-match-time <enter>
3. You should see the program's output printed off.

#### Example
```
sbt:converter> run "[PM] 0:00.000"
[info] Compiling 1 Scala source to converter/target/scala-2.13/classes ...
[info] running challenge.Main "[PM] 0:00.000"
00:00 – PRE_MATCH
[success]
```

### How to run the tests from sbt
1. Just run `test` from the sbt interactive mode
2. Find below an example of what gets printed to the output
```
sbt:converter> test
[info] Compiling 1 Scala source to converter/target/scala-2.13/test-classes ...
[info] ConverterTestSuite:
[info] - parseThenRender obtained values should match the expected values
[info] - parseMatchTime shows the result of all parsing rules, and error messages when it fails
[info] Run completed in 569 milliseconds.
[info] Total number of tests run: 2
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 2, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 1 s, completed 03-Jun-2020 00:56:11
```

### How to run open the coverage report
* With an open interactive session in sbt, run the following commands in sequence
  * coverage
  * test
  * coverageReport
  * coverageOff
* This will print out the location of the html file, which can be opened with any http client
```
sbt:converter> coverageReport
[info] Waiting for measurement data to sync...
[info] Reading scoverage instrumentation [converter/target/scala-2.13/scoverage-data/scoverage.coverage]
[info] Reading scoverage measurements...
[info] Generating scoverage reports...
[info] Written Cobertura report [converter/target/scala-2.13/coverage-report/cobertura.xml]
[info] Written XML coverage report [converter/target/scala-2.13/scoverage-report/scoverage.xml]
[info] Written HTML coverage report [converter/target/scala-2.13/scoverage-report/index.html]
[info] Statement coverage.: 87.84%
[info] Branch coverage....: 100.00%
[info] Coverage reports completed
[success] Total time: 1 s, completed 03-Jun-2020 01:02:21
```
