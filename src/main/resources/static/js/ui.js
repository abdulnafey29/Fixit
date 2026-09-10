// ============================================================================
// FixIt UI Utilities: Toasts, Badges, Formatters
// ============================================================================

const UI = {
    showToast(message, type = 'success') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = 'toast-item flex items-center gap-3 px-4 py-3 rounded-xl shadow-lg text-sm font-medium border';

        if (type === 'success') {
            toast.className += ' bg-emerald-50 text-emerald-800 border-emerald-200';
            toast.innerHTML = `<svg class="w-5 h-5 text-emerald-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg> <span>${message}</span>`;
        } else if (type === 'error') {
            toast.className += ' bg-rose-50 text-rose-800 border-rose-200';
            toast.innerHTML = `<svg class="w-5 h-5 text-rose-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg> <span>${message}</span>`;
        } else {
            toast.className += ' bg-indigo-50 text-indigo-800 border-indigo-200';
            toast.innerHTML = `<svg class="w-5 h-5 text-indigo-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg> <span>${message}</span>`;
        }

        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-10px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    },

    getStatusBadge(status) {
        switch (status) {
            case 'SUBMITTED':
                return `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
                    <span class="w-1.5 h-1.5 rounded-full bg-amber-500 animate-pulse"></span> Submitted
                </span>`;
            case 'ASSIGNED':
                return `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200">
                    <span class="w-1.5 h-1.5 rounded-full bg-blue-500"></span> Assigned
                </span>`;
            case 'IN_PROGRESS':
                return `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200">
                    <span class="w-1.5 h-1.5 rounded-full bg-indigo-500 animate-spin"></span> In Progress
                </span>`;
            case 'RESOLVED':
                return `<span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Resolved
                </span>`;
            default:
                return `<span class="px-2.5 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-700">${status}</span>`;
        }
    },

    getPriorityBadge(priority) {
        switch (priority) {
            case 'CRITICAL':
                return `<span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-bold bg-red-100 text-red-800 border border-red-200">CRITICAL</span>`;
            case 'HIGH':
                return `<span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold bg-orange-100 text-orange-800 border border-orange-200">HIGH</span>`;
            case 'MEDIUM':
                return `<span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">MEDIUM</span>`;
            case 'LOW':
                return `<span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-slate-100 text-slate-600">LOW</span>`;
            default:
                return `<span class="text-xs text-slate-600">${priority}</span>`;
        }
    },

    formatDate(dateStr) {
        if (!dateStr) return 'N/A';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    async initNavbar(activeTab) {
        let user = Auth.getCurrentUser();
        if (user) {
            this.renderNavbarUser(user);
        }
        if (Auth.isAuthenticated()) {
            try {
                const freshUser = await Auth.syncSession();
                if (freshUser) {
                    this.renderNavbarUser(freshUser);
                }
            } catch (e) {
                // fall back to stored user
            }
        }
    },

    renderNavbarUser(user) {
        if (!user) return;

        const userNameEl = document.getElementById('navbar-user-name');
        const userUsernameEl = document.getElementById('navbar-user-username');
        const userRoleEl = document.getElementById('navbar-user-role');
        const userAvatarEl = document.getElementById('navbar-user-avatar');
        const techSpecEl = document.getElementById('tech-specialization');

        if (userNameEl) userNameEl.textContent = user.name || '';
        if (userUsernameEl) {
            userUsernameEl.textContent = user.username ? `@${user.username}` : '';
        }
        if (userRoleEl) {
            userRoleEl.textContent = (user.role || '').replace('ROLE_', '');
        }
        if (userAvatarEl) {
            userAvatarEl.textContent = user.name ? user.name.charAt(0).toUpperCase() : 'U';
        }
        if (techSpecEl) {
            techSpecEl.textContent = user.specialization || 'General Maintenance';
        }

        if (window.lucide && typeof lucide.createIcons === 'function') {
            lucide.createIcons();
        }
    }
};

window.UI = UI;
