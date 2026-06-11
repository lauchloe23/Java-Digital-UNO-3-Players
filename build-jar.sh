#!/bin/zsh

# Compile all Java files
javac -d out $(find . -name '*.java')

# Copy resource files into the jar build directory
rm -rf out/resources
mkdir -p out/resources
cp -R Image out/resources/ 2>/dev/null || true
cp cards.csv out/resources/ 2>/dev/null || true

# Create a temporary jar content structure
rm -rf tmpjar
mkdir -p tmpjar
cp -R out/* tmpjar/
cp MANIFEST.MF tmpjar/

# Build the jar
cd tmpjar
jar cfm ../UnoGame.jar MANIFEST.MF .
cd ..

echo "Built UnoGame.jar"
