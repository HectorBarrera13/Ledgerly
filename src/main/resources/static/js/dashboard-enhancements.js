/**
 * MEJORAS OPCIONALES PARA EL DASHBOARD
 *
 * Este archivo contiene funcionalidades adicionales que puedes agregar al dashboard.js
 * para mejorar la experiencia visual y funcionalidad.
 */

// ============================================
// 1. GRÁFICOS CON CHART.JS
// ============================================
// Agrega esta librería en home.html antes de dashboard.js:
// <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

function initCharts() {
    // CPU Usage Chart
    const cpuCtx = document.getElementById('cpuChart');
    if (cpuCtx) {
        const cpuChart = new Chart(cpuCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'CPU %',
                    data: [],
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100
                    }
                }
            }
        });

        // Actualizar cada 5 segundos
        setInterval(() => {
            fetch('/api/panel/metrics')
                .then(res => res.json())
                .then(data => {
                    const now = new Date().toLocaleTimeString();
                    cpuChart.data.labels.push(now);
                    cpuChart.data.datasets[0].data.push(data.cpuUsage);

                    // Mantener solo los últimos 20 puntos
                    if (cpuChart.data.labels.length > 20) {
                        cpuChart.data.labels.shift();
                        cpuChart.data.datasets[0].data.shift();
                    }

                    cpuChart.update();
                });
        }, 5000);
    }
}

// ============================================
// 2. EXPORTAR DATOS A CSV
// ============================================

function exportUsersToCSV() {
    fetch('/api/panel/users')
        .then(res => res.json())
        .then(users => {
            const csv = convertToCSV(users);
            downloadCSV(csv, 'usuarios.csv');
            showNotification('Usuarios exportados correctamente', 'success');
        })
        .catch(error => {
            console.error('Error exporting users:', error);
            showNotification('Error al exportar usuarios', 'error');
        });
}

function convertToCSV(data) {
    if (data.length === 0) return '';

    const headers = Object.keys(data[0]);
    const csvRows = [];

    // Headers
    csvRows.push(headers.join(','));

    // Data
    for (const row of data) {
        const values = headers.map(header => {
            const value = row[header];
            return typeof value === 'string' && value.includes(',')
                ? `"${value}"`
                : value;
        });
        csvRows.push(values.join(','));
    }

    return csvRows.join('\n');
}

function downloadCSV(csv, filename) {
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.setAttribute('hidden', '');
    a.setAttribute('href', url);
    a.setAttribute('download', filename);
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

// ============================================
// 3. WEBSOCKETS PARA LOGS EN TIEMPO REAL
// ============================================

function connectWebSocket() {
    const ws = new WebSocket('ws://localhost:8080/ws/logs');

    ws.onopen = () => {
        console.log('WebSocket conectado');
        showNotification('Logs en tiempo real activados', 'success');
    };

    ws.onmessage = (event) => {
        const log = JSON.parse(event.data);
        addLogToContainer(log);
    };

    ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        showNotification('Error en conexión de logs', 'error');
    };

    ws.onclose = () => {
        console.log('WebSocket desconectado');
        // Intentar reconectar después de 5 segundos
        setTimeout(connectWebSocket, 5000);
    };
}

function addLogToContainer(log) {
    const logContainer = document.getElementById('logContainer');
    const logEntry = document.createElement('div');
    logEntry.className = `log-entry log-${log.level.toLowerCase()}`;
    logEntry.innerHTML = `
        <span class="log-time">[${formatDateTime(log.timestamp)}]</span>
        <span class="log-level">${log.level}</span>
        <span class="log-message">${log.message}</span>
    `;

    logContainer.insertBefore(logEntry, logContainer.firstChild);

    // Mantener solo los últimos 100 logs
    while (logContainer.children.length > 100) {
        logContainer.removeChild(logContainer.lastChild);
    }
}

// ============================================
// 4. MODAL PARA DETALLES DE USUARIO
// ============================================

