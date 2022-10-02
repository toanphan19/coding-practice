"""End-to-end test for redis.core.

Run this test by opening up the server with `lein run`, 
then `pytest` if it is installed, otherwise just `python test_server.py`.
"""
import time

import redis


r = redis.Redis(host="localhost", port=6379, db=0)


def test_PING_ECHO():
    """Run a few commands to ensure that the server is running correctly."""
    print("Testing...")

    assert r.ping() == True
    assert r.echo("hello").decode("utf-8") == "hello"
    # assert r.echo("Hello \r\n World!").decode("utf-8") == "Hello \r\n World!"


def test_GET_SET():
    assert r.set("server_name", "redis") == True
    assert r.get("server_name").decode("utf-8") == "redis"


def test_multiple_clients():
    r1 = redis.Redis(host="localhost", port=6379, db=0)
    r2 = redis.Redis(host="localhost", port=6379, db=0)
    r3 = redis.Redis(host="localhost", port=6379, db=0)

    assert r1.ping() == True
    assert r2.ping() == True
    assert r3.ping() == True


def test_INCR_INCRBY():
    r.set("cnt", 10)
    [r.incr("cnt") for _ in range(5)]
    assert r.get("cnt").decode() == "15"

    r.incr("cnt", 20)
    assert r.get("cnt").decode() == "35"


def test_DEL():
    r.set("key1", "hi")
    r.set("key2", "hi")
    assert r.delete("key1", "key2", "key3") == 2


def test_EXISTS():
    r.set("key_exist_1", 1)
    assert r.exists("key_exist_1", "key_exist_2") == 1


def test_FLUSHDB():
    r.set("key", 1)
    assert r.exists("key") == 1
    assert r.flushdb() == True
    assert r.exists("key") == 0


def test_COPY():
    r.set("key1", 1)
    assert r.copy("key1", "key2") == True
    assert r.get("key1") == r.get("key2")


def test_EXPIRE_EXPIREAT():
    r.set("key_to_expire", 1)
    assert r.exists("key_to_expire") == 1
    r.expire("key_to_expire", 1)
    time.sleep(1.001)
    assert r.exists("key_to_expire") == 0


def test_EXPIREAT():
    r.set("key_to_expire", 1)
    assert r.exists("key_to_expire") == 1
    r.expireat("key_to_expire", int(time.time() + 1))
    time.sleep(1.001)
    assert r.exists("key_to_expire") == 0
