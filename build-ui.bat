@echo off
echo Building SMQTTX UI...

cd smqttx-ui\src\main\frontend

echo Installing dependencies...
call npm install

echo Building frontend...
call npm run build

echo Frontend build completed!
echo Moving build files to resources...

cd ..\..\..\..
if not exist "smqttx-ui\src\main\resources\static" mkdir "smqttx-ui\src\main\resources\static"
xcopy "smqttx-ui\src\main\frontend\dist\*" "smqttx-ui\src\main\resources\static\" /E /Y

echo UI build process completed!
pause 