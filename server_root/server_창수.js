// 모듈 선언
var http = require('http');
var fs = require('fs');
var express = require('express');
var app = express();
const mysql = require('mysql');
// var bodyParser = require('body-parser');
// app.use(bodyParser.json());
app.use(express.json());

// server IP & port number
const hostname = "192.249.19.242";
const port = 80;

// 파일 업로드를 위해 multer 모듈 적용
var multer = require('multer');
const { endianness } = require('os');

// 파일의 저장경로 & 파일명 처리하기 위해
// DiskStorage 엔진이 필요. 
var storage = multer.diskStorage({
  // cb(콜백)함수를 통해 전송된 file 저장 디렉토리 설정
  destination: (req, file, cb) => {
    cb(null, 'uploads/')
  },
  // cb(콜백)함수를 통해 전송된 file 이름 설정
  filename: (req, file, cb) => {
    cb(null, file.originalname)
  }
})
var upload = multer({ storage: storage})


// mysql 연결
const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '',
  database: 'proj2'
});

app.listen(port, () => {
  console.log('Server Running at '+hostname);
});

// 앱 -> 서버 이미지 업로드
app.post('/upload', upload.single('ham1'), function(req, res, next) {

  var sql = "INSERT INTO uploads VALUES (?, ?)";
  var value = [req.file.originalname, req.file.path]

  connection.query(sql, value, (err, rows, fields) => {
    if(err) {
      console.log(req.file.originalname,': there is a same file...');
    } else {
      console.log(req.file.originalname, ': upload complete!');
    }
  });

  res.json({
    success: Boolean(true),
    message: "good job :)"
  })
})

//app.use('/users', express.static('uploads/'));

app.post('/download', function(req, res, next) {

  var sql = "SELECT * FROM uploads";

  connection.query(sql, (err, rows, fields) => {
    if(err) {
      console.log('mysql error...')

    } else {
      var nameArray = new Array()
      var pathArray = new Array()
      const num = rows.length
      console.log(num)

      for(var i=0; i<num; i++) {
          nameArray[i] = rows[i].name
          pathArray[i] = rows[i].path
      }

      res.json({
        name: nameArray,
        path: pathArray
      });
      console.log(nameArray);
      res.end();
    }
  });
});

  // fs.readFile('./uploads/1594779081800.jpg', 'base64', (err, data) => {
  //   res.json({ name: '1594779081800.jpg', img: data })
  //   res.end()
  // })

// app.post('/get_image', function (req, res, next) {
//   const path = req.query.path;
//   console.log(path);

//   app.use('/users', express.static(path));
// });

app.post('/delete', function (req, res, next) {
  console.log(req.body);

  const name = req.body.fileName
  const path = req.body.filePath
  var sql = "DELETE FROM uploads WHERE path = ?";

  var success = Boolean(true)
  var message = "delete complete"

  fs.unlink(path, (err) => {
    if(err) {
      success = Boolean(false)
      message = err
      console.log(err)

    } else {
      console.log('file delete!')
  
      connection.query(sql, path, (err, rows, fields) => {
        if(err) {
          success = Boolean(false)
          message = err
          console.log(err)
        } else {
          console.log('delete in mysql')
        }
      })
    }
  })

  res.json({
    success: success,
    message: message
  })
});

app.use('/users', express.static('uploads/'));