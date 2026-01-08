window.initWallet = function() {
    loadWalletInfo();
    loadTransactions();
};

function getUserId() {
    const userStr = localStorage.getItem('user_info');
    if (userStr) {
        return JSON.parse(userStr).userId;
    }
    return 1; // Fallback
}

async function loadWalletInfo() {
    const userId = getUserId();
    try {
        const res = await window.api.get('/wallet/info', { userId: userId });
        if (res.code === 200) {
            document.getElementById('walletBalance').innerText = res.data;
        }
    } catch (e) {
        console.error(e);
        alert('系统错误，请稍后重试');
    }
}

async function rechargeWallet() {
    const userId = getUserId();
    const amount = document.getElementById('walletRechargeAmount').value;
    if (!amount) return;

    try {
        const res = await window.api.postForm('/wallet/recharge', { userId: userId, amount: amount });
        if (res.code === 200) {
            alert('充值成功');
            loadWalletInfo();
            loadTransactions();
            document.getElementById('walletRechargeAmount').value = '';
        } else {
            alert('充值失败: ' + res.message);
        }
    } catch (e) {
        alert('Error');
    }
}

async function loadTransactions() {
    const userId = getUserId();
    const tbody = document.getElementById('walletTransBody');
    tbody.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';

    try {
        const res = await window.api.get('/wallet/transactions', { userId: userId });
        if (res.code === 200) {
            const list = res.data;
            if (!list || list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5">暂无记录</td></tr>';
                return;
            }

            let html = '';
            list.forEach(t => {
                const colorClass = t.transType === 'RECHARGE' ? 'text-cyan' : 'text-pink';
                const sign = t.transType === 'RECHARGE' ? '+' : '-';

                html += `
                    <tr>
                        <td>#${t.transactionId}</td>
                        <td>${t.transType}</td>
                        <td class="${colorClass}">${sign}¥${t.amount}</td>
                        <td>${t.description}</td>
                        <td>${new Date(t.createdAt).toLocaleString()}</td>
                    </tr>
                `;
            });
            tbody.innerHTML = html;
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="5">Error</td></tr>';
    }
}
