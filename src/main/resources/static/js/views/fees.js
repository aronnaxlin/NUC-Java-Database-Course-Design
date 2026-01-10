window.initFees = function() {
    loadArrears();
};

async function loadArrears() {
    const tbody = document.getElementById('arrearsTableBody');
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">Loading...</td></tr>';

    try {
        const res = await window.api.get('/fee/arrears');
        if (res.code === 200) {
            renderArrearsTable(res.data);
        } else {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">' + res.message + '</td></tr>';
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">Error loading data</td></tr>';
    }
}

function renderArrearsTable(list) {
    const tbody = document.getElementById('arrearsTableBody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">暂无欠费记录 (No Arrears)</td></tr>';
        return;
    }

    let html = '';
    list.forEach(item => {
        html += `
            <tr>
                <td>#${item.fee_id}</td>
                <td>${item.building_no}-${item.unit_no}-${item.room_no}</td>
                <td class="text-cyan">${item.owner_name || '未入住'}</td>
                <td>${formatFeeType(item.fee_type)}</td>
                <td class="text-pink">¥${item.amount}</td>
                <td>${formatDate(item.created_at)}</td>
                <td>
                    <button class="sci-btn" onclick="payFee(${item.fee_id})">线下缴费 (Pay Offline)</button>
                </td>
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
            // Check role to toggle Create Bill
            loadArrears();
            document.getElementById('createFeeAmount').value = '';
        } else {
            alert('创建失败: ' + res.message);
        }
    } catch (e) {
        alert('创建失败，系统错误');
    }
}

async function payFee(feeId) {
    if (!confirm('确认该住户已线下完成缴费？(Confirm Payment)')) return;

    try {
        const res = await window.api.post(`/fee/pay/${feeId}`);
        if (res.code === 200) {
            alert('操作成功');
            loadArrears(); // Refresh
        } else {
            alert('操作失败: ' + res.message);
        }
    } catch (e) {
        alert('缴费失败，系统错误');
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
