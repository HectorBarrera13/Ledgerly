// Dashboard functionality

// Theme Toggle
const themeToggle = document.getElementById('themeToggle');
const html = document.documentElement;

themeToggle.addEventListener('click', () => {
    html.classList.toggle('dark');
    const isDark = html.classList.contains('dark');
    themeToggle.querySelector('.theme-icon').textContent = isDark ? '🌙' : '☀️';
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
});

// Initialize theme from localStorage
const savedTheme = localStorage.getItem('theme') || 'dark';
if (savedTheme === 'light') {
    html.classList.remove('dark');
    themeToggle.querySelector('.theme-icon').textContent = '☀️';
}

// Section Navigation
const navItems = document.querySelectorAll('.nav-item');
const sections = document.querySelectorAll('.section');

navItems.forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        const targetSection = item.getAttribute('data-section');
        showSection(targetSection);

        // Update active nav item
        navItems.forEach(nav => nav.classList.remove('active'));
        item.classList.add('active');

        // Update page title
        const sectionName = item.querySelector('span:not(.icon)').textContent;
        document.querySelector('.page-title').textContent = sectionName;
    });
});

function showSection(sectionId) {
    sections.forEach(section => {
        section.classList.remove('active');
        if (section.id === sectionId) {
            section.classList.add('active');
        }
    });
}

// API Base URL
const API_BASE = '/api';

// Load User Count
async function loadUserCount() {
    try {
        const response = await fetch(`${API_BASE}/users/count`);
        if (response.ok) {
            const count = await response.text();
            document.getElementById('userCount').textContent = count;
        }
    } catch (error) {
        console.error('Error loading user count:', error);
        document.getElementById('userCount').textContent = 'Error';
    }
}

// Load System Info
async function loadSystemInfo() {
    try {
        const response = await fetch(`${API_BASE}/panel/system-info`);
        if (response.ok) {
            const data = await response.json();
            document.getElementById('javaVersion').textContent = data.javaVersion || '-';
            document.getElementById('osInfo').textContent = data.os || '-';
            document.getElementById('processors').textContent = data.processors || '-';
            document.getElementById('memory').textContent = data.memoryUsage || '-';
        }
    } catch (error) {
        console.error('Error loading system info:', error);
    }
}

// Load Metrics
async function loadMetrics() {
    try {
        const response = await fetch(`${API_BASE}/panel/metrics`);
        if (response.ok) {
            const data = await response.json();

            // Update CPU
            const cpuUsage = data.cpuUsage || 0;
            document.getElementById('cpuUsage').textContent = `${cpuUsage}%`;
            document.getElementById('cpuBar').style.width = `${cpuUsage}%`;

            // Update Memory
            const memoryUsage = data.memoryUsagePercent || 0;
            document.getElementById('memoryUsage').textContent = `${memoryUsage}%`;
            document.getElementById('memoryBar').style.width = `${memoryUsage}%`;

            // Update Threads
            document.getElementById('threadsCount').textContent = data.threads || '-';

            // Update Requests (placeholder)
            document.getElementById('requestsPerMin').textContent = data.requestsPerMin || '0';
        }
    } catch (error) {
        console.error('Error loading metrics:', error);
    }
}

