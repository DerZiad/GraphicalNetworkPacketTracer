#!/usr/bin/zsh

set -e

java -version
mvn clean compile assembly:single

rm -rf gnpt
mkdir gnpt

mv target/NetworkPacketTracer-1.0.jar-jar-with-dependencies.jar gnpt/NetworkPacketTracer.jar
cp -r javafx/ gnpt/

chmod -R 755 gnpt/

echo "Network Packet Tracer has been built and is ready for release."
echo "!/bin/sh
java --module-path ./javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.swing -jar NetworkPacketTracer.jar
" > gnpt/run.sh

zip -r gnpt.zip gnpt