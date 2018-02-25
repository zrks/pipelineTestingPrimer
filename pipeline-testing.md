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
│   │       └── Library.groovy
│   └── test
│       └── groovy
│           └── LibraryTest.groovy
├── test
│   ├── groovy
│   │   └── com
│   │       └── example
│   │           ├── Helper.groovy
│   │           └── TestSampleAppPipeline.groovy
│   └── resources
│       └── sampleAppPipeline.jenkinsfile
└── vars
    └── samplePipeline.groovy

13 directories, 12 files
```

