$(document).ready(function(){
    checkLoginView();
})

function setCookie(name, value, expDays) {
    const d = new Date();
    d.setTime(d.getTime() + (expDays * 24 * 60 * 60 * 1000));
    const expires = "expires=" + d.toUTCString();
    document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        const jsonValue = parts.pop().split(';').shift();
        // JSON.parse 함수로 객체로 변환
        if(jsonValue){
            return JSON.parse(jsonValue);
        }
    }
    return null;
}

function deleteCookie(name) {
    // 즉시 쿠키 만료
    const expires = "expires=Thu, 01 Jan 1970 00:00:00 UTC";
    document.cookie = name + "=" + ";" + expires + ";path=/";
}

// 로그인 유무에 따른 뷰 보여주기
function checkLoginView(){
    // 쿠키로부터 TokenInfo 객체를 가져옴
    const tokenInfo = getCookie("tokenInfo");

    // 로그인 유무 체크 후 그에 따른 뷰 보여주기
    if(tokenInfo){
        sendAuthorizedRequest('/auth/checkLogin', 'POST', 'application/json', tokenInfo, JSON.stringify(tokenInfo),
            function(data) {
                if(data) /* 로그인 유 */ {
                    loginStatus();
                }else /* 로그인 무 */ {
                    logoutStatus();
                }
            },
            function(error) {
                logoutStatus();
                console.log('error', error);
            }
        );
    }else{
        logoutStatus();
    }
}

// checkLogin 로그인 상태
function loginStatus(){
    // 로그인 버튼 숨기기
    $("#loginBtn").addClass("d-none");
    // 로그아웃 버튼 보이기
    $("#logoutBtn").removeClass("d-none");
}

// checkLogin 로그아웃 상태
function logoutStatus(){
    // 로그아웃 버튼 숨기기
    $("#logoutBtn").addClass("d-none");
    // 로그인 버튼 보이기
    $("#loginBtn").removeClass("d-none");
}

// 로그아웃
function logout(){
    // 쿠키로부터 TokenInfo 객체를 가져옴
    const tokenInfo = getCookie("tokenInfo");
    if(tokenInfo){
        sendAuthorizedRequest('/auth/logout', 'POST', 'application/json', tokenInfo, JSON.stringify(tokenInfo),
            function(data) {
                console.log('logout js success');
                // 쿠키 삭제
                deleteCookie("tokenInfo");
                location.reload();
            },
            function(error) {
                alert("로그인 상태가 아닙니다");
            }
        );
    }else{
        alert('로그인 상태가 아닙니다.');
    }
}

function sendAuthorizedRequest(url, method, contentType, tokenInfo, requestData, successCallback, errorCallback){

    $.ajax({
        url: url,
        type: method,
        contentType: contentType,
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
                sendAuthorizedRequest(url, method, contentType, getCookie("tokenInfo"), requestData, successCallback, errorCallback);
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