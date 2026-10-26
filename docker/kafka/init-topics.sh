#!/usr/bin/env bash
set -euo pipefail

BOOTSTRAP_SERVER="kafka:9093"

echo "Waiting for Kafka..."

until /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server "$BOOTSTRAP_SERVER" \
  --list >/dev/null 2>&1; do
  sleep 2
done

create_topic() {
  local topic="$1"
  local partitions="$2"

  echo "Creating topic: ${topic}"
  /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server "${BOOTSTRAP_SERVER}" \
    --create \
    --if-not-exists \
    --topic "${topic}" \
    --partitions "${partitions}" \
    --replication-factor 1
}

create_topic events 2

echo "Kafka topics initialized."