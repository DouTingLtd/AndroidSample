@echo off
cd..

echo ִ������...
call gradlew clean

echo ����...
call gradlew :ui:build

echo �ϴ�...
call gradlew :ui:publishReleasePublicationToMavenCentralRepository

pause