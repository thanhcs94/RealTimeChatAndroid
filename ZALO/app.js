var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var fs = require("fs");
server.listen(process.env.PORT || 3000);

app.get("/", function(req, res){
	res.sendFile(__dirname + "/index.html");	
});
var magUsetName = [];
var count = 0;
io.sockets.on('connection', function (socket) {
count++;		
  console.log("SO CONNECT : "+ count);
  socket.on('client-gui-username', function (data){
  	var ketQua = false;
  	if(magUsetName.indexOf(data)>-1){
  		console.log("THAT BAI : DA TON TAI "+data);
  		ketQua = false;
  	}
  	else{
  		ketQua = true;
  		magUsetName.push(data);
  		socket.un= data;
  		io.sockets.emit('danh-sach-user', {danhsachuser: magUsetName});
  		console.log("THANH CONG : USERNAME = " +data);
  		console.log("SO LUONG USER : = " +data.length);
  		
  	}

  	socket.emit('ket-qua-dang-ki', { noidung: ketQua});

  });


  socket.on('client-gui-tin-chat', function (ndchat){
  	io.sockets.emit('server-gui-tin-chat', {tinchat : socket.un +" : "+ ndchat});
  });
});