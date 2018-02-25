package groovy.com.example


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