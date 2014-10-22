var vertx = require('vertx');
var console = require('vertx/console');
var container = require('vertx/container');

var eb = vertx.eventBus;

var mongo_config = {
        "address": "mongodb-persistor",
        "host": "localhost",
        "port": 27017,
        "pool_size": 10,
        "db_name": "vertx"
}

container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", mongo_config);

var server = vertx.createHttpServer();

server.requestHandler(function(request) {
    console.log('An HTTP request has been received');
    var query = {
        "action": "find",
        "collection": "zips",
        "matcher": {
            "state": "AL"
        }
    };
    eb.send("mongodb-persistor", query, function (message) {
        request.response.end(JSON.stringify(message));
    });
});

server.listen(8080, "localhost");