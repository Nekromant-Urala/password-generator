(function () {
    'use strict';

    function getBasePath() {
        var raw = document.body && document.body.getAttribute('data-base-path');
        if (raw) {
            return raw;
        }
        return '/';
    }

    var basePath = getBasePath();

    function $(id) {
        return document.getElementById(id);
    }

    function openCreateModal() {
        $('modalTitle').innerText = 'Новая запись';
        $('entryForm').action = basePath + 'passwords/create';
        $('modalService').value = '';
        $('modalLogin').value = '';
        $('modalPassword').value = '';
        $('modalPassword').required = false;
        $('modalPasswordLabel').innerText = 'Пароль (необязательно)';
        $('modalDesc').value = '';

        // Добавляем очистку поля профиля
        var profileField = $('modalProfileName');
        if (profileField) {
            profileField.value = 'default';
        }

        var fp = $('vaultFormPage');
        if (fp) {
            fp.value = '0';
        }
        $('entryModal').classList.add('show');
    }

    function openEditModal(btn) {
        var id = btn.getAttribute('data-id');
        $('modalTitle').innerText = 'Редактировать запись';
        $('entryForm').action = basePath + 'passwords/' + id + '/update';
        $('modalService').value = btn.getAttribute('data-service');
        $('modalLogin').value = btn.getAttribute('data-login');
        $('modalPassword').value = '';
        $('modalPassword').required = false;
        $('modalPasswordLabel').innerText = 'Новый пароль (пусто = без изменений)';
        var desc = btn.getAttribute('data-desc');
        $('modalDesc').value = (desc === 'null' || !desc) ? '' : desc;

        // Очищаем поле профиля, чтобы оно не улетело случайно при обновлении
        var profileField = $('modalProfileName');
        if (profileField) {
            profileField.value = '';
        }

        $('entryModal').classList.add('show');
    }

    function openRevealModal(btn) {
        var id = btn.getAttribute('data-entry-id');
        $('revealForm').action = basePath + 'passwords/' + id + '/reveal';
        var wrap = document.querySelector('.vault-wrapper');
        var p = wrap ? wrap.getAttribute('data-vault-page') : '0';
        var pageField = $('revealFormPage');
        if (pageField) {
            pageField.value = p != null && p !== '' ? p : '0';
        }
        var unlock = $('revealUnlockPassword');
        if (unlock) {
            unlock.value = '';
        }
        $('revealModal').classList.add('show');
    }

    function closeModal() {
        $('entryModal').classList.remove('show');
    }

    function closeRevealModal() {
        $('revealModal').classList.remove('show');
    }

    function openSidebar() {
        $('userSidebar').classList.add('open');
        $('sidebarOverlay').classList.add('show');
    }

    function closeSidebar() {
        $('userSidebar').classList.remove('open');
        $('sidebarOverlay').classList.remove('show');
    }

    function onDocumentClick(event) {
        var trigger = event.target.closest('.menu-trigger-btn');
        if (trigger) {
            openSidebar();
            return;
        }

        if (event.target.id === 'sidebarOverlay') {
            closeSidebar();
            return;
        }

        var sidebarClose = event.target.closest('.close-sidebar-btn');
        if (sidebarClose && event.target.closest('#userSidebar')) {
            closeSidebar();
            return;
        }

        var createBtn = event.target.closest('.js-vault-open-create');
        if (createBtn) {
            openCreateModal();
            return;
        }

        var modalClose = event.target.closest('#entryModal .close-btn');
        if (modalClose) {
            closeModal();
            return;
        }

        var revealClose = event.target.closest('#revealModal .close-btn');
        if (revealClose) {
            closeRevealModal();
            return;
        }

        var revealBtn = event.target.closest('.js-vault-open-reveal');
        if (revealBtn) {
            openRevealModal(revealBtn);
            return;
        }

        var copyBtn = event.target.closest('.js-vault-copy-secret');
        if (copyBtn) {
            var wrap = copyBtn.closest('.vault-revealed-wrap');
            var secret = wrap && wrap.querySelector('.vault-revealed-secret');
            if (secret && secret.textContent) {
                if (navigator.clipboard && navigator.clipboard.writeText) {
                    navigator.clipboard.writeText(secret.textContent).catch(function () {});
                }
            }
            return;
        }

        var editBtn = event.target.closest('.js-vault-open-edit');
        if (editBtn) {
            openEditModal(editBtn);
        }
    }

    function onModalClick(event) {
        if (event.target === $('entryModal')) {
            closeModal();
        }
        if (event.target === $('revealModal')) {
            closeRevealModal();
        }
    }

    function onDeleteSubmit(event) {
        var form = event.target;
        if (!form.classList.contains('js-vault-delete-form')) {
            return;
        }
        var msg = form.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) {
            event.preventDefault();
        }
    }

    document.addEventListener('click', onDocumentClick);
    document.addEventListener('submit', onDeleteSubmit);

    var entryModal = $('entryModal');
    if (entryModal) {
        entryModal.addEventListener('click', onModalClick);
    }
    var revealModal = $('revealModal');
    if (revealModal) {
        revealModal.addEventListener('click', onModalClick);
    }
})();
