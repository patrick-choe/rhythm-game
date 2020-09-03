/*
 * Copyright (C) 2020 PatrickKR
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

plugins {
    kotlin("jvm") version "1.4.0"
}

group = "com.github.patrick-mc"
version = "0.3-beta"

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://dl.bintray.com/kotlin/dokka")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("com.github.noonmaru:tap:1.0.1")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}