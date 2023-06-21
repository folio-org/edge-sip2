buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  buildNode = 'jenkins-agent-java17'
  doDocker = {
    buildJavaDocker {
      publishMaster = true
      healthChk = false
    }
  }
}