// Load Users Table
async function loadUsers() {
    const tableBody = document.getElementById('usersTableBody');

    try {
        const response = await fetch(`${API_BASE}/panel/users`);
        if (response.ok) {
            const users = await response.json();

            if (users.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="6" class="text-center">No hay usuarios registrados</td></tr>';
                return;
            }

            tableBody.innerHTML = users.map(user => `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.name || 'N/A'}</td>
                    <td>${user.email}</td>
                    <td><span class="status-badge status-${user.active ? 'online' : 'offline'}">${user.active ? 'Activo' : 'Inactivo'}</span></td>
                    <td>${formatDate(user.createdAt)}</td>
                    <td>
                        <button class="btn-icon" onclick="viewUser('${user.id}')" title="Ver detalles">👁️</button>
                        <button class="btn-icon" onclick="editUser('${user.id}')" title="Editar">✏️</button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading users:', error);
        tableBody.innerHTML = '<tr><td colspan="6" class="text-center">Error al cargar usuarios</td></tr>';
    }
}

// Load Logs
async function loadLogs() {
    const logContainer = document.getElementById('logContainer');

    try {
        const response = await fetch(`${API_BASE}/panel/logs`);
        if (response.ok) {
            const logs = await response.json();

            if (logs.length === 0) {
                logContainer.innerHTML = '<div class="log-entry log-info"><span class="log-message">No hay logs disponibles</span></div>';
                return;
            }

            logContainer.innerHTML = logs.map(log => `
                <div class="log-entry log-${log.level.toLowerCase()}">
                    <span class="log-time">[${formatDateTime(log.timestamp)}]</span>
                    <span class="log-level">${log.level}</span>
                    <span class="log-message">${log.message}</span>
                </div>
            `).join('');

            // Scroll to bottom
            logContainer.scrollTop = logContainer.scrollHeight;
        }
    } catch (error) {
        console.error('Error loading logs:', error);
        logContainer.innerHTML = '<div class="log-entry log-error"><span class="log-message">Error al cargar logs</span></div>';
    }
}

// Refresh functions
function refreshSystemInfo() {
    loadSystemInfo();
    showNotification('Información del sistema actualizada', 'success');
}

function refreshUsers() {
    loadUsers();
    showNotification('Lista de usuarios actualizada', 'success');
}

function refreshLogs() {
    loadLogs();
    showNotification('Logs actualizados', 'success');
}

function refreshMetrics() {
    loadMetrics();
    showNotification('Métricas actualizadas', 'success');
}

// Clear Cache
function clearCache() {
    if (confirm('¿Estás seguro de que deseas limpiar la caché del sistema?')) {
        fetch(`${API_BASE}/panel/clear-cache`, { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    showNotification('Caché limpiada correctamente', 'success');
                } else {
                    showNotification('Error al limpiar la caché', 'error');
                }
            })
            .catch(error => {
                console.error('Error clearing cache:', error);
                showNotification('Error al limpiar la caché', 'error');
            });
    }
}

// Clear Logs
function clearLogs() {
    if (confirm('¿Estás seguro de que deseas limpiar los logs?')) {
        const logContainer = document.getElementById('logContainer');
        logContainer.innerHTML = '<div class="log-entry log-info"><span class="log-message">Logs limpiados</span></div>';
        showNotification('Logs limpiados', 'success');
    }
}

// User actions
function viewUser(userId) {
    alert(`Ver detalles del usuario: ${userId}`);
    // Implement user details modal
}

function editUser(userId) {
    alert(`Editar usuario: ${userId}`);
    // Implement user edit modal
}

// Utility functions
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

function showNotification(message, type = 'info') {
    // Simple notification system
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 16px 24px;
        background-color: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        z-index: 10000;
        animation: slideIn 0.3s ease;
        font-weight: 500;
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add animations to style
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Search functionality for users
const userSearch = document.getElementById('userSearch');
if (userSearch) {
    userSearch.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        const tableRows = document.querySelectorAll('#usersTableBody tr');

        tableRows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm) ? '' : 'none';
        });
    });
}

// Filter functionality for users
const userFilter = document.getElementById('userFilter');
if (userFilter) {
    userFilter.addEventListener('change', (e) => {
        const filterValue = e.target.value;
        loadUsers(); // Reload with filter (you can implement server-side filtering)
    });
}

// Log level filter
const logLevel = document.getElementById('logLevel');
if (logLevel) {
    logLevel.addEventListener('change', (e) => {
        const level = e.target.value;
        const logEntries = document.querySelectorAll('.log-entry');

        logEntries.forEach(entry => {
            if (level === 'all') {
                entry.style.display = '';
            } else {
                entry.style.display = entry.classList.contains(`log-${level}`) ? '' : 'none';
            }
        });
    });
}

// Auto-refresh intervals
let autoRefreshInterval;

function startAutoRefresh() {
    // Refresh every 30 seconds
    autoRefreshInterval = setInterval(() => {
        loadUserCount();
        loadSystemInfo();
        loadMetrics();
    }, 30000);
}

function stopAutoRefresh() {
    if (autoRefreshInterval) {
        clearInterval(autoRefreshInterval);
    }
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
    // Load initial data
    loadUserCount();
    loadSystemInfo();
    loadMetrics();

    // Start auto-refresh
    startAutoRefresh();

    // Load section-specific data based on active section
    const activeSection = document.querySelector('.section.active');
    if (activeSection) {
        switch (activeSection.id) {
            case 'users':
                loadUsers();
                break;
            case 'logs':
                loadLogs();
                break;
        }
    }
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    stopAutoRefresh();
});

// Observer for section changes to load data dynamically
const sectionObserver = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
        if (mutation.target.classList.contains('active')) {
            const sectionId = mutation.target.id;
            switch (sectionId) {
                case 'users':
                    loadUsers();
                    break;
                case 'logs':
                    loadLogs();
                    break;
                case 'metrics':
                    loadMetrics();
                    break;
            }
        }
    });
});

// Observe all sections
sections.forEach(section => {
    sectionObserver.observe(section, { attributes: true, attributeFilter: ['class'] });
});

