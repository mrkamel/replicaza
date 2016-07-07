
# replicaza

Replicaza is a highly available GTID-only mysql binlog to kafka replicator
written in Java. It's highly available, because you can run replicaza
processes on multiple hosts, while replicaza guarantees only one process is
running actively via leader election backed by zookeeper. Moreover, replicaza
is using zookeeper to store the GTID positions.

## Requirements

Replicaza currently requires every replicated table (you can fine-tune which
tables to replicate via `replicateTables` in config.properties) to have a
single-column primary key and the primary key must be the first first column of
the table. The primary key is the only column replicated to kafka.

