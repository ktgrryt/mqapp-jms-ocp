function addMessageToHistory(message, type = 'default', headers = null) {
    const messagesDiv = document.getElementById('messages-list');
    const newMessageDiv = document.createElement('div');
    newMessageDiv.classList.add('message');

    // メッセージの種類に応じてクラスを追加
    if (type === 'request') {
        newMessageDiv.classList.add('message-request');
    } else if (type === 'response') {
        newMessageDiv.classList.add('message-response');
    } else if (type === 'error') {
        newMessageDiv.classList.add('message-error');
    }

    // メッセージ内容
    const messageText = document.createElement('span');
    messageText.textContent = message;
    newMessageDiv.appendChild(messageText);

    // タイムスタンプを作成
    const timestamp = document.createElement('span');
    timestamp.classList.add('timestamp');
    const now = new Date();

    // ミリ秒まで表示するためのフォーマット作成
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    const milliseconds = now.getMilliseconds().toString().padStart(3, '0');

    // フォーマット例: HH:mm:ss.SSS
    timestamp.textContent = `${hours}:${minutes}:${seconds}.${milliseconds}`;
    newMessageDiv.appendChild(timestamp); // タイムスタンプをメッセージに追加

    // ヘッダーがある場合の処理
    if (headers) {
        const headerDiv = document.createElement('div');
        headerDiv.classList.add('message-headers');
        headerDiv.style.display = 'none'; // 初期は非表示

        Object.entries(headers).forEach(([key, value]) => {
            const headerItem = document.createElement('p');
            headerItem.innerHTML = `<strong>${key}:</strong> ${value}`;
            headerDiv.appendChild(headerItem);
        });

        newMessageDiv.appendChild(headerDiv);

        newMessageDiv.addEventListener('click', () => {
            headerDiv.style.display = headerDiv.style.display === 'none' ? 'block' : 'none';
        });
    }

    messagesDiv.insertBefore(newMessageDiv, messagesDiv.firstChild); // 新しいメッセージを上に追加
}
