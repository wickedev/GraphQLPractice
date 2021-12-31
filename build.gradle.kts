import com.palantir.gradle.docker.DockerComposeUp
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

group = "dev.wickedev.voca"
version = "0.1.0"

val kotlinCoroutineVersion = "1.6.0"
val spekVersion = "2.0.17"
val graphQLKotlinVersion = "5.3.1"

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.palantir.docker-compose") version "0.28.0"
    id("com.expediagroup.graphql") version "5.3.1"
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

graphql {
    schema {
        packages = listOf(
            "org.example",
            "java.time",
        )
    }
}

repositories {
    mavenCentral()
    maven("https://github.com/wickedev/graphql-jetpack/raw/deploy/maven-repo")
}

dependencies {
    graphqlSDL(project(":"))

    /* kotlin */
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${kotlinCoroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${kotlinCoroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    /* spring */
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor:reactor-tools")

    /* graphql */
    api("com.graphql-java:graphql-java:17.3")
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphQLKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-client:$graphQLKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider:$graphQLKotlinVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.graphql-java:graphql-java-extended-scalars:17.0")
    implementation("com.zhokhov.graphql:graphql-java-datetime:4.1.0")

    runtimeOnly("com.graphql-java-kickstart:graphiql-spring-boot-starter:11.1.0")
    runtimeOnly("com.graphql-java-kickstart:voyager-spring-boot-starter:11.1.0")

    /* database */
    implementation("io.github.wickedev:spring-data-graphql-r2dbc-starter:0.2.0")
    api("io.r2dbc:r2dbc-spi:0.9.0.RELEASE")
    implementation("org.postgresql:r2dbc-postgresql:0.9.0.RC1")
    implementation("name.nkonev.r2dbc-migrate:r2dbc-migrate-spring-boot-starter:1.8.0")

    /* security */
    implementation("io.github.wickedev:graphql-kotlin-spring-security:0.2.0")
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.69")
    implementation("com.google.crypto.tink:tink:1.6.1")

    /* testing */
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testImplementation("com.winterbe:expekt:0.5.0")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("io.mockk:mockk:1.12.1")

    /* testing testcontainers */
    testImplementation("org.testcontainers:mariadb:1.16.2")
    testImplementation("org.testcontainers:r2dbc:1.16.2")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.7.3")

    /* testing fixture */
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-generex:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
    testImplementation("io.leangen.geantyref:geantyref:1.3.13")
    testImplementation("net.jodah:typetools:0.6.3")
    testImplementation("com.google.guava:guava:31.0.1-jre")
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
