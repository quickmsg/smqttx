@echo off
echo Starting SMQTTX with JDK 21 compatibility...

java ^
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED ^
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED ^
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED ^
--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED ^
--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED ^
--add-opens=java.base/java.io=ALL-UNNAMED ^
--add-opens=java.base/java.nio=ALL-UNNAMED ^
--add-opens=java.base/java.util=ALL-UNNAMED ^
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED ^
--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED ^
--add-opens=java.base/java.lang=ALL-UNNAMED ^
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED ^
--add-exports=java.management/sun.management=ALL-UNNAMED ^
--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED ^
--add-exports=java.base/sun.nio.ch=ALL-UNNAMED ^
--add-exports=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED ^
--add-exports=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED ^
--add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED ^
--illegal-access=permit ^
-DIGNITE_UPDATE_NOTIFIER=false ^
-DIGNITE_QUIET=true ^
-Xms2g -Xmx2g ^
-XX:+UseG1GC ^
-XX:MaxGCPauseMillis=200 ^
-XX:+UseStringDeduplication ^
-Dio.netty.leakDetection.level=disabled ^
-Dio.netty.recycler.maxCapacity=32 ^
-Dio.netty.allocator.numDirectArenas=2 ^
-jar smqttx-bootstrap/target/smqttx-bootstrap-2.0.12.jar

pause 