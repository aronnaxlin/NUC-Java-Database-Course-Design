window.initUtility = function() {
    //
};

async function topUpCard() {
    const cardId = document.getElementById('ucCardId').value;
    const amount = document.getElementById('ucAmount').value;
    const resultDiv = document.getElementById('ucResult');
    const alertBox = document.getElementById('ucArrearsAlert');

    resultDiv.innerHTML = '';
    alertBox.style.display = 'none';

    if (!cardId || !amount) {
        resultDiv.innerHTML = '<span class="text-pink">请输入完整信息</span>';
        return;
    }

    try {
        const res = await window.api.postForm('/utility/card/topup', {
            cardId: cardId,
            amount: amount
        });

        if (res.code === 200) {
            resultDiv.innerHTML = '<span class="text-cyan">✅ 充值成功!</span>';
        } else {
            // Check if it's an interception message
            if (res.message && res.message.includes("欠费")) {
                document.getElementById('ucArrearsMsg').innerText = res.message;
                alertBox.style.display = 'block';
            } else {
                resultDiv.innerHTML = `<span class="text-pink">❌ ${res.message}</span>`;
            }
        }
    } catch (e) {
         resultDiv.innerHTML = '<span class="text-pink">System Error</span>';
    }
}

async function checkCardBalance() {
    const cardId = document.getElementById('ucSearchId').value;
    const resEl = document.getElementById('ucBalanceResult');

    if (!cardId) return;

    try {
        const res = await window.api.get(`/utility/card/${cardId}`);
        if (res.code === 200) {
            resEl.innerText = `余额 (Balance): ¥${res.data}`;
        } else {
            resEl.innerText = 'Card not found';
        }
    } catch (e) {
        resEl.innerText = 'Error';
    }
}
