import org.gradle.api.Project

const val ktorVersion = "3.4.0"
const val kotestVersion = "6.1.4"

fun Project.ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"

fun Project.ktorClient(module: String) = ktor("client-$module")

fun Project.ktorServer(module: String) = ktor("server-$module")

fun Project.kotest(module: String, version: String = kotestVersion) = "io.kotest:kotest-$module:$version"