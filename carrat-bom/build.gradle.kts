plugins {
    `java-platform`
}

val excluded = setOf(":carrat-bom")

dependencies {
    constraints {
        rootProject.subprojects.filter { it.path !in excluded }.forEach {
            evaluationDependsOn(it.path)
            it.publishing.publications.all {
                this as MavenPublication
//                if (!artifactId.endsWith("-kotlinMultiplatform") && !artifactId.endsWith("-metadata")){
                    add("api", "$groupId:$artifactId:$version")
//                }
            }
        }
        add("api", "org.carrat:carrat-web-webapi:0.0alpha0.0preview0")
    }
}

publishing {
    publications {
        create<MavenPublication>("platform") {
            from(components["javaPlatform"])
        }
    }
}
