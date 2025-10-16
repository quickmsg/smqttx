@echo off
echo ========================================
echo  SMQTTX UI 重新构建脚本
echo ========================================
echo.

echo [1/3] 构建前端...
cd smqttx-ui\src\main\frontend
call ..\..\target\node\node.exe ..\..\target\node\node_modules\npm\bin\npm-cli.js run build
if errorlevel 1 (
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo [2/3] 复制到 smqttx-core/src/main/resources/static...
cd ..\..\..\..
xcopy /E /Y /I smqttx-ui\src\main\frontend\dist\* smqttx-core\src\main\resources\static\

echo.
echo [3/3] 复制到 smqttx-core/target/classes/static...
xcopy /E /Y /I smqttx-ui\src\main\frontend\dist\* smqttx-core\target\classes\static\

echo.
echo ========================================
echo  构建完成！请重启应用并刷新浏览器
echo ========================================
pause

