plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.hypersistence:hypersistence-tsid:2.1.4")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.4")
}
