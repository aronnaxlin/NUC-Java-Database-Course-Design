window.initAIChat = function() {
    // Focus input
    document.getElementById('chatInput').focus();
};

function handleChatKey(e) {
    if (e.key === 'Enter') sendChat();
}

async function sendChat() {
    const input = document.getElementById('chatInput');
    const msg = input.value.trim();
    if (!msg) return;

    appendMsg(msg, 'user');
    input.value = '';

    // Show typing
    const typingId = 'typing-' + Date.now();
    const history = document.getElementById('chatHistory');
    history.insertAdjacentHTML('beforeend', `
        <div class="msg bot" id="${typingId}">
            <div class="bubble">...</div>
        </div>
    `);
    history.scrollTop = history.scrollHeight;

    try {
        const res = await window.api.post('/ai/chat', { message: msg });

        // Remove typing
        const typingEl = document.getElementById(typingId);
        if (typingEl) typingEl.remove();

        if (res.code === 200) {
            appendMsg(res.data, 'bot');
        } else {
            appendMsg('Error: ' + res.message, 'bot');
        }
    } catch (e) {
        document.getElementById(typingId)?.remove();
        appendMsg('Connection Error', 'bot');
    }
}

function appendMsg(text, type) {
    const history = document.getElementById('chatHistory');
    const div = document.createElement('div');
    div.className = `msg ${type}`;
    div.innerHTML = `<div class="bubble">${text}</div>`;
    history.appendChild(div);
    history.scrollTop = history.scrollHeight;
}
