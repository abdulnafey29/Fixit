// ============================================================================
// FixIt Centralized API Client with JWT Header Injection
// ============================================================================

const API = {
    baseUrl: '/api',

    getToken() {
        return localStorage.getItem('fixit_token');
    },

    setToken(token) {
        localStorage.setItem('fixit_token', token);
    },

    clearToken() {
        localStorage.removeItem('fixit_token');
        localStorage.removeItem('fixit_user');
    },

    getUser() {
        const u = localStorage.getItem('fixit_user');
        return u ? JSON.parse(u) : null;
    },

    setUser(user) {
        localStorage.setItem('fixit_user', JSON.stringify(user));
    },

    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const headers = options.headers || {};
        const token = this.getToken();

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        if (!(options.body instanceof FormData) && !headers['Content-Type']) {
            headers['Content-Type'] = 'application/json';
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle session expiry or unauthorized
            if (response.status === 401) {
                this.clearToken();
                if (!window.location.pathname.includes('login.html') && !window.location.pathname.includes('register.html')) {
                    window.location.href = '/login.html?expired=true';
                }
                throw new Error('Session expired. Please log in again.');
            }

            const data = await response.json().catch(() => null);

            if (!response.ok) {
                const message = (data && data.message) ? data.message : `HTTP error ${response.status}`;
                throw new Error(message);
            }

            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    },

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    },

    postMultipart(endpoint, formData) {
        return this.request(endpoint, {
            method: 'POST',
            body: formData
        });
    },

    put(endpoint, body) {
        return this.request(endpoint, {
            method: 'PUT',
            body: body ? JSON.stringify(body) : undefined
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

window.API = API;
