(function () {
    'use strict';

    function $(id) {
        return document.getElementById(id);
    }

    // Логика управления боковым меню (Sidebar)
    function openSidebar() {
        $('userSidebar').classList.add('open');
        $('sidebarOverlay').classList.add('show');
    }

    function closeSidebar() {
        $('userSidebar').classList.remove('open');
        $('sidebarOverlay').classList.remove('show');
    }

    function onDocumentClick(event) {
        // Открытие Sidebar
        var trigger = event.target.closest('.menu-trigger-btn');
        if (trigger) {
            openSidebar();
            return;
        }

        // Закрытие Sidebar по клику на затемненный фон
        if (event.target.id === 'sidebarOverlay') {
            closeSidebar();
            return;
        }

        // Закрытие Sidebar по клику на крестик внутри меню
        var sidebarClose = event.target.closest('.close-sidebar-btn');
        if (sidebarClose && event.target.closest('#userSidebar')) {
            closeSidebar();
            return;
        }
    }

    document.addEventListener('click', onDocumentClick);
})();