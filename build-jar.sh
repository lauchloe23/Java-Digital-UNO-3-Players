#!/bin/zsh
set -e

# Compile all Java files
rm -rf out
mkdir -p out
javac -d out $(find . -name '*.java')

# Build the jar with compiled classes
rm -f UnoGame.jar
jar cfm UnoGame.jar MANIFEST.MF -C out .

# Add resource files directly into the jar root
jar uf UnoGame.jar cards.csv || true
jar uf UnoGame.jar -C Image . || true

echo "Built UnoGame.jar with resources included"
