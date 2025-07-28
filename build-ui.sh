#!/bin/bash
echo "Building SMQTTX UI..."

cd smqttx-ui/src/main/frontend

echo "Installing dependencies..."
npm install

echo "Building frontend..."
npm run build

echo "Frontend build completed!"
echo "Moving build files to resources..."

cd ../../../
mkdir -p smqttx-ui/src/main/resources/static
cp -r smqttx-ui/src/main/frontend/dist/* smqttx-ui/src/main/resources/static/

echo "UI build process completed!" 