function createUserModal() {
    const modal = document.createElement('div');
    modal.id = 'userModal';
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>Detalles del Usuario</h3>
                <button class="modal-close" onclick="closeUserModal()">×</button>
            </div>
            <div class="modal-body" id="userModalBody">
                <!-- Content will be loaded dynamically -->
            </div>
            <div class="modal-footer">
                <button class="btn-secondary" onclick="closeUserModal()">Cerrar</button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

function showUserDetails(userId) {
    fetch(`/api/panel/users/${userId}`)
        .then(res => res.json())
        .then(user => {
            const modal = document.getElementById('userModal');
            const body = document.getElementById('userModalBody');

            body.innerHTML = `
                <div class="user-details">
                    <div class="detail-row">
                        <span class="label">ID:</span>
                        <span class="value">${user.id}</span>
                    </div>
                    <div class="detail-row">
                        <span class="label">Nombre:</span>
                        <span class="value">${user.name}</span>
                    </div>
                    <div class="detail-row">
                        <span class="label">Email:</span>
                        <span class="value">${user.email}</span>
                    </div>
                    <div class="detail-row">
                        <span class="label">Estado:</span>
                        <span class="value">${user.active ? 'Activo' : 'Inactivo'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="label">Registrado:</span>
                        <span class="value">${formatDateTime(user.createdAt)}</span>
                    </div>
                </div>
            `;

            modal.style.display = 'flex';
        })
        .catch(error => {
            console.error('Error loading user details:', error);
            showNotification('Error al cargar detalles del usuario', 'error');
        });
}

function closeUserModal() {
    const modal = document.getElementById('userModal');
    modal.style.display = 'none';
}

// Estilos para el modal (agregar a dashboard.css)
const modalStyles = `
.modal {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    justify-content: center;
    align-items: center;
    z-index: 10000;
}

.modal-content {
    background-color: var(--bg-secondary);
    border-radius: 12px;
    max-width: 600px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
}

.modal-header {
    padding: 20px 24px;
    border-bottom: 1px solid var(--border-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-close {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: var(--text-secondary);
}

.modal-body {
    padding: 24px;
}

.modal-footer {
    padding: 20px 24px;
    border-top: 1px solid var(--border-color);
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.user-details {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px;
    background-color: var(--bg-tertiary);
    border-radius: 8px;
}
`;

// ============================================
// 5. DARK MODE AVANZADO CON PREFERENCIA DEL SISTEMA
// ============================================

function initTheme() {
    // Verificar preferencia del sistema
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const savedTheme = localStorage.getItem('theme');

    if (!savedTheme) {
        // Si no hay tema guardado, usar preferencia del sistema
        if (prefersDark) {
            document.documentElement.classList.add('dark');
        } else {
            document.documentElement.classList.remove('dark');
        }
    }

    // Escuchar cambios en la preferencia del sistema
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (!localStorage.getItem('theme')) {
            if (e.matches) {
                document.documentElement.classList.add('dark');
            } else {
                document.documentElement.classList.remove('dark');
            }
        }
    });
}

// ============================================
// 6. KEYBOARD SHORTCUTS
// ============================================

function initKeyboardShortcuts() {
    document.addEventListener('keydown', (e) => {
        // Ctrl/Cmd + K para búsqueda rápida
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            document.getElementById('userSearch')?.focus();
        }

        // Ctrl/Cmd + R para refresh
        if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
            e.preventDefault();
            const activeSection = document.querySelector('.section.active');
            if (activeSection) {
                switch (activeSection.id) {
                    case 'users': refreshUsers(); break;
                    case 'logs': refreshLogs(); break;
                    case 'metrics': refreshMetrics(); break;
                    default: refreshSystemInfo(); break;
                }
            }
        }

        // Números 1-5 para navegar entre secciones
        if (e.key >= '1' && e.key <= '5') {
            const sections = ['dashboard', 'users', 'logs', 'metrics', 'config'];
            const index = parseInt(e.key) - 1;
            if (sections[index]) {
                showSection(sections[index]);
            }
        }
    });
}

// ============================================
// 7. PROGRESS BAR PARA CARGA DE DATOS
// ============================================

function showProgressBar() {
    const progressBar = document.createElement('div');
    progressBar.className = 'progress-bar';
    progressBar.innerHTML = '<div class="progress-bar-fill"></div>';
    document.body.appendChild(progressBar);

    setTimeout(() => {
        progressBar.querySelector('.progress-bar-fill').style.width = '100%';
    }, 10);

    return progressBar;
}

function hideProgressBar(progressBar) {
    setTimeout(() => {
        progressBar.remove();
    }, 300);
}

// Estilos para progress bar (agregar a dashboard.css)
const progressBarStyles = `
.progress-bar {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background-color: var(--bg-tertiary);
    z-index: 10001;
}

.progress-bar-fill {
    height: 100%;
    width: 0;
    background: linear-gradient(90deg, var(--accent-blue), var(--accent-purple));
    transition: width 0.3s ease;
}
`;

// ============================================
// INICIALIZAR MEJORAS
// ============================================

// Descomentar las funciones que quieras usar:
document.addEventListener('DOMContentLoaded', () => {
    initCharts();
    createUserModal();
    initTheme();
    initKeyboardShortcuts();
//     connectWebSocket(); // Requiere implementación backend
});

