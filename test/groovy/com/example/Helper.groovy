package groovy.com.example

import com.lesfurets.jenkins.unit.BasePipelineTest

class Helper extends BasePipelineTest {

    Helper(String name,
           String defaultVersion,
           String targetPath,
           String sourcePath) {

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