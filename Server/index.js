var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.maxConnections = 2;

server.listen(8080, function(){
	console.log("Server running");
});

io.on('connection', function(socket){
	console.log("Player Connected");
	socket.emit('socketID', { id: socket.id }); //Emit socket ID event to client.
	socket.broadcast.emit('newPlayer', {id: socket.id});
	socket.on('disconnect', function(){
		console.log("Player Disconnected");
	})
});