function sendMessage() {
    const queueEndpoint = document.getElementById('queueSelectSend').value;
    const message = document.getElementById('messageInput').value;

    if (!message) {
        alert("メッセージを入力してください");
        return;
    }

    // リクエスト実行メッセージを履歴に追加 (グレー表示)
    addMessageToHistory("メッセージの送信処理を開始しました", 'request');

    fetch(queueEndpoint, {
        method: 'POST',
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: new URLSearchParams({ msg: message })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTPエラー: ${response.status}`);
        }
        return response.json();
    })
    .then(parsedData => {
        addMessageToHistory(`送信: ${parsedData.message}`, 'response', parsedData.headers);
        messageInput.value = ''; // フォームをクリア
    })
    .catch(error => {
        addMessageToHistory("送信エラー: " + error.message, 'error');
    });

}    
