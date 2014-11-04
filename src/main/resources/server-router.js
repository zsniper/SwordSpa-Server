var vertx = require('vertx');
var console = require('vertx/console');
var container = require('vertx/container');

var eb = vertx.eventBus;

var mongo_config = {
        "address": "mongodb-persistor",
        "host": "localhost",
        "port": 27017,
        "pool_size": 10,
        "db_name": "swordspa-test"
}

container.deployModule("io.vertx~mod-mongo-persistor~2.1.1", mongo_config);

var server = vertx.createHttpServer();

var routeMatcher = new vertx.RouteMatcher();

routeMatcher.get('/', function(req) {
    req.response.sendFile("views/mongo-test.html"); 
});

routeMatcher.post('/find', function (req) {

    var params = req.params();
    var option = params.option;
    var userid = params.userid;
    var entryid = params.entryid;
    
    var query = {
        action: "find",
        collection: "users"
    };
    
    if (option === "entries" && userid.length > 0) {
        query.matcher = {
            "_id": userid
        };
        query.keys = {
            "profiles.personalId.entries": 1
        };
        
        if (entryid.length > 0) {
            var keyName = "profiles.personalId.entries." + entryid;
            query.keys = {
                keyName: 1
            };
        }
        
    }
    
    console.log(JSON.stringify(query));
    
    eb.send("mongodb-persistor", query, function (message) {
        req.response.end(JSON.stringify(message));
    });
});


routeMatcher.post('/insert', function (req) {

    var params = req.formAttributes();
    var name = params.get('name');
    var username = params.get('username');
    var password = params.get('password');

    console.log('name is ' + name + ', username is ' + username);
    
    var query = {
        action: "save",
        collection: "users",
    };
    
    query.document = {
        "name": name,
        "username": username,
        "password": password,
    };
    
    
    console.log(JSON.stringify(query));
    
    eb.send("mongodb-persistor", query, function (message) {
        req.response.end(JSON.stringify(message));
    });
});

server.requestHandler(routeMatcher).listen(8080, "localhost");