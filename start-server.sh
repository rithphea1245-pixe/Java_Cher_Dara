#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

echo "=== World of Wonder — Backend Server ==="

# 1. Ensure PostgreSQL is running
if ! pg_isready -q 2>/dev/null; then
    echo "[!] PostgreSQL does not appear to be running."
    echo "    Start it with:  sudo systemctl start postgresql"
    echo "    Or on macOS:     brew services start postgresql"
    exit 1
fi
echo "[OK] PostgreSQL is running."

# 2. Ensure the database exists
if ! psql -U postgres -lqt | cut -d '|' -f 1 | grep -qw world_of_wonder; then
    echo "[*] Creating database 'world_of_wonder' and loading schema..."
    psql -U postgres -c "CREATE DATABASE world_of_wonder;"
    psql -U postgres -d world_of_wonder -f schema.sql
    echo "[OK] Database created."
else
    echo "[OK] Database 'world_of_wonder' already exists."
fi

# 3. Compile Java sources
echo "[*] Compiling Java sources..."
find src -name '*.java' > /tmp/wow_sources.txt
javac -cp "lib/*" -d bin -sourcepath src @/tmp/wow_sources.txt
echo "[OK] Compilation succeeded."

# 4. Start the HTTP server on port 8080
echo "[*] Starting server on http://localhost:8080 ..."
java -cp "bin:lib/*" Main
