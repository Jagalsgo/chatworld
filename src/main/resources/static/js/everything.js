function setCookie(name, value, expDays) {
  const d = new Date();
  d.setTime(d.getTime() + (expDays * 24 * 60 * 60 * 1000));
  const expires = "expires=" + d.toUTCString();
  document.cookie = name + "=" + value + ";" + expires + ";path=/;HttpOnly";
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        // JSON.parse 함수로 객체로 변환
        return JSON.parse(parts.pop().split(';').shift());
    }
}

function deleteCookie(name) {
    // 즉시 쿠키 만료
    const expires = "expires=Thu, 01 Jan 1970 00:00:00 UTC";
    document.cookie = name + "=" + ";" + expires + ";path=/;HttpOnly";
}

// 토큰을 포함한 요청 예시
function sendLoginRequest2(event){
    // 폼 동작 막음
    event.preventDefault();
    console.log('BBB');

    // 쿠키로부터 TokenInfo 객체를 가져옴
    const tokenInfo = getCookie("tokenInfo");

    sendAuthorizedRequest('/user/login2', 'POST', tokenInfo, null,
        function(data) {
          console.log('Success:', data);
        },
        function(error) {
            if (confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
                  window.location.href = "/user/loginPage";
            }
        }
    );
}

// 로그아웃
function logout(){

    // 쿠키로부터 TokenInfo 객체를 가져옴
    const tokenInfo = getCookie("tokenInfo");
    // 쿠키 삭제
    deleteCookie("tokenInfo");
    location.reload();
        sendAuthorizedRequest('/user/logout', 'POST', tokenInfo, tokenInfo,
        function(data) {
            // 쿠키 삭제
            deleteCookie("tokenInfo");
            location.reload();
        },
        function(error) {
            alert(error);
        }
    );

}

function sendAuthorizedRequest(url, method, tokenInfo, requestData, successCallback, errorCallback){

    // 로그인 되어 있는지 tokenInfo 확인
    isTokenInfo(tokenInfo);

    $.ajax({
        url: url,
        type: method,
        data: requestData,
        beforeSend: function(xhr) {
          xhr.setRequestHeader('Authorization', 'Bearer ' + tokenInfo.accessToken);
        },
        success: function(data) /* access 토큰 인증 성공 */ {
            successCallback(data);
        },
        error: function(xhr, status, error) /* access 토큰 인증 실패 */ {
            // handleAuthError 호출
            handleAuthError(xhr, status, error, tokenInfo, function() /* handleAuthError retryCallback */ {
                // refresh 토큰을 통해 받은 access 토큰을 통해 다시 요청
                sendAuthorizedRequest(url, method, getCookie("tokenInfo"), requestData, successCallback, errorCallback);
            }, errorCallback);
        }
    })
}

function handleAuthError(xhr, status, error, tokenInfo, retryCallback, errorCallback){
    // 인증 오류(401), 토큰 refreshToken 유무
    if (xhr.status === 401 && tokenInfo.refreshToken){
        // requestNewToken 호출
        requestNewToken(tokenInfo, function(newTokenInfo){
            // 새로운 토큰 정보를 쿠키에 저장
            setCookie("tokenInfo", JSON.stringify(newTokenInfo), newTokenInfo.expires_in);
            // 새로운 access 토큰을 통해 다시 요청
            retryCallback();
        }, errorCallback); /* requestNewToken errorCallback refresh 토큰 만료, 토큰 정보가 올바르지 않음 */
    }else{
        errorCallback(error);
    }
}

function requestNewToken(tokenInfo, successCallback, errorCallback){
    // 새로운 access 토큰 요청
    $.ajax({
        url: '/user/refreshToken',
        type: 'POST',
        data:{
            "refreshToken": tokenInfo.refreshToken
        },
        success: function(newTokenInfo){
            successCallback(newTokenInfo);
        },
        error: function(xhr, status, error){
            errorCallback(error);
        }
    });
}

function isTokenInfo(tokenInfo) {
  // tokenInfo 유무 확인
  if (!tokenInfo) {
    if (confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
      window.location.href = "/user/loginPage";
    }
  }
}