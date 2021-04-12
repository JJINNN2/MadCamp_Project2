// 모듈 선언
var http = require('http');
var fs = require('fs');
//var socketio = require('socket.io');
var express = require('express');
var app = express();

const hostname = "192.249.19.242";
const port = 80;

var multer = require('multer');
// 입력한 파일이 uploads/ 폴더 내에 저장됨
var upload = multer({dest:'uploads/'})

// 웹서버 생성
var server = http.createServer(app);

server.listen(port, () => {
  console.log('Server Running at '+hostname);
});

// app.get('/upload', (request, response) => {
//   fs.readFile('HTMLPage.html', (error, data) => {
//     response.writeHead(200, { 'Content-Type': 'text/html' });
//     response.end(data);
//   });
// })

// var io = socketio.listen(server);
// io.sockets.on('connection', (socket) => {
//   // message
//   var roomName = null;

//   socket.on('join', (data) => {
//     roomName = data;
//     socket.join(data);
//   })

//   socket.on('message', (data) => {
//     io.sockets.in(roomName).emit('message', data);
//     console.log(data);
//   });

  // socket.on('image', (data)=>{
  //   io.sockets.in(roomName).emit('image', data);
  //   console.log('image: ' + data);
  // })

// });

// 폼데이터 또는 폼태그를 이용해 이미지 올리면
// req.file 으로 정보가 들어오고
// dest 속성에 지정해둔 경로에 이미지가 저장됨
// upload.single("img") : 미들웨어?
// 이미지 아닌 다른 데이터는 그대로 req.body로 들어옴

// 미들웨어 upload.single("img")는 function 실행 전에 먼저 실행됨.
// 미들웨어는 사용자가 전송한 데이터 중에서 만약 파일이 포함되어 있다면,
// 그 파일을 가공해서 req객체에 file 이라는 프로퍼티를
// 암시적으로 추가도록 약속되어 있는 함수.
// 그 안의 매개변수 "img" 부분에는 form을 통해 전송되는 파일의 name 속성을 가져야 함.
app.post('/upload', upload.single('ham1'), function(req, res, next) {
  console.log('-- app.post -- ');
  try {

    // var file = './uploads' + req.file.filename;
    console.log(req.file)
    // console.log(req.headers)
    // console.log(req.host)
    console.log(req.body)
    //console.log(req.file.filename)

    //var data = req.file;
    //res.send(data.location);

    res.json({
      'success' : Boolean(true),
      'message' : 'good job bro'
    });


  } catch (error) {
    console.error(error);
    next(error);
  }
});