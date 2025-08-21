#!/bin/bash
#
# Start script for authcode-changed-consumer

PORT=8082

exec java -jar -Dserver.port="${PORT}" "authcode-changed-consumer.jar"
