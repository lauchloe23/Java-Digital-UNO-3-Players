#!/bin/zsh
set -e

# Clean previous output
rm -rf out
rm -f UnoGame.jar

# Compile Java sources into temporary classes directory
mkdir -p out
javac -d out $(find . -name '*.java')

# Create manifest for executable jar
cat > MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Main-Class: UnoController
EOF

# Create jar containing compiled classes and resources
jar cfm UnoGame.jar MANIFEST.MF -C out .

# Add resource files into jar root
jar uf UnoGame.jar cards.csv
jar uf UnoGame.jar -C Image .

# Cleanup temporary files
rm -rf out
rm -f MANIFEST.MF

echo "Built UnoGame.jar with embedded images and cards.csv"
