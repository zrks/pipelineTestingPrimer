#Jenkins Pipeline testing.

In order to test jenkins pipeline files and check e.g., regressions when developing shared libraries we need to introduce test framework that allows us to load shared libraries and test them without using e.g., JenkinsRule.

Jenkins [PipelineUnit framework](https://github.com/jenkinsci/JenkinsPipelineUnit) allows quick and effecient way to execute tests against customly built libraries and jenkinsfiles by offering simple yet powerful mocking capabilities.
Although PipelineUnit framework already have [some methods built in](https://github.com/jenkinsci/JenkinsPipelineUnit/blob/master/src/main/groovy/com/lesfurets/jenkins/unit/BasePipelineTest.groovy#L48-L78) we can add more methods by extending BasePipelineTest class.

To better grasp this let us introduce simple example.

Lets start by initializing groovy-library type project:
```sh
[sample-project] $ gradle init --type groovy-library
```
We will need to adjust created layout to fit Jenkins shared library layout:
```sh
[sample-project] $ mkdir -p test/resources/ test/groovy/com/example vars/
```

Lets add some filling:
```sh
[sample-project] $ touch vars/samplePipeline.groovy \
					 	 test/resources/sampleAppPipeline.jenkinsfile \
					 	 test/resources/sampleAppPipeline.Failing.jenkinsfile \
					 	 test/groovy/com/example/TestSampleAppPipeline.groovy \
					 	 test/groovy/com/example/Helper.groovy
```

At this point our project should look like this:
```sh
.
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── src
│   ├── main
│   │   └── groovy
│   │       └── Library.groovy # Various utilities used in pipelines, e.g., service name validators and/or constants, e.g., urls etc.
│   └── test
│       └── groovy
│           └── LibraryTest.groovy
├── test # Actual tests for pipelines, e.g., for samplePipeline.groovy.
│   ├── groovy
│   │   └── com
│   │       └── example
│   │           ├── Helper.groovy # Helper class which contains various jenkins command and variable mocks.
│   │           └── TestSampleAppPipeline.groovy # Test class to test pipeline.
│   └── resources # Sample pipeline files which reference, e.g., dockerAppPipe.groovy.
│       └── sampleAppPipeline.jenkinsfile
└── vars
    └── samplePipeline.groovy # Pipeline definition which is referenced in Jenkinsfiles, `sampleAppPipeline { // Custom variables, etc. }`

13 directories, 12 files
```

Test Setup.

In order to add test cases that we will be using we need to tell gradle where to find pipeline tests.

```groovy
// build.gradle
...
sourceSets {
    main {
        groovy {
            srcDirs = ['src']
        }
    }

    test {
        groovy {
            srcDirs = ['test']
        }
    }
}
```

Next step is to add dependency required to use (Jenkins Pipeline Unit){https://github.com/jenkinsci/JenkinsPipelineUnit} library.
```groovy
// build.gradle
...
dependencies {
    // Use the latest Groovy version for building this library
    compile 'org.codehaus.groovy:groovy-all:2.4.12'

    // Use the awesome Spock testing and specification framework
    testCompile 'org.spockframework:spock-core:1.0-groovy-2.4'

    // Jenkins pipeline testing framework - https://github.com/jenkinsci/JenkinsPipelineUnit.
    testCompile 'com.lesfurets:jenkins-pipeline-unit:1.1'
}
...
```

One more thing - `gradle init` adds spock framework dependency which we won't use at the moment, so lets remove out `src/` tests.

```sh
[sample-project] $ rm -rf src/test/
```

Now as we have everything set lets proceed with first pipeline test.

```groovy
// test/groovy/com/example/TestSampleAppPipeline.groovy
package groovy.com.example

import org.junit.Before

class TestSampleAppPipeline {

    Helper pipelineHelper // Helper class which will contain mocked pipeline methods.

    // We will create these files afterwards.
    String successTestFileName = "sampleAppPipeline.jenkinsfile"
    String failingTestFileName = "sampleAppPipeline.Failing.jenkinsfile"

    @Before
    void setUp() throws Exception {
        pipelineHelper = new Helper()

        pipelineHelper.setUp()
        pipelineHelper.setJobVariables()
        pipelineHelper.registerPipelineMethods()
    }

}
```

Lets add pipeline files.
```sh
[sample-project] $ mkdir test/resources && touch !$/sampleAppPipeline.jenkinsfile $!/sampleAppPipeline.failing.jenkinsfile
```

One important thing that `JenkinsPipelineUnit` framework provides is simple jenkins pipeline method mocking. Lest see how to set that up.

```groovy
// test/groovy/com/example/Helper.groovy
package groovy.com.example

import com.lesfurets.jenkins.unit.BasePipelineTest

class Helper extends BasePipelineTest {

    Helper(String name, // Pipeline shared library name.
           String defaultVersion, // Default shared library version, usually master.
           String targetPath, // Path where shared library is located.
           String sourcePath) { // TODO: Check these two paths.

        def library = library()
                .name(name)
                .defaultVersion(defaultVersion)
                .allowOverride(true)
                .implicit(true)
                .targetPath(targetPath)
                .retriever(localSource(sourcePath))
                .build()
        helper.registerSharedLibrary(library)

        setScriptRoots([ 'src', 'vars', 'test/resources' ] as String[])
        setScriptExtension('jenkinsfile')

    }

    void setJobVariables() {

    }

    void registerPipelineMethods() {

    }

}
```