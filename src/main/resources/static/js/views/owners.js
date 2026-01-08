window.initOwners = function() {
    searchOwners(); // Load all on init
};

async function searchOwners() {
    const input = document.getElementById('ownerSearchInput');
    const keyword = input ? input.value // Default empty string handled by backend effectively
                  : '';

    const loading = '<tr><td colspan="5" style="text-align:center;">Loading...</td></tr>';
    document.getElementById('ownersTableBody').innerHTML = loading;

    try {
        const res = await window.api.get('/owner/search', { keyword: keyword });
        if (res.code === 200) {
            renderOwnerTable(res.data);
        } else {
            alert('查询失败: ' + res.message);
        }
    } catch (e) {
        console.error(e);
        document.getElementById('ownersTableBody').innerHTML = '<tr><td colspan="5" style="text-align:center;color:var(--accent-pink);">Error loading data</td></tr>';
    }
}

function renderOwnerTable(list) {
    const tbody = document.getElementById('ownersTableBody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">暂无数据 (No Data)</td></tr>';
        return;
    }

    let html = '';
    list.forEach(owner => {
        // Owner object mapping from Map<String, Object> or DTO
        // Typically keys from JDBC map are lower_case or CamelCase depending on mapper.
        // My UserDAO uses BeanPropertyRowMapper or custom?
        // Let's check UserDAO/Service return. Service returns Map for search?
        // OwnerService.searchOwners returns List<Map<String, Object>>.
        // Keys: "user_id", "user_name", "phone", "gender", "properties" (count)

        // Actually OwnerServiceImpl returns full User/Owner logic?
        // check OwnerServiceImpl.searchOwners.
        // It injects properies info.

        html += `
            <tr>
                <td>#${owner.user_id}</td>
                <td class="text-cyan">${owner.name}</td>
                <td>${owner.phone}</td>
                <td>${owner.gender || '-'}</td>
                <td>
                    <button class="sci-btn" onclick="viewOwnerDetail(${owner.user_id})">详情</button>
                </td>
            </tr>
        `;
    });
    tbody.innerHTML = html;
}

async function viewOwnerDetail(id) {
    const modal = document.getElementById('ownerDetailModal');
    const content = document.getElementById('ownerDetailContent');

    modal.style.display = 'block';
    content.innerHTML = 'Loading details...';

    try {
        // /getOwnerWithProperties(id)
        const res = await window.api.get(`/owner/${id}`);
        if (res.code === 200) {
            const data = res.data;
            const owner = data; // Data is the flat owner map
            const properties = data.properties;

            let propsHtml = '';
            if (properties && properties.length > 0) {
                propsHtml = '<ul class="prop-list">' + properties.map(p => `
                    <li>
                        ${p.buildingNo}栋 ${p.unitNo}单元 ${p.roomNo}室
                        (${p.area}㎡) - <span class="text-cyan">${translateStatus(p.pstatus)}</span>
                    </li>
                `).join('') + '</ul>';
            } else {
                propsHtml = '<p class="text-dim">暂无房产</p>';
            }

            content.innerHTML = `
                <div class="row">
                    <div class="col-6">
                        <p><strong>ID:</strong> ${owner.user_id}</p>
                        <p><strong>姓名:</strong> ${owner.name}</p>
                        <p><strong>电话:</strong> ${owner.phone}</p>
                        <p><strong>性别:</strong> ${owner.gender}</p>
                    </div>
                    <div class="col-6">
                        <h4>名下房产</h4>
                        ${propsHtml}
                    </div>
                </div>
            `;
        }
    } catch (e) {
        content.innerText = '加载失败';
    }
}

function translateStatus(status) {
    const map = { 'SOLD': '已售', 'UNSOLD': '待售' };
    return map[status] || status;
}

function closeOwnerModal() {
    document.getElementById('ownerDetailModal').style.display = 'none';
}
