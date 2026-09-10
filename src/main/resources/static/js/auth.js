// ============================================================================
// FixIt Authentication & Session Helper
// ============================================================================

const Auth = {
    async login(username, password) {
        const res = await API.post('/auth/login', { username, password });
        if (res.success && res.data) {
            API.setToken(res.data.token);
            API.setUser(res.data);
            return res.data;
        }
        throw new Error(res.message || 'Login failed');
    },

    async register(name, username, email, password, phone, role, specialization) {
        const res = await API.post('/auth/register', { name, username, email, password, phone, role, specialization });
        if (res.success && res.data) {
            API.setToken(res.data.token);
            API.setUser(res.data);
            return res.data;
        }
        throw new Error(res.message || 'Registration failed');
    },

    logout() {
        try {
            API.clearToken();
            localStorage.clear();
            sessionStorage.clear();
        } catch (e) {
            console.error('Logout error:', e);
        }
        window.location.replace('/login.html');
    },

    isAuthenticated() {
        return !!API.getToken();
    },

    getCurrentUser() {
        return API.getUser();
    },

    async syncSession() {
        const token = API.getToken();
        if (!token) return null;
        try {
            const res = await API.get('/users/me');
            if (res && res.success && res.data) {
                API.setUser(res.data);
                return res.data;
            }
        } catch (e) {
            console.warn('Session sync failed:', e);
            API.clearToken();
            const path = window.location.pathname;
            if (!path.includes('login.html') && !path.includes('register.html') && path !== '/' && !path.endsWith('index.html')) {
                window.location.replace('/login.html?expired=true');
            }
        }
        return null;
    },

    normalizeRole(role) {
        if (!role) return '';
        const r = String(role).toUpperCase().trim();
        return r.startsWith('ROLE_') ? r : `ROLE_${r}`;
    },

    redirectBasedOnRole(role) {
        const norm = this.normalizeRole(role);
        const currentPath = window.location.pathname;

        let target = '/student/dashboard.html';
        if (norm === 'ROLE_ADMIN') {
            target = '/admin/dashboard.html';
        } else if (norm === 'ROLE_TECHNICIAN') {
            target = '/technician/dashboard.html';
        }

        // GUARD: Never reload if already on the target page or within portal directory
        if (currentPath === target) return;
        if (norm === 'ROLE_ADMIN' && currentPath.startsWith('/admin/')) return;
        if (norm === 'ROLE_TECHNICIAN' && currentPath.startsWith('/technician/')) return;
        if (norm === 'ROLE_STUDENT' && currentPath.startsWith('/student/')) return;

        window.location.replace(target);
    },

    requireRole(expectedRole) {
        const token = API.getToken();
        const user = API.getUser();

        if (!token || !user) {
            window.location.replace('/login.html');
            return false;
        }

        const userRole = this.normalizeRole(user.role);
        const targetRole = this.normalizeRole(expectedRole);

        if (targetRole && userRole !== targetRole) {
            console.warn(`Role mismatch: Expected ${targetRole}, got ${userRole}`);
            this.redirectBasedOnRole(userRole);
            return false;
        }

        return true;
    }
};

window.Auth = Auth;
