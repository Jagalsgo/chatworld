$(document).ready(function() {
    // 회원가입 시 완료 메세지
    const message = $('#message').text();
    if (message) {
        alert(message);
    }
});

function sendLoginRequest(event){
    // 폼 동작 막음
    event.preventDefault();
    console.log('AAA');

    // 유저 아이디, 패스워드
    let inputData = {
        "userId" : $('#userId').val(),
        "password" : $('#password').val()
    }

    $.ajax({
    		url: "/user/login",
    		type: "POST",
    		contentType: "application/json",
            data: JSON.stringify(inputData),
    		success: function(data) {
    			alert('로그인 성공!');
    			const tokenInfo = data;
    			// TokenInfo 를 JSON 형태의 문자열로 변환해서 쿠키에 저장
    			setCookie("tokenInfo", JSON.stringify(tokenInfo), 7);
    			location.href = "/";
    		},
    		error: function(error) {
    			console.log(error);
    		}
    	})

}