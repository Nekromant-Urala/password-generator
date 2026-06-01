(() => {
    'use strict';

    const getBasePath = () => document.body?.getAttribute('data-base-path') || '/';
    const basePath = getBasePath();
    const $ = (id) => document.getElementById(id);

    const openCreateModal = () => {
        $('modalTitle').innerText = 'Новая запись';
        $('entryForm').action = `${basePath}passwords/create`;
        $('modalService').value = '';
        $('modalLogin').value = '';
        $('modalPassword').value = '';
        $('modalPassword').required = false;
        $('modalPasswordLabel').innerText = 'Пароль (необязательно)';
        $('modalDesc').value = '';

        const profileField = $('modalProfileName');

        const fp = $('vaultFormPage');
        if (fp) {
            fp.value = '0';
        }
        $('entryModal').classList.add('show');
    };

    const openEditModal = (btn) => {
        const id = btn.getAttribute('data-id');
        $('modalTitle').innerText = 'Редактировать запись';
        $('entryForm').action = `${basePath}passwords/${id}/update`;
        $('modalService').value = btn.getAttribute('data-service') || '';
        $('modalLogin').value = btn.getAttribute('data-login') || '';
        $('modalPassword').value = '';
        $('modalPassword').required = false;
        $('modalPasswordLabel').innerText = 'Новый пароль (пусто = без изменений)';

        const desc = btn.getAttribute('data-desc');
        $('modalDesc').value = (desc === 'null' || !desc) ? '' : desc;

        const profileField = $('modalProfileName');
        if (profileField) {
            profileField.value = '';
        }

        $('entryModal').classList.add('show');
    };

    const closeModal = () => {
        $('entryModal').classList.remove('show');
    };

    document.addEventListener('click', (event) => {
        if (event.target.closest('.js-vault-open-create')) {
            openCreateModal();
            return;
        }

        const editBtn = event.target.closest('.js-vault-open-edit');
        if (editBtn) {
            openEditModal(editBtn);
            return;
        }

        if (event.target.closest('#entryModal .close-btn') || event.target === $('entryModal')) {
            closeModal();
            return;
        }

        // Копирование секретов в буфер обмена
        const copyBtn = event.target.closest('.js-vault-copy-secret');
        if (copyBtn) {
            const wrap = copyBtn.closest('.vault-revealed-wrap');
            const secret = wrap?.querySelector('.vault-revealed-secret');
            if (secret?.textContent && navigator.clipboard?.writeText) {
                navigator.clipboard.writeText(secret.textContent).catch(() => {});
            }
        }
    });

    document.addEventListener('submit', (event) => {
        const form = event.target;
        if (!form.classList.contains('js-vault-delete-form')) return;

        const msg = form.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) {
            event.preventDefault();
        }
    });
})();