import redis


def test_server():
    """Run a few commands to ensure that the server is running correctly."""
    r = redis.Redis(host="localhost", port=6379, db=0)
    print("Testing...")

    assert r.ping() == True
    assert r.echo("hello").decode("utf-8") == "hello"
    # assert r.echo("Hello \r\n World!").decode("utf-8") == "Hello \r\n World!"
    assert r.set("server_name", "redis") == True
    assert r.get("server_name").decode("utf-8") == "redis"

    print("Test completed!")


def test_multiple_clients():
    r1 = redis.Redis(host="localhost", port=6379, db=0)
    r2 = redis.Redis(host="localhost", port=6379, db=0)
    r3 = redis.Redis(host="localhost", port=6379, db=0)

    assert r1.ping() == True
    assert r2.ping() == True
    assert r3.ping() == True


def test_incr():
    r = redis.Redis(host="localhost", port=6379, db=0)
    r.set("cnt", 10)
    [r.incr("cnt") for _ in range(5)]
    assert r.get("cnt").decode() == "15"

    r.incr("cnt", 20)
    assert r.get("cnt").decode() == "35"


if __name__ == "__main__":
    test_server()
    test_multiple_clients()
