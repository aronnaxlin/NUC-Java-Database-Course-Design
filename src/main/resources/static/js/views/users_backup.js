/**
 * 用户管理页面初始化
 * 加载所有用户列表
 */
window.initUsers = function() {
    loadUsers();
};

/**
 * 加载所有用户
 */
async function loadUsers() {
    const tbody = document.getElementById('usersTableBody');
    const loading = '<tr><td colspan="7" style="text-align:center;">加载中...</td></tr>';
    tbody.innerHTML = loading;

    try {
        const res = await window.api.get('/user/list');
        if (res.code === 200) {
            renderUserTable(res.data);
        } else {
            alert('加载失败: ' + res.message);
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">加载失败</td></tr>';
        }
    } catch (e) {
        console.error('加载错误:', e);
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">系统错误</td></tr>';
    }
}

/**
 * 搜索用户
 */
async function searchUsers() {
    const input = document.getElementById('userSearchInput');
    const keyword = input ? input.value.trim() : '';

    if (keyword.length > 50) {
        alert('搜索关键词过长，请控制在50字以内');
        return;
    }

    const tbody = document.getElementById('usersTableBody');
    const loading = '<tr><td colspan="7" style="text-align:center;">搜索中...</td></tr>';
    tbody.innerHTML = loading;

    try {
        const res = await window.api.get('/user/search', { keyword: keyword });
        if (res.code === 200) {
            renderUserTable(res.data);
        } else {
            alert('搜索失败: ' + res.message);
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">搜索失败</td></tr>';
        }
    } catch (e) {
        console.error('搜索错误:', e);
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--accent-pink);">系统错误</td></tr>';
    }
}

/**
 * 渲染用户表格
 */
function renderUserTable(list) {
    const tbody = document.getElementById('usersTableBody');

    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">暂无数据</td></tr>';
        return;
    }

    let html = '';
    list.forEach(user => {
        html += `
            <tr>
                <td>#${user.userId}</td>
                <td class="text-cyan">${user.userName || '-'}</td>
                <td>${user.name || '-'}</td>
                <td>${user.gender || '-'}</td>
                <td>${user.phone || '-'}</td>
                <td>${translateUserType(user.userType)}</td>
                <td>
                    <button class="sci-btn" onclick="showEditUserModal(${user.userId})" style="margin-right: 5px;">编辑</button>
                    <button class="sci-btn danger" onclick="deleteUser(${user.userId})">删除</button>
                </td>
            </tr>
        `;
    });
    tbody.innerHTML = html;
}

/**
 * 显示新建用户模态框
 */
function showCreateUserModal() {
    document.getElementById('userModalTitle').innerText = '新建用户';
    document.getElementById('userModalId').value = '';
    document.getElementById('userModalUserName').value = '';
    document.getElementById('userModalPassword').value = '';
    document.getElementById('userModalName').value = '';
    document.getElementById('userModalGender').value = '';
    document.getElementById('userModalPhone').value = '';
    document.getElementById('userModalUserType').value = '';
    document.getElementById('userModal').style.display = 'block';
}

/**
 * 显示编辑用户模态框
 */
async function showEditUserModal(userId) {
    try {
        const res = await window.api.get(`/user/${userId}`);
        if (res.code === 200) {
            const user = res.data;
            document.getElementById('userModalTitle').innerText = '编辑用户';
            document.getElementById('userModalId').value = user.userId;
            document.getElementById('userModalUserName').value = user.userName;
            document.getElementById('userModalPassword').value = ''; // 不显示密码
            document.getElementById('userModalName').value = user.name;
            document.getElementById('userModalGender').value = user.gender || '';
            document.getElementById('userModalPhone').value = user.phone || '';
            document.getElementById('userModalUserType').value = user.userType || '';
            document.getElementById('userModal').style.display = 'block';
        } else {
            alert('获取用户详情失败: ' + res.message);
        }
    } catch (e) {
        console.error('获取用户详情错误:', e);
        alert('系统错误');
    }
}

/**
 * 关闭用户模态框
 */
function closeUserModal() {
    document.getElementById('userModal').style.display = 'none';
}

/**
 * 保存用户（创建或更新）
 */
async function saveUser() {
    const userId = document.getElementById('userModalId').value;
    const userName = document.getElementById('userModalUserName').value.trim();
    const password = document.getElementById('userModalPassword').value.trim();
    const name = document.getElementById('userModalName').value.trim();
    const gender = document.getElementById('userModalGender').value;
    const phone = document.getElementById('userModalPhone').value.trim();
    const userType = document.getElementById('userModalUserType').value;

    // 表单验证
    if (!userName) {
        alert('请输入用户名');
        return;
    }
    if (!name) {
        alert('请输入真实姓名');
        return;
    }
    if (!userType) {
        alert('请选择用户类型');
        return;
    }

    const userData = {
        userName: userName,
        name: name,
        gender: gender,
        phone: phone,
        userType: userType
    };

    try {
        let res;
        if (userId) {
            // 更新用户
            userData.userId = parseInt(userId);
            if (password) {
                userData.password = password; // 仅在填写了密码时才更新
            }
            res = await window.api.put('/user/update', userData);
        } else {
            // 创建新用户
            if (!password) {
                alert('请输入密码');
                return;
            }
            userData.password = password;
            res = await window.api.post('/user/create', userData);
        }

        if (res.code === 200) {
            alert(userId ? '更新成功' : '创建成功');
            closeUserModal();
            loadUsers(); // 重新加载列表
        } else {
            alert('操作失败: ' + res.message);
        }
    } catch (e) {
        console.error('保存用户错误:', e);
        alert('系统错误');
    }
}

/**
 * 删除用户
 */
async function deleteUser(userId) {
    if (!confirm('确认删除该用户？此操作不可恢复！')) {
        return;
    }

    try {
        const res = await window.api.delete(`/user/${userId}`);
        if (res.code === 200) {
            alert('删除成功');
            loadUsers(); // 重新加载列表
        } else {
            alert('删除失败: ' + res.message);
        }
    } catch (e) {
        console.error('删除用户错误:', e);
        alert('系统错误');
    }
}

/**
 * 导出用户CSV
 */
function exportUsers() {
    // 直接打开导出URL，浏览器会自动下载
    window.open('/api/export/users', '_blank');
}

/**
 * 翻译用户类型
 */
function translateUserType(type) {
    const map = { 'ADMIN': '管理员', 'OWNER': '业主' };
    return map[type] || type;
}
