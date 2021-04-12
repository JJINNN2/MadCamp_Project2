var mysql = require('mysql');
var express = require('express');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.listen(80, function () {
    console.log('서버 실행 중...');
});

var connection = mysql.createConnection({
    host: "localhost",
    user: "root",
    database: "project2",
    password: ""
});

app.post('/user/join', function (req, res) {
    console.log(req.body);
    var userEmail = req.body.userEmail;
    var userPwd = req.body.userPwd;
    var userName = req.body.userName;

    // 삽입을 수행하는 sql문.
    var sql = 'INSERT INTO Users (userEmail, userPwd, userName) VALUES (?, ?, ?)';
    var params = [userEmail, userPwd, userName];
    connection.connect();
	console.log('test1 ');
    // sql 문의 ?는 두번째 매개변수로 넘겨진 params의 값으로 치환된다.
    connection.query(sql, params, function (err, result, field) {
        var resultCode = 404;
        var message = '에러가 발생했습니다';
	console.log('test 2');
        if (err) {
            console.log('에러: ', err);
        } else {
            resultCode = 200;
            message = '회원가입에 성공했습니다.';
	    console.log('잘됨: '+result[0]);
        }

        res.json({
            'code': resultCode,
            'message': message
        });
    });
});

app.post('/user/login', function (req, res) {
    var userEmail = req.body.userEmail;
    var userPwd = req.body.userPwd;
    var sql = 'select * from Users where userEmail = ?';

    connection.query(sql, userEmail, function (err, result) {
        var resultCode = 404;
        var message = '에러가 발생했습니다';

        if (err) {
            console.log(err);
        } else {
            if (result.length === 0) {
                resultCode = 204;
                message = '존재하지 않는 계정입니다!';
            } else if (userPwd !== result[0].userPwd) {
                resultCode = 204;
                message = '비밀번호가 틀렸습니다!';
            } else {
                resultCode = 200;
                message = '로그인 성공! ' + result[0].userName + '님 환영합니다!';
            }
        }

        res.json({
            'code': resultCode,
            'message': message
        });
    })
});

app.get('/phone', function (req, res) {
	var name = 'null';
	var number = '01012345678';
	var sql = 'select NAME, NUMBER from phone';

	connection.query(sql, function (err, result) {
		var resultCode = 404;
		var message = '에러가 발생했습니다';

		if(err){
			console.log(err);
		}else{
			console.log('잘됨');
			name = result[0].NAME;
			number = result[1].NUMBER;

		}

		res.json({
			'name': name,
			'number': number
		});
	})
});


