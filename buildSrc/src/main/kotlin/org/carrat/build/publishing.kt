import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

fun Project.version(baseVersion : String) {
    val releaseVersion = findProperty("releaseVersion")
    if(releaseVersion is String) {
        if(!releaseVersion.startsWith("$baseVersion.")) {
            throw InvalidUserDataException("Release version doesn't match base version")
        }
        version = releaseVersion
    } else {
        version = "$baseVersion-SNAPSHOT"
    }
}
