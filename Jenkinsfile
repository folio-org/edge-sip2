buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  publishAPI = false
  runLintRamlCop = false
  buildNode = 'jenkins-agent-java11'
  doDocker = {
    buildJavaDocker {
      publishMaster = true
      healthChk = false
    }
  }
}
