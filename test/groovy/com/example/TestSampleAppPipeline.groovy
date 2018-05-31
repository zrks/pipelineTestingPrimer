package groovy.com.example

import org.junit.Before

class TestSampleAppPipeline {

    Helper pipelineHelper

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