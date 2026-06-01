document.addEventListener('DOMContentLoaded', () => {
    'use strict';

    const sidebar = document.getElementById('userSidebar');
    const overlay = document.getElementById('sidebarOverlay');

    if (!sidebar || !overlay) return;

    const openSidebar = () => {
        sidebar.classList.add('open');
        overlay.classList.add('show');
    };

    const closeSidebar = () => {
        sidebar.classList.remove('open');
        overlay.classList.remove('show');
    };

    document.addEventListener('click', (event) => {
        // Открытие Sidebar
        if (event.target.closest('.menu-trigger-btn')) {
            openSidebar();
            return;
        }

        // Закрытие по клику на затемненный фон overlay
        if (event.target === overlay) {
            closeSidebar();
            return;
        }

        // Закрытие по клику на крестик внутри меню
        if (event.target.closest('.close-sidebar-btn')) {
            closeSidebar();
            return;
        }
    });
});