# redis-clj

Redis implemented in Clojure.

This is just a small learning project so it does not handle error/edge cases very well and doesn't even pass the `redis-benchmark` default test. But it works :)

## Usage

Start a Redis server at the default port 6379 (similar to `redis-server`) by running

```bash
lein run
```

Supporting commands:

```bash
PING, ECHO, GET, SET, INCR, INCRBY, EXISTS, DEL, COPY, EXPIRE, EXPIREAT, FLUSHDB
```

## Test

Unit test:

```bash
lein test
```

Test using Redis' official client:

```bash
pytest
```
