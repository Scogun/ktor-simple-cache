import org.gradle.api.Project

const val ktorVersion = "2.3.12"
const val kotestVersion = "5.9.1"

fun Project.ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"

fun Project.ktorClient(module: String) = ktor("client-$module")

fun Project.ktorServer(module: String) = ktor("server-$module")

fun Project.kotest(module: String, version: String = kotestVersion) = "io.kotest:kotest-$module:$version"

fun Project.kotestEx(module: String, version: String) = "io.kotest.extensions:kotest-$module:$version"