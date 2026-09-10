#!/usr/bin/env sh
# FixIt Maven Wrapper Script

DIR="$(cd "$(dirname "$0")" && pwd)"

if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
    elif [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null)"
    fi
fi

if [ -x "$DIR/.tools/maven/bin/mvn" ]; then
    exec "$DIR/.tools/maven/bin/mvn" "$@"
elif command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "Error: Maven not found. Please install Maven or check .tools/maven." >&2
    exit 1
fi
