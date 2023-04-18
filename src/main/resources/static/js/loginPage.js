$(document).ready(function() {

    // 회원가입 시 완료 메세지
    const message = $('#message').text();
    if (message) {
        alert(message);
    }

    $('#userId').on('input', function() {
        const userId = $(this).val();
        //  아이디가 입력 될 때 input 에 데이터가 들어가 있으면 removeClass
        if (userId.trim() !== ''){
            $('#userId').removeClass('is-invalid');
            $('#userIdFeedback').text('');
            return;
        }
    });

    $('#password').on('input', function() {
            const password = $(this).val();
            //  패스워드 입력 될 때 input 에 데이터가 들어가 있으면 removeClass
            if (password.trim() !== ''){
                $('#password').removeClass('is-invalid');
                $('#passwordFeedback').text('');
                return;
            }
        });

});

function sendLoginRequest(event){
    // 폼 동작 막음
    event.preventDefault();

    const userId = $('#userId').val();
    const password = $('#password').val();

    // 각 필드의 유효성 검사
    if (userId.trim() === '') {
        $('#userId').addClass('is-invalid');
        $('#userIdFeedback').text('아이디를 입력해주세요.');
        return;
    }

    if (password.trim() === '') {
        $('#password').addClass('is-invalid');
        $('#passwordFeedback').text('비밀번호를 입력해주세요.');
        return;
    }

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
                const tokenInfo = data;
                // 기존 토큰 정보 삭제
                deleteCookie("tokenInfo");
                // 새로운 TokenInfo 를 JSON 형태의 문자열로 변환해서 쿠키에 저장
                setCookie("tokenInfo", JSON.stringify(tokenInfo), 7);
                alert('로그인 성공!');
                location.href = "/";
    		},
    		error: function(error) {
    		    console.log(error);
                alert('잘못된 아이디를 입력하셨습니다.');
    		}
    })
}