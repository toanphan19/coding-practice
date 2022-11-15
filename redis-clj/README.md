# redis-clj

Redis implemented in Clojure.

This is just a small learning project so it does not handle error/edge cases very well. It also does not pass the `redis-benchmark` test. But it works :)

## Requirements

Clojure installed with [Leiningen](https://leiningen.org/).

## Usage

Start a Redis server at the default port 6379 (similar to `redis-server`) by running

```bash
lein run
```

Then, in another terminal, assuming you have redis installed, you can connect to the server with
```bash
redis-cli
```

Now try to use it as a normal redis server! For example:
```bash
SET counter 100
INCR counter
GET counter  # should return "101"
```


Supporting commands: `PING, ECHO, GET, SET, INCR, INCRBY, EXISTS, DEL, COPY, EXPIRE, EXPIREAT, FLUSHDB`.

## Test

Unit test:

```bash
lein test
```

Test using Redis' official client:

```bash
pytest
```
