window.initFees = function() {
    const user = JSON.parse(localStorage.getItem('user_info'));
    const role = user.userType || 'OWNER';

    // 业主：隐藏创建账单功能
    if (role === 'OWNER') {
        const adminSection = document.getElementById('adminFeeCreateSection');
        if (adminSection) adminSection.style.display = 'none';
    }

    loadArrears();
};

async function loadArrears() {
    const tbody = document.getElementById('arrearsTableBody');
    if (!tbody) {
        console.error('arrearsTableBody element not found');
        return;
    }
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Loading...</td></tr>';

    try {
        const res = await window.api.get('/fee/arrears');
        if (res.code === 200) {
            renderArrearsTable(res.data);
        } else {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">' + res.message + '</td></tr>';
        }
    } catch (e) {
        console.error('加载欠费数据失败:', e);
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">Error loading data</td></tr>';
    }
}

function renderArrearsTable(list) {
    const tbody = document.getElementById('arrearsTableBody');
    if (!tbody) {
        console.error('arrearsTableBody element not found');
        return;
    }

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">暂无欠费记录 (No Arrears)</td></tr>';
        return;
    }

    const user = JSON.parse(localStorage.getItem('user_info'));
    const role = user.userType || 'OWNER';

    let html = '';
    list.forEach(item => {
        // 根据费用类型和角色决定按钮
        let payButton;
        if (item.fee_type === 'WATER_FEE' || item.fee_type === 'ELECTRICITY_FEE') {
            if (role === 'OWNER') {
                payButton = `<button class="sci-btn" onclick="payFeeFromCard(${item.fee_id})">从水电卡扣费</button>`;
            } else {
                payButton = `<span style="color: var(--text-dim);">请指导业主使用水电卡</span>`;
            }
        } else {
            payButton = `<button class="sci-btn" onclick="payFeeFromWallet(${item.fee_id})">钱包缴费</button>`;
        }

        html += `
            <tr>
                <td>#${item.fee_id}</td>
                <td>${item.building_no}-${item.unit_no}-${item.room_no}</td>
                <td class="text-cyan">${item.owner_name || '未入住'}</td>
                <td>${formatFeeType(item.fee_type)}</td>
                <td class="text-pink">¥${item.amount}</td>
                <td>${formatDate(item.created_at)}</td>
                <td>${payButton}</td>
            </tr>
        `;
    });
    tbody.innerHTML = html;
}

async function createFee() {
    const pId = document.getElementById('createFeePid').value;
    const type = document.getElementById('createFeeType').value;
    const amount = document.getElementById('createFeeAmount').value;

    if (!pId || !amount) {
        alert('请填写完整信息');
        return;
    }

    try {
        // /fee/create?propertyId=...&feeType=...&amount=...
        const res = await window.api.postForm('/fee/create', {
            propertyId: pId,
            feeType: type,
            amount: amount
        });

        if (res.code === 200) {
            alert('创建成功');
            loadArrears();
            document.getElementById('createFeeAmount').value = '';
        } else {
            alert('创建失败: ' + res.message);
        }
    } catch (e) {
        alert('创建失败，系统错误');
    }
}

// 钱包缴费（物业费/取暖费）
async function payFeeFromWallet(feeId) {
    if (!confirm('确认从钱包扣费缴纳此账单？系统将扣除账户余额并生成流水记录。')) return;

    try {
        const res = await window.api.postForm('/wallet/pay-fee', { feeId: feeId });
        if (res.code === 200) {
            alert('缴费成功！已从钱包扣款并生成流水记录。');
            loadArrears();
        } else {
            alert('缴费失败: ' + res.message);
        }
    } catch (e) {
        alert('缴费失败: ' + (e.message || '系统错误'));
    }
}

// 水电卡扣费（水费/电费）
async function payFeeFromCard(feeId) {
    if (!confirm('确认从水电卡扣费缴纳此账单？\n系统将从对应房产的水电卡余额中扣除。')) return;

    try {
        const res = await window.api.postForm('/fee/pay-from-card', { feeId: feeId });
        if (res.code === 200) {
            alert('水电费缴纳成功！');
            loadArrears();
        } else {
            alert('缴费失败: ' + res.message);
        }
    } catch (e) {
        alert('缴费失败: ' + (e.message || '系统错误'));
    }
}

function formatFeeType(type) {
    const map = {
        'PROPERTY_FEE': '物业费',
        'HEATING_FEE': '取暖费',
        'WATER_FEE': '水费',
        'ELECTRICITY_FEE': '电费'
    };
    return map[type] || type;
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString();
}

/**
 * 导出欠费名单CSV
 */
function exportArrears() {
    window.open('/api/export/arrears', '_blank');
}

/**
 * 导出所有账单CSV
 */
function exportAllFees() {
    window.open('/api/export/fees', '_blank');
}
