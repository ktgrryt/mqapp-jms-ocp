function receiveMessage() {
    const queueEndpoint = document.getElementById('queueSelectRecv').value;

    // リクエスト実行メッセージを履歴に追加 (グレー表示)
    addMessageToHistory("メッセージの受信処理を開始しました", 'request');

    fetch(queueEndpoint)
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTPエラー: ${response.status}`);
        }
        return response.json();
    })
    .then(parsedData => {
        addMessageToHistory(`受信: ${parsedData.message}`, 'response', parsedData.headers);
    })
    .catch(error => {
        addMessageToHistory("受信エラー: " + error.message, 'error');
    });
    
}
