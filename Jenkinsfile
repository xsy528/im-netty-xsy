@Library('shared-library') _

def buildTemplate = libraryResource 'cn/gyyx/jenkins/libraries/buildTemplateJDK11.yaml'
def projectlist = 'http://git.gydev.cn/support/gydev-instant-messenger-video/raw/develop/project-list.yaml'

k8sCluster(projectURL: projectlist,podTemplate:buildTemplate)