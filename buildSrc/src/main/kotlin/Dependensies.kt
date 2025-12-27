import org.gradle.api.Project

const val ktorVersion = "3.3.3"
const val kotestVersion = "6.0.7"

fun Project.ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"

fun Project.ktorClient(module: String) = ktor("client-$module")

fun Project.ktorServer(module: String) = ktor("server-$module")

fun Project.kotest(module: String, version: String = kotestVersion) = "io.kotest:kotest-$module:$version"