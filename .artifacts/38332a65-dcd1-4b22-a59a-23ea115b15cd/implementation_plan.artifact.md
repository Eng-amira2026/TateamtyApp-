# Fix Kotlin Compile Daemon Connection Issue

The error "Could not connect to Kotlin compile daemon" usually occurs due to memory constraints, corrupted daemon processes, or JVM argument conflicts. This plan involves increasing the heap memory for both Gradle and Kotlin daemons and clearing existing daemon processes.

## User Review Required

> [!IMPORTANT]
> After these changes are applied, you should run `./gradlew --stop` in your terminal to ensure all existing (possibly corrupted) daemons are terminated before the next build.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle.properties](file:///C:/Users/drmik/AndroidStudioProjects/Tateamtyapp1/gradle.properties)
- Increase `org.gradle.jvmargs` to 4GB to provide more headroom.
- Add `kotlin.daemon.jvmargs` to explicitly allocate memory for the Kotlin compiler daemon.
- Add `kotlin.incremental=true` and `kotlin.daemon.performance.metric=false` to stabilize daemon communication.

## Verification Plan

### Manual Verification
1. Run `./gradlew --stop` to kill existing daemons.
2. Run `./gradlew :app:compileDebugKotlin` to verify the fix.
