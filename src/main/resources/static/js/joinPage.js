$(document).ready(function() {

    const userIdRegex = /^[a-zA-Z0-9]{4,12}$/;
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])?(?=.*[0-9]).{8,16}$/;
    const nicknameRegex = /^[a-zA-Z가-힣0-9]{2,10}$/;
    // 중복 검사 결과가 저장될 변수
    let userIdInvalid = true;
    let passwordInvalid = true;
    let nicknameInvalid = true;

    $('#userId').on('input', function() {
      const userId = $(this).val();

      // 아이디 정규식 검사
      if (!userIdRegex.test(userId)) {
          userIdInvalid = true;
          $('#userId').addClass('is-invalid');
          $('#userIdFeedback').text('4~12자의 영문 대소문자와 숫자만 사용 가능합니다.');
        } else {
            $('#userId').removeClass('is-invalid');
            $('#userIdFeedback').text('');

            // 아이디 중복 검사
            $.ajax({
              url: '/user/checkUserIdDuplication',
              method: 'GET',
              data: { "userId": userId },
              success: function(response) {
                 userIdInvalid = response.duplicated;
                if (userIdInvalid) {
                  $('#userId').addClass('is-invalid');
                  $('#userIdFeedback').text('이미 사용중인 아이디입니다.');
                } else {
                  console.log('not duplicated');
                  $('#userId').removeClass('is-invalid');
                  $('#userIdFeedback').text('');
                }
              },
              error: function(xhr, status, error) {
                console.error('Ajax request error:', error);
              }
            });
      }

      toggleSubmitButton();

    });

    $('#password').on('input', function() {
      const password = $(this).val();

      // 패스워드 정규식 검사
      if (!passwordRegex.test(password)) {
        passwordInvalid = true;
        $('#password').addClass('is-invalid');
        $('#passwordFeedback').text('8~16자의 영문 대소문자, 숫자, 특수문자만 사용 가능하며 대소문자, 숫자가 최소 들어가야 합니다.');
      } else {
        passwordInvalid = false;
        $('#password').removeClass('is-invalid');
        $('#passwordFeedback').text('');
      }

      toggleSubmitButton();

    });

    $('#nickname').on('input', function() {

      const nickname = $(this).val();

      // 닉네임 정규식 검사
      if (!nicknameRegex.test(nickname)) {
          nicknameInvalid = true;
          $('#nickname').addClass('is-invalid');
          $('#nicknameFeedback').text('2~10자의 영문 대소문자, 한글, 숫자만 사용 가능합니다.');
        } else {
            $('#nickname').removeClass('is-invalid');
            $('#nicknameFeedback').text('');

            // 닉네임 중복 검사
            $.ajax({
              url: '/user/checkNicknameDuplication',
              method: 'GET',
              data: { "nickname": nickname },
              success: function(response) {
                nicknameInvalid = response.duplicated;
                if (nicknameInvalid) {
                  $('#nickname').addClass('is-invalid');
                  $('#nicknameFeedback').text('이미 사용중인 닉네임입니다.');
                } else {
                  $('#nickname').removeClass('is-invalid');
                  $('#nicknameFeedback').text('');
                }
                toggleSubmitButton();
              },
              error: function(xhr, status, error) {
                console.error('Ajax request error:', error);
              }
            });
          }

        toggleSubmitButton();

    });

    // submit 버튼 활성화/비활성화 함수
    function toggleSubmitButton() {
        if (userIdInvalid || passwordInvalid || nicknameInvalid) {
          $('#submitBtn').prop('disabled', true);
        } else {
          $('#submitBtn').prop('disabled', false);
        }
    }

    // submitBtn 클릭 시
    $('#joinForm').submit(function(event) {
      // 폼 동작 막음
      event.preventDefault();

      const userId = $('#userId').val();
      const password = $('#password').val();
      const passwordCheck = $('#passwordCheck').val();
      const nickname = $('#nickname').val();

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

      if (passwordCheck.trim() === '') {
        $('#passwordCheck').addClass('is-invalid');
        $('#passwordCheckFeedback').text('비밀번호 확인을 입력해주세요.');
        return;
      }

      if (password !== passwordCheck) {
        $('#passwordCheck').addClass('is-invalid');
        $('#passwordCheckFeedback').text('비밀번호가 일치하지 않습니다.');
        return;
      }

      if (nickname.trim() === '') {
        $('#nickname').addClass('is-invalid');
        $('#nicknameFeedback').text('닉네임을 입력해주세요.');
        return;
      }

      // 서버에 회원가입 요청 보내기
      $.ajax({
        url: '/user/join',
        method: 'POST',
        contentType: "application/json",
        data: JSON.stringify({
            "userId": userId,
            "password": password,
            "nickname": nickname
        }),
        success: function(response) {
          console.log(response);
          alert('회원가입이 완료되었습니다!');
          window.location.href = '/user/loginPage';
        },
        error: function(xhr, status, error) {
          console.error('Ajax request error:', error);
        }
      });

    });

});