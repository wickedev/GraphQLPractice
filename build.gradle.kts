import com.palantir.gradle.docker.DockerComposeUp
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

group = "dev.wickedev.voca"
version = "0.1.0"

val kotlinCoroutineVersion = "1.4.3"
val spekVersion = "2.1.0-alpha.0.27+e76356a"
val graphQLKotlinVersion = "4.0.0-alpha.17"

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("org.springframework.boot") version "2.4.4"
    id("org.jetbrains.kotlin.plugin.spring") version "1.4.31"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.palantir.docker-compose") version "0.26.0"
    id("com.expediagroup.graphql") version "3.7.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

ktlint {
    version.set("0.39.0")
    disabledRules.set(
        setOf(
            "no-wildcard-imports",
            "no-consecutive-blank-lines"
        )
    )
    filter {
        exclude { element ->
            element.file.path
                .contains("generated")
        }
        exclude("*.kts")
        include("**/kotlin/**")
    }
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/spekframework/spek-dev/")
    maven("https://github.com/novonetworks/spring-fu/raw/patch-context/maven-repo")
}

dependencies {
    /* kotlin */
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${kotlinCoroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${kotlinCoroutineVersion}")

    /* spring */
    implementation("org.springframework.fu:spring-fu-kofu:0.5.0.7-patch-context")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    /* graphql */
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphQLKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-client:4.0.0-alpha.17")

    /* database */
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.mariadb:r2dbc-mariadb:1.0.1")

    /* security */
    implementation("io.jsonwebtoken:jjwt-api:0.11.1")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.1")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.1")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.66")

    /* testing */
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testImplementation("com.winterbe:expekt:0.5.0")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("io.mockk:mockk:1.10.4")

    /* testing testcontainers */
    testImplementation("org.testcontainers:mariadb:1.15.1")
    testImplementation("org.testcontainers:r2dbc:1.15.1")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.1.2")

    /* testing fixture */
    testImplementation("com.appmattus.fixture:fixture:1.0.0")
    testImplementation("com.appmattus.fixture:fixture-generex:1.0.0")
    testImplementation("com.appmattus.fixture:fixture-javafaker:1.0.0")
}

sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/java")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xinline-classes")
    }
}

tasks.withType<BootRun> {
    dependsOn(tasks.withType<DockerComposeUp>())

    systemProperty("spring.profiles.active", "dev")
    systemProperty("spring.devtools.restart.enabled", "true")
    systemProperty("spring.devtools.livereload.enabled", "true")
}

tasks.withType<Test> {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform {
        includeEngines = setOf("spek2")
    }
}