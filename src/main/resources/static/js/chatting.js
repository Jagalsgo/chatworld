// 채팅방 입장 전 로그인 유무 확인
checkLogin();

// 채팅방 퇴장
function leaveChat() {
    const userId = $('#userId').val();

    let chatMessage = {
        type: 'LEAVE',
        sender: userId,
        content: ''
    };
    stompClient.send(`/app/chat.leave/${chatId}`, {}, JSON.stringify(chatMessage));
    stompClient.disconnect();
}

// 채팅 메시지 전송
function sendMessage() {
    const userId = $('#userId').val();

    let messageInput = $('#chatMessageInput').val();
    let messageContent = messageInput.value.trim(); // 공백 제거
    let originalMessageContent = messageInput.value; // 원본 메시지 저장
    if (messageContent && stompClient) {
        let chatMessage = {
            type: 'CHAT',
            sender: userId,
            content: originalMessageContent // 원본 메시지 사용
        };
        stompClient.send(`/app/chat.send/${chatId}`, {}, JSON.stringify(chatMessage));
        messageInput.value = ''; // 입력창 비우기
    }
}

// 받은 메시지 보여주기
function showMessage(message) {
    let messageArea = $('#messageArea');
    let newMessage = document.createElement('div');
    newMessage.classList.add('message');
    newMessage.innerText = `${message.sender}: ${message.content}`;
    messageArea.appendChild(newMessage);
}

// 채팅방 입장에 로그인 유무 로그인 상태가 아니면 로그인 페이지로 이동
function checkLogin() {
    const tokenInfo = getCookie("tokenInfo");
    if(tokenInfo){
        sendAuthorizedRequest('/auth/checkLogin', 'POST', 'application/json', tokenInfo, JSON.stringify(tokenInfo),
            function(data) {
                if(data) {
                    // 로그인 상태 웹소켓 연결
                    connectWebSocket();
                } else {
                    // 로그인 안된 상태
                    alert('로그인이 필요합니다.');
                    window.location.href = '/user/loginPage';
                }
            },
            function(error) {
                console.log('error', error);
            }
        );
    } else {
        alert('로그인2이 필요합니다.');
        window.location.href = '/user/loginPage';
    }
}

// 웹소켓 연결
function connectWebSocket() {
    const socket = new SockJS('/chatWebsocket');
    stompClient = Stomp.over(socket);

    const userId = $('#userId').val();
    const chatId = $('#chatId').val();

    // 연결될 시
    stompClient.connect({}, function(frame) {
        let chatMessage = {
            type: 'JOIN',
            sender: userId,
            content: ''
        };
        // 채팅방 입장
        stompClient.send(`/app/chat.join/${chatId}`, {}, JSON.stringify(chatMessage));
        // 구독 요청
        stompClient.subscribe(`/topic/chat/${chatId}`, function(response) {
            // 서버로부터 새로운 메시지를 수신하면 호출
            let message = JSON.parse(response.body);
            showMessage(message);
        });
    });

    // 페이지가 닫힐 시 채팅방 퇴장
    window.addEventListener('beforeunload', function(event) {
        event.preventDefault();
        leaveChat();
    });
}